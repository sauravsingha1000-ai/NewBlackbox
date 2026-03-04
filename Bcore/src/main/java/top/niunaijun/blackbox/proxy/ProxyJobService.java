package top.niunaijun.blackbox.proxy;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

/**
 * Stub JobService for virtual app job scheduling.
 */
public class ProxyJobService extends JobService {
    private static final String TAG = "ProxyJobService";

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onStartJob: " + params.getJobId());
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
