package com.video.youtuberplayer.ui.view.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.video.youtuberplayer.R;
import com.video.youtuberplayer.ui.view.activity.BaseActivity;
import com.video.youtuberplayer.utils.ServerUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by hoanghiep on 5/5/17.
 */

public abstract class BaseFragment extends Fragment {
  protected Snackbar mSnackbar;
  protected Unbinder mBinder;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(getViewLayout(), container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    injectViews(view);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    onInitContent(savedInstanceState);
  }

  private void injectViews(View view) {
    mBinder = ButterKnife.bind(this, view);
  }

  protected abstract int getViewLayout();

  protected abstract void onInitContent(Bundle savedInstanceState);

  public boolean isInternetConnected() {
    return ServerUtils.isNetworkConnected(getActivity());
  }

  protected void startFragment(int fragmentID, Fragment fragment) {
    FragmentManager fm = getChildFragmentManager();
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

  public void onError(int msgID) {
    mSnackbar = Snackbar
      .make(getCoordinatorLayout(), msgID, Snackbar.LENGTH_LONG);

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

  protected abstract View.OnClickListener onSnackbarClickListener();

  public CoordinatorLayout getCoordinatorLayout() {
    return ((BaseActivity) getActivity()).getCoordinatorLayout();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }

  @Override
  public void onDetach() {
    super.onDetach();

    if (mBinder != null)
      mBinder.unbind();

    if (mSnackbar != null)
      mSnackbar.dismiss();
  }
}
