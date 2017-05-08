package com.video.youtuberplayer.ui.view.fragment;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.gson.Gson;
import com.video.youtuberplayer.R;
import com.video.youtuberplayer.model.Account;
import com.video.youtuberplayer.ui.view.activity.SplashActivity;
import com.video.youtuberplayer.ui.view.widget.SplashVideoView;
import com.video.youtuberplayer.utils.PrefsUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.video.youtuberplayer.utils.ViewUtils.showSnackToast;

/**
 * Created by hoanghiep on 5/5/17.
 */

public class LoginFragment extends BaseFragment implements EasyPermissions.PermissionCallbacks {
  private static final String TAG = LoginFragment.class.getSimpleName();
  public static final String VIDEO_NAME = "welcome_video.mp4";
  static final int REQUEST_ACCOUNT_PICKER = 1000;
  static final int REQUEST_AUTHORIZATION = 1001;
  static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
  static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

  private static final String PREF_ACCOUNT_NAME = "accountName";
  private static final String[] SCOPES = {YouTubeScopes.YOUTUBE, YouTubeScopes.YOUTUBE_FORCE_SSL, YouTubeScopes.YOUTUBE_UPLOAD,
          YouTubeScopes.YOUTUBEPARTNER, YouTubeScopes.YOUTUBEPARTNER_CHANNEL_AUDIT};

  @BindView(R.id.login_google_button)
  AppCompatButton mCallApiButton;
  @BindView(R.id.videoView)
  SplashVideoView videoView;
  @BindView(R.id.coordenation_layout)
  CoordinatorLayout coordinatorLayout;

  GoogleAccountCredential mCredential;
  ProgressDialog mProgress;

  @Override
  protected int getViewLayout() {
    return R.layout.fragment_login;
  }

