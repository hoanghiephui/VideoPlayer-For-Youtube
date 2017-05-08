package com.video.youtuberplayer.ui.presenter;

import android.view.View;

import com.video.youtuberplayer.model.GetYouTubeVideos;
import com.video.youtuberplayer.model.VideoCategory;
import com.video.youtuberplayer.ui.contracts.GetFeaturedVideoContract;

import java.io.IOException;

import io.reactivex.Observer;
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
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .unsubscribeOn(Schedulers.io())
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
          if (videos.noMoreVideoPages()) {
            mView.setListVideo(videos.getNextVideos(), videos.noMoreVideoPages());
            mView.setupRecyclerView();
          } else {
            mView.addAllVideo(videos.getNextVideos(), videos.noMoreVideoPages());
            mView.onUpdateView();
          }
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
