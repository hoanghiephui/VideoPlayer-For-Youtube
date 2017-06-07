package com.video.youtuberplayer.ui.contracts;

import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.video.youtuberplayer.model.VideoCategory;
import com.video.youtuberplayer.ui.presenter.IPresenter;
import com.video.youtuberplayer.ui.view.IView;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by hoanghiep on 5/10/17.
 */

public class GetVideoDetailContract {
  public interface IGetVideoDetailInterceptor {
    Observable<List<Video>> getVideoDetail(String token, String id) throws IOException;

    Observable<List<SearchResult>> getRelatedToVideoId(String id, String token, String tokenPage) throws IOException;

    Observable<ChannelListResponse> geChannel(String id) throws IOException;
  }

  public interface IGetVideoDetailPresenter extends IPresenter<IGetVideoDetailView> {

    void getVideoDetail(String token, String id) throws IOException;

    void getRelatedToVideoId(String id, String token, String tokenPage) throws IOException;

    void getChannel(String id) throws IOException;
  }

  public interface IGetVideoDetailView extends IView {

    void onUpdateView(List<Video> videoList);

    void onUpdateViewRelated(List<SearchResult> resultList);

    void onUpdateChannel(ChannelListResponse channelListResponse);
  }
}
