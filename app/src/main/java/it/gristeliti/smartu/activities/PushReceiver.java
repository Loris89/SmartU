package it.gristeliti.smartu.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParseAnalytics;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

public class PushReceiver extends ParsePushBroadcastReceiver {

    @Override
    protected Class<? extends Activity> getActivity(Context context, Intent intent) {
        //Here is data you sent
        Log.i("PUSH RECEIVED", intent.getExtras().getString("com.parse.Data"));
        return Board.class;
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            String uri = json.getString("uri");
            Log.i("PUSH RECEIVED", uri);
            Intent myIntent = new Intent(context, Board.class);
            myIntent.putExtra(MyCourses.COURSE_KEY, uri);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(myIntent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
