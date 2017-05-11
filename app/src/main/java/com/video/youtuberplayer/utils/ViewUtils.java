package com.video.youtuberplayer.utils;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by hoanghiep on 5/5/17.
 */

public class ViewUtils {
  public static void showSnackToast(View view, String content) {
    Snackbar.make(view, content, Snackbar.LENGTH_LONG).show();
  }

  public static void onShowImage(ImageView imageView, String url) {
    Glide.with(imageView.getContext())
            .load(url)
            .into(imageView);
  }
}
