package com.video.youtuberplayer.ui.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.devspark.robototextview.widget.RobotoTextView;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;
import com.google.api.services.youtube.model.Video;
import com.video.youtuberplayer.R;
import com.video.youtuberplayer.model.VideoCategory;
import com.video.youtuberplayer.model.VideoDuration;
import com.video.youtuberplayer.model.YouTubeVideo;
import com.video.youtuberplayer.ui.contracts.GetVideoDetailContract;
import com.video.youtuberplayer.ui.interceptor.GetVideoDetailInterceptor;
import com.video.youtuberplayer.ui.presenter.GetVideoDetailPresenter;
import com.video.youtuberplayer.ui.view.adapters.VideoPlayerMoreAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;

import static com.video.youtuberplayer.utils.ViewUtils.onShowImage;

/**
 * Created by hoanghiep on 5/7/17.
 */

public class PlayerVideoActivity extends BaseActivity implements GetVideoDetailContract.IGetVideoDetailView {
  private static final String TAG = PlayerVideoActivity.class.getSimpleName();
  public static final String VIDEO = "video";

  @BindView(R.id.playbackCurrentTime)
  RobotoTextView playbackCurrentTime;
  @BindView(R.id.playbackEndTime)
  RobotoTextView playbackEndTime;
  @BindView(R.id.detail_thumbnail_image_view)
  AppCompatImageView thumbnailVideo;
  @BindView(R.id.recyclerView)
  RecyclerView recyclerView;

  private VideoPlayerMoreAdapter adapter;
  private List<Video> videoList = new ArrayList<>();
  private YouTubeVideo video;

  private GetVideoDetailContract.IGetVideoDetailPresenter presenter;
  private GetVideoDetailContract.IGetVideoDetailInterceptor interceptor;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    if (getIntent() != null) {
      video = (YouTubeVideo) getIntent().getExtras().getSerializable(VIDEO);
    }
    super.onCreate(savedInstanceState);
  }

  @Override
  protected int getActivityBaseViewID() {
    return R.layout.activity_player_video;
  }

  @Override
  protected void onContent() {
    setTransToolbar();
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
      WindowManager.LayoutParams.FLAG_FULLSCREEN);
    presenter.onBindView(this);

    try {
      presenter.getVideoDetail(VideoCategory.VIDEODETAIL, null, video.getId());
    } catch (IOException e) {
      e.printStackTrace();
    }
    adapter = new VideoPlayerMoreAdapter(videoList);
    recyclerView.setNestedScrollingEnabled(false);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(adapter);
  }

  @Override
  protected View.OnClickListener onSnackbarClickListener() {
    return null;
  }

  @Override
  protected int getMenuLayoutID() {
    return 0;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_more:
        showBottomMenu();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  protected void initPresenter() {
    interceptor = new GetVideoDetailInterceptor();
    presenter = new GetVideoDetailPresenter(interceptor, new CompositeDisposable());
  }

  private void showBottomMenu() {
    final BottomSheetMenuDialog dialog = new BottomSheetBuilder(this)
      .setMode(BottomSheetBuilder.MODE_LIST)
      .setMenu(R.menu.menu_bottom_player)
      .setIconTintColor(ContextCompat.getColor(this, R.color.video_cell_text_details))
      .setTitleTextColor(ContextCompat.getColor(this, R.color.video_cell_text_details))
      .setItemClickListener(new BottomSheetItemClickListener() {
        @Override
        public void onBottomSheetItemClick(MenuItem item) {
          switch (item.getItemId()) {
            case R.id.menu_quality:
              break;
            case R.id.menu_backgroud:
              break;
            default:
              break;
          }
        }
      })
      .createDialog();

    dialog.show();
  }

  @Override
  public void setProgressVisibility(int visibityState) {

  }

  @Override
  public boolean isAdded() {
    return false;
  }

  @Override
  public void onUpdateView(List<Video> videoList) {
    for (Video video : videoList) {
      playbackEndTime.setText(VideoDuration.toHumanReadableString(video.getContentDetails().getDuration()));
      onShowImage(thumbnailVideo, video.getSnippet().getThumbnails().getHigh().getUrl());

    }
    this.videoList.addAll(videoList);
    adapter.setList(this.videoList);
    adapter.notifyDataSetChanged();
    Log.d(TAG, "onUpdateView: " + videoList.get(0).getSnippet().getTitle());
  }

  @Override
  protected void onDestroy() {
    if (presenter != null) {
      presenter.onUnbindView();
    }
    super.onDestroy();
  }
}
