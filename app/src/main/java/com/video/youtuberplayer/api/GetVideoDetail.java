package com.video.youtuberplayer.api;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.video.youtuberplayer.api.base.BaseVideoDetail;
import com.video.youtuberplayer.model.GetYouTubeVideos;
import com.video.youtuberplayer.model.YouTubeVideo;
import com.video.youtuberplayer.remote.YouTubeAPI;
import com.video.youtuberplayer.remote.YouTubeAPIKey;

import java.io.IOException;
import java.util.List;

/**
 * Created by hoanghiep on 5/10/17.
 */

public class GetVideoDetail extends BaseVideoDetail {
  protected YouTube.Videos.List videosList = null;
  private List<Video> listVideo;

  public GetVideoDetail(String token, String id) throws IOException {
    videosList = YouTubeAPI.create().videos().list("snippet, statistics, contentDetails");
    videosList.setFields("items(id, snippet/defaultAudioLanguage, snippet/defaultLanguage, snippet/publishedAt, " +
            "snippet/title, snippet/channelId, snippet/channelTitle," +
            "snippet/thumbnails/high, contentDetails/duration, statistics)," +
            "nextPageToken");
    videosList.setKey(YouTubeAPIKey.get().getYouTubeAPIKey());
    videosList.setId(id);
    listVideo = videosList.execute().getItems();
    if (token != null) {
      videosList.setOauthToken(token);
    }
  }

  @Override
  public void init(long maxResults, String token, String tokenNextPage) throws IOException {

  }

  @Override
  public List<YouTubeVideo> getNextVideos() {
    return null;
  }

  @Override
  public String tokenNextPage() {
    return null;
  }

  @Override
  public boolean noMoreVideoPages() {
    return false;
  }

  @Override
  public List<Video> getVideoDetail() {
    return listVideo;
  }
}
