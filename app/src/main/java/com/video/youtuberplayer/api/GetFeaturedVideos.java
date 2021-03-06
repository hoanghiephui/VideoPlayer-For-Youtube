/*
 * SkyTube
 * Copyright (C) 2015  Ramon Mifsud
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation (version 3 of the License).
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.video.youtuberplayer.api;

import android.util.Log;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.video.youtuberplayer.model.GetYouTubeVideos;
import com.video.youtuberplayer.model.YouTubeVideo;
import com.video.youtuberplayer.remote.YouTubeAPI;
import com.video.youtuberplayer.remote.YouTubeAPIKey;

import java.io.IOException;
import java.util.List;

import static com.video.youtuberplayer.utils.LocaleUtils.getLocaleCountryISO;

/**
 * Get today's featured YouTube videos.
 */
public class GetFeaturedVideos extends GetYouTubeVideos {

  private static final String TAG = GetFeaturedVideos.class.getSimpleName();
  protected YouTube.Videos.List videosList = null;
  private VideoListResponse response;

  public GetFeaturedVideos(final long maxResults, final String token, String tokenNextPage) throws IOException{
    videosList = YouTubeAPI.create().videos().list("snippet, statistics, contentDetails, liveStreamingDetails");
    videosList.setKey(YouTubeAPIKey.get().getYouTubeAPIKey());
    videosList.setChart("mostPopular");
    videosList.setRegionCode(getLocaleCountryISO());
    videosList.setMaxResults(maxResults);
    if (token != null) {
      videosList.setOauthToken(token);
    }
    videosList.setPageToken(tokenNextPage);
    response = videosList.execute();
    nextPageToken = response.getNextPageToken();
  }

  public VideoListResponse getResponse() {
    return response;
  }

  @Override
  public void init(final long maxResults, final String token, String tokenNextPage) throws IOException {
    videosList = YouTubeAPI.create().videos().list("snippet, statistics, contentDetails");
    videosList.setFields("items(id, snippet/defaultAudioLanguage, snippet/defaultLanguage, snippet/publishedAt, " +
            "snippet/title, snippet/channelId, snippet/channelTitle," +
            "snippet/thumbnails/high, contentDetails/duration, statistics)," +
            "nextPageToken");
    videosList.setKey(YouTubeAPIKey.get().getYouTubeAPIKey());
    videosList.setChart("mostPopular");
    videosList.setRegionCode(getPreferredRegion());
    videosList.setMaxResults(maxResults);
    if (token != null) {
      videosList.setOauthToken(token);
    }
    response = videosList.execute();
    nextPageToken = tokenNextPage;
  }

  @Override
  public List<YouTubeVideo> getNextVideos() {
    List<Video> searchResultList = null;

    try {
      // set the page token/id to retrieve
      videosList.setPageToken(nextPageToken);

      // communicate with YouTube
      VideoListResponse response = videosList.execute();

      // get videos
      searchResultList = response.getItems();

      // set the next page token
      nextPageToken = response.getNextPageToken();

      // if nextPageToken is null, it means that there are no more videos
      if (nextPageToken == null)
        noMoreVideoPages = true;
    } catch (IOException e) {
      Log.e(TAG, "Error has occurred while getting Featured Videos.", e);
    }

    return toYouTubeVideoList(searchResultList);
  }

  @Override
  public String tokenNextPage() {
    return nextPageToken;
  }


  @Override
  public boolean noMoreVideoPages() {
    return noMoreVideoPages;
  }


  /**
   * @return The maximum number of items that should be retrieved per YouTube query.
   */
  protected Long getMaxResults() {
    return 50L;
  }

}
