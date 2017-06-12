package com.video.youtuberplayer.model;

import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;

import java.util.List;

/**
 * Created by hoanghiep on 6/12/17.
 */

public class VideoRealatedAndChanel {
    private List<Video> videoList;
    private List<SearchResult> resultList;
    private ChannelListResponse channelListResponse;

    public VideoRealatedAndChanel(List<SearchResult> resultList, ChannelListResponse channelListResponse) {
        this.resultList = resultList;
        this.channelListResponse = channelListResponse;
    }

    public VideoRealatedAndChanel(List<Video> videoList, List<SearchResult> resultList, ChannelListResponse channelListResponse) {
        this.videoList = videoList;
        this.resultList = resultList;
        this.channelListResponse = channelListResponse;
    }

    public List<Video> getVideoList() {
        return videoList;
    }

    public List<SearchResult> getResultList() {
        return resultList;
    }

    public ChannelListResponse getChannelListResponse() {
        return channelListResponse;
    }
}