  @Override
  protected void onInitContent(Bundle savedInstanceState) {
    File videoFile = getActivity().getFileStreamPath(VIDEO_NAME);
    if (!videoFile.exists()) {
      videoFile = copyVideoFile();
    }

    playVideo(videoFile);

    mCallApiButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        getResultsFromApi();
      }
    });
    mProgress = new ProgressDialog(getActivity());
    mProgress.setMessage("Calling YouTube Data API ...");
    // Initialize credentials and service object.
    mCredential = GoogleAccountCredential.usingOAuth2(
            getContext().getApplicationContext(), Arrays.asList(SCOPES))
            .setBackOff(new ExponentialBackOff());
  }

  @Override
  protected View.OnClickListener onSnackbarClickListener() {
    return null;
  }

  private void playVideo(File videoFile) {
    videoView.setVideoPath(videoFile.getPath());
    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
      @Override
      public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
      }
    });
    videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
      @Override
      public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        coordinatorLayout.setBackgroundResource(R.drawable.bg);
        videoView.setVisibility(View.GONE);
        return false;
      }
    });
  }

  @NonNull
  private File copyVideoFile() {
    File videoFile;
    try {
      FileOutputStream fos = getActivity().openFileOutput(VIDEO_NAME, MODE_PRIVATE);
      InputStream in = getResources().openRawResource(R.raw.welcome_video);
      byte[] buff = new byte[1024];
      int len = 0;
      while ((len = in.read(buff)) != -1) {
        fos.write(buff, 0, len);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    videoFile = getActivity().getFileStreamPath(VIDEO_NAME);
    if (!videoFile.exists())
      throw new RuntimeException("video file has problem, are you sure you have welcome_video.mp4 in res/raw folder?");
    return videoFile;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    videoView.stopPlayback();
  }

  /**
   * Attempt to call the API, after verifying that all the preconditions are
   * satisfied. The preconditions are: Google Play Services installed, an
   * account was selected and the device currently has online access. If any
   * of the preconditions are not satisfied, the app will prompt the user as
   * appropriate.
   */
  private void getResultsFromApi() {
    if (!isGooglePlayServicesAvailable()) {
      acquireGooglePlayServices();
    } else if (mCredential.getSelectedAccountName() == null) {
      chooseAccount();
    } else if (!isDeviceOnline()) {
      showSnackToast(coordinatorLayout, "No network connection available.");
    } else {
      new MakeRequestTask(mCredential).execute();
    }
  }

  /**
   * Attempts to set the account used with the API credentials. If an account
   * name was previously saved it will use that one; otherwise an account
   * picker dialog will be shown to the user. Note that the setting the
   * account to use with the credentials object requires the app to have the
   * GET_ACCOUNTS permission, which is requested here if it is not already
   * present. The AfterPermissionGranted annotation indicates that this
   * function will be rerun automatically whenever the GET_ACCOUNTS permission
   * is granted.
   */
  @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
  private void chooseAccount() {
    if (EasyPermissions.hasPermissions(
            getActivity(), Manifest.permission.GET_ACCOUNTS)) {
      String accountName = getActivity().getPreferences(Context.MODE_PRIVATE)
              .getString(PREF_ACCOUNT_NAME, null);
      if (accountName != null) {
        mCredential.setSelectedAccountName(accountName);
        getResultsFromApi();
      } else {
        // Start a dialog from which the user can choose an account
        startActivityForResult(
                mCredential.newChooseAccountIntent(),
                REQUEST_ACCOUNT_PICKER);
      }
    } else {
      // Request the GET_ACCOUNTS permission via a user dialog
      EasyPermissions.requestPermissions(
              this,
              "This app needs to access your Google account (via Contacts).",
              REQUEST_PERMISSION_GET_ACCOUNTS,
              Manifest.permission.GET_ACCOUNTS);
    }
  }

  /**
   * Called when an activity launched here (specifically, AccountPicker
   * and authorization) exits, giving you the requestCode you started it with,
   * the resultCode it returned, and any additional data from it.
   *
   * @param requestCode code indicating which activity result is incoming.
   * @param resultCode  code indicating the result of the incoming
   *                    activity result.
   * @param data        Intent (containing result data) returned by incoming
   *                    activity result.
   */
  @Override
  public void onActivityResult(
          int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case REQUEST_GOOGLE_PLAY_SERVICES:
        if (resultCode != RESULT_OK) {
          showSnackToast(coordinatorLayout, "This app requires Google Play Services. " +
                  "Please install Google Play Services on your device and relaunch this app.");
        } else {
          getResultsFromApi();
        }
        break;
      case REQUEST_ACCOUNT_PICKER:
        if (resultCode == RESULT_OK && data != null &&
                data.getExtras() != null) {
          String accountName =
                  data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
          if (accountName != null) {
            SharedPreferences settings =
                    getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(PREF_ACCOUNT_NAME, accountName);
            editor.apply();
            mCredential.setSelectedAccountName(accountName);
            getResultsFromApi();
          }
        }
        break;
      case REQUEST_AUTHORIZATION:
        if (resultCode == RESULT_OK) {
          getResultsFromApi();
        }
        break;
    }
  }

  /**
   * Respond to requests for permissions at runtime for API 23 and above.
   *
   * @param requestCode  The request code passed in
   *                     requestPermissions(android.app.Activity, String, int, String[])
   * @param permissions  The requested permissions. Never null.
   * @param grantResults The grant results for the corresponding permissions
   *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
   */
  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    EasyPermissions.onRequestPermissionsResult(
            requestCode, permissions, grantResults, this);
  }

  /**
   * Callback for when a permission is granted using the EasyPermissions
   * library.
   *
   * @param requestCode The request code associated with the requested
   *                    permission
   * @param list        The requested permission list. Never null.
   */
  @Override
  public void onPermissionsGranted(int requestCode, List<String> list) {
    // Do nothing.
  }

  /**
   * Callback for when a permission is denied using the EasyPermissions
   * library.
   *
   * @param requestCode The request code associated with the requested
   *                    permission
   * @param list        The requested permission list. Never null.
   */
  @Override
  public void onPermissionsDenied(int requestCode, List<String> list) {
    // Do nothing.
  }

  /**
   * Checks whether the device currently has a network connection.
   *
   * @return true if the device has a network connection, false otherwise.
   */
  private boolean isDeviceOnline() {
    ConnectivityManager connMgr =
            (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    return (networkInfo != null && networkInfo.isConnected());
  }

  /**
   * Check that Google Play services APK is installed and up to date.
   *
   * @return true if Google Play Services is available and up to
   * date on this device; false otherwise.
   */
  private boolean isGooglePlayServicesAvailable() {
    GoogleApiAvailability apiAvailability =
            GoogleApiAvailability.getInstance();
    final int connectionStatusCode =
            apiAvailability.isGooglePlayServicesAvailable(getContext());
    return connectionStatusCode == ConnectionResult.SUCCESS;
  }

  /**
   * Attempt to resolve a missing, out-of-date, invalid or disabled Google
   * Play Services installation via a user dialog, if possible.
   */
  private void acquireGooglePlayServices() {
    GoogleApiAvailability apiAvailability =
            GoogleApiAvailability.getInstance();
    final int connectionStatusCode =
            apiAvailability.isGooglePlayServicesAvailable(getContext());
    if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
      showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
    }
  }


  /**
   * Display an error dialog showing that Google Play Services is missing
   * or out of date.
   *
   * @param connectionStatusCode code describing the presence (or lack of)
   *                             Google Play Services on this device.
   */
  void showGooglePlayServicesAvailabilityErrorDialog(
          final int connectionStatusCode) {
    GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
    Dialog dialog = apiAvailability.getErrorDialog(
            getActivity(),
            connectionStatusCode,
            REQUEST_GOOGLE_PLAY_SERVICES);
    dialog.show();
  }

  /**
   * An asynchronous task that handles the YouTube Data API call.
   * Placing the API calls in their own task ensures the UI stays responsive.
   */
  private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
    private com.google.api.services.youtube.YouTube mService = null;
    private Exception mLastError = null;

    MakeRequestTask(GoogleAccountCredential credential) {
      HttpTransport transport = AndroidHttp.newCompatibleTransport();
      JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
      mService = new com.google.api.services.youtube.YouTube.Builder(
              transport, jsonFactory, credential)
              .setApplicationName(getString(R.string.app_name))
              .build();
    }

    /**
     * Background task to call YouTube Data API.
     *
     * @param params no parameters needed for this task.
     */
    @Override
    protected List<String> doInBackground(Void... params) {
      try {
        String account = new Gson().toJson(new Account(mCredential.getSelectedAccount().name,
                mCredential.getToken(), mCredential.getScope()));
        PrefsUtils.saveAccount(getContext(), account);
        Log.d(TAG, "getResultsFromApi: " + mCredential.getScope() + " | " + mCredential.getToken()
                + " | " + mCredential.getSelectedAccount().name);
      } catch (IOException | GoogleAuthException e) {
        e.printStackTrace();
      }
      try {
        return getDataFromApi();
      } catch (Exception e) {
        mLastError = e;
        cancel(true);
        return null;
      }
    }

    /**
     * Fetch information about the "GoogleDevelopers" YouTube channel.
     *
     * @return List of Strings containing information about the channel.
     * @throws IOException
     */
    private List<String> getDataFromApi() throws IOException {
      // Get a list of up to 10 files.
      List<String> channelInfo = new ArrayList<String>();
      ChannelListResponse result = mService.channels().list("snippet,contentDetails,statistics")
              .setForUsername("GoogleDevelopers")
              .execute();
      List<Channel> channels = result.getItems();
      if (channels != null) {
        Channel channel = channels.get(0);
        channelInfo.add("This channel's ID is " + channel.getId() + ". " +
                "Its title is '" + channel.getSnippet().getTitle() + ", " +
                "and it has " + channel.getStatistics().getViewCount() + " views.");
      }
      return channelInfo;
    }


    @Override
    protected void onPreExecute() {
      if (mProgress != null) {
        mProgress.show();
      }
    }

    @Override
    protected void onPostExecute(List<String> output) {
      Log.d(TAG, "onPostExecute: " + output);
      mProgress.hide();
      if (output == null || output.size() == 0) {
        showSnackToast(coordinatorLayout, "No results returned.");
      } else {
        output.add(0, "Data retrieved using the YouTube Data API:");
        //showSnackToast(coordinatorLayout, TextUtils.join("\n", output));
        SplashActivity activity = ((SplashActivity) getActivity());
        if (activity != null) {
          activity.onOpenMain();
        }
      }
    }

    @Override
    protected void onCancelled() {
      mProgress.hide();
      if (mLastError != null) {
        if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
          showGooglePlayServicesAvailabilityErrorDialog(
                  ((GooglePlayServicesAvailabilityIOException) mLastError)
                          .getConnectionStatusCode());
        } else if (mLastError instanceof UserRecoverableAuthIOException) {
          startActivityForResult(
                  ((UserRecoverableAuthIOException) mLastError).getIntent(),
                  REQUEST_AUTHORIZATION);
        } else {
          showSnackToast(coordinatorLayout, "The following error occurred:\n"
                  + mLastError.getMessage());
        }
      } else {
        showSnackToast(coordinatorLayout, "Request cancelled.");
      }
    }
  }

  @Override
  public void onDestroyView() {
    new MakeRequestTask(mCredential).onCancelled();
    if (mProgress != null) {
      mProgress.cancel();
    }
    super.onDestroyView();
  }
}
