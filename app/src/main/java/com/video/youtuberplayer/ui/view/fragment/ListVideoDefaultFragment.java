package com.video.youtuberplayer.ui.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.VideoListResponse;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.video.youtuberplayer.R;
import com.video.youtuberplayer.api.GetVideosDetailsByIDs;
import com.video.youtuberplayer.model.VideoListHome;
import com.video.youtuberplayer.model.YouTubeVideo;
import com.video.youtuberplayer.ui.contracts.GetFeaturedVideoContract;
import com.video.youtuberplayer.ui.interceptor.GetFeaturedVideoInterceptor;
import com.video.youtuberplayer.ui.presenter.GetTrendingAndPopularPresenter;
import com.video.youtuberplayer.ui.view.activity.BaseActivity;
import com.video.youtuberplayer.ui.view.activity.PlayerVideoActivity;
import com.video.youtuberplayer.ui.view.adapters.VideoAdapter;
import com.video.youtuberplayer.ui.view.widget.EndlessRecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;

import static com.video.youtuberplayer.ui.view.activity.PlayerVideoActivity.VIDEO;

/**
 * Created by hoanghiep on 5/7/17.
 */

public class ListVideoDefaultFragment extends BaseFragment implements GetFeaturedVideoContract.IGeFeaturedVideoView, VideoAdapter.VideoCallBack {
  private static final String ARG_ID = "id";
  private static final String ARG_SORT = "sort";
  private static final String ARG_TYPE_LAYOUT = "ListVideoDefaultFragment.ARG_TYPE_LAYOUT";
  private static final String ARG_NUM_COLUME = "ListVideoDefaultFragment.ARG_NUM_COLUME";
  private static final String ARG_ORIENTATIONS = "ListVideoDefaultFragment.ARG_ORIENTATIONS";
  private static final String ARG_REVERSE_LAYOUT = "ListVideoDefaultFragment.ARG_REVERSE_LAYOUT";
  private static final String ARG_SPAN_COUNT = "ListVideoDefaultFragment.ARG_SPAN_COUNT";
  private static final String ARG_LAYOUT_ID = "ListVideoDefaultFragment.ARG_LAYOUT_ID";
  private static final String ARG_FRAGMENT_LAYOUT_ID = "ListVideoDefaultFragment.ARG_FRAGMENT_LAYOUT_ID";
  private static final String ARG_TOKEN = "ListVideoDefaultFragment.ARG_TOKEN";
  private static final String ARG_MAX_RESULT = "ListVideoDefaultFragment.ARG_MAX_RESULT";
  private static final String ARG_STATUS = "status";
  public static final int GET_TRENDING = 0;
  public static final int GET_POPULAR = 1;

  @StateRule
  private int state;
  @IntDef({GET_TRENDING, GET_POPULAR})
  private @interface StateRule {
  }

  @BindView(R.id.list_movies_recycler_view)
  RecyclerView mMoviesRecyclerView;
  @BindView(R.id.list_movies_principal_progress)
  ProgressWheel mPrincipalProgress;
  @Nullable
  @BindView(R.id.swipeRefreshLayout)
  SwipeRefreshLayout mSwipeRefreshLayout;
  @BindView(R.id.movie_null)
  TextView mMovieNull;
  @Nullable
  @BindView(R.id.logo)
  AppCompatImageView logo;

  private RecyclerView.LayoutManager mLayoutManager;
  private boolean hasMorePages;
  private String tokenNextPage;

  private int mID;
  private int mLayoutID;
  private int mOrientation;
  private int mTypeListLayout;
  private int mColunas;
  private int mSpanCount;
  private boolean mReverseLayout;
  private int mFragmentLayoutId;
  private GridLayoutManager gridLayoutManager;
  private List<YouTubeVideo> mlistVideo = new ArrayList<>();
  private VideoAdapter videoAdapter;
  private GetFeaturedVideoContract.IGetFeaturedVideoPresenter presenter;
  private GetFeaturedVideoContract.IGetFeaturedVideoInterceptor interceptor;

  private String token;
  private long maxResult;

  public static Bundle createLinearListArguments(int orientation, boolean reverseLayout) {
    Bundle bundle = new Bundle();
    bundle.putInt(ARG_TYPE_LAYOUT, BaseActivity.LINEAR_LAYOUT);
    bundle.putInt(ARG_ORIENTATIONS, orientation);
    bundle.putBoolean(ARG_REVERSE_LAYOUT, reverseLayout);
    return bundle;
  }

