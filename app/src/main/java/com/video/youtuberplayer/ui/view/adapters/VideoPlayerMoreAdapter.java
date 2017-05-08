package com.video.youtuberplayer.ui.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.video.youtuberplayer.R;

/**
 * Created by hoanghiep on 5/8/17.
 */

public class VideoPlayerMoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private static final int HEADER = 0;
  private static final int CONTENT = 1;

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case HEADER:
        return new ViewHeader(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_player_video, parent, false));
      default:
        return new ViewContent(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stream, parent, false));
    }
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder.getItemViewType() == HEADER) {
      if (holder instanceof ViewHeader) {

      }
    } else {
      if (holder instanceof ViewContent) {

      }
    }
  }

  @Override
  public int getItemViewType(int position) {
    if (position == 0) {
      return HEADER;
    } else {
      return CONTENT;
    }
  }

  @Override
  public int getItemCount() {
    return 0;
  }

  public static class ViewHeader extends RecyclerView.ViewHolder {

    public ViewHeader(View itemView) {
      super(itemView);
    }
  }

  public static class ViewContent extends RecyclerView.ViewHolder {

    public ViewContent(View itemView) {
      super(itemView);
    }
  }
}
