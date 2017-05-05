package com.video.youtuberplayer.utils;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by hoanghiep on 5/5/17.
 */

public class ViewUtils {
  public static void showSnackToast(View view, String content) {
    Snackbar.make(view, content, Snackbar.LENGTH_LONG).show();
  }
}
