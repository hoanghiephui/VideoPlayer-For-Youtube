package com.video.youtuberplayer.ui.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.google.api.services.youtube.model.GuideCategory;
import com.google.gson.Gson;
import com.video.youtuberplayer.R;
import com.video.youtuberplayer.enums.ListType;
import com.video.youtuberplayer.model.Account;
import com.video.youtuberplayer.model.ListActivityDTO;
import com.video.youtuberplayer.ui.contracts.GuideCategoriesContract;
import com.video.youtuberplayer.ui.interceptor.GetGuideCategoriesInterceptor;
import com.video.youtuberplayer.ui.presenter.GetGuideCategoriesPresenter;
import com.video.youtuberplayer.ui.view.activity.ListDefaultActivity;
import com.video.youtuberplayer.ui.view.adapters.GuideCategoriesAdapter;
import com.video.youtuberplayer.utils.PrefsUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

import static com.video.youtuberplayer.ui.view.fragment.ListVideoDefaultFragment.GET_POPULAR;
import static com.video.youtuberplayer.ui.view.fragment.ListVideoDefaultFragment.GET_TRENDING;
import static com.video.youtuberplayer.utils.LocaleUtils.getLocaleCountryISO;
import static com.video.youtuberplayer.utils.LocaleUtils.getLocaleLanguageAndCountry;

/**
 * Created by hoanghiep on 5/5/17.
 */

public class MainFragment extends BaseFragment implements GuideCategoriesContract.IGuideCategoriesView,
        GuideCategoriesAdapter.GuideCategoriesCallBack {
  private static final String TAG = MainFragment.class.getSimpleName();

  @BindView(R.id.more_recently)
  View moreRecently;
  @BindView(R.id.highlights_keywords_recycler_view)
  RecyclerView mTagRecyclerView;

  private GuideCategoriesContract.IGuideCategoriesPresenter presenter;

  @Override
  protected int getViewLayout() {
    return R.layout.fragment_playlist;
  }

  @Override
  protected void onInitContent(Bundle savedInstanceState) {
    startFragment(R.id.container_trending,
            ListVideoDefaultFragment.newInstance(10L, null, R.layout.item_video_home,
            R.layout.fragment_trending,
            ListVideoDefaultFragment.createLinearListArguments(RecyclerView.HORIZONTAL, false)
                    ,GET_POPULAR));

    startFragment(R.id.container_recently,
            ListVideoDefaultFragment.newInstance(10L, null, R.layout.item_video_home,
                    R.layout.fragment_trending,
                    ListVideoDefaultFragment.createLinearListArguments(RecyclerView.HORIZONTAL, false)
                    ,GET_POPULAR));

    GuideCategoriesContract.IGuideCategoriesInterceptor interceptor = new GetGuideCategoriesInterceptor();
    presenter = new GetGuideCategoriesPresenter(interceptor, new CompositeDisposable());
    presenter.onBindView(this);
    Account account = new Gson().fromJson(PrefsUtils.getAccount(getContext()), Account.class);
    //get tag
    try {
      presenter.getGuideCategories(getLocaleCountryISO(), getLocaleLanguageAndCountry(Locale.getDefault()), account.getToken());
    } catch (IOException e) {
      e.printStackTrace();
    }
    mTagRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayout.HORIZONTAL, false));
  }

  @Override
  protected View.OnClickListener onSnackbarClickListener() {
    return null;
  }

  @OnClick(R.id.more_recently)
  public void onMoreRecently() {
    startActivity(ListDefaultActivity.newIntent(getActivity(), new ListActivityDTO(0, getString(R.string.recent_upload),
            getString(R.string.recent_upload_subtitle), R.layout.item_list_video, ListType.VIDEO)));

  }

  private void initGuideCategores(List<GuideCategory> guideCategoryList) {
    mTagRecyclerView.setAdapter(new GuideCategoriesAdapter(guideCategoryList,
            this, R.layout.item_guide_categories_home));
  }

  @Override
  public void setProgressVisibility(int visibityState) {

  }

  @Override
  public void updateView(List<GuideCategory> guideCategoryList) {
    initGuideCategores(guideCategoryList);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    presenter.onUnbindView();
  }

  @Override
  public void onItemSelected(GuideCategory item) {

  }
}
