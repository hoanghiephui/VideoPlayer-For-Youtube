package com.video.youtuberplayer.api;

/**
 * Created by hoanghiep on 6/13/17.
 */

import java.io.IOException;

/**
 * A class that is able to query YouTube and returns information regarding the supplied videos IDs.
 */
public class GetVideosDetailsByIDs extends GetFeaturedVideos {

    public GetVideosDetailsByIDs(long maxResults, String token, String tokenNextPage, String videoIds) throws IOException {
        super(maxResults, token, tokenNextPage);
        super.videosList.setId(videoIds);
        super.videosList.setChart(null);
    }

    /**
     * Initialise object.
     *
     * @param videoIds Comma separated videos IDs.
     * @throws IOException
     */
    public void init(String videoIds) throws IOException {
        //super.init();
        super.videosList.setId(videoIds);
        super.videosList.setChart(null);
    }

}