  public static Bundle createGridListArguments(int colume) {
    Bundle bundle = new Bundle();
    bundle.putInt(ARG_TYPE_LAYOUT, BaseActivity.GRID_LAYOUT);
    bundle.putInt(ARG_NUM_COLUME, colume);
    return bundle;
  }

  public static Bundle createStaggeredListArguments(int spanCount, int orientation) {
    Bundle bundle = new Bundle();
    bundle.putInt(ARG_TYPE_LAYOUT, BaseActivity.STAGGERED);
    bundle.putInt(ARG_ORIENTATIONS, orientation);
    bundle.putInt(ARG_SPAN_COUNT, spanCount);
    return bundle;
  }

  public static ListVideoDefaultFragment newInstance(long maxResult, String token, int layoutID,
                                                     int fragmentLayoutID, Bundle arguments, int state) {
    arguments.putInt(ARG_LAYOUT_ID, layoutID);
    arguments.putInt(ARG_FRAGMENT_LAYOUT_ID, fragmentLayoutID);
    arguments.putLong(ARG_MAX_RESULT, maxResult);
    arguments.putString(ARG_TOKEN, token);
    arguments.putInt(ARG_STATUS, state);
    ListVideoDefaultFragment fragment = new ListVideoDefaultFragment();
    fragment.setArguments(arguments);
    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mOrientation = getArguments().getInt(ARG_ORIENTATIONS, LinearLayout.HORIZONTAL);
    mFragmentLayoutId = getArguments().getInt(ARG_FRAGMENT_LAYOUT_ID, R.layout.fragment_list_video_default);
    mID = getArguments().getInt(ARG_ID);
    mColunas = getArguments().getInt(ARG_NUM_COLUME, 2);
    mTypeListLayout = getArguments().getInt(ARG_TYPE_LAYOUT);
    mReverseLayout = getArguments().getBoolean(ARG_REVERSE_LAYOUT);
    mSpanCount = getArguments().getInt(ARG_SPAN_COUNT);
    mLayoutID = getArguments().getInt(ARG_LAYOUT_ID, R.layout.item_video_home);
    maxResult = getArguments().getLong(ARG_MAX_RESULT);
    token = getArguments().getString(ARG_TOKEN);
    state = getArguments().getInt(ARG_STATUS);
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
  }

  @Override
  public void onDetach() {
    super.onDetach();
  }

  @Override
  protected int getViewLayout() {
    return mFragmentLayoutId;
  }

