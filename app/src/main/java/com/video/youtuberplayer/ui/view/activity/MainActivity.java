package com.video.youtuberplayer.ui.view.activity;

import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;

import com.video.youtuberplayer.R;
import com.video.youtuberplayer.ui.view.fragment.MainFragment;

public class MainActivity extends BaseActivity {
  @Override
  protected void initPresenter() {

  }

  @Override
  protected int getActivityBaseViewID() {
    return R.layout.activity_main;
  }

  @Override
  protected void onContent() {
    onSetupDrawerLayout();
    startFragment(R.id.content_fragment, new MainFragment());
  }

  @Override
  protected View.OnClickListener onSnackbarClickListener() {
    return null;
  }

  @Override
  protected int getMenuLayoutID() {
    return 0;
  }

  private void onSetupDrawerLayout() {
    ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
      this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    if (mDrawerLayout != null) {
      mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
    }
    actionBarDrawerToggle.syncState();
  }
}
