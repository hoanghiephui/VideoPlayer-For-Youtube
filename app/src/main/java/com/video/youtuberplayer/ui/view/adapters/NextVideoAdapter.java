package com.video.youtuberplayer.ui.view.adapters;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.video.youtuberplayer.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hoanghiep on 6/6/17.
 */

class NextVideoAdapter extends RecyclerView.Adapter<NextVideoAdapter.NextVideoViewHolder> {
    private List<SearchResult> videoList;

    public void setVideoList(List<SearchResult> videoList) {
        this.videoList = videoList;
        notifyDataSetChanged();
    }

    private VideoPlayerMoreAdapter.OnListenAdapter onListenAdapter;
    public NextVideoAdapter(List<SearchResult> videoList, VideoPlayerMoreAdapter.OnListenAdapter onListenAdapter) {
        this.videoList = videoList;
        this.onListenAdapter = onListenAdapter;
    }

    @Override
    public NextVideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NextVideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stream,
                parent, false));
    }

    @Override
    public void onBindViewHolder(NextVideoViewHolder holder, int position) {
        final SearchResult video = videoList.get(position);
        holder.itemVideoTitleView.setText(video.getSnippet().getTitle());
        Glide.with(holder.itemView.getContext())
                .load(video.getSnippet().getThumbnails().getHigh().getUrl())
                .into(holder.itemThumbnailView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onListenAdapter.onClickVideoContent(video);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }


    public static class NextVideoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemThumbnailView)
        AppCompatImageView itemThumbnailView;
        @BindView(R.id.itemDurationView)
        TextView itemDurationView;
        @BindView(R.id.itemVideoTitleView)
        TextView itemVideoTitleView;
        @BindView(R.id.itemUploaderView)
        TextView itemUploaderView;
        @BindView(R.id.itemUploadDateView)
        TextView itemUploadDateView;
        @BindView(R.id.itemViewCountView)
        TextView itemViewCountView;

        public NextVideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
