package com.video.youtuberplayer.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

/**
 * Created by hoanghiep on 5/5/17.
 */

public class ServerUtils {
  public static boolean isNetworkConnected(Context context) {
    try {
      ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
      return isNetworkAvailable &&
        cm.getActiveNetworkInfo().isConnected();
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public static boolean isWifiConnected(Context context) {
    ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
      Network[] network = connManager.getAllNetworks();
      if (network != null && network.length > 0) {
        for (Network aNetwork : network) {
          NetworkInfo networkInfo = connManager.getNetworkInfo(aNetwork);
          int networkType = networkInfo.getType();

          if (ConnectivityManager.TYPE_WIFI == networkType)
            return true;
        }
      }
    } else {
      NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
      return mWifi.isConnected();
    }

    return false;
  }
}
