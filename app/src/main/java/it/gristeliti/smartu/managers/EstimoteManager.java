package it.gristeliti.smartu.managers;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.eddystone.Eddystone;
import com.estimote.sdk.eddystone.EddystoneTelemetry;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EstimoteManager {

    /**
     * Time (ms) to wait before performing the queries
     *
     * This is a debug value
     */
    private static final int QUERIES_DELAY_MILLIS = 10000;

    /**
     * Time to wait before clearing the UI if no beacon is detected
     *
     * This is a debug value
     */
    private static final int CLEAR_DELAY_MILLIS = 20000;

    private static BeaconManager beaconManager;
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);

    private static Context currentContext;

    /**
     * Represents the previous discovered beacon's id
     */
    private static String oldUUID;

    /**
     * Represents the latest discovered beacon's id
     */
    private static String newUUID;

    /**
     * true if we are going to clear the UI
     */
    private static boolean clearing;

    /**
     * Handler to manage the delayed query
     */
    private static Handler beaconsHandler = new Handler();

    private static StringBuilder stringBuilder = new StringBuilder();

    public static final String CLEAR_ACTION = "ACTION_CLEAR";
    public static final String CLASSROOM_CHANGED_ACTION = "CLASSROOM_CHANGED_ACTION";
    public static final String CLASSROOM_CHANGED = "CLASSROOM_CHANGED";

    // Create everything we need to monitor the beacons
    public static void create(Context context) {
        try {
            currentContext = context;

            // Create a beacon manager
            beaconManager = new BeaconManager(currentContext);

            // We want the beacons heartbeat to be set at one second.
            beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(5), 0);

            // beacons ranging
            beaconManager.setRangingListener(new BeaconManager.RangingListener() {
                @Override
                public void onBeaconsDiscovered(Region ALL_ESTIMOTE_BEACON, List<Beacon> beacons) {
                    // perform some operation on the list of beacon if this is not empty
                    // STO CAPTANDO QUALCHE BEACON
                    if (!beacons.isEmpty()) {
                        Log.i("EstimoteManager", "beacon discovered!");
                        // if myClearRunnable was running, then stop it
                        beaconsHandler.removeCallbacks(clearRunnable);

                        // the boolean variable "clearing" indicates if the myClearRunnable
                        // is running or not
                        // here we set it to false
                        clearing = false;

                        // the first beacon of the list is the nearest one
                        // stringBuilder.append(beacons.get(0).getProximityUUID() + beacons.get(0).getMajor() + beacons.get(0).getMinor());
                        stringBuilder.append(beacons.get(0).getProximityUUID());
                        stringBuilder.append(beacons.get(0).getMajor());
                        stringBuilder.append(beacons.get(0).getMinor());
                        newUUID = stringBuilder.toString();
                        stringBuilder.delete(0, stringBuilder.length());

                        // If the more recent UUID is different from the older one,
                        // restart the timer
                        if (!newUUID.equals(oldUUID)) {
                            beaconsHandler.removeCallbacks(queriesRunnable);
                            beaconsHandler.postDelayed(queriesRunnable, QUERIES_DELAY_MILLIS);
                            oldUUID = newUUID; // update the oldID
                            Log.d("BEACON MANAGER", "new beacon found: " + newUUID);
                        }
                    }
                    // NON STO CAPTANDO NESSUN BEACON
                    else {
                        Log.i("EstimoteManager", "no beacon detected");
                        // if the myClearRunnable is not running, start it
                        if (!clearing) {
                            Log.i("EstimoteManager", "clearing");
                            clearing = true;
                            beaconsHandler.postDelayed(clearRunnable, CLEAR_DELAY_MILLIS); // 20 sec (debug value)
                        }
                    }
                }
            });

            // connection to the service
            beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                @Override
                public void onServiceReady() {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
                }
            });
        } catch (Exception e) {
            Log.e("EstimoteManager", e.getMessage());
        }
    }

    // Stop beacons monitoring, and closes the service
    public static void stop() {
        try {
            Log.i("EstimoteManager", "ranging stopped");
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
            beaconManager.disconnect();
        } catch (Exception e) {
            Log.e("EstimoteManager", e.getMessage());
        }
    }

    private static final Runnable queriesRunnable = new Runnable() {
        @Override
        public void run() {
            queryClassroom(newUUID);
        }
    };

    private static final Runnable clearRunnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(CLEAR_ACTION);
            currentContext.sendBroadcast(intent);
        }
    };

    private static void queryClassroom(String UUID) {
        HashMap<String, String> map = new HashMap<>();
        map.put("beaconUUID", UUID);
        ParseCloud.callFunctionInBackground("getClassroom", map, new FunctionCallback<String>() {
            @Override
            public void done(String result, ParseException parseException) {
                if (parseException == null) {
                    Intent intent = new Intent();
                    intent.setAction(CLASSROOM_CHANGED_ACTION);
                    intent.putExtra(CLASSROOM_CHANGED, result);
                    currentContext.sendBroadcast(intent);
                    Log.i("Classroom retrieved", result);
                } else {
                    Log.e("Classroom retrieved", result);
                }
            }
        });
    }
}
