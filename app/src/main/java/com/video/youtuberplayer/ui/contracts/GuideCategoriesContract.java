package com.video.youtuberplayer.ui.contracts;

import com.google.api.services.youtube.model.GuideCategory;
import com.video.youtuberplayer.model.GetYouTubeVideos;
import com.video.youtuberplayer.model.VideoCategory;
import com.video.youtuberplayer.ui.presenter.IPresenter;
import com.video.youtuberplayer.ui.view.IView;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by hoanghiep on 5/6/17.
 */

public class GuideCategoriesContract {

  public interface IGuideCategoriesInterceptor {
    Observable<GetYouTubeVideos> getGuideCategories(VideoCategory videoCategory, final String regionCode, final String hl, final String token) throws IOException;
  }

  public interface IGuideCategoriesView extends IView {
    void updateView(List<GuideCategory> guideCategoryList);
  }

  public interface IGuideCategoriesPresenter extends IPresenter<IGuideCategoriesView> {
    void getGuideCategories(VideoCategory videoCategory, final String regionCode, final String hl, final String token) throws IOException;
  }
}
