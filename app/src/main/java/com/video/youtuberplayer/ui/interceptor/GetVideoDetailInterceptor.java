package com.video.youtuberplayer.ui.interceptor;

import com.google.api.services.youtube.model.Video;
import com.video.youtuberplayer.model.VideoCategory;
import com.video.youtuberplayer.remote.mothods.VideoMethod;
import com.video.youtuberplayer.ui.contracts.GetVideoDetailContract;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by hoanghiep on 5/10/17.
 */

public class GetVideoDetailInterceptor implements GetVideoDetailContract.IGetVideoDetailInterceptor{
  private VideoMethod videoMethod;

  public GetVideoDetailInterceptor() {
    videoMethod = new VideoMethod();
  }

  @Override
  public Observable<List<Video>> getVideoDetail(VideoCategory videos, String token, String id) throws IOException {
    return videoMethod.getVideoDetail(videos, token, id);
  }
}