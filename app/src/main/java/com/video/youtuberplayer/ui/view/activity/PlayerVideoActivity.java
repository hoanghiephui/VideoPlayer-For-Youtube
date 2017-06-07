package com.video.youtuberplayer.ui.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.robototextview.widget.RobotoTextView;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.video.youtuberplayer.R;
import com.video.youtuberplayer.StreamExtractorWorker;
import com.video.youtuberplayer.model.VideoCategory;
import com.video.youtuberplayer.model.VideoDuration;
import com.video.youtuberplayer.model.YouTubeVideo;
import com.video.youtuberplayer.ui.contracts.GetVideoDetailContract;
import com.video.youtuberplayer.ui.interceptor.GetVideoDetailInterceptor;
import com.video.youtuberplayer.ui.player.BasePlayer;
import com.video.youtuberplayer.ui.player.PopupVideoPlayer;
import com.video.youtuberplayer.ui.player.VideoPlayer;
import com.video.youtuberplayer.ui.presenter.GetVideoDetailPresenter;
import com.video.youtuberplayer.ui.view.adapters.VideoPlayerMoreAdapter;
import com.video.youtuberplayer.utils.AnimationUtils;
import com.video.youtuberplayer.utils.PermissionHelper;
import com.video.youtuberplayer.utils.Utils;

import org.schabi.newpipe.extractor.stream_info.StreamInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;

import static com.video.youtuberplayer.utils.AnimationUtils.animateView;
import static com.video.youtuberplayer.utils.ViewUtils.onShowImage;

/**
 * Created by hoanghiep on 5/7/17.
 */

