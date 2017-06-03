package com.video.youtuberplayer.utils;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.video.youtuberplayer.R;

import java.text.NumberFormat;
import java.util.Locale;

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
            .diskCacheStrategy(DiskCacheStrategy.RESULT)
            .into(imageView);
  }

  public static String localizeViewCount(long viewCount, Context context) {
    Resources res = context.getResources();
    String viewsString = res.getString(R.string.view_count_text);

    NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
    String formattedViewCount = nf.format(viewCount);
    return String.format(viewsString, formattedViewCount);
  }
}
