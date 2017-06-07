package com.video.youtuberplayer.ui.view.adapters;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.video.youtuberplayer.R;
import com.video.youtuberplayer.utils.DisplayUtils;
import com.video.youtuberplayer.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.video.youtuberplayer.utils.ViewUtils.localizeDate;
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
    private String urlImageChannel;


    public VideoPlayerMoreAdapter(List<Video> list, OnListenAdapter onListenAdapter) {
        this.list = list;
        this.onListenAdapter = onListenAdapter;
        this.typeList = new ArrayList<>();
        this.resultList = new ArrayList<>();
    }


    public void setList(List<Video> list) {
        this.list = list;
        buildTypeList();
        notifyItemChanged(0);
    }

    public void setResultList(List<SearchResult> resultList) {
        if (this.resultList.size() > 0) {
            this.resultList.clear();
        }
        this.resultList = resultList;
        notifyItemChanged(1);
    }

    public void setUrlImageChannel(String urlImageChannel) {
        this.urlImageChannel = urlImageChannel;
        notifyItemChanged(0);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER:
                return new ViewHeader(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_player_video, parent, false));
            case FOOTER:
                return new FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer, parent, false));
            default:
                return new ViewContent(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBindView(holder.itemView.getContext(), list, onListenAdapter);
        holder.onBindView(resultList, onListenAdapter);
    }

    @Override
    public int getItemViewType(int position) {
        return typeList.get(position);
    }

    @Override
    public int getItemCount() {
        return typeList.size();
    }

    private void buildTypeList() {
        if (typeList.size() > 0) {
            typeList.clear();
        }
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
        @BindView(R.id.detail_toggle_description_view)
        AppCompatImageView videoTitleToggleArrow;
        @BindView(R.id.detail_description_root_layout)
        RelativeLayout videoDescriptionRootLayout;
        @BindView(R.id.detail_upload_date_view)
        TextView videoUploadDateView;
        @BindView(R.id.detail_description_view)
        TextView videoDescriptionView;

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
            videoDescriptionView.setMovementMethod(LinkMovementMethod.getInstance());
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

            if (urlImageChannel != null) {
                ViewUtils.onShowImage(avatar, urlImageChannel);
            }

            String description = video.getSnippet().getDescription();
            if (!TextUtils.isEmpty(description)) { //noinspection deprecation
                videoDescriptionView.setText(description);
            }
            videoDescriptionView.setVisibility(!TextUtils.isEmpty(description) ? View.VISIBLE : View.GONE);

            if (!TextUtils.isEmpty(video.getSnippet().getPublishedAt().toString())) {
                videoUploadDateView.setText(localizeDate(video.getSnippet().getPublishedAt().toStringRfc3339(), context));
            }
            videoUploadDateView.setVisibility(!TextUtils.isEmpty(video.getSnippet().getPublishedAt().toString()) ? View.VISIBLE : View.GONE);
        }

        @Override
        protected void onBindView(List<SearchResult> resultList, OnListenAdapter onListenAdapter) {

        }

        @Override
        protected void onRecycled() {

        }

        @OnClick(R.id.detail_toggle_description_view)
        public void onClickToggle(){
            toggleTitleAndDescription();
        }

        private void toggleTitleAndDescription() {
            if (videoDescriptionRootLayout.getVisibility() == View.VISIBLE) {
                title.setMaxLines(1);
                videoDescriptionRootLayout.setVisibility(View.GONE);
                videoTitleToggleArrow.setImageResource(R.drawable.ic_arrow_drop_down_grey_600_24dp);
            } else {
                title.setMaxLines(10);
                videoDescriptionRootLayout.setVisibility(View.VISIBLE);
                videoTitleToggleArrow.setImageResource(R.drawable.ic_arrow_drop_up_grey_600_24dp);
            }
        }
    }

    class ViewContent extends ViewHolder {

        @BindView(R.id.recyclerView)
        RecyclerView recyclerView;

        @Override
        protected boolean hasFooter() {
            return !DisplayUtils.isLandscape(itemView.getContext())
                    && DisplayUtils.getNavigationBarHeight(itemView.getContext().getResources()) != 0;
        }

        ViewContent(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            recyclerView.setLayoutManager(
                    new LinearLayoutManager(
                            itemView.getContext(),
                            LinearLayoutManager.VERTICAL,
                            false));
        }

        @Override
        protected void onBindView(Context context, List<Video> videoList, OnListenAdapter onListenAdapter) {

        }

        @Override
        protected void onBindView(List<SearchResult> resultList, OnListenAdapter onListenAdapter) {
            recyclerView.setAdapter(new NextVideoAdapter(resultList, onListenAdapter));
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
