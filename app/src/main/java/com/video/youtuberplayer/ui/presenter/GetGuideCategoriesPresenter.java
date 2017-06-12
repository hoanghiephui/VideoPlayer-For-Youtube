package com.video.youtuberplayer.ui.presenter;

import com.google.api.services.youtube.model.GuideCategory;
import com.video.youtuberplayer.model.GetYouTubeVideos;
import com.video.youtuberplayer.model.VideoCategory;
import com.video.youtuberplayer.ui.contracts.GuideCategoriesContract;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hoanghiep on 5/6/17.
 */

public class GetGuideCategoriesPresenter extends BasePresenter<GuideCategoriesContract.IGuideCategoriesView, GuideCategoriesContract.IGuideCategoriesInterceptor>
        implements GuideCategoriesContract.IGuideCategoriesPresenter {

  public GetGuideCategoriesPresenter(GuideCategoriesContract.IGuideCategoriesInterceptor interceptor, CompositeDisposable compositeDisposable) {
    super(interceptor, compositeDisposable);
  }

  @Override
  public void getGuideCategories(String regionCode, String hl, String token) throws IOException {
    mInterceptor.getGuideCategories(regionCode, hl, token)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onGetGuideCategorie());
  }

  private Observer<List<GuideCategory>> onGetGuideCategorie() {
    return new Observer<List<GuideCategory>>() {
      @Override
      public void onSubscribe(@NonNull Disposable d) {
        mSubscribers.add(d);
      }

      @Override
      public void onNext(@NonNull List<GuideCategory> videos) {
        if (videos != null) {
          mView.updateView(videos);
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
