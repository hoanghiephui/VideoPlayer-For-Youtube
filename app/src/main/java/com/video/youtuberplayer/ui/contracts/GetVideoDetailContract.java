package com.video.youtuberplayer.ui.contracts;

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
    Observable<List<Video>> getVideoDetail(VideoCategory videoCategory, String token, String id) throws IOException;
  }

  public interface IGetVideoDetailPresenter extends IPresenter<IGetVideoDetailView> {
    void getVideoDetail(VideoCategory videos, String token, String id) throws IOException;
  }

  public interface IGetVideoDetailView extends IView {
    void onUpdateView(List<Video> videoList);
  }
}
