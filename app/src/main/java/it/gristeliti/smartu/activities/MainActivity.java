package it.gristeliti.smartu.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseUser;

import java.util.Timer;
import java.util.TimerTask;

import it.gristeliti.smartu.R;
import it.gristeliti.smartu.managers.EstimoteManager;
import it.gristeliti.smartu.services.HeartbeatService;
import it.gristeliti.smartu.managers.QueriesManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Constant value for the bluetooth activation request
     */
    private static final int REQUEST_ENABLE_BT = 1234;

    private static final int QUERIES_INTERVAL = 5000;

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private LinearLayout statusLayout;
    private ListView navigationDrawerListView;

    // TextViews
    private TextView connectionLabel;
    private TextView bluetoothLabel;
    private TextView rangingLabel;

    private TextView classroomTextView;
    private TextView studentsTextView;
    private TextView lectureTextView;
    private TextView professorTextView;
    private TextView noiseTextView;

    // buttons
    private Button hideShowButton;

    // network and bluetooth BroadcastReceiver
    private MyBroadcastReceiver broadcastReceiver;
    private boolean isBroadcastReceiverRegistered;

    private final Handler mainDataHandler = new Handler();

    /**
     * Timer for schedule the queries at a fixed time rate
     */
    private Timer mDataTimer = null;

    // intent filter for BroadcastReceiver
    private IntentFilter intentFilter;

    // bluetooth on or off
    private boolean bluetoohOn;

    // connection on or off
    private boolean connectionOn;

    // professor or student
    private boolean isProfessor;

    // contains
    private String[] navigation_drawer_elements;

    private String classroom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // VIEW'S STUFF

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        statusLayout = (LinearLayout)findViewById(R.id.statusLayout);
        statusLayout.setVisibility(View.GONE);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationDrawerListView = (ListView) findViewById(R.id.lst_menu_items);
        navigation_drawer_elements = getResources().getStringArray(R.array.classrooms_array);
        // Set the adapter for the list view
        navigationDrawerListView.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, R.id.drawer_textview, navigation_drawer_elements));
        // Set the list's click listener
        navigationDrawerListView.setOnItemClickListener(new DrawerItemClickListener());

        connectionLabel = (TextView)findViewById(R.id.connection_label);
        bluetoothLabel  = (TextView)findViewById(R.id.bluetooth_label);
        rangingLabel = (TextView)findViewById(R.id.ranging_label);

        hideShowButton = (Button)findViewById(R.id.hide_show_button);
        hideShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = statusLayout.getVisibility();
                if(visibility == View.VISIBLE) {
                    statusLayout.animate()
                            .alpha(0f)
                            .setDuration(500)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    statusLayout.setVisibility(View.GONE);
                                }
                            });
                }
                else {
                    statusLayout.animate()
                            .alpha(1f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    statusLayout.setVisibility(View.VISIBLE);
                                }
                            });
                }
            }
        });

        // initialize bluetooth filters
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(EstimoteManager.CLEAR_ACTION);
        intentFilter.addAction(EstimoteManager.CLASSROOM_CHANGED_ACTION);

        // creates the session on Parse
        ParseUser.becomeInBackground(ParseUser.getCurrentUser().getSessionToken(), new LogInCallback() {
            @Override
            public void done(ParseUser user2, ParseException arg1) {
                if(user2 != null) {
                    //Toast.makeText(getBaseContext(), "become in background done", Toast.LENGTH_LONG).show();
                }
                else {
                    //Toast.makeText(getBaseContext(), "become in background failed", Toast.LENGTH_LONG).show();
                }
            }
        });

        // get data from intent (coming from SignupActivity or LoginRegActivity)
        Intent dataIntent = getIntent();
        isProfessor = dataIntent.getBooleanExtra("IS_PROFESSOR", false); // false è un valore di default

        // cancel if already existed
        if (mDataTimer != null) {
            mDataTimer.cancel();
        } else {
            // recreate new
            mDataTimer = new Timer();
        }
    }

    /*private void startTimer() {
        // schedule task
        if(mDataTimer != null) {
            mDataTimer.scheduleAtFixedRate(new QueriesTimerTask(), 0, QUERIES_INTERVAL);
        }
    }*/

    private void stopTimer() {
        if (mDataTimer != null) {
            mDataTimer.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Other onResume() code here

        // bluetooth status
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            bluetoothLabel.setText("Bluetooth: NOT SUPPORTED");
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                bluetoothLabel.setText("Bluetooth: OFF");
                // allows the user to turn-on the bluetooth
                //Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else {
                bluetoothLabel.setText("Bluetooth: ON");
                // starts the raging
                EstimoteManager.create(MainActivity.this);
                rangingLabel.setText("Ranging: ON");
            }
        }

        // register broadcast receiver
        if (!isBroadcastReceiverRegistered) {
            if (broadcastReceiver == null)
                broadcastReceiver = new MyBroadcastReceiver();
            registerReceiver(broadcastReceiver, intentFilter);
            isBroadcastReceiverRegistered = true;
        }
    }

    protected void onPause() {
        super.onPause();
        /*if (isBroadcastReceiverRegistered) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
            isBroadcastReceiverRegistered = false;
        }*/

        // Other onPause() code here
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // unregister receiver
        if (isBroadcastReceiverRegistered) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
            isBroadcastReceiverRegistered = false;
        }
        // stop ranging
        EstimoteManager.stop();
        // stop heartbeat
        stopService(new Intent(MainActivity.this, HeartbeatService.class));

        // log-out from the system
        //ParseUser.logOut();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bluetoothLabel.setText("Bluetooth: ON");
                // starts the ranging
                EstimoteManager.create(MainActivity.this);
                rangingLabel.setText("Ranging: ON");
            } else {
                bluetoothLabel.setText("Bluetooth: OFF");
                rangingLabel.setText("Ranging: OFF");
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // QUESTO IN REALTA' NON HO CAPITO A CHE CAZZO SERVE

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Handles the click event on a Navigation Drawer's element
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            // Da modificare perché non tutti gli elementi cliccabili nel navigation drawer
            // rappresentano una classe
            if(navigation_drawer_elements[position].equals("Register to Courses")) {
                Intent intent = new Intent(MainActivity.this, CoursesRegistration.class);
                startActivity(intent);
            }
            else if(navigation_drawer_elements[position].equals("My Courses")) {
                Intent intent = new Intent(MainActivity.this, MyCourses.class);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(MainActivity.this, Classroom.class);
                intent.putExtra("CLASSROOM_LABEL", navigation_drawer_elements[position]);
                startActivity(intent);
            }
        }
    }

    /**
     * Queries Parse to update the data shown in the UI
     */
    private class QueriesTimerTask extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mainDataHandler.post(new Runnable() {
                @Override
                public void run() {
                    // queries to parse
                    QueriesManager.queryNumberOfStudents(classroom, studentsTextView);
                    QueriesManager.queryAverageNoise(classroom, noiseTextView);
                    QueriesManager.queryLecture(classroom, lectureTextView);
                }
            });
        }
    }

    /**
     * Checks Internet Connection and Bluetooth
     * Updates MainActivity UI
     * Stops/Starts Services according to connection state
     *
     * I start ranging beacons and sending heartbeat if i have both connection and bluetooth on
     */
    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.d("BroadcastReceiver", "stocazzo");

            // bluetooth status changed
            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                    bluetoothLabel.setText("Bluetooth: OFF");
                    // stop "things"
                    EstimoteManager.stop();
                    rangingLabel.setText("Ranging: OFF");
                    stopTimer();
                    stopService(new Intent(MainActivity.this, HeartbeatService.class));
                }
                else if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {
                    bluetoothLabel.setText("Bluetooth: ON");
                    // start/restart "things"
                    EstimoteManager.create(MainActivity.this);
                    rangingLabel.setText("Ranging: ON");
                    //startTimer();
                }
            }

            // connection status changed
            if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE );
                NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
                if(activeNetInfo == null) {
                    connectionLabel.setText("Connection: OFF");
                    // stop services
                    EstimoteManager.stop();
                    stopService(new Intent(MainActivity.this, HeartbeatService.class));
                    stopTimer();
                } else {
                    connectionLabel.setText("Connection: " + activeNetInfo.getTypeName());
                    // start/restart services
                    EstimoteManager.create(MainActivity.this);
                    //startTimer();
                }
            }

            if(action.equals(EstimoteManager.CLASSROOM_CHANGED_ACTION)) {
                // NON SO PROPRIO SICURO DI STO "PASTROCCHIO"
                // stop and restart the HeartbeatService with new data
                stopService(new Intent(MainActivity.this, HeartbeatService.class));
                Intent serviceIntent = new Intent(MainActivity.this, HeartbeatService.class);
                classroom = intent.getStringExtra(EstimoteManager.CLASSROOM_CHANGED);
                Log.d("CLASSROOM", classroom);
                // update classroom label
                //startTimer();
                serviceIntent.putExtra(EstimoteManager.CLASSROOM_CHANGED, classroom);
                startService(serviceIntent);
            }

            if(action.equals(EstimoteManager.CLEAR_ACTION)) {
                // stop sending Heartbeat
                stopService(new Intent(MainActivity.this, HeartbeatService.class));
                // clear textviews..

                // stop timer
                stopTimer();
            }
        }
    }

    /**
     * Questo receiver filtra le push notifications che arrivano
     * da Parse
     */
    private class PushReceiver extends ParsePushBroadcastReceiver {

        public PushReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }
}
