package com.video.youtuberplayer.ui.player;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.exoplayer2.PlaybackParameters;
import com.video.youtuberplayer.BuildConfig;
import com.video.youtuberplayer.Constants;
import com.video.youtuberplayer.R;
import com.video.youtuberplayer.StreamExtractorWorker;
import com.video.youtuberplayer.VideoPlayerApplication;
import com.video.youtuberplayer.ui.view.activity.MainActivity;
import com.video.youtuberplayer.ui.view.activity.PlayerVideoActivity;
import com.video.youtuberplayer.ui.view.activity.ReCaptchaActivity;
import com.video.youtuberplayer.utils.Utils;

import org.schabi.newpipe.extractor.MediaFormat;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.stream_info.StreamInfo;

import java.util.ArrayList;

import static com.video.youtuberplayer.utils.AnimationUtils.animateView;
import static com.video.youtuberplayer.utils.Utils.getOpenVideoPlayerIntent;

/**
 * Created by hoanghiep on 6/2/17.
 */

public class PopupVideoPlayer extends Service {
    private static final String TAG = ".PopupVideoPlayer";
    private static final boolean DEBUG = BasePlayer.DEBUG;

    private static final int NOTIFICATION_ID = 40028922;
    public static final String ACTION_CLOSE = "com.video.youtuberplayer.ui.player.PopupVideoPlayer.CLOSE";
    public static final String ACTION_PLAY_PAUSE = "com.video.youtuberplayer.ui.player.PopupVideoPlayer.PLAY_PAUSE";
    public static final String ACTION_OPEN_DETAIL = "com.video.youtuberplayer.ui.player.PopupVideoPlayer.OPEN_DETAIL";
    public static final String ACTION_REPEAT = "com.video.youtuberplayer.ui.player.PopupVideoPlayer.REPEAT";

    private static final String POPUP_SAVED_WIDTH = "popup_saved_width";
    private static final String POPUP_SAVED_X = "popup_saved_x";
    private static final String POPUP_SAVED_Y = "popup_saved_y";

    private WindowManager windowManager;
    private WindowManager.LayoutParams windowLayoutParams;
    private GestureDetector gestureDetector;

    private float screenWidth, screenHeight;
    private float popupWidth, popupHeight;

    private float minimumWidth, minimumHeight;
    private float maximumWidth, maximumHeight;

    private final String setAlphaMethodName = "setImageAlpha";
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notBuilder;
    private RemoteViews notRemoteView;



    private VideoPlayerImpl playerImpl;
    private StreamExtractorWorker currentExtractorWorker;

    /*//////////////////////////////////////////////////////////////////////////
    // Service LifeCycle
    //////////////////////////////////////////////////////////////////////////*/

