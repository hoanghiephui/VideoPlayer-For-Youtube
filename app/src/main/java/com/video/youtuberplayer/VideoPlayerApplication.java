package com.video.youtuberplayer;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.video.youtuberplayer.rx.RxBus;
import com.video.youtuberplayer.ui.player.PopupVideoPlayer;
import com.video.youtuberplayer.ui.view.activity.BaseActivity;

import org.schabi.newpipe.extractor.NewPipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hoanghiep on 5/5/17.
 */

public class VideoPlayerApplication extends MultiDexApplication {
    private static final String TAG = VideoPlayerApplication.class.getSimpleName();
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static final String KEY_SUBSCRIPTIONS_LAST_UPDATED = "KEY_SUBSCRIPTIONS_LAST_UPDATED";
    private List<BaseActivity> activityList;

    /**
     * Application instance.
     */
    private static VideoPlayerApplication videoPlayerApp = null;

    public static VideoPlayerApplication getVideoPlayerApp() {
        return videoPlayerApp;
    }

    /**
     * Returns a localised string.
     *
     * @param stringResId String resource ID (e.g. R.string.my_string)
     * @return Localised string, from the strings XML file.
     */
    public static String getStr(int stringResId) {
        return videoPlayerApp.getString(stringResId);
    }

    /**
     * Given a string array resource ID, it returns an array of strings.
     *
     * @param stringArrayResId String array resource ID (e.g. R.string.my_array_string)
     * @return Array of String.
     */
    public static String[] getStringArray(int stringArrayResId) {
        return videoPlayerApp.getResources().getStringArray(stringArrayResId);
    }

    /**
     * Given a string array resource ID, it returns an list of strings.
     *
     * @param stringArrayResId String array resource ID (e.g. R.string.my_array_string)
     * @return List of String.
     */
    public static List<String> getStringArrayAsList(int stringArrayResId) {
        return Arrays.asList(getStringArray(stringArrayResId));
    }

    /**
     * Returns the App's {@link SharedPreferences}.
     *
     * @return {@link SharedPreferences}
     */
    public static SharedPreferences getPreferenceManager() {
        return PreferenceManager.getDefaultSharedPreferences(videoPlayerApp);
    }

    /**
     * Returns the dimension value that is specified in R.dimens.*.  This value is NOT converted into
     * pixels, but rather it is kept as it was originally written (e.g. dp).
     *
     * @return The dimension value.
     */
    public static float getDimension(int dimensionId) {
        return videoPlayerApp.getResources().getDimension(dimensionId);
    }

    /**
     * @return {@link Context}.
     */
    public static Context getContext() {
        return videoPlayerApp.getApplicationContext();
    }

    /**
     * Restart the app.
     */
    public static void restartApp() {
        Intent i = getContext().getPackageManager().getLaunchIntentForPackage(getContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Runtime.getRuntime().exit(0);
        getContext().startActivity(i);
    }

    private RxBus rxBus;
    private Application.ActivityLifecycleCallbacks activityCallbacks;

    public ActivityLifecycleCallbacks getActivityCallbacks() {
        return activityCallbacks;
    }

    public void setActivityCallbacks(ActivityLifecycleCallbacks activityCallbacks) {
        this.activityCallbacks = activityCallbacks;
    }

    private static boolean isBackGround = false;
    private ArrayList<Activity> activities = new ArrayList<>();
    public Handler mHandler = new Handler();

    public ArrayList<Activity> getActivities() {
        return activities;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        videoPlayerApp = this;
        activityList = new ArrayList<>();
        NewPipe.init(Downloader.getInstance());
        rxBus = new RxBus();
        setupActivityMonitor();
    }

    protected void onBackgroundMode() {
        activities.clear();
        Log.i(TAG, "App has entered background mode");
    }

    private void setupActivityMonitor() {
        if (activityCallbacks != null) {
            return;
        }
        registerActivityLifecycleCallbacks(activityCallbacks = new ActivityMonitor());
    }

    private class ActivityMonitor implements Application.ActivityLifecycleCallbacks {
        private boolean mActive = false;
        private int mRunningActivities = 0;

        class InactivityChecker implements Runnable {
            private boolean isCanceled;

            public void cancel() {
                isCanceled = true;
            }

            @Override
            public void run() {
                synchronized (VideoPlayerApplication.this) {
                    if (!isCanceled) {
                        if (ActivityMonitor.this.mRunningActivities == 0 && mActive) {
                            mActive = false;
                            isBackGround = true;
                        }
                    }
                }
            }
        }

        private InactivityChecker mLastChecker;

        @Override
        public synchronized void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            Log.i(TAG, "Activity created:" + activity);
            if (!activities.contains(activity)) {
                activities.add(activity);
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.i(TAG, "Activity started:" + activity);
        }

        @Override
        public synchronized void onActivityResumed(Activity activity) {
            Log.i(TAG, "Activity resumed:" + activity);
            if (activities.contains(activity)) {
                mRunningActivities++;
                Log.i(TAG, "runningActivities=" + mRunningActivities);
                checkActivity();
            }

        }

        @Override
        public synchronized void onActivityPaused(Activity activity) {
            Log.i(TAG, "Activity paused:" + activity);
            if (activities.contains(activity)) {
                mRunningActivities--;
                Log.i(TAG, "runningActivities=" + mRunningActivities);
                checkActivity();
            }

        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.i(TAG, "Activity stopped:" + activity);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public synchronized void onActivityDestroyed(Activity activity) {
            Log.i(TAG, "Activity destroyed:" + activity);
            if (activities.contains(activity)) {
                activities.remove(activity);
            }
        }

        void startInactivityChecker() {
            if (mLastChecker != null) mLastChecker.cancel();
            VideoPlayerApplication.this.mHandler.postDelayed(
                    (mLastChecker = new InactivityChecker()), 2000);
        }

        void checkActivity() {

            if (mRunningActivities == 0) {
                if (mActive) startInactivityChecker();
            } else if (mRunningActivities > 0) {
                if (!mActive) {
                    mActive = true;
                }
                if (mLastChecker != null) {
                    mLastChecker.cancel();
                    mLastChecker = null;
                }
            }
        }
    }

    public void addActivity(@NonNull BaseActivity a) {
        for (BaseActivity activity : activityList) {
            if (activity.equals(a)) {
                return;
            }
        }
        activityList.add(a);
    }

    @Nullable
    public BaseActivity getTopActivity() {
        if (activityList != null && activityList.size() > 0) {
            return activityList.get(activityList.size() - 1);
        } else {
            return null;
        }
    }

    public RxBus rxBus() {
        return rxBus;
    }
}
