package com.video.youtuberplayer.ui.presenter;


import com.video.youtuberplayer.ui.view.IView;

public interface IPresenter<V extends IView> {

  void onBindView(V view);

  void onUnbindView();
}
