package com.video.youtuberplayer.ui.view.activity;

import android.view.View;

import com.video.youtuberplayer.R;

/**
 * Created by hoanghiep on 5/7/17.
 */

public class PlayerVideoActivity extends BaseActivity {
  @Override
  protected int getActivityBaseViewID() {
    return R.layout.activity_player_video;
  }

  @Override
  protected void onContent() {
    setTransToolbar();
  }

  @Override
  protected View.OnClickListener onSnackbarClickListener() {
    return null;
  }

  @Override
  protected int getMenuLayoutID() {
    return R.menu.menu_player;
  }
}
