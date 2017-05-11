package com.video.youtuberplayer.api;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.video.youtuberplayer.remote.YouTubeAPI;
import com.video.youtuberplayer.remote.YouTubeAPIKey;

import java.io.IOException;
import java.util.List;

/**
 * Created by hoanghiep on 5/10/17.
 */

public class GetRelatedVideos {
  private List<SearchResult> videoList;

  public GetRelatedVideos(String id, String token, String tokenPage) throws IOException {
    YouTube.Search.List list;
    list = YouTubeAPI.create().search().list("snippet")
            .setRelatedToVideoId(id)
            .setType("video")
            .setKey(YouTubeAPIKey.get().getYouTubeAPIKey())
            .setMaxResults(25L)
            .setPageToken(tokenPage);
    videoList = list.execute().getItems();
  }
}
