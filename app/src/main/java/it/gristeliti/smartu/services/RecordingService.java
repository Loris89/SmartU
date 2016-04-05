package it.gristeliti.smartu.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.parse.ParseUser;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import it.gristeliti.smartu.managers.EstimoteManager;

public class RecordingService extends Service {

    private static Handler recordingHandler = new Handler();

    private static final int RECORD_INTERVAL = 15000;

    private static MediaRecorder mRecorder = null;

    private int decibels;

    private final String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/noise.3gp";

    private String classroom;

    /**
     * Timer for schedule the query at a fixed time rate
     */
    private Timer mTimer = null;

    @Override
    public void onCreate() {
        // RECORDER INITIALIZATION

        if(mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile(mFileName);
        }

        // TIMER INITIALIZATION

        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }

        // schedule task
        mTimer.scheduleAtFixedRate(new RecordingTimerTask(), 0, RECORD_INTERVAL);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        classroom = intent.getStringExtra(EstimoteManager.CLASSROOM_CHANGED);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.start();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder  = null;

        mTimer.cancel();
        mTimer = null;
    }

    /**
     * This implements the Timer that get decibels
     * at a fixed time rate
     */
    private class RecordingTimerTask extends TimerTask {
        @Override
        public void run() {
            // run in another thread
            recordingHandler.post(new Runnable() {
                @Override
                public void run() {
                    double amplitude = mRecorder.getMaxAmplitude();
                    decibels = (int) (20 * Math.log10(amplitude / 0.9));
                    Log.i("DECIBELS", "Classroom: " + classroom + " | decibels: " + decibels + " dB");
                    if(decibels >= 20 && decibels <= 120) {
                        // send decibels to Parse
                    }
                }
            });
        }
    }
}
