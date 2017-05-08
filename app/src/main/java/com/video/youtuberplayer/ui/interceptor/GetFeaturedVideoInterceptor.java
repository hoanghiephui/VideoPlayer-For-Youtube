package com.video.youtuberplayer.ui.interceptor;

import com.video.youtuberplayer.model.GetYouTubeVideos;
import com.video.youtuberplayer.model.VideoCategory;
import com.video.youtuberplayer.remote.mothods.VideoMethod;
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
  public Observable<GetYouTubeVideos> getFeaturedVideo(VideoCategory videos, long maxResults, String token, String tokenNextPage) throws IOException {
    return videoMethod.getFeaturedVideo(videos, maxResults, token, tokenNextPage);
  }
}
