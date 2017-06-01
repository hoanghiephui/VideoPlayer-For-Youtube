package com.video.youtuberplayer.ui.player;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.AppCompatImageView;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.video.youtuberplayer.R;
import com.video.youtuberplayer.model.YouTubeVideo;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hoanghiep on 6/1/17.
 */

public abstract class VideoPlayer extends BasePlayer implements SimpleExoPlayer.VideoListener, SeekBar.OnSeekBarChangeListener,
        View.OnClickListener, ExoPlayer.EventListener {

    public static final boolean DEBUG = BasePlayer.DEBUG;

    /*//////////////////////////////////////////////////////////////////////////
    // Intent
    //////////////////////////////////////////////////////////////////////////*/

    public static final String VIDEO_STREAMS_LIST = "video_streams_list";
    public static final String VIDEO_ONLY_AUDIO_STREAM = "video_only_audio_stream";
    public static final String INDEX_SEL_VIDEO_STREAM = "index_selected_video_stream";
    public static final String STARTED_FROM_NEWPIPE = "started_from_newpipe";

    private int selectedIndexStream;
    private YouTubeVideo video;

    /*//////////////////////////////////////////////////////////////////////////
    // Player
    //////////////////////////////////////////////////////////////////////////*/

    public static final int DEFAULT_CONTROLS_HIDE_TIME = 3000;  // 3 Seconds

    private boolean startedFromNewPipe = true;
    private boolean wasPlaying = false;

    /*//////////////////////////////////////////////////////////////////////////
    // Views
    //////////////////////////////////////////////////////////////////////////*/

    private View rootView;

    @BindView(R.id.aspectRatioLayout)
    AspectRatioFrameLayout aspectRatioFrameLayout;
    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;
    @BindView(R.id.surfaceForeground)
    View surfaceForeground;

    @BindView(R.id.detail_thumbnail_image_view)
    AppCompatImageView endScreen;
    @BindView(R.id.controlAnimationView)
    ImageView controlAnimationView;

    @BindView(R.id.playbackControlRoot)
    View controlsRoot;

    @BindView(R.id.bottomControls)
    View bottomControlsRoot;
    @BindView(R.id.playbackSeekBar)
    SeekBar playbackSeekBar;
    @BindView(R.id.playbackCurrentTime)
    TextView playbackCurrentTime;
    @BindView(R.id.playbackEndTime)
    TextView playbackEndTime;

    @BindView(R.id.topControls)
    View topControlsRoot;
    @BindView(R.id.qualityTextView)
    TextView qualityTextView;
    @BindView(R.id.fullScreenButton)
    AppCompatImageView fullScreenButton;

    private ValueAnimator controlViewAnimator;
    private Handler controlsVisibilityHandler = new Handler();

    public VideoPlayer(Context context) {
        super(context);
        this.context = context;
    }

    public void setup(View rootView) {
        initViews(rootView);
        setup();
    }

    public void setup() {
        if (simpleExoPlayer == null) {
            initPlayer();
        }
        initListeners();
    }

    public void initViews(View rootView) {
        this.rootView = rootView;
        ButterKnife.bind(context, rootView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            playbackSeekBar.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        }
        this.playbackSeekBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public void initListeners() {
        super.initListeners();
        playbackSeekBar.setOnSeekBarChangeListener(this);
        fullScreenButton.setOnClickListener(this);
    }

    @Override
    public void initPlayer() {
        super.initPlayer();
        simpleExoPlayer.setVideoSurfaceView(surfaceView);
        simpleExoPlayer.setVideoListener(this);
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
        if (intent == null) {
            return;
        }

        selectedIndexStream = intent.getIntExtra(INDEX_SEL_VIDEO_STREAM, -1);
        Serializable serializable = intent.getSerializableExtra(VIDEO_STREAMS_LIST);
        startedFromNewPipe = intent.getBooleanExtra(STARTED_FROM_NEWPIPE, true);
        play(true);
    }

    public void play(boolean autoPlay) {
        //playUrl(video.getVideoUrl(), MediaFormat.getSuffixById(getSelectedVideoStream().format), autoPlay);
    }
}
