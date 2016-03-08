package it.gristeliti.smartu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import it.gristeliti.smartu.R;
import it.gristeliti.smartu.managers.QueriesManager;

/**
 * Mostra le informazioni relative alla Classe richiesta.
 * Chiamata dalla MainActivity. Riceve un Intent con la label
 * della classe richiesta.
 */
public class Classroom extends AppCompatActivity {

    private static final int UPDATE_INTERVAL = 10000; // 10 seconds

    private Toolbar toolbar;

    private TextView label;
    private TextView seats;
    private TextView students;
    private TextView lecture;
    private TextView averageNoise;
    private TextView professor;

    private String lab;

    private final Handler queriesHandler = new Handler();

    /**
     * Timer for schedule the queries at a fixed time rate
     */
    private Timer mTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        label = (TextView)findViewById(R.id.classroom_label);
        lecture = (TextView)findViewById(R.id.lecture_label);

        // get the Intent and extract the classroom's label
        Intent intent = getIntent();
        lab = intent.getStringExtra("CLASSROOM_LABEL");
        label.setText("Classroom: " + lab);

        // get the views

        // TIMER INITIALIZATION

        // cancel if it already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
    }

    public void onStart() {
        super.onStart();

        // starts the timer that will perfoms the query to
        // the Parse Cloud every UPDATE_INTERVAL seconds
        // schedule task
        //mTimer.scheduleAtFixedRate(new QueriesTimerTask(), 0, UPDATE_INTERVAL);

        // debug
        QueriesManager.queryLecture(lab, lecture);
    }

    @Override
    public void onStop() {
        super.onStop();

        // stops the handler
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class QueriesTimerTask extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            queriesHandler.post(new Runnable() {
                @Override
                public void run() {
                    // queries to parse
                    QueriesManager.queryNumberOfStudents(lab, students);
                    QueriesManager.queryNumberOfSeats(lab, seats);
                    QueriesManager.queryAverageNoise(lab, averageNoise);
                    QueriesManager.queryLecture(lab,lecture);
                }
            });
        }
    }

    /**
     * Retrieves the number of students from the given classroom
     */
    private void queryNumberOfStudents(String classroomText) {
        HashMap<String, String> map = new HashMap<>();
        map.put("getClassroom", classroomText);
        ParseCloud.callFunctionInBackground("getStudentsNumber", map, new FunctionCallback<Integer>() {
            @Override
            public void done(Integer result, ParseException parseException) {
                if (parseException == null) {
                    students.setText("Number of students: " + String.valueOf(result));
                } else {
                    Toast.makeText(Classroom.this, "Errore query numero studenti", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Retrieves the number of seats from the given classroom
     */
    private void queryNumberOfSeats(String classroomLabel) {
        HashMap<String, String> map = new HashMap<>();
        map.put("getSeats", classroomLabel);
        ParseCloud.callFunctionInBackground("getSeatsNumber", map, new FunctionCallback<Integer>() {
            @Override
            public void done(Integer result, ParseException parseException) {
                if (parseException == null) {
                    seats.setText("Number of seats: " + String.valueOf(result));
                } else {
                    Toast.makeText(Classroom.this, "Errore query numero posti", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Retrieve the current Lecture of the given classroom
    **/
    private void queryLecture(String classroomLabel) {
        HashMap<String, String> map = new HashMap<>();
        map.put("getLabel", classroomLabel);
        ParseCloud.callFunctionInBackground("getCurrentLesson", map, new FunctionCallback<String>() {
            @Override
            public void done(String result, ParseException parseException) {
                if (parseException == null) {
                    lecture.setText("Lecture: " + result);
                } else {
                    Toast.makeText(Classroom.this, "Errore query lezione corrente", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Retrieve the average noise of the given classroom
     */
    private void queryAverageNoise(String classroomLabel) {
        HashMap<String, String> map = new HashMap<>();
        map.put("getNoise", classroomLabel);
        ParseCloud.callFunctionInBackground("getClassroomNoise", map, new FunctionCallback<Object>() {
            @Override
            public void done(Object result, ParseException parseException) {
                if (parseException == null) {
                    if (result instanceof Integer) {
                        int res = (int) result;
                        averageNoise.setText("Average Noise: " + String.valueOf(res) + " dB");
                    }
                    if (result instanceof Double) {
                        double res = (double) result;
                        int round = (int) Math.round(res);
                        averageNoise.setText("Average Noise: " + String.valueOf(res) + " dB");
                    }
                } else {
                    Toast.makeText(Classroom.this, "Errore query rumore medio", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
