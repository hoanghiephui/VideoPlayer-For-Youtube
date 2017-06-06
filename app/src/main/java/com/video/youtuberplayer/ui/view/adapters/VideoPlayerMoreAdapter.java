package com.video.youtuberplayer.ui.view.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.C;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.video.youtuberplayer.R;
import com.video.youtuberplayer.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.video.youtuberplayer.utils.ViewUtils.localizeViewCount;

/**
 * Created by hoanghiep on 5/8/17.
 */

public class VideoPlayerMoreAdapter extends RecyclerView.Adapter<VideoPlayerMoreAdapter.ViewHolder> {
    private static final int HEADER = 0;
    private static final int CONTENT = 1;
    private static final int FOOTER = 2;
    private List<Video> list;
    private List<SearchResult> resultList;
    private OnListenAdapter onListenAdapter;
    private List<Integer> typeList; // information of view holder.

    public VideoPlayerMoreAdapter(List<Video> list, OnListenAdapter onListenAdapter) {
        this.list = list;
        this.onListenAdapter = onListenAdapter;
        this.typeList = new ArrayList<>();
        this.resultList = new ArrayList<>();
    }


    public void setList(List<Video> list) {
        this.list = list;
        //buildTypeList();
    }

    public void setResultList(List<SearchResult> resultList) {
        if (this.resultList.size() > 0) {
            this.resultList.clear();
        }
        this.resultList = resultList;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER:
                return new ViewHeader(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_player_video, parent, false));
            case FOOTER:
                return new FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer, parent, false));
            default:
                return new ViewContent(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stream, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        if (holder.getItemViewType() == HEADER) {
            if (list.size() == 0) {
                return;
            }
            Video video = list.get(0);
            if (holder instanceof ViewHeader) {
                ((ViewHeader) holder).title.setText(video.getSnippet().getTitle());
                ((ViewHeader) holder).count_view.setText(localizeViewCount(video.getStatistics().getViewCount().longValue(), context));
                if (video.getStatistics().getLikeCount() != null) {
                    ((ViewHeader) holder).detail_like.setText(localizeViewCount(video.getStatistics().getLikeCount().longValue(), context));
                }

                if (video.getStatistics().getDislikeCount() != null) {
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
        } else if (holder.getItemViewType() == CONTENT ){
            if (holder instanceof ViewContent) {
                final SearchResult video = resultList.get(position -1);
                ((ViewContent)holder).itemVideoTitleView.setText(video.getSnippet().getTitle());
                Glide.with(holder.itemView.getContext())
                        .load(video.getSnippet().getThumbnails().getHigh().getUrl())
                        .into(((ViewContent)holder).itemThumbnailView);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onListenAdapter.onClickVideoContent(video);
                    }
                });
            }
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.onRecycled();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
          return HEADER;
        } else {
            if (position == getItemCount() - 1) {
                return FOOTER;
            } else {
                return CONTENT;
            }
        }
    }

    @Override
    public int getItemCount() {
        return resultList.size() + 2;
    }

    private void buildTypeList() {
        typeList.add(0, HEADER);
        typeList.add(1, CONTENT);
        typeList.add(2, FOOTER);
    }

    class ViewHeader extends ViewHolder {

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

        @Override
        protected boolean hasFooter() {
            return false;
        }

        ViewHeader(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void onBindView(Context context, List<Video> videoList, final OnListenAdapter onListenAdapter) {
            if (videoList.size() == 0) {
                return;
            }
            Video video = videoList.get(0);
            title.setText(video.getSnippet().getTitle());
            count_view.setText(localizeViewCount(video.getStatistics().getViewCount().longValue(), context));
            if (video.getStatistics().getLikeCount() != null) {
                detail_like.setText(localizeViewCount(video.getStatistics().getLikeCount().longValue(), context));
            }

            if (video.getStatistics().getDislikeCount() != null) {
                detail_dislike.setText(localizeViewCount(video.getStatistics().getDislikeCount().longValue(), context));
            }

            uploader.setText(video.getSnippet().getChannelTitle());
            //((ViewHeader) holder).countSub.setText(localizeViewCount(video.getStatistics().g));
            viewPopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onListenAdapter.onShowPopup();
                }
            });
        }

        @Override
        protected void onBindView(List<SearchResult> resultList, OnListenAdapter onListenAdapter) {

        }

        @Override
        protected void onRecycled() {

        }
    }

    class ViewContent extends ViewHolder {

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

        @Override
        protected boolean hasFooter() {
            return !DisplayUtils.isLandscape(itemView.getContext())
                    && DisplayUtils.getNavigationBarHeight(itemView.getContext().getResources()) != 0;
        }

        ViewContent(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        @Override
        protected void onBindView(Context context, List<Video> videoList, OnListenAdapter onListenAdapter) {

        }

        @Override
        protected void onBindView(List<SearchResult> resultList, OnListenAdapter onListenAdapter) {

        }

        @Override
        protected void onRecycled() {

        }
    }

    private class FooterHolder extends ViewHolder {

        @Override
        protected boolean hasFooter() {
            return false;
        }

        private FooterHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBindView(Context context, List<Video> videoList, OnListenAdapter onListenAdapter) {

        }

        @Override
        protected void onBindView(List<SearchResult> resultList, OnListenAdapter onListenAdapter) {

        }

        @Override
        protected void onRecycled() {

        }


    }

    public interface OnListenAdapter {
        void onShowPopup();

        void onClickVideoContent(SearchResult searchResult);
    }

    abstract class ViewHolder extends RecyclerView.ViewHolder {

        protected abstract boolean hasFooter();

        protected boolean isFooter(int position) {
            return hasFooter() && position == getItemCount() - 1;
        }

        ViewHolder(View itemView) {
            super(itemView);
        }

        protected abstract void onBindView(Context context, List<Video> videoList, OnListenAdapter onListenAdapter);

        protected abstract void onBindView(List<SearchResult> resultList, OnListenAdapter onListenAdapter);

        protected abstract void onRecycled();
    }
}
