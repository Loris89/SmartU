package it.gristeliti.smartu.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import it.gristeliti.smartu.managers.EstimoteManager;

/**
 * This Service sends an heartbeat every minute to Parse in
 * order to update the database with the data of the current user
 *
 * The user must be logged in and currently in a classroom
 *
 * Non so se magari potrebbe essere sfruttato per inviare i dati
 * sul rumore/temperatura
 */
public class HeartbeatService extends Service {

    private String classroom;

    private static Handler queryHandler = new Handler();

    private static final int INTERVAL = 5000;

    /**
     * Timer for schedule the query at a fixed time rate
     */
    private Timer mTimer = null;

    public void onCreate() {

        // TIMER INITIALIZATION

        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new HeartbeatTimerTask(), 0, INTERVAL);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        classroom = intent.getStringExtra(EstimoteManager.CLASSROOM_CHANGED);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mTimer.cancel();
        mTimer = null;
    }

    /**
     * This implements the Timer that queries
     * Parse at a fixed time rate
     */
    private class HeartbeatTimerTask extends TimerTask {
        @Override
        public void run() {
            // run in another thread
            queryHandler.post(new Runnable() {
                @Override
                public void run() {
                    // query to Parse
                    queryUpdateAttending(classroom, ParseUser.getCurrentUser().getObjectId());
                    Log.d("HEARTBEAT", "query sent");
                }
            });
        }
    }

    private void queryUpdateAttending(String classroomLabel, String userObjId) {
        HashMap<String, String> map = new HashMap<>();
        map.put("getLabel", classroomLabel);
        map.put("getUser", userObjId);
        ParseCloud.callFunctionInBackground("updateAttending", map, new FunctionCallback<String>() {
            @Override
            public void done(String result, ParseException parseException) {
                if (parseException == null) {
                    Log.d("HEARTBEAT", "heartbeat ok");
                }else{
                    Toast.makeText(HeartbeatService.this,"Error sending hearbeat", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
