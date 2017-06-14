package com.video.youtuberplayer.api;

import android.util.Log;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.video.youtuberplayer.model.GetYouTubeVideos;
import com.video.youtuberplayer.model.YouTubeVideo;
import com.video.youtuberplayer.remote.YouTubeAPI;
import com.video.youtuberplayer.remote.YouTubeAPIKey;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

/**
 * Created by hoanghiep on 6/13/17.
 */

public class GetPopularVideos extends GetYouTubeVideos {
    private static final String TAG = GetPopularVideos.class.getSimpleName();
    protected YouTube.Search.List videosList = null;
    private SearchListResponse response;

    public GetPopularVideos(final long maxResults, String tokenNextPage) throws IOException {
        videosList = YouTubeAPI.create().search().list("id");
        videosList.setKey(YouTubeAPIKey.get().getYouTubeAPIKey());
        videosList.setType("video");
        videosList.setSafeSearch("none");
        videosList.setMaxResults(maxResults);
        videosList.setPublishedAfter(getOneDayBefore());
        videosList.setOrder("viewCount");
        videosList.setPageToken(tokenNextPage);
        response = videosList.execute();
        nextPageToken = response.getNextPageToken();
    }

    public SearchListResponse getResponse() {
        return response;
    }

    /**
     * Returns a date that is 24 hours in the past.
     *
     * @return 24 hours ago ({@link DateTime})
     */
    private DateTime getOneDayBefore() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        String dateRFC3339 = String.format("%d-%02d-%02dT%02d:%02d:%02dZ",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND));
        Log.d(TAG, "24 Hours before: " + dateRFC3339);
        return new DateTime(dateRFC3339);
    }

    @Override
    public void init(long maxResults, String token, String tokenNextPage) throws IOException {

    }

    @Override
    public List<YouTubeVideo> getNextVideos() {
        return null;
    }

    @Override
    public String tokenNextPage() {
        return nextPageToken;
    }

    @Override
    public boolean noMoreVideoPages() {
        return false;
    }
}