public class PlayerVideoActivity extends BaseActivity implements GetVideoDetailContract.IGetVideoDetailView,
        StreamExtractorWorker.OnStreamInfoReceivedListener, VideoPlayerMoreAdapter.OnListenAdapter {
    private static final String TAG = PlayerVideoActivity.class.getSimpleName();
    public static final String VIDEO = "video";

    @BindView(R.id.playbackCurrentTime)
    RobotoTextView playbackCurrentTime;
    @BindView(R.id.playbackEndTime)
    RobotoTextView playbackEndTime;
    @BindView(R.id.endScreen)
    AppCompatImageView thumbnailVideo;
    @BindView(R.id.detailImage)
    AppCompatImageView detailImage;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.viewVideo)
    FrameLayout viewVideo;
    @BindView(R.id.detail_thumbnail_root_layout)
    RelativeLayout detailThumb;
    @BindView(R.id.viewTitle)
    View viewTitle;

    private VideoPlayerMoreAdapter adapter;
    private List<Video> videoList = new ArrayList<>();
    private YouTubeVideo video;

    private GetVideoDetailContract.IGetVideoDetailPresenter presenter;
    private GetVideoDetailContract.IGetVideoDetailInterceptor interceptor;

    private StreamInfo currentStreamInfo = null;
    private StreamExtractorWorker curExtractorWorker;

    private String videoTitle;
    private String videoUrl;
    private int serviceId = -1;
    private boolean activityPaused;

    private VideoPlayerImpl playerImpl;
    private AudioManager audioManager;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (getIntent() != null) {
            video = (YouTubeVideo) getIntent().getExtras().getSerializable(VIDEO);
        }
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getActivityBaseViewID() {
        return R.layout.activity_player_video;
    }

    @Override
    protected void onContent() {
        setTransToolbar();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        presenter.onBindView(this);
        playerImpl = new VideoPlayerImpl();
        playerImpl.setup(findViewById(android.R.id.content), this);
        if (video != null) {
            try {
                presenter.getVideoDetail(null, video.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
            adapter = new VideoPlayerMoreAdapter(videoList, this);
            recyclerView.setNestedScrollingEnabled(true);
            recyclerView.setHasFixedSize(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(
                    this,
                    LinearLayoutManager.VERTICAL,
                    false));
            recyclerView.setAdapter(adapter);
            videoUrl = video.getVideoUrl();
            videoTitle = video.getTitle();
            playbackEndTime.setText(VideoDuration.toHumanReadableString(video.getDuration()));
            onShowImage(thumbnailVideo, video.getThumbnailUrl());
            onShowImage(detailImage, video.getThumbnailUrl());
            if (currentStreamInfo == null) {
                selectAndLoadVideo(0, videoUrl, videoTitle);
            }
        } else {
            detailImage.setVisibility(View.GONE);
            playerImpl.handleIntent(getIntent());
            viewVideo.getLayoutParams().height = FrameLayout.LayoutParams.MATCH_PARENT;
            viewVideo.getLayoutParams().width = FrameLayout.LayoutParams.MATCH_PARENT;
            viewVideo.requestLayout();
            toggleOrientation();
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_more:
                showBottomMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void initPresenter() {
        interceptor = new GetVideoDetailInterceptor();
        presenter = new GetVideoDetailPresenter(interceptor, new CompositeDisposable());
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

    @Override
    public void setProgressVisibility(int visibityState) {

    }

    @Override
    public boolean isAdded() {
        return false;
    }

    @Override
    public void onUpdateView(List<Video> videoList) {
        try {
            presenter.getRelatedToVideoId(videoList.get(0).getId(), null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.videoList.clear();
        this.videoList.addAll(videoList);
        adapter.setList(this.videoList);
        adapter.notifyItemChanged(0);
    }

    @Override
    public void onUpdateViewRelated(List<SearchResult> resultList) {
        Log.d(TAG, "onUpdateViewRelated: " + resultList.get(0).getEtag());
        adapter.setResultList(resultList);
    }


    private void openInPopup() {
        if (isLoading.get()) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !PermissionHelper.checkSystemAlertWindowPermission(PlayerVideoActivity.this)) {
            Toast.makeText(PlayerVideoActivity.this, R.string.msg_popup_permission, Toast.LENGTH_LONG).show();
            return;
        }

        startService(Utils.getOpenVideoPlayerIntent(this, PopupVideoPlayer.class, playerImpl));
        if (playerImpl != null) {
            playerImpl.destroyPlayer();
        }

         ((View)playerImpl. getControlAnimationView().getParent()).setVisibility(View.GONE);
    }


    @Override
    protected void onStop() {
        activityPaused = true;
        if (playerImpl.getPlayer() != null) {
            playerImpl.setVideoStartPos((int) playerImpl.getPlayer().getCurrentPosition());
            playerImpl.destroyPlayer();
        }

        if (curExtractorWorker != null && curExtractorWorker.isRunning()) {
            curExtractorWorker.cancel();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        if (activityPaused) {
            playerImpl.initPlayer();
            playerImpl.getPlayPauseButton().setImageResource(R.drawable.ic_play_circle_filled_white_24dp);
            playerImpl.play(false);
            activityPaused = false;
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (presenter != null) {
            presenter.onUnbindView();
        }
        if (playerImpl != null) {
            playerImpl.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                hideSystemUi();
                break;
            default:
                break;
        }

    }

    public void selectAndLoadVideo(int serviceId, String videoUrl, String videoTitle) {
        selectAndLoadVideo(serviceId, videoUrl, videoTitle, true);
    }

    public void selectAndLoadVideo(int serviceId, String videoUrl, String videoTitle, boolean scrollToTop) {
        selectVideo(serviceId, videoUrl, videoTitle);
        prepareAndLoad(serviceId, videoUrl);
    }

    private void prepareAndLoad(int serviceId, String videoUrl) {
        isLoading.set(true);
        if (curExtractorWorker != null && curExtractorWorker.isRunning()) {
            curExtractorWorker.cancel();
        }
        curExtractorWorker = new StreamExtractorWorker(this, serviceId, videoUrl, this);
        curExtractorWorker.start();
    }

    public void selectVideo(int serviceId, String videoUrl, String videoTitle) {
        this.videoUrl = videoUrl;
        this.videoTitle = videoTitle;
        this.serviceId = serviceId;
    }

    //callback info video
    @Override
    public void onReceive(StreamInfo info) {
        Intent mIntent = new Intent()
                .putExtra(BasePlayer.VIDEO_TITLE, info.title)
                .putExtra(BasePlayer.VIDEO_URL, info.webpage_url)
                .putExtra(BasePlayer.VIDEO_THUMBNAIL_URL, info.thumbnail_url)
                .putExtra(BasePlayer.CHANNEL_NAME, info.uploader)
                .putExtra(VideoPlayer.INDEX_QUALITY_VIDEO_STREAM, 0)
                .putExtra(VideoPlayer.VIDEO_STREAMS_LIST,
                        Utils.getSortedStreamVideosList(this, info.video_streams, info.video_only_streams, false))
                .putExtra(VideoPlayer.VIDEO_ONLY_AUDIO_STREAM, Utils.getHighestQualityAudio(info.audio_streams));
        if (info.start_position > 0)
            mIntent.putExtra(BasePlayer.START_POSITION, info.start_position * 1000);
        playerImpl.handleIntent(mIntent);
        Log.d(TAG, "onReceive: " + info.video_streams.get(0).url);
        isLoading.set(false);
        currentStreamInfo = info;
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
    //callback info video


    /*//////////////////////////////////////////////////////////////////////////
    // Utils
    //////////////////////////////////////////////////////////////////////////*/

    private void showSystemUi() {
        Log.d(TAG, "showSystemUi() called");
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void hideSystemUi() {
        Log.d(TAG, "hideSystemUi() called");
        int visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(visibility);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void toggleOrientation() {
        setRequestedOrientation(getResources().getDisplayMetrics().heightPixels > getResources().getDisplayMetrics().widthPixels
                ? ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                : ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        viewTitle.setVisibility(getResources().getDisplayMetrics().heightPixels > getResources().getDisplayMetrics().widthPixels
                ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onShowPopup() {
        openInPopup();
    }

    @Override
    public void onClickVideoContent(SearchResult searchResult) {
        try {
            presenter.getVideoDetail(null, searchResult.getId().getVideoId());
        } catch (IOException e) {
            e.printStackTrace();
        }
        selectAndLoadVideo(0, "https://youtu.be/" + searchResult.getId().getVideoId(), searchResult.getSnippet().getTitle());
    }


    public class VideoPlayerImpl extends VideoPlayer {
        private TextView titleTextView;
        private TextView channelTextView;
        private TextView volumeTextView;
        private TextView brightnessTextView;
        private AppCompatImageButton repeatButton;
        private AppCompatImageButton back;
        private View viewTitle;

        private AppCompatImageButton screenRotationButton;
        private AppCompatImageView playPauseButton;

        VideoPlayerImpl() {
            super(PlayerVideoActivity.this);
        }

        @Override
        public void initViews(View rootView) {
            super.initViews(rootView);
            this.titleTextView = (TextView) rootView.findViewById(R.id.titleTextView);
            this.channelTextView = (TextView) rootView.findViewById(R.id.channelTextView);
            this.volumeTextView = (TextView) rootView.findViewById(R.id.volumeTextView);
            this.brightnessTextView = (TextView) rootView.findViewById(R.id.brightnessTextView);
            this.repeatButton = (AppCompatImageButton) rootView.findViewById(R.id.repeatButton);
            this.viewTitle = rootView.findViewById(R.id.viewTitle);
            this.screenRotationButton = (AppCompatImageButton) rootView.findViewById(R.id.screenRotationButton);
            this.playPauseButton = (AppCompatImageView) rootView.findViewById(R.id.detail_thumbnail_play_button);

            // Due to a bug on lower API, lets set the alpha instead of using a drawable
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                repeatButton.setImageAlpha(77);
            else { //noinspection deprecation
                repeatButton.setAlpha(77);
            }

            getRootView().setKeepScreenOn(true);
        }

        @Override
        public void initListeners() {
            super.initListeners();

            MySimpleOnGestureListener listener = new MySimpleOnGestureListener();
            gestureDetector = new GestureDetector(context, listener);
            gestureDetector.setIsLongpressEnabled(false);
            playerImpl.getRootView().setOnTouchListener(listener);

            repeatButton.setOnClickListener(this);
            playPauseButton.setOnClickListener(this);
            screenRotationButton.setOnClickListener(this);
        }

        @Override
        public void handleIntent(Intent intent) {
            super.handleIntent(intent);
            titleTextView.setText(getVideoTitle());
            channelTextView.setText(getChannelName());
        }

        @Override
        public void playUrl(String url, String format, boolean autoPlay) {
            super.playUrl(url, format, autoPlay);
            playPauseButton.setImageResource(autoPlay ? R.drawable.ic_pause_circle_filled_white_24dp :
                    R.drawable.ic_play_circle_filled_white_24dp);
        }

        @Override
        public void onFullScreenButtonClicked() {
            if (DEBUG) Log.d(TAG, "onFullScreenButtonClicked() called");
            if (playerImpl.getPlayer() == null) return;

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && !PermissionHelper.checkSystemAlertWindowPermission(PlayerVideoActivity.this)) {
                Toast.makeText(PlayerVideoActivity.this, R.string.msg_popup_permission, Toast.LENGTH_LONG).show();
                return;
            }

            context.startService(NavigationHelper.getOpenVideoPlayerIntent(context, PopupVideoPlayer.class, playerImpl));
            if (playerImpl != null) playerImpl.destroyPlayer();

            ((View) getControlAnimationView().getParent()).setVisibility(View.GONE);
            PlayerVideoActivity.this.finish();*/
            //recyclerView.setVisibility(View.GONE);
            toggleOrientation();
            //hideSystemUi();
            ((View) getControlAnimationView().getParent()).setVisibility(View.GONE);
            viewVideo.getLayoutParams().height = FrameLayout.LayoutParams.MATCH_PARENT;
            viewVideo.getLayoutParams().width = FrameLayout.LayoutParams.MATCH_PARENT;
            viewVideo.requestLayout();

        }

        @Override
        @SuppressWarnings("deprecation")
        public void onRepeatClicked() {
            super.onRepeatClicked();
            if (DEBUG) Log.d(TAG, "onRepeatClicked() called");
            switch (getCurrentRepeatMode()) {
                case REPEAT_DISABLED:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        repeatButton.setImageAlpha(77);
                    else repeatButton.setAlpha(77);

                    break;
                case REPEAT_ONE:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        repeatButton.setImageAlpha(255);
                    else repeatButton.setAlpha(255);

                    break;
                case REPEAT_ALL:
                    // Waiting :)
                    break;
            }
        }

        @Override
        public void onClick(View v) {
            super.onClick(v);
            if (v.getId() == repeatButton.getId()) onRepeatClicked();
            else if (v.getId() == playPauseButton.getId()) onVideoPlayPause();
            else if (v.getId() == screenRotationButton.getId()) onScreenRotationClicked();

            if (getCurrentState() != STATE_COMPLETED) {
                getControlsVisibilityHandler().removeCallbacksAndMessages(null);
                animateView(playerImpl.getControlsRoot(), true, 300, 0, new Runnable() {
                    @Override
                    public void run() {
                        if (getCurrentState() == STATE_PLAYING) {
                            hideControls(300, DEFAULT_CONTROLS_HIDE_TIME);
                        }
                    }
                });
            }
        }

        private void onScreenRotationClicked() {
            if (DEBUG) Log.d(TAG, "onScreenRotationClicked() called");
            toggleOrientation();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            super.onStopTrackingTouch(seekBar);
            if (playerImpl.wasPlaying()) {
                hideControls(100, 0);
            }
        }

        /*@Override
        public void onDismiss(PopupMenu menu) {
            super.onDismiss(menu);
            if (isPlaying()) hideControls(300, 0);
        }*/

        @Override
        public void onError(Exception exception) {
            exception.printStackTrace();
            Toast.makeText(context, "Failed to play this video", Toast.LENGTH_SHORT).show();
            finish();
        }

        /*//////////////////////////////////////////////////////////////////////////
        // States
        //////////////////////////////////////////////////////////////////////////*/

        @Override
        public void onLoading() {
            super.onLoading();
            playPauseButton.setImageResource(R.drawable.ic_pause_circle_filled_white_24dp);
            animateView(playPauseButton, AnimationUtils.Type.SCALE_AND_ALPHA, false, 100);
            getRootView().setKeepScreenOn(true);
        }

        @Override
        public void onBuffering() {
            super.onBuffering();
            animateView(playPauseButton, AnimationUtils.Type.SCALE_AND_ALPHA, false, 100);
            getRootView().setKeepScreenOn(true);
        }

        @Override
        public void onPlaying() {
            super.onPlaying();
            detailImage.setVisibility(View.GONE);
            animateView(playPauseButton, AnimationUtils.Type.SCALE_AND_ALPHA, false, 80, 0, new Runnable() {
                @Override
                public void run() {
                    playPauseButton.setImageResource(R.drawable.ic_pause_circle_filled_white_24dp);
                    animateView(playPauseButton, AnimationUtils.Type.SCALE_AND_ALPHA, true, 200);
                }
            });
            showSystemUi();
            getRootView().setKeepScreenOn(true);
        }

        @Override
        public void onPaused() {
            super.onPaused();
            animateView(playPauseButton, AnimationUtils.Type.SCALE_AND_ALPHA, false, 80, 0, new Runnable() {
                @Override
                public void run() {
                    playPauseButton.setImageResource(R.drawable.ic_play_circle_filled_white_24dp);
                    animateView(playPauseButton, AnimationUtils.Type.SCALE_AND_ALPHA, true, 200);
                }
            });

            showSystemUi();
            getRootView().setKeepScreenOn(false);
        }

        @Override
        public void onPausedSeek() {
            super.onPausedSeek();
            animateView(playPauseButton, AnimationUtils.Type.SCALE_AND_ALPHA, false, 100);
            getRootView().setKeepScreenOn(true);
        }


        @Override
        public void onCompleted() {
            if (getCurrentRepeatMode() == RepeatMode.REPEAT_ONE) {
                playPauseButton.setImageResource(R.drawable.ic_pause_circle_filled_white_24dp);
            } else {
                showSystemUi();
                animateView(playPauseButton, AnimationUtils.Type.SCALE_AND_ALPHA, false, 0, 0, new Runnable() {
                    @Override
                    public void run() {
                        playPauseButton.setImageResource(R.drawable.ic_replay_white_24dp);
                        animateView(playPauseButton, AnimationUtils.Type.SCALE_AND_ALPHA, true, 300);
                    }
                });
            }
            getRootView().setKeepScreenOn(false);
            super.onCompleted();
        }

        /*//////////////////////////////////////////////////////////////////////////
        // Utils
        //////////////////////////////////////////////////////////////////////////*/

        @Override
        public void hideControls(final long duration, long delay) {
            if (DEBUG) Log.d(TAG, "hideControls() called with: delay = [" + delay + "]");
            getControlsVisibilityHandler().removeCallbacksAndMessages(null);
            getControlsVisibilityHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    animateView(getControlsRoot(), false, duration, 0, new Runnable() {
                        @Override
                        public void run() {
                            //hideSystemUi();
                        }
                    });
                }
            }, delay);
        }

        ///////////////////////////////////////////////////////////////////////////
        // Getters
        ///////////////////////////////////////////////////////////////////////////

        public TextView getTitleTextView() {
            return titleTextView;
        }

        public TextView getChannelTextView() {
            return channelTextView;
        }

        public TextView getVolumeTextView() {
            return volumeTextView;
        }

        public TextView getBrightnessTextView() {
            return brightnessTextView;
        }

        public AppCompatImageButton getRepeatButton() {
            return repeatButton;
        }

        public AppCompatImageView getPlayPauseButton() {
            return playPauseButton;
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }
    }


    private class MySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {
        private boolean isMoving;

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "onDoubleTap() called with: e = [" + e + "]" + "rawXy = " + e.getRawX() + ", " + e.getRawY() + ", xy = " + e.getX() + ", " + e.getY());
            if (!playerImpl.isPlaying()) return false;
            if (e.getX() > playerImpl.getRootView().getWidth() / 2) playerImpl.onFastForward();
            else playerImpl.onFastRewind();
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d(TAG, "onSingleTapConfirmed() called with: e = [" + e + "]");
            if (playerImpl.getCurrentState() != BasePlayer.STATE_PLAYING) return true;

            if (playerImpl.isControlsVisible()) playerImpl.hideControls(150, 0);
            else {
                playerImpl.showControlsThenHide();
                showSystemUi();
            }
            return true;
        }

        private final boolean isGestureControlsEnabled = true;

        private final float stepsBrightness = 15, stepBrightness = (1f / stepsBrightness), minBrightness = .01f;
        private float currentBrightness = .5f;

        private int currentVolume, maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        private final float stepsVolume = 15, stepVolume = (float) Math.ceil(maxVolume / stepsVolume), minVolume = 0;

        private final String brightnessUnicode = new String(Character.toChars(0x2600));
        private final String volumeUnicode = new String(Character.toChars(0x1F508));

        private final int MOVEMENT_THRESHOLD = 40;
        private final int eventsThreshold = 8;
        private boolean triggered = false;
        private int eventsNum;

        // TODO: Improve video gesture controls
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            //noinspection PointlessBooleanExpression
            Log.d(TAG, "MainVideoPlayer.onScroll = " +
                    ", e1.getRaw = [" + e1.getRawX() + ", " + e1.getRawY() + "]" +
                    ", e2.getRaw = [" + e2.getRawX() + ", " + e2.getRawY() + "]" +
                    ", distanceXy = [" + distanceX + ", " + distanceY + "]");
            float abs = Math.abs(e2.getY() - e1.getY());
            if (!triggered) {
                triggered = abs > MOVEMENT_THRESHOLD;
                return false;
            }

            if (eventsNum++ % eventsThreshold != 0 || playerImpl.getCurrentState() == BasePlayer.STATE_COMPLETED)
                return false;
            isMoving = true;
//            boolean up = !((e2.getY() - e1.getY()) > 0) && distanceY > 0; // Android's origin point is on top
            boolean up = distanceY > 0;


            if (e1.getX() > playerImpl.getRootView().getWidth() / 2) {
                double floor = Math.floor(up ? stepVolume : -stepVolume);
                currentVolume = (int) (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + floor);
                if (currentVolume >= maxVolume) currentVolume = maxVolume;
                if (currentVolume <= minVolume) currentVolume = (int) minVolume;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);

                Log.d(TAG, "onScroll().volumeControl, currentVolume = " + currentVolume);
                playerImpl.getVolumeTextView().setText(volumeUnicode + " " + Math.round((((float) currentVolume) / maxVolume) * 100) + "%");

                if (playerImpl.getVolumeTextView().getVisibility() != View.VISIBLE)
                    animateView(playerImpl.getVolumeTextView(), true, 200);
                if (playerImpl.getBrightnessTextView().getVisibility() == View.VISIBLE)
                    playerImpl.getBrightnessTextView().setVisibility(View.GONE);
            } else {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                currentBrightness += up ? stepBrightness : -stepBrightness;
                if (currentBrightness >= 1f) currentBrightness = 1f;
                if (currentBrightness <= minBrightness) currentBrightness = minBrightness;

                lp.screenBrightness = currentBrightness;
                getWindow().setAttributes(lp);
                Log.d(TAG, "onScroll().brightnessControl, currentBrightness = " + currentBrightness);
                int brightnessNormalized = Math.round(currentBrightness * 100);

                playerImpl.getBrightnessTextView().setText(brightnessUnicode + " " + (brightnessNormalized == 1 ? 0 : brightnessNormalized) + "%");

                if (playerImpl.getBrightnessTextView().getVisibility() != View.VISIBLE)
                    animateView(playerImpl.getBrightnessTextView(), true, 200);
                if (playerImpl.getVolumeTextView().getVisibility() == View.VISIBLE)
                    playerImpl.getVolumeTextView().setVisibility(View.GONE);
            }
            return true;
        }

        private void onScrollEnd() {
            Log.d(TAG, "onScrollEnd() called");
            triggered = false;
            eventsNum = 0;
            /* if (playerImpl.getVolumeTextView().getVisibility() == View.VISIBLE) playerImpl.getVolumeTextView().setVisibility(View.GONE);
            if (playerImpl.getBrightnessTextView().getVisibility() == View.VISIBLE) playerImpl.getBrightnessTextView().setVisibility(View.GONE);*/
            if (playerImpl.getVolumeTextView().getVisibility() == View.VISIBLE)
                animateView(playerImpl.getVolumeTextView(), false, 200, 200);
            if (playerImpl.getBrightnessTextView().getVisibility() == View.VISIBLE)
                animateView(playerImpl.getBrightnessTextView(), false, 200, 200);

            if (playerImpl.isControlsVisible() && playerImpl.getCurrentState() == BasePlayer.STATE_PLAYING) {
                playerImpl.hideControls(300, VideoPlayer.DEFAULT_CONTROLS_HIDE_TIME);
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //noinspection PointlessBooleanExpression
            Log.d(TAG, "onTouch() called with: v = [" + v + "], event = [" + event + "]");
            gestureDetector.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_UP && isMoving) {
                isMoving = false;
                onScrollEnd();
            }
            return true;
        }

    }
}
