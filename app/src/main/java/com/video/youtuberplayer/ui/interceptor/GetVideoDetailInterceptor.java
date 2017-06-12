package com.video.youtuberplayer.ui.interceptor;

import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.video.youtuberplayer.model.VideoRealatedAndChanel;
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
  public Observable<VideoRealatedAndChanel> getRelatedAndChannel(String idVideo, String idChannel, String token
          , String tokenPage) throws IOException {
    return videoMethod.getRealatedAndChanel(idVideo, idChannel, token, tokenPage);
  }
}
