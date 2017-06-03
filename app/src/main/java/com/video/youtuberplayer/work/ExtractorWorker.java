package com.video.youtuberplayer.work;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import java.util.List;

/**
 * Created by hoanghiep on 6/2/17.
 */

public abstract class ExtractorWorker extends AbstractWorker {
    public static final int REQUESTED_STREAM = 1;
    public static final int REQUESTED_CHANNEL = 7;
    public static final String REQUESTED_STREAM_STRING = "requested stream";
    public static final String REQUESTED_CHANNEL_STRING = "requested channel";
    private final String url;

    public ExtractorWorker(Context context, String url, int serviceId) {
        super(context, serviceId);
        this.url = url;
        if (url.length() >= 40) setName("Thread-" + url.substring(url.length() - 11, url.length()));
    }

    @Override
    protected void doWork(int serviceId) throws Exception {
        doWork(serviceId, url);
    }

    /**
     * Here is the place that the heavy work is realized
     *
     * @param serviceId     serviceId that was passed when created this object
     * @param url           url that was passed when created this object
     *
     * @throws Exception    these exceptions are handled by the {@link #handleException(Exception, int, String)}
     */
    protected abstract void doWork(int serviceId, String url) throws Exception;

    @Override
    protected void handleException(Exception exception, int serviceId) {
        handleException(exception, serviceId, url);
    }

    /**
     * Method that handle the exception thrown by the {@link #doWork(int, String)}.
     *
     * @param exception {@link Exception} that was thrown by {@link #doWork(int, String)}
     */
    protected abstract void handleException(Exception exception, int serviceId, String url);

    /**
     * Handle the errors <b>during</b> extraction and shows a Report button to the user.<br/>
     * Subclasses <b>maybe</b> call this method.
     *
     * @param errorsList        list of exceptions that happened during extraction
     * @param errorUserAction   what action was the user performing during the error.
     *                          (One of the {@link ErrorActivity}.REQUEST_* error (message) ids)
     */
    protected void handleErrorsDuringExtraction(List<Throwable> errorsList, int errorUserAction){
        String errorString = "<error id>";
        switch (errorUserAction) {
            case REQUESTED_STREAM:
                errorString=  REQUESTED_STREAM_STRING;
                break;
            case REQUESTED_CHANNEL:
                errorString=  REQUESTED_CHANNEL_STRING;
                break;
        }

        Log.e(errorString, "OCCURRED ERRORS DURING EXTRACTION:");
        for (Throwable e : errorsList) {
            e.printStackTrace();
            Log.e(errorString, "------");
        }

        /*if (getContext() instanceof Activity) {
            View rootView = getContext() instanceof Activity ? ((Activity) getContext()).findViewById(android.R.id.content) : null;
            ErrorActivity.reportError(getHandler(), getContext(), errorsList, null, rootView, ErrorActivity.ErrorInfo.make(errorUserAction, getServiceName(), url, 0 *//* no message for the user *//*));
        }*/
    }

    public String getUrl() {
        return url;
    }
}
