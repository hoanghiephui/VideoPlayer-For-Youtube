package com.video.youtuberplayer.ui.interceptor;

import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.video.youtuberplayer.remote.methods.VideoMethod;
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
  public Observable<List<Video>> getVideoDetail(String token, String id) throws IOException {
    return videoMethod.getVideoDetail(token, id);
  }

  @Override
  public Observable<List<SearchResult>> getRelatedToVideoId(String id, String token, String tokenPage) throws IOException {
    return videoMethod.getRelatedVideos(id, token, tokenPage);
  }

  @Override
  public Observable<ChannelListResponse> geChannel(String id) throws IOException {
    return videoMethod.getChannelYoutube(id);
  }
}
