package com.video.youtuberplayer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;

import org.schabi.newpipe.extractor.NewPipe;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hoanghiep on 5/5/17.
 */

public class VideoPlayerApplication extends MultiDexApplication {
    public static final String KEY_SUBSCRIPTIONS_LAST_UPDATED = "KEY_SUBSCRIPTIONS_LAST_UPDATED";
    /**
     * Application instance.
     */
    private static VideoPlayerApplication videoPlayerApp = null;

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
        return videoPlayerApp.getBaseContext();
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

    @Override
    public void onCreate() {
        super.onCreate();
        videoPlayerApp = this;
        NewPipe.init(Downloader.getInstance());
    }
}
