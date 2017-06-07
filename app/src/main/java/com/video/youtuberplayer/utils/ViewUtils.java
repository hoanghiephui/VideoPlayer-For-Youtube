package com.video.youtuberplayer.utils;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelSnippet;
import com.google.api.services.youtube.model.ThumbnailDetails;
import com.video.youtuberplayer.R;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hoanghiep on 5/5/17.
 */

public class ViewUtils {
  public static void showSnackToast(View view, String content) {
    Snackbar.make(view, content, Snackbar.LENGTH_LONG).show();
  }

  public static void showSnackToast(View view, int content) {
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

  public static String thumbnailNormalUrl(Channel channel) {
    String thumbnailNormalUrl = null;
    ChannelSnippet snippet = channel.getSnippet();
    if (snippet != null) {
      ThumbnailDetails thumbnail = snippet.getThumbnails();
      if (thumbnail != null) {
        thumbnailNormalUrl = snippet.getThumbnails().getDefault().getUrl();

        // YouTube Bug:  channels with no thumbnail/avatar will return a link to the default
        // thumbnail that does NOT start with "http" or "https", but rather it starts with
        // "//s.ytimg.com/...".  So in this case, we just add "https:" in front.
        String thumbnailUrlLowerCase = thumbnailNormalUrl.toLowerCase();
        if (!(thumbnailUrlLowerCase.startsWith("http://") || thumbnailUrlLowerCase.startsWith("https://")))
          thumbnailNormalUrl = "https:" + thumbnailNormalUrl;
      }
    }
    return thumbnailNormalUrl;
  }

  private static String formatDate(String date, Context context) {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    Date datum = null;
    try {
      datum = formatter.parse(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());

    return df.format(datum);
  }

  public static String localizeDate(String date, Context context) {
    Resources res = context.getResources();
    String dateString = res.getString(R.string.upload_date_text);

    String formattedDate = formatDate(date, context);
    return String.format(dateString, formattedDate);
  }
}