    @Override
    public void onCreate() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        notificationManager = ((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
        playerImpl = new VideoPlayerImpl();
    }

    @Override
    @SuppressWarnings("unchecked")
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if (DEBUG)
            Log.d(TAG, "onStartCommand() called with: intent = [" + intent + "], flags = [" + flags + "], startId = [" + startId + "]");
        if (playerImpl.getPlayer() == null) initPopup();
        if (!playerImpl.isPlaying()) playerImpl.getPlayer().setPlayWhenReady(true);


        if (intent.getStringExtra(Constants.KEY_URL) != null) {
            playerImpl.setStartedFromNewPipe(false);
            currentExtractorWorker = new StreamExtractorWorker(this, 0, intent.getStringExtra(Constants.KEY_URL)
                    , new FetcherRunnable(this));
            currentExtractorWorker.start();
        } else {
            playerImpl.setStartedFromNewPipe(true);
            playerImpl.handleIntent(intent);
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        updateScreenSize();
        updatePopupSize(windowLayoutParams.width, -1);
        checkPositionBounds();
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "onDestroy() called");
        stopForeground(true);
        if (playerImpl != null) {
            playerImpl.destroy();
            if (playerImpl.getRootView() != null)
                windowManager.removeView(playerImpl.getRootView());
        }
        if (notificationManager != null) notificationManager.cancel(NOTIFICATION_ID);
        if (currentExtractorWorker != null) {
            currentExtractorWorker.cancel();
            currentExtractorWorker = null;
        }
        Application.ActivityLifecycleCallbacks activityCallbacks = ((VideoPlayerApplication)getApplication()).getActivityCallbacks();
        if (activityCallbacks != null) {
            getApplication().unregisterActivityLifecycleCallbacks(activityCallbacks);
            ((VideoPlayerApplication)getApplication()).setActivityCallbacks(null);
        }
        savePositionAndSize();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    /*//////////////////////////////////////////////////////////////////////////
    // Init
    //////////////////////////////////////////////////////////////////////////*/

    @SuppressLint("RtlHardcoded")
    private void initPopup() {
        if (DEBUG) Log.d(TAG, "initPopup() called");
        View rootView = View.inflate(this, R.layout.player_popup, null);
        playerImpl.setup(rootView);

        updateScreenSize();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean popupRememberSizeAndPos = true;

        float defaultSize = getResources().getDimension(R.dimen.popup_default_width);
        popupWidth = sharedPreferences.getFloat(POPUP_SAVED_WIDTH, defaultSize);

        windowLayoutParams = new WindowManager.LayoutParams(
                (int) popupWidth, (int) getMinimumVideoHeight(popupWidth),
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        windowLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;

        int centerX = (int) (screenWidth / 2f - popupWidth / 2f);
        int centerY = (int) (screenHeight / 2f - popupHeight / 2f);
        windowLayoutParams.x = sharedPreferences.getInt(POPUP_SAVED_X, centerX);
        windowLayoutParams.y = sharedPreferences.getInt(POPUP_SAVED_Y, centerY);

        checkPositionBounds();

        MySimpleOnGestureListener listener = new MySimpleOnGestureListener();
        gestureDetector = new GestureDetector(this, listener);
        //gestureDetector.setIsLongpressEnabled(false);
        rootView.setOnTouchListener(listener);
        playerImpl.getLoadingPanel().setMinimumWidth(windowLayoutParams.width);
        playerImpl.getLoadingPanel().setMinimumHeight(windowLayoutParams.height);
        windowManager.addView(rootView, windowLayoutParams);
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Notification
    //////////////////////////////////////////////////////////////////////////*/

    private NotificationCompat.Builder createNotification() {
        notRemoteView = new RemoteViews(BuildConfig.APPLICATION_ID, R.layout.player_popup_notification);

        if (playerImpl.getVideoThumbnail() == null) {
            notRemoteView.setImageViewResource(R.id.notificationCover, R.drawable.dummy_thumbnail);
        } else {
            notRemoteView.setImageViewBitmap(R.id.notificationCover, playerImpl.getVideoThumbnail());
        }

        notRemoteView.setTextViewText(R.id.notificationSongName, playerImpl.getVideoTitle());
        notRemoteView.setTextViewText(R.id.notificationArtist, playerImpl.getChannelName());

        notRemoteView.setOnClickPendingIntent(R.id.notificationPlayPause,
                PendingIntent.getBroadcast(this, NOTIFICATION_ID, new Intent(ACTION_PLAY_PAUSE), PendingIntent.FLAG_UPDATE_CURRENT));
        notRemoteView.setOnClickPendingIntent(R.id.notificationStop,
                PendingIntent.getBroadcast(this, NOTIFICATION_ID, new Intent(ACTION_CLOSE), PendingIntent.FLAG_UPDATE_CURRENT));
        notRemoteView.setOnClickPendingIntent(R.id.notificationContent,
                PendingIntent.getBroadcast(this, NOTIFICATION_ID, new Intent(ACTION_OPEN_DETAIL), PendingIntent.FLAG_UPDATE_CURRENT));
        notRemoteView.setOnClickPendingIntent(R.id.notificationRepeat,
                PendingIntent.getBroadcast(this, NOTIFICATION_ID, new Intent(ACTION_REPEAT), PendingIntent.FLAG_UPDATE_CURRENT));

        switch (playerImpl.getCurrentRepeatMode()) {
            case REPEAT_DISABLED:
                notRemoteView.setInt(R.id.notificationRepeat, setAlphaMethodName, 77);
                break;
            case REPEAT_ONE:
                notRemoteView.setInt(R.id.notificationRepeat, setAlphaMethodName, 255);
                break;
            case REPEAT_ALL:
                // Waiting :)
                break;
        }

        return new NotificationCompat.Builder(this)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_play_arrow_white_24dp)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContent(notRemoteView);
    }

    /**
     * Updates the notification, and the play/pause button in it.
     * Used for changes on the remoteView
     *
     * @param drawableId if != -1, sets the drawable with that id on the play/pause button
     */
    private void updateNotification(int drawableId) {
        if (DEBUG)
            Log.d(TAG, "updateNotification() called with: drawableId = [" + drawableId + "]");
        if (notBuilder == null || notRemoteView == null) return;
        if (drawableId != -1)
            notRemoteView.setImageViewResource(R.id.notificationPlayPause, drawableId);
        notificationManager.notify(NOTIFICATION_ID, notBuilder.build());
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Misc
    //////////////////////////////////////////////////////////////////////////*/

    public void onVideoClose() {
        if (DEBUG) Log.d(TAG, "onVideoClose() called");
        savePositionAndSize();
        stopSelf();
    }

    public void onOpenDetail(Context context, String videoUrl, String videoTitle) {
        if (DEBUG)
            Log.d(TAG, "onOpenDetail() called with: context = [" + context + "], videoUrl = [" + videoUrl + "]");
        Intent i = new Intent(context, MainActivity.class);
        i.putExtra(Constants.KEY_SERVICE_ID, 0);
        i.putExtra(Constants.KEY_URL, videoUrl);
        i.putExtra(Constants.KEY_TITLE, videoTitle);
        i.putExtra(Constants.KEY_LINK_TYPE, StreamingService.LinkType.STREAM);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Utils
    //////////////////////////////////////////////////////////////////////////*/

    private void checkPositionBounds() {
        if (windowLayoutParams.x > screenWidth - windowLayoutParams.width)
            windowLayoutParams.x = (int) (screenWidth - windowLayoutParams.width);
        if (windowLayoutParams.x < 0) windowLayoutParams.x = 0;
        if (windowLayoutParams.y > screenHeight - windowLayoutParams.height)
            windowLayoutParams.y = (int) (screenHeight - windowLayoutParams.height);
        if (windowLayoutParams.y < 0) windowLayoutParams.y = 0;
    }

    private void savePositionAndSize() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PopupVideoPlayer.this);
        sharedPreferences.edit().putInt(POPUP_SAVED_X, windowLayoutParams.x).apply();
        sharedPreferences.edit().putInt(POPUP_SAVED_Y, windowLayoutParams.y).apply();
        sharedPreferences.edit().putFloat(POPUP_SAVED_WIDTH, windowLayoutParams.width).apply();
    }

    private float getMinimumVideoHeight(float width) {
        //if (DEBUG) Log.d(TAG, "getMinimumVideoHeight() called with: width = [" + width + "], returned: " + height);
        return width / (16.0f / 9.0f); // Respect the 16:9 ratio that most videos have
    }

    private void updateScreenSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);

        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        if (DEBUG)
            Log.d(TAG, "updateScreenSize() called > screenWidth = " + screenWidth + ", screenHeight = " + screenHeight);

        popupWidth = getResources().getDimension(R.dimen.popup_default_width);
        popupHeight = getMinimumVideoHeight(popupWidth);

        minimumWidth = getResources().getDimension(R.dimen.popup_minimum_width);
        minimumHeight = getMinimumVideoHeight(minimumWidth);

        maximumWidth = screenWidth;
        maximumHeight = screenHeight;
    }

