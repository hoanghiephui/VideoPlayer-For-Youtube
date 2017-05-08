package com.video.youtuberplayer.remote.mothods;

import com.google.api.services.youtube.YouTube;
import com.video.youtuberplayer.api.GetGuideCategories;
import com.video.youtuberplayer.model.GetYouTubeVideos;
import com.video.youtuberplayer.model.VideoCategory;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by hoanghiep on 5/5/17.
 */

public class VideoMethod {
  public Observable<GetYouTubeVideos> getFeaturedVideo(VideoCategory videoCategory, final long maxResults, final String token, final String tokenNextPage) throws IOException {
    return Observable.just(videoCategory.createGetYouTubeVideos())
            .doAfterNext(new Consumer<GetYouTubeVideos>() {
              @Override
              public void accept(@NonNull GetYouTubeVideos videos) throws Exception {
                videos.init(maxResults, token, tokenNextPage);
              }
            });
  }

  public Observable<GetYouTubeVideos> getGuideCategories(VideoCategory videoCategory, final String regionCode, final String hl, final String token) {
    return Observable.just(videoCategory.createGetYouTubeVideos())
            .doAfterNext(new Consumer<GetYouTubeVideos>() {
              @Override
              public void accept(@NonNull GetYouTubeVideos videos) throws Exception {
                videos.initGuideCategories(regionCode, hl, token);
              }
            });
  }

  /*try {
    YouTube.Activities.List videosList = YouTubeAPI.create()
            .activities().list("snippet, contentDetails")
            .setHome(true)
            .setMaxResults(25L)
            .setOauthToken(account.getToken())
            .setKey(YouTubeAPIKey.get().getYouTubeAPIKey());
    ActivityListResponse activityListResponse = videosList.execute();
    for (Activity activity : activityListResponse.getItems()) {
      Log.d(TAG, "onInitContent: " + activity.getSnippet().getTitle());
    }
  } catch (IOException e) {
    e.printStackTrace();
  }*/
}
