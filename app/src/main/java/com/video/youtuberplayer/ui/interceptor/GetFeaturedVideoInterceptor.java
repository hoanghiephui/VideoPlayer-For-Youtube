package com.video.youtuberplayer.ui.interceptor;

import com.google.api.services.youtube.model.VideoListResponse;
import com.video.youtuberplayer.remote.methods.VideoMethod;
import com.video.youtuberplayer.ui.contracts.GetFeaturedVideoContract;

import java.io.IOException;

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
