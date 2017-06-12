package com.video.youtuberplayer.remote.methods;

import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.GuideCategory;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.video.youtuberplayer.api.GetChannel;
import com.video.youtuberplayer.api.GetFeaturedVideos;
import com.video.youtuberplayer.api.GetGuideCategories;
import com.video.youtuberplayer.api.GetRelatedVideos;
import com.video.youtuberplayer.api.GetVideoDetail;
import com.video.youtuberplayer.model.VideoRealatedAndChanel;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function3;

/**
 * Created by hoanghiep on 5/5/17.
 */

public class VideoMethod {
    public Observable<VideoListResponse> getFeaturedVideo(final long maxResults, final String token,
                                                          final String tokenNextPage) throws IOException {
        return Observable.create(new ObservableOnSubscribe<VideoListResponse>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<VideoListResponse> e) throws Exception {
                e.onNext(new GetFeaturedVideos(maxResults, token, tokenNextPage).getResponse());
                e.onComplete();
            }
        });
    }

    public Observable<List<GuideCategory>> getGuideCategories(final String regionCode,
                                                        final String hl, final String token) throws IOException {
        return Observable.create(new ObservableOnSubscribe<List<GuideCategory>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<GuideCategory>> e) throws Exception {
                e.onNext(new GetGuideCategories(regionCode, hl, token).listGuideCategories());
                e.onComplete();
            }
        });
    }

    public Observable<List<Video>> getVideoDetail(final String token, final String id) throws IOException {
        return Observable.create(new ObservableOnSubscribe<List<Video>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<Video>> e) throws Exception {
                e.onNext(new GetVideoDetail(token, id).getVideoDetail());
                e.onComplete();
            }
        });
    }

    public Observable<List<SearchResult>> getRelatedVideos(final String id, final String token, final String tokenPage) throws IOException {
        return Observable.create(new ObservableOnSubscribe<List<SearchResult>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<SearchResult>> e) throws Exception {
                e.onNext(new GetRelatedVideos(id, token, tokenPage).getVideoList());
                e.onComplete();
            }
        });
    }

    public Observable<ChannelListResponse> getChannelYoutube(final String id) throws IOException {
        return Observable.create(new ObservableOnSubscribe<ChannelListResponse>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ChannelListResponse> e) throws Exception {
                e.onNext(new GetChannel(id).getResponse());
                e.onComplete();
            }
        });
    }

    public Observable<VideoRealatedAndChanel> getRealatedAndChanel(final String idVideo, String idChannel,
                                                                   final String token, final String tokenPage)
            throws IOException {
        return Observable.zip(getVideoDetail(null, idVideo), getRelatedVideos(idVideo, token, tokenPage),
                getChannelYoutube(idChannel), new Function3<List<Video>, List<SearchResult>, ChannelListResponse,
                        VideoRealatedAndChanel>() {
                    @Override
                    public VideoRealatedAndChanel apply(List<Video> videos, List<SearchResult> searchResults,
                                                        ChannelListResponse channelListResponse) throws Exception {
                        return new VideoRealatedAndChanel(videos, searchResults, channelListResponse);
                    }
                });
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
