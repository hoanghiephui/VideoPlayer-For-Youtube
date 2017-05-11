package com.video.youtuberplayer.ui.presenter;

import android.util.Log;

import com.google.api.services.youtube.model.Video;
import com.video.youtuberplayer.model.VideoCategory;
import com.video.youtuberplayer.ui.contracts.GetVideoDetailContract;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hoanghiep on 5/10/17.
 */

public class GetVideoDetailPresenter extends BasePresenter<GetVideoDetailContract.IGetVideoDetailView, GetVideoDetailContract.IGetVideoDetailInterceptor>
 implements GetVideoDetailContract.IGetVideoDetailPresenter {
  private static final String TAG = GetVideoDetailPresenter.class.getSimpleName();

  public GetVideoDetailPresenter(GetVideoDetailContract.IGetVideoDetailInterceptor interceptor, CompositeDisposable compositeDisposable) {
    super(interceptor, compositeDisposable);
  }

  @Override
  public void getVideoDetail(VideoCategory videos, String token, String id) throws IOException {
    mInterceptor.getVideoDetail(videos, token, id)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onGetVideoDetail());
  }

  private Observer<List<Video>> onGetVideoDetail() {
    return new Observer<List<Video>>() {
      @Override
      public void onSubscribe(@NonNull Disposable d) {
        mSubscribers.add(d);
      }

      @Override
      public void onNext(@NonNull List<Video> videoList) {
        mView.onUpdateView(videoList);
      }

      @Override
      public void onError(@NonNull Throwable e) {
        Log.d(TAG, "onError: " + e.getMessage());
      }

      @Override
      public void onComplete() {

      }
    };
  }
}
