package com.video.youtuberplayer.ui.presenter;

import android.view.View;

import com.video.youtuberplayer.ui.view.IView;

public abstract class BasePresenter<V extends IView, I> implements IPresenter<V> {

  protected V mView;
  protected I mInterceptor;

  protected BasePresenter(I interceptor) {
    mInterceptor = interceptor;
  }

  protected BasePresenter() {
  }

  @Override
  public void onBindView(V view) {
    mView = view;
  }

  @Override
  public void onUnbindView() {
    if (mView != null)
      mView = null;
  }

  protected void noConnectionError() {

    if (mView.isAdded()) {
      mView.onErrorNoConnection();
      mView.setProgressVisibility(View.GONE);
    }

  }

}
