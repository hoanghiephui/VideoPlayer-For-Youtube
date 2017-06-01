package com.video.youtuberplayer.remote.mothods;

import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.video.youtuberplayer.api.GetChannel;
import com.video.youtuberplayer.api.GetFeaturedVideos;
import com.video.youtuberplayer.api.GetRelatedVideos;
import com.video.youtuberplayer.model.GetYouTubeVideos;
import com.video.youtuberplayer.model.VideoCategory;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by hoanghiep on 5/5/17.
 */

public class VideoMethod {
    public Observable<VideoListResponse> getFeaturedVideo(final VideoCategory videoCategory, final long maxResults, final String token,
                                                          final String tokenNextPage) throws IOException {
        return Observable.create(new ObservableOnSubscribe<VideoListResponse>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<VideoListResponse> e) throws Exception {
                e.onNext(new GetFeaturedVideos(maxResults, token, tokenNextPage).getResponse());
                e.onComplete();
            }
        });
    }

    public Observable<GetYouTubeVideos> getGuideCategories(VideoCategory videoCategory, final String regionCode,
                                                           final String hl, final String token) throws IOException {
        return Observable.just(videoCategory.createGetYouTubeVideos(0, "", ""))
                .doOnNext(new Consumer<GetYouTubeVideos>() {
                    @Override
                    public void accept(@NonNull GetYouTubeVideos videos) throws Exception {
                        videos.initGuideCategories(regionCode, hl, token);
                    }
                });
    }

    public Observable<List<Video>> getVideoDetail(VideoCategory videoCategory, final String token, final String id) throws IOException {
        return Observable.just(videoCategory.getVideoDetail(token, id).getVideoDetail());
    }

    public Observable<List<SearchResult>> getRelatedVideos(String id, String token, String tokenPage) throws IOException {
        return Observable.just(new GetRelatedVideos(id, token, tokenPage).getVideoList());
    }

    public Observable<ChannelListResponse> getChannelYoutube(String id) throws IOException {
        return Observable.just(new GetChannel(id).getResponse());
    }
  /*try {
    YouTube.Activities.List videosList = YouTubeAPI.create()
            .activities().list("snippet, contentDetails")
            .setHome(true)
            .setMaxResults(25L)
            .setOauthToken(account.getToken())
            .setKey(YouTubeAPIKey.get().getYouTubeAPIKey());
    ActivityListResponse activityListResponse = videosList.execute();
    for (Activity activity : activityListResponse.getItems()) {
      Log.d(TAG, "onInitContent: " + activity.getSnippet().getTitle());
    }
  } catch (IOException e) {
    e.printStackTrace();
  }*/
}