  @Override
  protected void onInitContent(Bundle savedInstanceState) {
    interceptor = new GetFeaturedVideoInterceptor();
    presenter = new GetTrendingAndPopularPresenter(interceptor, new CompositeDisposable());
    presenter.onBindView(this);
    setupLayoutManager();
    mMoviesRecyclerView.addOnScrollListener(createOnScrollListener());
    mMoviesRecyclerView.setNestedScrollingEnabled(false);
    setupAdapter();
    if (mSwipeRefreshLayout != null) {
      mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
          mlistVideo.clear();
          loadVideo(null);
        }
      });
    }
    loadVideo(null);
  }

  private void loadVideo(String tokenNextPage) {
    if (mSwipeRefreshLayout != null) {
      mSwipeRefreshLayout.setRefreshing(false);
    }
    try {
      switch (state) {
        case GET_TRENDING:
          presenter.getListVideoHome(maxResult, token, tokenNextPage);
          break;
        case GET_POPULAR:
          presenter.getFeaturedVideo(maxResult, token, tokenNextPage);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected View.OnClickListener onSnackbarClickListener() {
    return null;
  }

  @Override
  public void setProgressVisibility(int visibityState) {
    mPrincipalProgress.setVisibility(visibityState);
  }

  @Override
  public void setRecyclerViewVisibility(int visibilityState) {

  }



  @Override
  public void setupRecyclerView() {
    if (!mlistVideo.isEmpty()){
      onUpdateView();
    }
  }

  @Override
  public void onUpdateView() {
    if (!mlistVideo.isEmpty()) {
      if (videoAdapter != null) {
        videoAdapter.setList(mlistVideo);
        videoAdapter.notifyDataSetChanged();
      } else
        setupRecyclerView();
    }

    mMoviesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
      }

      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
      }
    });
  }

  @Override
  public void addAllVideo(List<YouTubeVideo> nextVideos) {
    mlistVideo.addAll(nextVideos);
  }

  @Override
  public void setTokenNextPage(String tokenNextPage) {
    this.tokenNextPage = tokenNextPage;
  }

  @Override
  public void onUpdateViewHome(VideoListHome videoListHome) {
    VideoListResponse videos = videoListHome.getVideoListResponse();
    SearchListResponse listResponse = videoListHome.getListResponse();
    if (videos != null) {
      addAllVideo(presenter.toYouTubeVideoList(videos.getItems()));
      onUpdateView();
      if (videos.getNextPageToken()!= null) {
        setTokenNextPage(videos.getNextPageToken());
      }
      setRecyclerViewVisibility(View.VISIBLE);
      setProgressVisibility(View.GONE);

    } else if (listResponse != null){
      /*try {
        presenter.getFeaturedVideo(maxResult, token, tokenNextPage);
      } catch (IOException e) {
        e.printStackTrace();
      }
      addAllVideo(presenter.toYouTubeVideoList(listResponse.getItems()));
      onUpdateView();
      if (listResponse.getNextPageToken()!= null) {
        setTokenNextPage(listResponse.getNextPageToken());
      }
      setRecyclerViewVisibility(View.VISIBLE);
      setProgressVisibility(View.GONE);*/

    } else {
      onErrorInServer();
    }
  }

  private void setupAdapter() {
    if (videoAdapter == null) {
      videoAdapter = new VideoAdapter(mlistVideo,
              mLayoutID, this);
      mMoviesRecyclerView.setAdapter(videoAdapter);
    } else
      onUpdateView();
  }

  private void setupLayoutManager() {
    switch (mTypeListLayout) {
      case BaseActivity.LINEAR_LAYOUT:
        mLayoutManager = new LinearLayoutManager(getActivity(), mOrientation, mReverseLayout);
        break;
      case BaseActivity.STAGGERED:
        mLayoutManager = new StaggeredGridLayoutManager(mSpanCount, mOrientation);
        break;
      default:
        break;
    }
    /*if (mTypeListLayout == ListsDefaultActivity.GRID_LAYOUT && BuildConfig.ENABLE_ADS && !adapterWrapperAds.isLoadAdsError()) {
      gridLayoutManager = new GridLayoutManager(getActivity(), mColunas);
      gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
          //set span 2 for ad block, otherwise 1
          if (adapterWrapperAds.getItemViewType(position) == adapterWrapperAds.getViewTypeAdExpress())
            return 2;
          else return 1;
        }
      });
      mMoviesRecyclerView.setLayoutManager(gridLayoutManager);
    } else {*/
      if (mTypeListLayout == BaseActivity.GRID_LAYOUT) {
        mLayoutManager = new GridLayoutManager(getActivity(), mColunas);
      }
      mMoviesRecyclerView.setLayoutManager(mLayoutManager);
    //}
  }

  private RecyclerView.OnScrollListener createOnScrollListener() {
    return new EndlessRecyclerView(mLayoutManager != null ? mLayoutManager : gridLayoutManager) {

      @Override
      public void onLoadMore(int current_page) {
        if (hasMorePages) {
          loadVideo(tokenNextPage);
        }

      }
    };
  }

  @Override
  public void onVideoSelect(YouTubeVideo video) {
    Intent intent = new Intent(getActivity(), PlayerVideoActivity.class);
    Bundle bundle = new Bundle();
    bundle.putSerializable(VIDEO, video);
    intent.putExtras(bundle);
    startActivity(intent);
  }

  @Override
  public void onLoadMore() {
    loadVideo(tokenNextPage);
  }

  @Override
  public void onDestroyView() {
    if (presenter != null) {
      presenter.onUnbindView();
    }
    super.onDestroyView();
  }

  private List<YouTubeVideo> getVideosList(List<SearchResult> searchResultList) throws IOException {
    StringBuilder videoIds = new StringBuilder();

    // append the video IDs into a strings (CSV)
    for (SearchResult res : searchResultList) {
      videoIds.append(res.getId().getVideoId());
      videoIds.append(',');
    }

    // get video details by supplying the videos IDs
    GetVideosDetailsByIDs getVideo = new GetVideosDetailsByIDs(maxResult, token, tokenNextPage, videoIds.toString());

    return getVideo.getNextVideos();
  }
}
