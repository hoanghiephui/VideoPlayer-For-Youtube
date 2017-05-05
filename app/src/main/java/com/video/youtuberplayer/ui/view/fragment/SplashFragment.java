package com.video.youtuberplayer.ui.view.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.video.youtuberplayer.R;
import com.video.youtuberplayer.ui.view.activity.SplashActivity;

/**
 * Created by hoanghiep on 5/5/17.
 */

public class SplashFragment extends BaseFragment {

  @Override
  protected int getViewLayout() {
    return R.layout.fragment_splash;
  }

  @Override
  protected void onInitContent(Bundle savedInstanceState) {
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        ((SplashActivity) getActivity()).onOpenLogin();
      }
    }, 3000);
  }

  @Override
  protected View.OnClickListener onSnackbarClickListener() {
    return null;
  }
}
