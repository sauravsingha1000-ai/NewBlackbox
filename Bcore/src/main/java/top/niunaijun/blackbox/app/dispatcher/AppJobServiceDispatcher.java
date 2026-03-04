package top.niunaijun.blackbox.app.dispatcher;

import android.app.job.JobParameters;
import android.content.Context;
import android.util.Log;

/**
 * Dispatches job scheduler calls to virtual apps.
 */
public class AppJobServiceDispatcher {
    private static final String TAG = "AppJobServiceDispatcher";

    public static boolean dispatchStartJob(Context context, JobParameters params) {
        Log.d(TAG, "dispatchStartJob: " + params.getJobId());
        return false;
    }

    public static boolean dispatchStopJob(Context context, JobParameters params) {
        Log.d(TAG, "dispatchStopJob: " + params.getJobId());
        return false;
    }
}