    private void updatePopupSize(int width, int height) {
        //if (DEBUG) Log.d(TAG, "updatePopupSize() called with: width = [" + width + "], height = [" + height + "]");

        width = (int) (width > maximumWidth ? maximumWidth : width < minimumWidth ? minimumWidth : width);

        if (height == -1) height = (int) getMinimumVideoHeight(width);
        else
            height = (int) (height > maximumHeight ? maximumHeight : height < minimumHeight ? minimumHeight : height);

        windowLayoutParams.width = width;
        windowLayoutParams.height = height;
        popupWidth = width;
        popupHeight = height;

        if (DEBUG)
            Log.d(TAG, "updatePopupSize() updated values:  width = [" + width + "], height = [" + height + "]");
        windowManager.updateViewLayout(playerImpl.getRootView(), windowLayoutParams);
    }

    ///////////////////////////////////////////////////////////////////////////

    private class VideoPlayerImpl extends VideoPlayer {
        private TextView resizingIndicator;

        VideoPlayerImpl() {
            super(PopupVideoPlayer.this);
        }

        @Override
        public void playUrl(String url, String format, boolean autoPlay) {
            super.playUrl(url, format, autoPlay);

            windowLayoutParams.width = (int) popupWidth;
            windowLayoutParams.height = (int) getMinimumVideoHeight(popupWidth);
            windowManager.updateViewLayout(getRootView(), windowLayoutParams);

            notBuilder = createNotification();
            startForeground(NOTIFICATION_ID, notBuilder.build());
        }

