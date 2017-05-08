package com.video.youtuberplayer.ui.view.adapters;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.video.youtuberplayer.R;
import com.video.youtuberplayer.model.YouTubeVideo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hoanghiep on 5/6/17.
 */

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private List<YouTubeVideo> videoList;
  private int mLayoutMovieResID;
  private VideoCallBack videoCallBack;

  public VideoAdapter(List<YouTubeVideo> videoList, int mLayoutMovieResID, VideoCallBack videoCallBack) {
    this.videoList = videoList;
    this.mLayoutMovieResID = mLayoutMovieResID;
    this.videoCallBack = videoCallBack;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new VideoCategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(mLayoutMovieResID, parent, false));
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
    final YouTubeVideo youTubeVideo = videoList.get(position);
    if (holder instanceof VideoCategoryViewHolder) {
      holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          videoCallBack.onVideoSelect(youTubeVideo);
        }
      });
      ((VideoCategoryViewHolder) holder).title.setText(youTubeVideo.getTitle());
      Glide.with(holder.itemView.getContext()).load(youTubeVideo.getThumbnailUrl()).into(((VideoCategoryViewHolder) holder).thumbnail);
      ((VideoCategoryViewHolder) holder).channel.setText(youTubeVideo.getChannelName());
      ((VideoCategoryViewHolder) holder).duration.setText(youTubeVideo.getDuration());
      ((VideoCategoryViewHolder) holder).publishDate.setText(youTubeVideo.getPublishDatePretty());
      ((VideoCategoryViewHolder) holder).thumbsUp.setText(youTubeVideo.getThumbsUpPercentageStr());
      ((VideoCategoryViewHolder) holder).views.setText(youTubeVideo.getViewsCount());

      if (position >= getItemCount() - 1) {
        if (videoList != null)
          videoCallBack.onLoadMore();
      }
    }
  }

  @Override
  public int getItemCount() {
    return videoList.size();
  }

  public void setList(List<YouTubeVideo> list) {
    this.videoList = list;
  }

  public static class VideoCategoryViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.thumbnail_image_view)
    AppCompatImageView thumbnail;
    @BindView(R.id.title)
    AppCompatTextView title;
    @BindView(R.id.thumbs_up_text_view)
    TextView thumbsUp;
    @BindView(R.id.video_duration_text_view)
    TextView duration;
    @BindView(R.id.channel_text_view)
    TextView channel;
    @BindView(R.id.views_text_view)
    TextView views;
    @BindView(R.id.separator_text_view)
    TextView sepatator;
    @BindView(R.id.publish_date_text_view)
    TextView publishDate;

    public VideoCategoryViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  public interface VideoCallBack {
    void onVideoSelect(YouTubeVideo video);
    void onLoadMore();
  }
}
