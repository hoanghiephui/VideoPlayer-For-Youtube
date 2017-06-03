package com.video.youtuberplayer.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.video.youtuberplayer.R;
import com.video.youtuberplayer.ui.player.BasePlayer;
import com.video.youtuberplayer.ui.player.VideoPlayer;

import org.schabi.newpipe.extractor.MediaFormat;
import org.schabi.newpipe.extractor.stream_info.AudioStream;
import org.schabi.newpipe.extractor.stream_info.StreamInfo;
import org.schabi.newpipe.extractor.stream_info.VideoStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hoanghiep on 6/2/17.
 */

public class Utils {

    private static final List<String> HIGH_RESOLUTION_LIST = Arrays.asList("1440p", "2160p", "1440p60", "2160p60");

    /**
     * Join the two lists of video streams (video_only and normal videos), and sort them according with preferred format
     * chosen by the user
     *
     * @param context          context to search for the format to give preference
     * @param videoStreams     normal videos list
     * @param videoOnlyStreams video only stream list
     * @param ascendingOrder   true -> smallest to greatest | false -> greatest to smallest
     * @return the sorted list
     */
    public static ArrayList<VideoStream> getSortedStreamVideosList(Context context, List<VideoStream> videoStreams, List<VideoStream> videoOnlyStreams, boolean ascendingOrder) {
        //boolean showHigherResolutions = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.show_higher_resolutions_key), false);
        String preferredFormatString = "MPEG-4";
        MediaFormat preferredFormat = MediaFormat.WEBM;
        switch (preferredFormatString) {
            case "WebM":
                preferredFormat = MediaFormat.WEBM;
                break;
            case "MPEG-4":
                preferredFormat = MediaFormat.MPEG_4;
                break;
            case "3GPP":
                preferredFormat = MediaFormat.v3GPP;
                break;
            default:
                break;
        }
        return getSortedStreamVideosList(preferredFormat, true, videoStreams, videoOnlyStreams, ascendingOrder);
    }

    /**
     * Join the two lists of video streams (video_only and normal videos), and sort them according with preferred format
     * chosen by the user
     *
     * @param preferredFormat       format to give preference
     * @param showHigherResolutions show >1080p resolutions
     * @param videoStreams          normal videos list
     * @param videoOnlyStreams      video only stream list
     * @param ascendingOrder        true -> smallest to greatest | false -> greatest to smallest    @return the sorted list
     * @return the sorted list
     */
    public static ArrayList<VideoStream> getSortedStreamVideosList(MediaFormat preferredFormat, boolean showHigherResolutions, List<VideoStream> videoStreams, List<VideoStream> videoOnlyStreams, boolean ascendingOrder) {
        ArrayList<VideoStream> retList = new ArrayList<>();
        HashMap<String, VideoStream> hashMap = new HashMap<>();

        if (videoOnlyStreams != null) {
            for (VideoStream stream : videoOnlyStreams) {
                if (!showHigherResolutions && HIGH_RESOLUTION_LIST.contains(stream.resolution)) continue;
                retList.add(stream);
            }
        }
        if (videoStreams != null) {
            for (VideoStream stream : videoStreams) {
                if (!showHigherResolutions && HIGH_RESOLUTION_LIST.contains(stream.resolution)) continue;
                retList.add(stream);
            }
        }

        // Add all to the hashmap
        for (VideoStream videoStream : retList) hashMap.put(videoStream.resolution, videoStream);

        // Override the values when the key == resolution, with the preferredFormat
        for (VideoStream videoStream : retList) {
            if (videoStream.format == preferredFormat.id) hashMap.put(videoStream.resolution, videoStream);
        }

        retList.clear();
        retList.addAll(hashMap.values());
        sortStreamList(retList, ascendingOrder);
        return retList;
    }

    /**
     * Sort the streams list depending on the parameter ascendingOrder;
     * <p>
     * It works like that:<br>
     * - Take a string resolution, remove the letters, replace "0p60" (for 60fps videos) with "1"
     * and sort by the greatest:<br>
     * <blockquote><pre>
     *      720p     ->  720
     *      720p60   ->  721
     *      360p     ->  360
     *      1080p    ->  1080
     *      1080p60  ->  1081
     * <p>
     *  ascendingOrder  ? 360 < 720 < 721 < 1080 < 1081
     *  !ascendingOrder ? 1081 < 1080 < 721 < 720 < 360/pre></blockquote>
     * <p>
     * @param videoStreams   list that the sorting will be applied
     * @param ascendingOrder true -> smallest to greatest | false -> greatest to smallest
     */
    public static void sortStreamList(List<VideoStream> videoStreams, final boolean ascendingOrder) {
        Collections.sort(videoStreams, new Comparator<VideoStream>() {
            @Override
            public int compare(VideoStream o1, VideoStream o2) {
                int res1 = Integer.parseInt(o1.resolution.replace("0p60", "1").replaceAll("[^\\d.]", ""));
                int res2 = Integer.parseInt(o2.resolution.replace("0p60", "1").replaceAll("[^\\d.]", ""));

                return ascendingOrder ? res1 - res2 : res2 - res1;
            }
        });
    }