        @Override
        public void initViews(View rootView) {
            super.initViews(rootView);
            resizingIndicator = (TextView) rootView.findViewById(R.id.resizing_indicator);
        }

        @Override
        public void destroy() {
            super.destroy();
            if (notRemoteView != null)
                notRemoteView.setImageViewBitmap(R.id.notificationCover, null);
        }

        @Override
        public void onThumbnailReceived(Bitmap thumbnail) {
            super.onThumbnailReceived(thumbnail);
            if (thumbnail != null) {
                if (notRemoteView != null)
                    notRemoteView.setImageViewBitmap(R.id.notificationCover, thumbnail);
                updateNotification(-1);
            }
        }

        @Override
        public void onFullScreenButtonClicked() {
            if (DEBUG) Log.d(TAG, "onFullScreenButtonClicked() called");
            ArrayList<Activity> activities = ((VideoPlayerApplication)getApplication()).getActivities();
            for (Activity activity : activities) {
                if (activity instanceof PlayerVideoActivity) {
                    ((VideoPlayerApplication)getApplication()).rxBus().send((getOpenVideoPlayerIntent(playerImpl)));
                    break;
                } else {
                    if (((MainActivity) activity).isResume()){
                        Intent intent;
                        intent = Utils.getOpenVideoPlayerIntent(context, PlayerVideoActivity.class, playerImpl);
                        if (!playerImpl.isStartedFromNewPipe()) {
                            intent.putExtra(VideoPlayer.STARTED_FROM_NEWPIPE, false);
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        if (playerImpl != null) {
                            playerImpl.destroyPlayer();
                        }
                        break;
                    }
                }
            }
            stopSelf();
        }

        @Override
        public void onRepeatClicked() {
            super.onRepeatClicked();
            switch (getCurrentRepeatMode()) {
                case REPEAT_DISABLED:
                    // Drawable didn't work on low API :/
                    //notRemoteView.setImageViewResource(R.id.notificationRepeat, R.drawable.ic_repeat_disabled_white);
                    // Set the icon to 30% opacity - 255 (max) * .3
                    notRemoteView.setInt(R.id.notificationRepeat, setAlphaMethodName, 77);
                    break;
                case REPEAT_ONE:
                    notRemoteView.setInt(R.id.notificationRepeat, setAlphaMethodName, 255);
                    break;
                case REPEAT_ALL:
                    // Waiting :)
                    break;
            }
            updateNotification(-1);
        }

        @Override
        public void onError(Exception exception) {
            exception.printStackTrace();
            Toast.makeText(context, "Failed to play this video", Toast.LENGTH_SHORT).show();
            stopSelf();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            super.onStopTrackingTouch(seekBar);
            if (playerImpl.wasPlaying()) {
                hideControls(100, 0);
            }
        }
/*//////////////////////////////////////////////////////////////////////////
        // Broadcast Receiver
        //////////////////////////////////////////////////////////////////////////*/

        @Override
        protected void setupBroadcastReceiver(IntentFilter intentFilter) {
            super.setupBroadcastReceiver(intentFilter);
            if (DEBUG)
                Log.d(TAG, "setupBroadcastReceiver() called with: intentFilter = [" + intentFilter + "]");
            intentFilter.addAction(ACTION_CLOSE);
            intentFilter.addAction(ACTION_PLAY_PAUSE);
            intentFilter.addAction(ACTION_OPEN_DETAIL);
            intentFilter.addAction(ACTION_REPEAT);
        }

        @Override
        public void onBroadcastReceived(Intent intent) {
            super.onBroadcastReceived(intent);
            if (DEBUG) Log.d(TAG, "onBroadcastReceived() called with: intent = [" + intent + "]");
            switch (intent.getAction()) {
                case ACTION_CLOSE:
                    onVideoClose();
                    break;
                case ACTION_PLAY_PAUSE:
                    playerImpl.onVideoPlayPause();
                    break;
                case ACTION_OPEN_DETAIL:
                    onOpenDetail(PopupVideoPlayer.this, playerImpl.getVideoUrl(), playerImpl.getVideoTitle());
                    break;
                case ACTION_REPEAT:
                    playerImpl.onRepeatClicked();
                    break;
            }
        }
        /*//////////////////////////////////////////////////////////////////////////
        // States
        //////////////////////////////////////////////////////////////////////////*/

        @Override
        public void onLoading() {
            super.onLoading();
            updateNotification(R.drawable.ic_play_arrow_white_24dp);
        }

        @Override
        public void onPlaying() {
            super.onPlaying();
            updateNotification(R.drawable.ic_pause_white_24dp);
        }

        @Override
        public void onBuffering() {
            super.onBuffering();
            updateNotification(R.drawable.ic_play_arrow_white_24dp);
        }

        @Override
        public void onPaused() {
            super.onPaused();
            updateNotification(R.drawable.ic_play_arrow_white_24dp);
            showAndAnimateControl(R.drawable.ic_play_arrow_white_24dp, false);
        }

        @Override
        public void onPausedSeek() {
            super.onPausedSeek();
            updateNotification(R.drawable.ic_play_arrow_white_24dp);
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
            updateNotification(R.drawable.ic_replay_white_i_24dp);
            showAndAnimateControl(R.drawable.ic_replay_white_i_24dp, false);
        }


        @SuppressWarnings("WeakerAccess")
        public TextView getResizingIndicator() {
            return resizingIndicator;
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }
    }

