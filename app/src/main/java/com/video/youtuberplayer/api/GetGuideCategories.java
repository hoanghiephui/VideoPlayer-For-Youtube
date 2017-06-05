package com.video.youtuberplayer.api;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.GuideCategory;
import com.google.api.services.youtube.model.GuideCategoryListResponse;
import com.video.youtuberplayer.model.GetYouTubeVideos;
import com.video.youtuberplayer.model.YouTubeVideo;
import com.video.youtuberplayer.remote.YouTubeAPI;
import com.video.youtuberplayer.remote.YouTubeAPIKey;

import java.io.IOException;
import java.util.List;

/**
 * Created by hoanghiep on 5/6/17.
 */

public class GetGuideCategories extends GetYouTubeVideos {
  protected YouTube.GuideCategories.List list;

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
  public void init(long maxResults, String token, String tokenNextPage) throws IOException {

  }

  @Override
  public void initGuideCategories(String regionCode, String hl, String token) throws IOException{
    list = YouTubeAPI.create().guideCategories().list("snippet")
            .setHl(hl)
            .setRegionCode(regionCode)
            .setKey(YouTubeAPIKey.get().getYouTubeAPIKey());
  }

  public GetGuideCategories(String regionCode, String hl, String token) throws IOException {
    list = YouTubeAPI.create().guideCategories().list("snippet")
            .setHl(hl)
            .setRegionCode(regionCode)
            .setKey(YouTubeAPIKey.get().getYouTubeAPIKey());
  }

  @Override
  public List<GuideCategory> listGuideCategories() {
    try {
      if (list.executeUsingHead().getStatusCode() == 401){
        return null;
      } else {
        GuideCategoryListResponse listResponse = list.execute();
        return listResponse.getItems();
      }
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}
