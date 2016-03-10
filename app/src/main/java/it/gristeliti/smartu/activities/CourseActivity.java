package it.gristeliti.smartu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import it.gristeliti.smartu.R;

public class CourseActivity extends AppCompatActivity {

    private TextView courseName;
    private TextView professorName;
    private TextView rating;

    private Button boardButton;
    private Button unsubscribeButton;

    private String course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        courseName = (TextView)findViewById(R.id.course_name_txt);
        professorName = (TextView)findViewById(R.id.professor_course_name_txt);
        rating = (TextView)findViewById(R.id.course_rating_txt);

        boardButton = (Button)findViewById(R.id.board_button);
        unsubscribeButton = (Button)findViewById(R.id.unsubscribe_button);

        Intent intent = getIntent();
        course = intent.getStringExtra(MyCourses.COURSE_KEY);

        courseName.setText("Name: " + course);
        setProfessorName();

        boardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseActivity.this, Board.class);
                intent.putExtra(MyCourses.COURSE_KEY, course);
                startActivity(intent);
            }
        });

        unsubscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsubscribe();
            }
        });
    }

    private void unsubscribe() {
        HashMap<String, String> map = new HashMap<>();
        map.put("getUserObjId", ParseUser.getCurrentUser().getObjectId());
        map.put("getCourseName", course);
        ParseCloud.callFunctionInBackground("unsubscribeFromCourse", map, new FunctionCallback<String>() {
            @Override
            public void done(String result, ParseException parseException) {
                if (parseException == null) {
                    Intent intent = new Intent(CourseActivity.this, MyCourses.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d("COURSE UNSUBSCRIPTION", parseException.getMessage());
                }
            }
        });
    }

    private void setProfessorName() {
        HashMap<String, String> map = new HashMap<>();
        map.put("getCourseName", course);
        ParseCloud.callFunctionInBackground("getProfessorFromCourse", map, new FunctionCallback<String>() {
            @Override
            public void done(String result, ParseException parseException) {
                if (parseException == null) {
                    professorName.setText("Professor: " + result);
                } else {
                    Log.d("COURSE NOT FOUND", parseException.getMessage());
                }
            }
        });
    }
}
