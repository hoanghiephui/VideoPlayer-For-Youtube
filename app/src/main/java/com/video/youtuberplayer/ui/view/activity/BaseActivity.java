package com.video.youtuberplayer.ui.view.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.video.youtuberplayer.R;
import com.video.youtuberplayer.VideoPlayerApplication;
import com.video.youtuberplayer.utils.ServerUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.video.youtuberplayer.utils.AnimationUtils.animateView;

/**
 * Created by hoanghiep on 5/5/17.
 */

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        DrawerLayout.DrawerListener {

    public static final int LINEAR_LAYOUT = 0;
    public static final int GRID_LAYOUT = 1;
    public static final int STAGGERED = 2;

    protected Unbinder mBinder;
    protected Snackbar mSnackbar;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @Nullable
    @BindView(R.id.contentPanel)
    CoordinatorLayout mCoordinatorLayout;
    @Nullable
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Nullable
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @Nullable
    @BindView(R.id.error_message_view)
    TextView errorTextView;
    @Nullable
    @BindView(R.id.error_button_retry)
    Button errorButtonRetry;
    @Nullable
    @BindView(R.id.error_panel)
    View errorPanel;

    protected AtomicBoolean isLoading = new AtomicBoolean(false);
    protected AtomicBoolean wasLoading = new AtomicBoolean(false);
    private CompositeDisposable disposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VideoPlayerApplication.getVideoPlayerApp().addActivity(this);
        setContentView(getActivityBaseViewID());
        initPresenter();
        if (mToolbar != null) {
            onSetupActionBar();
        }
        onSetupNavigationDrawer();
        onContent();
        disposable = new CompositeDisposable();
        onSubscribeEventRx();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    private void onSetupNavigationDrawer() {
        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
            View view = mNavigationView.getHeaderView(0);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onCloseNavigation();
                }
            });
            if (mDrawerLayout != null) {
                mDrawerLayout.addDrawerListener(this);
            }
        }

    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        injectViews();
    }

    private void injectViews() {
        mBinder = ButterKnife.bind(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            default:
                return false;
        }

    }

    private void onSetupActionBar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getMenuLayoutID() != 0)
            getMenuInflater().inflate(getMenuLayoutID(), menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }

    public void onError(int msgID) {
        mSnackbar = Snackbar
                .make(getCoordinatorLayout(), getString(msgID), Snackbar.LENGTH_LONG);

        mSnackbar.setActionTextColor(Color.RED);
        mSnackbar.show();
        mSnackbar.setAction(getString(R.string.try_again).toUpperCase(), onSnackbarClickListener());

    }

    public void onErrorNoConnection() {
        onError(R.string.error_no_internet);
    }

    public void onErrorInServer() {
        onError(R.string.error_no_server);
    }

    public void onErrorUnexpected() {
        onError(R.string.erro_unexpected);
    }

    public boolean isInternetConnected() {
        return ServerUtils.isNetworkConnected(this);
    }

    protected abstract void initPresenter();

    protected abstract int getActivityBaseViewID();

    protected abstract void onContent();

    public CoordinatorLayout getCoordinatorLayout() {
        return mCoordinatorLayout;
    }

    protected void startFragment(int fragmentID, Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentById(fragmentID);

        if (null == f) {
            fm.beginTransaction()
                    .add(fragmentID, fragment)
                    .commitAllowingStateLoss();
        } else {
            fm.beginTransaction()
                    .replace(fragmentID, fragment)
                    .commitAllowingStateLoss();
        }
    }

    protected void startFragment(int fragmentID, Fragment fragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentById(fragmentID);

        if (null == f) {
            fm.beginTransaction()
                    .add(fragmentID, fragment, tag)
                    .commitAllowingStateLoss();
        } else {
            fm.beginTransaction()
                    .replace(fragmentID, fragment, tag)
                    .commitAllowingStateLoss();
        }
    }

    protected void setActivityTitle(String title) {
        if (!TextUtils.isEmpty(title))
            if (mToolbar != null) {
                mToolbar.setTitle(title);
            }
    }

    protected void setTransToolbar() {
        if (mToolbar != null) {
            mToolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        }
    }

    protected void setActivitySubtitle(String subtitle) {
        if (!TextUtils.isEmpty(subtitle))
            if (mToolbar != null) {
                mToolbar.setSubtitle(subtitle);
            }
    }

    protected abstract View.OnClickListener onSnackbarClickListener();

    protected abstract int getMenuLayoutID();

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        if (mCoordinatorLayout != null) {
            mCoordinatorLayout.setX(slideOffset * drawerView.getWidth());
        }
    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START, true);
        } else {
            super.onBackPressed();
        }
    }

    private void onCloseNavigation() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START, true);
        }
    }

    protected void setErrorMessage(String message, boolean showRetryButton) {
        if (errorTextView == null) {
            return;
        }

        errorTextView.setText(message);
        if (showRetryButton) {
            animateView(errorButtonRetry, true, 300);
        } else {
            animateView(errorButtonRetry, false, 0);
        }

        animateView(errorPanel, true, 300);
        isLoading.set(false);

        //animateView(loadingProgressBar, false, 200);
    }

    @Optional
    @OnClick(R.id.error_button_retry)
    public void onRetry() {
        onRetryButtonClicked();
    }

    protected void onRetryButtonClicked() {
        reloadContent();
    }

    protected abstract void reloadContent();

    private void onSubscribeEventRx() {
        disposable.add(((VideoPlayerApplication)getApplication())
                .rxBus()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .delay(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object object) throws Exception {
                        onSubscribeEvent(object);
                    }
                }));
    }

    public void onSubscribeEvent(Object object) {

    }
}
