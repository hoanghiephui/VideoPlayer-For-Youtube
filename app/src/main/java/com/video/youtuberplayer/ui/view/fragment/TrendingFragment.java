package com.video.youtuberplayer.ui.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.video.youtuberplayer.R;
import com.video.youtuberplayer.model.VideoCategory;
import com.video.youtuberplayer.model.YouTubeVideo;
import com.video.youtuberplayer.ui.contracts.GetFeaturedVideoContract;
import com.video.youtuberplayer.ui.interceptor.GetFeaturedVideoInterceptor;
import com.video.youtuberplayer.ui.presenter.GetFraturedViewPresenter;
import com.video.youtuberplayer.ui.view.adapters.VideoAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hoanghiep on 5/5/17.
 */

public class TrendingFragment extends BaseFragment implements GetFeaturedVideoContract.IGeFeaturedVideoView, VideoAdapter.VideoCallBack {
  private static final String TAG = TrendingFragment.class.getSimpleName();
  public static final String MAX_RESULT = "max_result";
  public static final String TOKEN = "token";

  @BindView(R.id.list_movies_recycler_view)
  RecyclerView tv;

  private GetFeaturedVideoContract.IGetFeaturedVideoPresenter presenter;
  private GetFeaturedVideoContract.IGetFeaturedVideoInterceptor interceptor;

  private String token;
  private long maxResult;
  private List<YouTubeVideo> mlistVideo = new ArrayList<>();

  public static TrendingFragment newInstance(long maxResult, String token) {

    Bundle args = new Bundle();
    args.putLong(MAX_RESULT, maxResult);
    args.putString(TOKEN, token);
    TrendingFragment fragment = new TrendingFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  protected int getViewLayout() {
    return R.layout.fragment_trending;
  }

  @Override
  protected void onInitContent(Bundle savedInstanceState) {
    interceptor = new GetFeaturedVideoInterceptor();
    presenter = new GetFraturedViewPresenter(interceptor, new CompositeDisposable());

    presenter.onBindView(this);
    if (getArguments() != null) {
      token = getArguments().getString(TOKEN);
      maxResult = getArguments().getLong(MAX_RESULT);
    }
    try {
      presenter.getFeaturedVideo(VideoCategory.FEATURED, maxResult, token, null);
    } catch (IOException e) {
      e.printStackTrace();
    }


    tv.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));

  }

  @Override
  protected View.OnClickListener onSnackbarClickListener() {
    return null;
  }

  @Override
  public void setProgressVisibility(int visibityState) {

  }


  public void onUpdateView(final List<YouTubeVideo> listVideo) {
    Log.d(TAG, "onUpdateView: " + listVideo.get(0).getTitle());
    mlistVideo = listVideo;
    tv.setAdapter(new VideoAdapter(mlistVideo,
            R.layout.item_video_home, this));
    tv.getAdapter().notifyDataSetChanged();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    presenter.onUnbindView();
  }

  @Override
  public void onVideoSelect(YouTubeVideo video) {

  }

  @Override
  public void onLoadMore() {

  }

  @Override
  public void setRecyclerViewVisibility(int visibilityState) {

  }

  @Override
  public void setListVideo(List<YouTubeVideo> listVideo, boolean hasMorePages) {

  }

  @Override
  public void setupRecyclerView() {

  }

  @Override
  public void onUpdateView() {

  }

  @Override
  public void addAllVideo(List<YouTubeVideo> nextVideos, boolean hasMorePage) {

  }

  @Override
  public void setTokenNextPage(String tokenNextPage) {

  }
}