    private class MySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {
        private int initialPopupX, initialPopupY;
        private boolean isMoving;

        private int onDownPopupWidth = 0;
        private boolean isResizing;
        private boolean isResizingRightSide;

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (DEBUG)
                Log.d(TAG, "onDoubleTap() called with: e = [" + e + "]" + "rawXy = " + e.getRawX() + ", " + e.getRawY() + ", xy = " + e.getX() + ", " + e.getY());
            if (!playerImpl.isPlaying()) return false;
            if (e.getX() > popupWidth / 2) playerImpl.onFastForward();
            else playerImpl.onFastRewind();
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (DEBUG) Log.d(TAG, "onSingleTapConfirmed() called with: e = [" + e + "]");
            if (playerImpl.getPlayer() == null) return false;
            playerImpl.onVideoPlayPause();
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            if (DEBUG) Log.d(TAG, "onDown() called with: e = [" + e + "]");
            initialPopupX = windowLayoutParams.x;
            initialPopupY = windowLayoutParams.y;
            popupWidth = windowLayoutParams.width;
            popupHeight = windowLayoutParams.height;
            onDownPopupWidth = windowLayoutParams.width;
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (DEBUG) Log.d(TAG, "onLongPress() called with: e = [" + e + "]");
            playerImpl.showAndAnimateControl(-1, true);
            playerImpl.getLoadingPanel().setVisibility(View.GONE);

