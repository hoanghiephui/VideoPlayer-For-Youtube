package com.video.youtuberplayer.ui.view.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.video.youtuberplayer.R;
import com.video.youtuberplayer.model.ListActivityDTO;
import com.video.youtuberplayer.ui.view.fragment.ListVideoDefaultFragment;

/**
 * Created by hoanghiep on 5/7/17.
 */

public class ListDefaultActivity extends BaseActivity {
  private static final String ARG_FTO = "com.media.movie.dto";
  private static final String ARG_DISCOVER_DTO = "com.media.movie.discover_dto";
  private static final String ARG_PERSON_LIST = "com.media.movie.person_list";
  private static final String ARG_MOVIES_LIST = "com.media.movie.movies_list";

  private ListActivityDTO mListActivityDTO;

  public static Intent newIntent(Context context, ListActivityDTO listDTO) {
    Intent intent = new Intent(context, ListDefaultActivity.class);
    intent.putExtra(ARG_FTO, listDTO);
    return intent;
  }

  @Override
  protected void initPresenter() {

  }

  @Override
  protected int getActivityBaseViewID() {
    return R.layout.activity_main;
  }

  @Override
  protected void onContent() {
    mListActivityDTO = getIntent().getParcelableExtra(ARG_FTO);
    setActivityTitle(mListActivityDTO.getNameActivity());
    setActivitySubtitle(mListActivityDTO.getSubtitleActivity());
    onUpdateUI();
  }

  @Override
  protected View.OnClickListener onSnackbarClickListener() {
    return null;
  }

  @Override
  protected int getMenuLayoutID() {
    return 0;
  }

  private void onUpdateUI() {
    Fragment fragment = null;
    switch (mListActivityDTO.getListType()) {
      case VIDEO:
        fragment = ListVideoDefaultFragment.newInstance(25L, null, R.layout.item_list_video, R.layout.fragment_list_video_default,
                ListVideoDefaultFragment.createLinearListArguments(RecyclerView.VERTICAL, false));
        break;
    }
    startFragment(R.id.content_fragment, fragment);
  }
}
