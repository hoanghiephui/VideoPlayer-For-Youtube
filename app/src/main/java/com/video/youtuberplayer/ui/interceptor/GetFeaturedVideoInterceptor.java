package com.video.youtuberplayer.ui.interceptor;

import com.google.api.services.youtube.model.VideoListResponse;
import com.video.youtuberplayer.model.GetYouTubeVideos;
import com.video.youtuberplayer.model.VideoCategory;
import com.video.youtuberplayer.model.YouTubeVideo;
import com.video.youtuberplayer.remote.mothods.VideoMethod;
import com.video.youtuberplayer.ui.contracts.GetFeaturedVideoContract;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by hoanghiep on 5/5/17.
 */

public class GetFeaturedVideoInterceptor implements GetFeaturedVideoContract.IGetFeaturedVideoInterceptor {
  private VideoMethod videoMethod;

  public GetFeaturedVideoInterceptor() {
    this.videoMethod = new VideoMethod();
  }

  @Override
  public Observable<VideoListResponse> getFeaturedVideo(long maxResults, String token, String tokenNextPage) throws IOException {
    return videoMethod.getFeaturedVideo(maxResults, token, tokenNextPage);
  }
}
