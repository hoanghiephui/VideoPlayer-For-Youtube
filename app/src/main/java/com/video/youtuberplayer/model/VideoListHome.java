package com.video.youtuberplayer.model;

import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.VideoListResponse;

import java.util.List;

/**
 * Created by hoanghiep on 6/13/17.
 */

public class VideoListHome {
    private VideoListResponse videoListResponse;
    private SearchListResponse listResponse;

    public VideoListHome(VideoListResponse videoListResponse, SearchListResponse listResponse) {
        this.videoListResponse = videoListResponse;
        this.listResponse = listResponse;
    }

    public VideoListResponse getVideoListResponse() {
        return videoListResponse;
    }

    public SearchListResponse getListResponse() {
        return listResponse;
    }
}
