package it.gristeliti.smartu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

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
    private TextView currentLecture;
    private TextView nextLecture;
    private TextView prevLecture;
    private TextView professor;
    private TextView noise;

    private FloatingActionButton floatingActionButton;

    private String classroom_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        label = (TextView)findViewById(R.id.classroom_label);
        currentLecture = (TextView)findViewById(R.id.textView_lecture);
        nextLecture = (TextView)findViewById(R.id.textView_nextlecture);
        prevLecture = (TextView)findViewById(R.id.textView_prevlecture);
        professor = (TextView)findViewById(R.id.textView_professor);
        students = (TextView)findViewById(R.id.textView_students);
        seats = (TextView)findViewById(R.id.textView_seats);
        noise = (TextView)findViewById(R.id.textView_noise);

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
        QueriesManager.queryCourse(classroom_name, professor, currentLecture);
        QueriesManager.queryNextLecture(classroom_name, nextLecture);
        QueriesManager.queryPrevoiusLecture(classroom_name, prevLecture);
        QueriesManager.queryNumberOfStudents(classroom_name, students);
        QueriesManager.querySeats(classroom_name, seats);
        QueriesManager.gueryNoise(classroom_name, noise);
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
