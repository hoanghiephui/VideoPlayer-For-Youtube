package com.video.youtuberplayer.ui.presenter;

import android.view.View;

import com.video.youtuberplayer.model.GetYouTubeVideos;
import com.video.youtuberplayer.model.VideoCategory;
import com.video.youtuberplayer.ui.contracts.GetFeaturedVideoContract;

import java.io.IOException;
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
  public void getFeaturedVideo(VideoCategory videos, long maxResults, String token, String tokenNextPage) throws IOException {
    mView.setProgressVisibility(View.VISIBLE);
    if (mView.isInternetConnected()) {
      mInterceptor.getFeaturedVideo(videos, maxResults, token, tokenNextPage)
              .delay(2000, TimeUnit.MILLISECONDS)
              .subscribeOn(Schedulers.newThread())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(onGetFeatured());
    } else {
      noConnectionError();
    }

  }

  private Observer<GetYouTubeVideos> onGetFeatured() {
    return new Observer<GetYouTubeVideos>() {
      @Override
      public void onSubscribe(@NonNull Disposable d) {
        mSubscribers.add(d);
      }

      @Override
      public void onNext(@NonNull GetYouTubeVideos videos) {
        if (videos != null) {
          mView.addAllVideo(videos.getNextVideos(), videos.noMoreVideoPages());
          mView.onUpdateView();
          if (videos.tokenNextPage() != null) {
            mView.setTokenNextPage(videos.tokenNextPage());
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
}