            playerImpl.hideControls(0, 0);
            animateView(playerImpl.getCurrentDisplaySeek(), false, 0, 0);
            animateView(playerImpl.getResizingIndicator(), true, 200, 0);

            isResizing = true;
            isResizingRightSide = e.getRawX() > windowLayoutParams.x + (windowLayoutParams.width / 2f);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (isResizing) return false;

            if (playerImpl.getCurrentState() != BasePlayer.STATE_BUFFERING
                    && (!isMoving || playerImpl.getControlsRoot().getAlpha() != 1f))
                playerImpl.showControls(0);
            isMoving = true;

            float diffX = (int) (e2.getRawX() - e1.getRawX()), posX = (int) (initialPopupX + diffX);
            float diffY = (int) (e2.getRawY() - e1.getRawY()), posY = (int) (initialPopupY + diffY);

            if (posX > (screenWidth - popupWidth)) posX = (int) (screenWidth - popupWidth);
            else if (posX < 0) posX = 0;

            if (posY > (screenHeight - popupHeight)) posY = (int) (screenHeight - popupHeight);
            else if (posY < 0) posY = 0;

            windowLayoutParams.x = (int) posX;
            windowLayoutParams.y = (int) posY;

            //noinspection PointlessBooleanExpression
            if (DEBUG && false) Log.d(TAG, "PopupVideoPlayer.onScroll = " +
                    ", e1.getRaw = [" + e1.getRawX() + ", " + e1.getRawY() + "]" +
                    ", e2.getRaw = [" + e2.getRawX() + ", " + e2.getRawY() + "]" +
                    ", distanceXy = [" + distanceX + ", " + distanceY + "]" +
                    ", posXy = [" + posX + ", " + posY + "]" +
                    ", popupWh = [" + popupWidth + " x " + popupHeight + "]");
            windowManager.updateViewLayout(playerImpl.getRootView(), windowLayoutParams);
            return true;
        }

        private void onScrollEnd() {
            if (DEBUG) Log.d(TAG, "onScrollEnd() called");
            if (playerImpl.isControlsVisible() && playerImpl.getCurrentState() == BasePlayer.STATE_PLAYING) {
                playerImpl.hideControls(300, VideoPlayer.DEFAULT_CONTROLS_HIDE_TIME);
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            gestureDetector.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_MOVE && isResizing && !isMoving) {
                //if (DEBUG) Log.d(TAG, "onTouch() ACTION_MOVE > v = [" + v + "],  e1.getRaw = [" + event.getRawX() + ", " + event.getRawY() + "]");
                int width;
                if (isResizingRightSide) width = (int) event.getRawX() - windowLayoutParams.x;
                else {
                    width = (int) (windowLayoutParams.width + (windowLayoutParams.x - event.getRawX()));
                    if (width > minimumWidth)
                        windowLayoutParams.x = initialPopupX - (width - onDownPopupWidth);
                }
                if (width <= maximumWidth && width >= minimumWidth) updatePopupSize(width, -1);
                return true;
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (DEBUG)
                    Log.d(TAG, "onTouch() ACTION_UP > v = [" + v + "],  e1.getRaw = [" + event.getRawX() + ", " + event.getRawY() + "]");
                if (isMoving) {
                    isMoving = false;
                    onScrollEnd();
                }

                if (isResizing) {
                    isResizing = false;
                    animateView(playerImpl.getResizingIndicator(), false, 100, 0);
                    playerImpl.changeState(playerImpl.getCurrentState());
                }
                savePositionAndSize();
            }
            return true;
        }

    }

