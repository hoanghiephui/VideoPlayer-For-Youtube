package com.video.youtuberplayer.ui.presenter;

import android.util.Log;

import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.video.youtuberplayer.model.VideoRealatedAndChanel;
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
    public void getRelatedAndChannel(String idVideo, String idChannel, String token, String tokenPage) throws IOException {
        mInterceptor.getRelatedAndChannel(idVideo, idChannel, token, tokenPage)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onRelatedAndChanel());
    }

    private Observer<VideoRealatedAndChanel> onRelatedAndChanel() {
        return new Observer<VideoRealatedAndChanel>() {
            @Override
            public void onSubscribe(Disposable d) {
                mSubscribers.add(d);
            }

            @Override
            public void onNext(VideoRealatedAndChanel videoRealatedAndChanel) {
                mView.onUpdateViewRelatedAndChannel(videoRealatedAndChanel);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        };
    }

}
