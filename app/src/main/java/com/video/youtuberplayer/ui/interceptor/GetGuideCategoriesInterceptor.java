package com.video.youtuberplayer.ui.interceptor;

import com.video.youtuberplayer.model.GetYouTubeVideos;
import com.video.youtuberplayer.model.VideoCategory;
import com.video.youtuberplayer.remote.mothods.VideoMethod;
import com.video.youtuberplayer.ui.contracts.GuideCategoriesContract;

import java.io.IOException;

import io.reactivex.Observable;

/**
 * Created by hoanghiep on 5/6/17.
 */

public class GetGuideCategoriesInterceptor implements GuideCategoriesContract.IGuideCategoriesInterceptor {
  private VideoMethod videoMethod;

  public GetGuideCategoriesInterceptor() {
    videoMethod = new VideoMethod();
  }

  @Override
  public Observable<GetYouTubeVideos> getGuideCategories(VideoCategory videoCategory, String regionCode, String hl, String token) throws IOException {
    return videoMethod.getGuideCategories(videoCategory, regionCode, hl, token);
  }
}
