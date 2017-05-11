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

package com.video.youtuberplayer.model;

import com.google.api.services.youtube.model.Activity;
import com.google.api.services.youtube.model.GuideCategory;
import com.google.api.services.youtube.model.Video;
import com.video.youtuberplayer.R;
import com.video.youtuberplayer.VideoPlayerApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Returns a list of YouTube videos.
 * <p>
 * <p>Do not run this directly, but rather use {@link }.</p>
 */
public abstract class GetYouTubeVideos {
  protected String nextPageToken;
  protected boolean noMoreVideoPages = false;

  /**
   * Initialise this object.
   *
   * @throws IOException
   */
  public abstract void init(long maxResults, final String token, final String tokenNextPage) throws IOException;

  public void initGuideCategories(String regionCode, String hl, String token) throws IOException{

  }

  /**
   * Sets user's query. [optional]
   */
  public void setQuery(String query) {
  }


  /**
   * Gets the next page of videos.
   *
   * @return List of {@link YouTubeVideo}s.
   */
  public abstract List<YouTubeVideo> getNextVideos();

  public abstract String tokenNextPage();



  /**
   * @return True if YouTube states that there will be no more video pages; false otherwise.
   */
  public abstract boolean noMoreVideoPages();


  /**
   * Converts {@link List} of {@link Video} to {@link List} of {@link YouTubeVideo}.
   *
   * @param videoList {@link List} of {@link Video}.
   * @return {@link List} of {@link YouTubeVideo}.
   */
  protected List<YouTubeVideo> toYouTubeVideoList(List<Video> videoList) {
    List<YouTubeVideo> youTubeVideoList = new ArrayList<>();

    if (videoList != null) {
      YouTubeVideo youTubeVideo;

      for (Video video : videoList) {
        youTubeVideo = new YouTubeVideo(video);
        if (!youTubeVideo.filterVideoByLanguage())
          youTubeVideoList.add(youTubeVideo);
      }
    }

    return youTubeVideoList;
  }




  protected String getPreferredRegion() {
    String region = VideoPlayerApplication.getPreferenceManager()
      .getString(VideoPlayerApplication.getStr(R.string.pref_key_preferred_region), "").trim();
    return (region.isEmpty() ? null : region);
  }

  /**
   * Reset the fetching of videos. This will be called when a swipe to refresh is done.
   */
  public void reset() {
    nextPageToken = null;
    noMoreVideoPages = false;
  }

  public List<GuideCategory> listGuideCategories() {
    return null;
  }
}
