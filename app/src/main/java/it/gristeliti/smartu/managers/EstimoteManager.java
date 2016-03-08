package it.gristeliti.smartu.managers;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
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
     * Time to wait to update the discovered beacon
     */
    private static final int TIME_LIMIT = 5000;

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
            beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 0);

            // beacons ranging
            beaconManager.setRangingListener(new BeaconManager.RangingListener() {
                @Override
                public void onBeaconsDiscovered(Region ALL_ESTIMOTE_BEACON, List<Beacon> beacons) {
                    // perform some operation on the list of beacon if this is not empty
                    if (!beacons.isEmpty()) {
                        // Log.i("main: ", "beacon discovered");
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
                            /*beaconsHandler.removeCallbacks(queriesRunnable);
                            beaconsHandler.postDelayed(queriesRunnable, QUERIES_DELAY_MILLIS); // 10 sec (debug value) */
                            oldUUID = newUUID; // update the oldID

                            Log.d("BEACON MANAGER", "new beacon found: " + newUUID);

                            queryClassroom(newUUID);
                        }
                    }
                    // in this case
                    else {
                        // if the myClearRunnable is not running, start it
                        if (!clearing) {
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
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
            beaconManager.disconnect();
        } catch (Exception e) {
            Log.e("EstimoteManager", e.getMessage());
        }
    }

    private static final Runnable clearRunnable = new Runnable() {

        @Override
        public void run() {
            // simply send a broadcast intent that will be received
            // by the main activity that will clear its UI
            // and will stop the HeartbeatService
            Intent intent = new Intent(CLEAR_ACTION);
            currentContext.sendBroadcast(intent);
        }
    };

    /*private static void queryClassroom(String UUID) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Classroom");
        query.whereEqualTo("BeaconUUID", UUID);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException parseException) {
                if (parseException == null) {
                    String result = null;
                    try {
                        result = object.getString("Label");
                    } catch (Exception e1) {
                        System.out.println("There is no result");
                    }

                    if (result != null) {
                        Log.i("Classroom", "Retrieved " + result);

                        // add the student to the new classroom
                        //classroom.setClassroom(result);
                        //QueryUtils.sendNotification(result);
                        Intent intent = new Intent(CLASSROOM_CHANGED);
                        intent.putExtra("Classroom", result);
                        currentContext.sendBroadcast(intent);
                    }
                } else {
                    Log.e("queryClassroom error: ", parseException.getMessage());
                }
            }
        });
    }*/

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
                    Log.d("Classroom retrieved", result);
                } else {
                    //Toast.makeText(Classroom.this, "Errore query lezione corrente", Toast.LENGTH_LONG).show();
                    Log.e("Classroom retrieved", result);
                }
            }
        });
    }
}
