package com.video.youtuberplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.video.youtuberplayer.model.Account;

/**
 * Created by hoanghiep on 5/5/17.
 */

public class PrefsUtils {
  public static final String USER_PREFS_ID = "user_prefs";

  public static void saveAccount(final Context context, String account) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    sp.edit().putString(USER_PREFS_ID, account).apply();
  }

  public static String getAccount(final Context context) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    return sp.getString(USER_PREFS_ID, null);
  }
}
