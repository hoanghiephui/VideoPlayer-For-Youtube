package com.video.youtuberplayer.ui.view.activity;

import android.content.Intent;
import android.view.View;

import com.video.youtuberplayer.R;
import com.video.youtuberplayer.ui.view.fragment.LoginFragment;
import com.video.youtuberplayer.ui.view.fragment.SplashFragment;
import com.video.youtuberplayer.utils.PrefsUtils;

/**
 * Created by hoanghiep on 5/5/17.
 */

public class SplashActivity extends BaseActivity {
  @Override
  protected void initPresenter() {

  }

  @Override
  protected int getActivityBaseViewID() {
    return R.layout.activity_splash;
  }

  @Override
  protected void onContent() {
    startFragment(R.id.content, new SplashFragment());
  }

  @Override
  protected View.OnClickListener onSnackbarClickListener() {
    return null;
  }

  @Override
  protected int getMenuLayoutID() {
    return 0;
  }

  @Override
  protected void reloadContent() {

  }

  public void onOpenLogin(){
    if (PrefsUtils.getAccount(this) != null) {
      onOpenMain();
    } else {
      startFragment(R.id.content, new LoginFragment());
    }
  }

  public void onOpenMain() {
    startActivity(new Intent(this, MainActivity.class));
    finish();
  }
}
