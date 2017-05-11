package com.video.youtuberplayer.api.base;

import com.google.api.services.youtube.model.Video;
import com.video.youtuberplayer.model.GetYouTubeVideos;

import java.util.List;

/**
 * Created by hoanghiep on 5/10/17.
 */

public abstract class BaseVideoDetail extends GetYouTubeVideos {

  public abstract List<Video> getVideoDetail();
}
