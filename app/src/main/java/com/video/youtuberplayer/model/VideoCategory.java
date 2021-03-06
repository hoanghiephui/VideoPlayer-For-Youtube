package com.video.youtuberplayer.model;

/**
 * Created by hoanghiep on 5/5/17.
 */

import com.video.youtuberplayer.api.GetFeaturedVideos;
import com.video.youtuberplayer.api.GetVideoDetail;
import com.video.youtuberplayer.api.base.BaseVideoDetail;

import java.io.IOException;

/**
 * Represents a video category/group.
 */
public enum VideoCategory {
  /** Featured videos */
  FEATURED (0),
  /** Most popular videos */
  MOST_POPULAR (1),
  /** Videos related to a search query */
  SEARCH_QUERY (2),
  /** Videos that are owned by a channel */
  CHANNEL_VIDEOS (3),
  /** Videos pertaining to the user's subscriptions feed */
  SUBSCRIPTIONS_FEED_VIDEOS (4),
  /** Videos bookmarked by the user */
  BOOKMARKS_VIDEOS (5),
  GUIDECATEGORIES(6),
  VIDEODETAIL(7);

  // *****************
  // DON'T FORGET to update getVideoCategory() and createGetYouTubeVideos() methods...
  // *****************

  private final int id;
  private static final String TAG = VideoCategory.class.getSimpleName();



  VideoCategory(int id) {
    this.id = id;
  }


  /**
   * Convert the given id integer number to {@link VideoCategory}.
   *
   * @param id ID number representing the position of the item in video_categories array (see
   *           the respective strings XML file).
   *
   * @return A new instance of {@link VideoCategory}.
   */
  public static VideoCategory getVideoCategory(int id) {
    return VideoCategory.values()[id];
  }


  /**
   * Creates a new instance of {@link GetFeaturedVideos} or {@link //GetMostPopularVideos} depending
   * on the video category.
   *
   * @return New instance of {@link GetYouTubeVideos}.
   */
  public GetYouTubeVideos createGetYouTubeVideos(final long maxResults, final String token,
                                                 final String tokenNextPage) throws IOException {
    switch (getVideoCategory(id)) {
      case FEATURED:
        return new GetFeaturedVideos(maxResults, token, tokenNextPage);
    }

    // this will notify the developer is he forgot to amend this method when a new type is added
    throw new UnsupportedOperationException();
  }

  public BaseVideoDetail getVideoDetail(final String token, final String id) throws IOException {
    return new GetVideoDetail(token, id);
  }
}
