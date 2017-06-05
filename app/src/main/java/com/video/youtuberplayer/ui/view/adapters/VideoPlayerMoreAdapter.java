package com.video.youtuberplayer.ui.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.api.services.youtube.model.Video;
import com.video.youtuberplayer.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.video.youtuberplayer.utils.ViewUtils.localizeViewCount;
import static com.video.youtuberplayer.utils.ViewUtils.onShowImage;

/**
 * Created by hoanghiep on 5/8/17.
 */

public class VideoPlayerMoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private static final int HEADER = 0;
  private static final int CONTENT = 1;
  private List<Video> list;
  private Context context;
  private OnListenAdapter onListenAdapter;

  public VideoPlayerMoreAdapter(List<Video> list, OnListenAdapter onListenAdapter) {
    this.list = list;
    this.onListenAdapter = onListenAdapter;
  }


  public void setList(List<Video> list) {
    this.list = list;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    context = parent.getContext();
    switch (viewType) {
      case HEADER:
        return new ViewHeader(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_player_video, parent, false));
      default:
        return new ViewContent(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stream, parent, false));
    }
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    Video video = list.get(position);

    if (holder.getItemViewType() == HEADER) {
      if (holder instanceof ViewHeader) {
        ((ViewHeader) holder).title.setText(video.getSnippet().getTitle());
        ((ViewHeader) holder).count_view.setText(localizeViewCount(video.getStatistics().getViewCount().longValue(), context));
        if (video.getStatistics().getLikeCount() != null) {
          ((ViewHeader) holder).detail_like.setText(localizeViewCount(video.getStatistics().getLikeCount().longValue(), context));
        }

        if (video.getStatistics().getDislikeCount() != null ){
          ((ViewHeader) holder).detail_dislike.setText(localizeViewCount(video.getStatistics().getDislikeCount().longValue(), context));
        }

        ((ViewHeader) holder).uploader.setText(video.getSnippet().getChannelTitle());
        //((ViewHeader) holder).countSub.setText(localizeViewCount(video.getStatistics().g));
        ((ViewHeader) holder).viewPopup.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            onListenAdapter.onShowPopup();
          }
        });
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
    return list.size();
  }

  public static class ViewHeader extends RecyclerView.ViewHolder {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.count_view)
    TextView count_view;
    @BindView(R.id.detail_like)
    TextView detail_like;
    @BindView(R.id.detail_dislike)
    TextView detail_dislike;
    @BindView(R.id.viewDownload)
    View viewDownload;
    @BindView(R.id.viewPopup)
    View viewPopup;
    @BindView(R.id.detail_uploader_thumbnail_view)
    CircleImageView avatar;
    @BindView(R.id.detail_uploader_text_view)
    TextView uploader;

    public ViewHeader(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  public static class ViewContent extends RecyclerView.ViewHolder {

    public ViewContent(View itemView) {
      super(itemView);
    }
  }

  public interface OnListenAdapter {
    void onShowPopup();
  }
}
