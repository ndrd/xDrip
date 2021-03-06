package com.eveningoutpost.dexdrip.UtilityModels;

import android.app.Service;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.eveningoutpost.dexdrip.Models.UserError.Log;

import static com.eveningoutpost.dexdrip.UtilityModels.Notifications.ongoingNotificationId;

/**
 * Created by Emma Black on 12/25/14.
 */
public class ForegroundServiceStarter {

    private static final String TAG = "FOREGROUND";

    final private Service mService;
    final private Context mContext;
    final private boolean run_service_in_foreground;
    final private Handler mHandler;


    public ForegroundServiceStarter(Context context, Service service) {
        mContext = context;
        mService = service;
        mHandler = new Handler(Looper.getMainLooper());
        // Force foreground with Oreo and above
        run_service_in_foreground = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O || Pref.getBoolean("run_service_in_foreground", false);
    }

    public void start() {
        if (mService == null) {
            Log.e(TAG, "SERVICE IS NULL - CANNOT START!");
            return;
        }
        if (run_service_in_foreground) {
            Log.d(TAG, "should be moving to foreground");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // TODO use constants
                    final long end = System.currentTimeMillis() + (60000 * 5);
                    final long start = end - (60000 * 60 * 3) - (60000 * 10);
                    mService.startForeground(ongoingNotificationId, new Notifications().createOngoingNotification(new BgGraphBuilder(mContext, start, end), mContext));
                }
            });
        }
    }

    public void stop() {
        if (run_service_in_foreground) {
            Log.d(TAG, "should be moving out of foreground");
            mService.stopForeground(true);
        }
    }

}
