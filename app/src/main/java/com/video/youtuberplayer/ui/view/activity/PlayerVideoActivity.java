package com.video.youtuberplayer.ui.view.activity;

import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;
import com.video.youtuberplayer.R;

/**
 * Created by hoanghiep on 5/7/17.
 */

public class PlayerVideoActivity extends BaseActivity {
  @Override
  protected int getActivityBaseViewID() {
    return R.layout.activity_player_video;
  }

  @Override
  protected void onContent() {
    setTransToolbar();
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
      WindowManager.LayoutParams.FLAG_FULLSCREEN);

  }

  @Override
  protected View.OnClickListener onSnackbarClickListener() {
    return null;
  }

  @Override
  protected int getMenuLayoutID() {
    return R.menu.menu_player;
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
}
