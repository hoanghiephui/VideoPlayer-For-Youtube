package com.video.youtuberplayer.ui.view.fragment;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Activity;
import com.google.api.services.youtube.model.ActivityListResponse;
import com.video.youtuberplayer.GetYouTubeVideosTask;
import com.video.youtuberplayer.R;
import com.video.youtuberplayer.model.GetYouTubeVideos;
import com.video.youtuberplayer.model.YouTubeVideo;
import com.video.youtuberplayer.remote.YouTubeAPI;
import com.video.youtuberplayer.remote.YouTubeAPIKey;

import java.io.IOException;
import java.util.List;

/**
 * Created by hoanghiep on 5/5/17.
 */

public class MainFragment extends BaseFragment {
  private static final String TAG = MainFragment.class.getSimpleName();
  @Override
  protected int getViewLayout() {
    return R.layout.fragment_playlist;
  }

  @Override
  protected void onInitContent(Bundle savedInstanceState) {
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);
    try {
      YouTube.Activities.List videosList = YouTubeAPI.create()
        .activities().list("snippet, statistics, contentDetails")
        .setKey(YouTubeAPIKey.get().getYouTubeAPIKey());
      ActivityListResponse activityListResponse = videosList.execute();
      for (Activity activity : activityListResponse.getItems()) {
        Log.d(TAG, "onInitContent: " + activity.getSnippet().getTitle());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected View.OnClickListener onSnackbarClickListener() {
    return null;
  }
}
