package com.video.youtuberplayer.api;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.video.youtuberplayer.remote.YouTubeAPI;
import com.video.youtuberplayer.remote.YouTubeAPIKey;

import java.io.IOException;

/**
 * Created by hoanghiep on 5/12/17.
 */

public class GetChannel {
  private ChannelListResponse response;

  public GetChannel(String channelId) throws IOException {
    YouTube youtube = YouTubeAPI.create();
    YouTube.Channels.List channelInfo = youtube.channels().list("snippet, statistics, brandingSettings");
    channelInfo.setKey(YouTubeAPIKey.get().getYouTubeAPIKey());
    channelInfo.setId(channelId);
    response = channelInfo.execute();
  }

  public ChannelListResponse getResponse() {
    return response;
  }
}
