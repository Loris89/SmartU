package it.gristeliti.smartu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

    private Toolbar toolbar;

    private TextView label;
    private TextView seats;
    private TextView students;
    private TextView lecture;
    private TextView professor;

    private FloatingActionButton floatingActionButton;

    private String classroom_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        label = (TextView)findViewById(R.id.classroom_label);
        lecture = (TextView)findViewById(R.id.lecture_label);
        professor = (TextView)findViewById(R.id.professor_classroom_txt);
        students = (TextView)findViewById(R.id.students_classroom_txt);
        seats = (TextView)findViewById(R.id.seats_classroom_txt);

        floatingActionButton = (FloatingActionButton)findViewById(R.id.fab_classroom);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });

        // get the Intent and extract the classroom's label
        Intent intent = getIntent();
        classroom_name = intent.getStringExtra("CLASSROOM_LABEL");
        label.setText("Classroom " + classroom_name);
    }

    private void update() {
        QueriesManager.queryLecture(classroom_name, lecture);
        //QueriesManager.queryProfessor(lecture, professor);
    }

    public void onStart() {
        super.onStart();
        update();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
