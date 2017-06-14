package com.video.youtuberplayer.ui.contracts;

import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.video.youtuberplayer.model.GetYouTubeVideos;
import com.video.youtuberplayer.model.VideoCategory;
import com.video.youtuberplayer.model.VideoListHome;
import com.video.youtuberplayer.model.YouTubeVideo;
import com.video.youtuberplayer.ui.presenter.IPresenter;
import com.video.youtuberplayer.ui.view.IView;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by hoanghiep on 5/5/17.
 */

public class GetFeaturedVideoContract {
  public interface IGetFeaturedVideoInterceptor {
    Observable<VideoListResponse> getFeaturedVideo(final long maxResults, final String token, final String tokenNextPage) throws IOException;
    Observable<VideoListHome> getListVideoHome(long maxResults, String token, String tokenNextPage) throws IOException;
  }

  public interface IGetFeaturedVideoPresenter extends IPresenter<IGeFeaturedVideoView> {
    void getFeaturedVideo(long maxResults, String token, String tokenNextPage) throws IOException;

    void getListVideoHome(long maxResults, String token, String tokenNextPage) throws IOException;

    List<YouTubeVideo> toYouTubeVideoList(List<Video> videoList);

  }

  public interface IGeFeaturedVideoView extends IView {
    void setRecyclerViewVisibility(int visibilityState);
    void setupRecyclerView();
    void onUpdateView();
    void addAllVideo(List<YouTubeVideo> nextVideos);

    void setTokenNextPage(String tokenNextPage);

    void onUpdateViewHome(VideoListHome videoListHome);
  }
}
