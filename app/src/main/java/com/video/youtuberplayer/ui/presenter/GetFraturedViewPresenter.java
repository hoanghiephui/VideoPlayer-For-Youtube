package com.video.youtuberplayer.ui.presenter;

import android.view.View;

import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.video.youtuberplayer.model.GetYouTubeVideos;
import com.video.youtuberplayer.model.VideoCategory;
import com.video.youtuberplayer.model.YouTubeVideo;
import com.video.youtuberplayer.ui.contracts.GetFeaturedVideoContract;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hoanghiep on 5/5/17.
 */

public class GetFraturedViewPresenter extends BasePresenter<GetFeaturedVideoContract.IGeFeaturedVideoView, GetFeaturedVideoContract.IGetFeaturedVideoInterceptor>
        implements GetFeaturedVideoContract.IGetFeaturedVideoPresenter {
  public GetFraturedViewPresenter(GetFeaturedVideoContract.IGetFeaturedVideoInterceptor interceptor, CompositeDisposable compositeDisposable) {
    super(interceptor, compositeDisposable);
  }

  @Override
  public void getFeaturedVideo(long maxResults, String token, String tokenNextPage) throws IOException {
    mView.setProgressVisibility(View.VISIBLE);
    if (mView.isInternetConnected()) {
      mInterceptor.getFeaturedVideo(maxResults, token, tokenNextPage)
              .subscribeOn(Schedulers.computation())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(onGetFeatured());
    } else {
      noConnectionError();
    }

  }

  private Observer<VideoListResponse> onGetFeatured() {
    return new Observer<VideoListResponse>() {
      @Override
      public void onSubscribe(@NonNull Disposable d) {
        mSubscribers.add(d);
      }

      @Override
      public void onNext(@NonNull VideoListResponse videos) {
        if (videos != null) {
          mView.addAllVideo(toYouTubeVideoList(videos.getItems()));
          mView.onUpdateView();
          if (videos.getNextPageToken()!= null) {
            mView.setTokenNextPage(videos.getNextPageToken());
          }
          mView.setRecyclerViewVisibility(View.VISIBLE);
          mView.setProgressVisibility(View.GONE);

        } else {
          mView.onErrorInServer();
        }

      }

      @Override
      public void onError(@NonNull Throwable e) {
        e.printStackTrace();
      }

      @Override
      public void onComplete() {

      }
    };
  }

  protected List<YouTubeVideo> toYouTubeVideoList(List<Video> videoList) {
    List<YouTubeVideo> youTubeVideoList = new ArrayList<>();

    if (videoList != null) {
      YouTubeVideo youTubeVideo;

      for (Video video : videoList) {
        youTubeVideo = new YouTubeVideo(video);
        if (!youTubeVideo.filterVideoByLanguage())
          youTubeVideoList.add(youTubeVideo);
      }
    }

    return youTubeVideoList;
  }
}
