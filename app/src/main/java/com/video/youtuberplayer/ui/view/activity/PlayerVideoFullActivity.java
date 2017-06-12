package com.video.youtuberplayer.ui.view.activity;

import android.media.AudioManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.video.youtuberplayer.R;
import com.video.youtuberplayer.StreamExtractorWorker;
import com.video.youtuberplayer.model.VideoRealatedAndChanel;
import com.video.youtuberplayer.ui.contracts.GetVideoDetailContract;

import org.schabi.newpipe.extractor.stream_info.StreamInfo;

import java.util.List;

import butterknife.BindView;

/**
 * Created by hoanghiep on 6/2/17.
 */

public class PlayerVideoFullActivity extends BaseActivity implements GetVideoDetailContract.IGetVideoDetailView,
        StreamExtractorWorker.OnStreamInfoReceivedListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.viewVideo)
    FrameLayout viewVideo;
    @BindView(R.id.detail_thumbnail_root_layout)
    RelativeLayout detailThumb;

    private PlayerVideoActivity.VideoPlayerImpl playerImpl;
    private AudioManager audioManager;
    private GestureDetector gestureDetector;
    @Override
    protected void initPresenter() {

    }

    @Override
    protected int getActivityBaseViewID() {
        return R.layout.activity_player_video;
    }

    @Override
    protected void onContent() {
       // playerImpl = new PlayerVideoActivity.VideoPlayerImpl();
    }

    @Override
    protected View.OnClickListener onSnackbarClickListener() {
        return null;
    }

    @Override
    protected int getMenuLayoutID() {
        return 0;
    }

    @Override
    protected void reloadContent() {

    }

    @Override
    public void setProgressVisibility(int visibityState) {

    }

    @Override
    public boolean isAdded() {
        return false;
    }

    @Override
    public void onUpdateViewRelatedAndChannel(VideoRealatedAndChanel videoRealatedAndChanel) {

    }

    @Override
    public void onReceive(StreamInfo info) {

    }

    @Override
    public void onReCaptchaException() {

    }

    @Override
    public void onBlockedByGemaError() {

    }

    @Override
    public void onContentErrorWithMessage(int messageId) {

    }

    @Override
    public void onContentError() {

    }

    @Override
    public void onUnrecoverableError(Exception exception) {

    }
}