    /**
     * Get the audio from the list with the highest bitrate
     *
     * @param audioStreams list the audio streams
     * @return audio with highest average bitrate
     */
    public static AudioStream getHighestQualityAudio(List<AudioStream> audioStreams) {
        int highestQualityIndex = 0;

        for (int i = 1; i < audioStreams.size(); i++) {
            AudioStream audioStream = audioStreams.get(i);
            if (audioStream.avgBitrate > audioStreams.get(highestQualityIndex).avgBitrate) highestQualityIndex = i;
        }

        return audioStreams.get(highestQualityIndex);
    }

    public static Intent getOpenVideoPlayerIntent(Context context, Class targetClazz, StreamInfo info, int selectedStreamIndex) {
        Intent mIntent = new Intent(context, targetClazz)
                .putExtra(BasePlayer.VIDEO_TITLE, info.title)
                .putExtra(BasePlayer.VIDEO_URL, info.webpage_url)
                .putExtra(BasePlayer.VIDEO_THUMBNAIL_URL, info.thumbnail_url)
                .putExtra(BasePlayer.CHANNEL_NAME, info.uploader)
                .putExtra(VideoPlayer.INDEX_SEL_VIDEO_STREAM, selectedStreamIndex)
                .putExtra(VideoPlayer.VIDEO_STREAMS_LIST, Utils.getSortedStreamVideosList(context, info.video_streams, info.video_only_streams, false))
                .putExtra(VideoPlayer.VIDEO_ONLY_AUDIO_STREAM, Utils.getHighestQualityAudio(info.audio_streams));
        if (info.start_position > 0) mIntent.putExtra(BasePlayer.START_POSITION, info.start_position * 1000);
        return mIntent;
    }

    public static Intent getOpenVideoPlayerIntent(Context context, Class targetClazz, VideoPlayer instance) {
        return new Intent(context, targetClazz)
                .putExtra(BasePlayer.VIDEO_TITLE, instance.getVideoTitle())
                .putExtra(BasePlayer.VIDEO_URL, instance.getVideoUrl())
                .putExtra(BasePlayer.VIDEO_THUMBNAIL_URL, instance.getVideoThumbnailUrl())
                .putExtra(BasePlayer.CHANNEL_NAME, instance.getChannelName())
                .putExtra(VideoPlayer.INDEX_SEL_VIDEO_STREAM, instance.getSelectedStreamIndex())
                .putExtra(VideoPlayer.VIDEO_STREAMS_LIST, instance.getVideoStreamsList())
                .putExtra(VideoPlayer.VIDEO_ONLY_AUDIO_STREAM, instance.getAudioStream())
                .putExtra(BasePlayer.START_POSITION, ((int) instance.getPlayer().getCurrentPosition()));
    }

    public static int getPopupDefaultResolution(Context context, List<VideoStream> videoStreams) {
        SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (defaultPreferences == null) return 0;

        String defaultResolution = defaultPreferences
                .getString(context.getString(R.string.default_popup_resolution_key),
                        context.getString(R.string.default_popup_resolution_value));

        String preferredFormat = defaultPreferences
                .getString(context.getString(R.string.preferred_video_format_key),
                        context.getString(R.string.preferred_video_format_default));

        return getDefaultResolution(defaultResolution, preferredFormat, videoStreams);
    }

    public static int getDefaultResolution(String defaultResolution, String preferredFormat, List<VideoStream> videoStreams) {

        if (defaultResolution.equals("Best resolution")) {
            return 0;
        }

        // first try to find the one with the right resolution
        int selectedFormat = 0;
        for (int i = 0; i < videoStreams.size(); i++) {
            VideoStream item = videoStreams.get(i);
            if (defaultResolution.equals(item.resolution)) {
                selectedFormat = i;
            }
        }

        // than try to find the one with the right resolution and format
        for (int i = 0; i < videoStreams.size(); i++) {
            VideoStream item = videoStreams.get(i);
            if (defaultResolution.equals(item.resolution)
                    && preferredFormat.equals(MediaFormat.getNameById(item.format))) {
                selectedFormat = i;
            }
        }

        if (selectedFormat == 0 && !videoStreams.get(selectedFormat).resolution.contains(defaultResolution.replace("p60", "p"))) {
            // Maybe there's no 60 fps variant available, so fallback to the normal version
            String replace = defaultResolution.replace("p60", "p");
            for (int i = 0; i < videoStreams.size(); i++) {
                VideoStream item = videoStreams.get(i);
                if (replace.equals(item.resolution)) selectedFormat = i;
            }

            // than try to find the one with the right resolution and format
            for (int i = 0; i < videoStreams.size(); i++) {
                VideoStream item = videoStreams.get(i);
                if (replace.equals(item.resolution)
                        && preferredFormat.equals(MediaFormat.getNameById(item.format))) {
                    selectedFormat = i;
                }
            }

        }

        // this is actually an error,
        // but maybe there is really no stream fitting to the default value.
        return selectedFormat;
    }
}
