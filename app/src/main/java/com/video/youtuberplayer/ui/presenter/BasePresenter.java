package com.video.youtuberplayer.ui.presenter;

import android.view.View;

import com.video.youtuberplayer.ui.view.IView;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BasePresenter<V extends IView, I> implements IPresenter<V> {

  protected V mView;
  protected I mInterceptor;

  protected CompositeDisposable mSubscribers;

  protected BasePresenter(I interceptor, CompositeDisposable compositeDisposable) {
    mSubscribers = compositeDisposable;
    mInterceptor = interceptor;
  }

  @Override
  public void onBindView(V view) {
    mView = view;
  }

  @Override
  public void onUnbindView() {
    if (mView != null)
      mView = null;

    if (mSubscribers != null)
      mSubscribers.dispose();

  }

  protected void noConnectionError() {

    if (mView.isAdded()) {
      mView.onErrorNoConnection();
      mView.setProgressVisibility(View.GONE);
    }

  }
}
