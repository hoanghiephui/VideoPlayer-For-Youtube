package com.video.youtuberplayer.ui.presenter;

import android.util.Log;

import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
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
    public void getVideoDetail(String token, String id) throws IOException {
        mInterceptor.getVideoDetail(token, id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onGetVideoDetail());
    }

    @Override
    public void getRelatedToVideoId(String id, String token, String tokenPage) throws IOException {
        mInterceptor.getRelatedToVideoId(id, token, tokenPage)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onRelatedToVideoId());
    }

    @Override
    public void getChannel(String id) throws IOException {
        mInterceptor.geChannel(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onChannel());
    }

    private Observer<? super ChannelListResponse> onChannel() {
        return new Observer<ChannelListResponse>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                mSubscribers.add(d);
            }

            @Override
            public void onNext(@NonNull ChannelListResponse channelListResponse) {
                mView.onUpdateChannel(channelListResponse);
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

    private Observer<? super List<SearchResult>> onRelatedToVideoId() {
        return new Observer<List<SearchResult>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                mSubscribers.add(d);
            }

            @Override
            public void onNext(@NonNull List<SearchResult> resultList) {
                mView.onUpdateViewRelated(resultList);
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