    /**
     * Fetcher used if open by a link out of NewPipe
     */
    private class FetcherRunnable implements StreamExtractorWorker.OnStreamInfoReceivedListener {
        private final Context context;
        private final Handler mainHandler;

        FetcherRunnable(Context context) {
            this.mainHandler = new Handler(PopupVideoPlayer.this.getMainLooper());
            this.context = context;
        }

        @Override
        public void onReceive(StreamInfo info) {
            playerImpl.setVideoTitle(info.title);
            playerImpl.setVideoUrl(info.webpage_url);
            playerImpl.setVideoThumbnailUrl(info.thumbnail_url);
            playerImpl.setChannelName(info.uploader);

            playerImpl.setVideoStreamsList(Utils.getSortedStreamVideosList(context, info.video_streams, info.video_only_streams, false));
            playerImpl.setAudioStream(Utils.getHighestQualityAudio(info.audio_streams));

            int defaultResolution = Utils.getPopupDefaultResolution(context, playerImpl.getVideoStreamsList());
            playerImpl.setSelectedIndexStream(defaultResolution);

            if (DEBUG) {
                Log.d(TAG, "FetcherRunnable.StreamExtractor: chosen = "
                        + MediaFormat.getNameById(info.video_streams.get(defaultResolution).format) + " "
                        + info.video_streams.get(defaultResolution).resolution + " > "
                        + info.video_streams.get(defaultResolution).url);
            }

            if (info.start_position > 0) playerImpl.setVideoStartPos(info.start_position * 1000);
            else playerImpl.setVideoStartPos(-1);

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    playerImpl.play(true);
                }
            });
            Log.d(TAG, "onReceive: " + info.uploader_thumbnail_url);
            Glide.with(context)
                    .load(info.uploader_thumbnail_url)
                    .asBitmap()
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(final Bitmap resource, String model, Target<Bitmap> target,
                                                       boolean isFromMemoryCache, boolean isFirstResource) {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    playerImpl.setVideoThumbnail(resource);
                                    if (resource != null) {
                                        notRemoteView.setImageViewBitmap(R.id.notificationCover, resource);
                                        Log.d(TAG, "run: ");
                                    }
                                    updateNotification(-1);
                                }
                            });
                            return false;
                        }
                    });
        }

        @Override
        public void onError(final int messageId) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, messageId, Toast.LENGTH_LONG).show();
                }
            });
            stopSelf();
        }

        @Override
        public void onReCaptchaException() {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, R.string.recaptcha_request_toast, Toast.LENGTH_LONG).show();
                }
            });
            // Starting ReCaptcha Challenge Activity
            Intent intent = new Intent(context, ReCaptchaActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            stopSelf();
        }

        @Override
        public void onBlockedByGemaError() {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, R.string.blocked_by_gema, Toast.LENGTH_LONG).show();
                }
            });
            stopSelf();
        }

        @Override
        public void onContentErrorWithMessage(final int messageId) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, messageId, Toast.LENGTH_LONG).show();
                }
            });
            stopSelf();
        }

        @Override
        public void onContentError() {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, R.string.content_not_available, Toast.LENGTH_LONG).show();
                }
            });
            stopSelf();
        }

        @Override
        public void onUnrecoverableError(Exception exception) {
            exception.printStackTrace();
            stopSelf();
        }
    }

}
