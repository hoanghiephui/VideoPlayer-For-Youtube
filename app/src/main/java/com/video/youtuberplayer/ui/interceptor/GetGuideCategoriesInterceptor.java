package com.video.youtuberplayer.ui.interceptor;

import com.google.api.services.youtube.model.GuideCategory;
import com.video.youtuberplayer.remote.methods.VideoMethod;
import com.video.youtuberplayer.ui.contracts.GuideCategoriesContract;

import java.io.IOException;
import java.util.List;

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
  public Observable<List<GuideCategory>> getGuideCategories(String regionCode, String hl, String token) throws IOException {
    return videoMethod.getGuideCategories(regionCode, hl, token);
  }
}
