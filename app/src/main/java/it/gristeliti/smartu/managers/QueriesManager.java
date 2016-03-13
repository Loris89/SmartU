package it.gristeliti.smartu.managers;

import android.util.Log;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.HashMap;

public class QueriesManager {

    /**
     * Retrieve the current Lecture of the given classroom
     **/
    public synchronized static void queryLecture(String classroomLabel, final TextView textView) {
        HashMap<String, String> map = new HashMap<>();
        map.put("getLabel", classroomLabel);
        ParseCloud.callFunctionInBackground("getCurrentLesson", map, new FunctionCallback<String>() {
            @Override
            public void done(String result, ParseException parseException) {
                if (parseException == null) {
                    textView.setText("Lecture: " + result);
                } else {
                    //Toast.makeText(Classroom.this, "Errore query lezione corrente", Toast.LENGTH_LONG).show();
                    textView.setText("Lecture: " + parseException.getMessage());
                }
            }
        });
    }

    public synchronized static void queryProfessor(String course, final TextView textView) {
        HashMap<String, String> map = new HashMap<>();
        map.put("getCourseName", course);
        ParseCloud.callFunctionInBackground("getProfessorFromCourse", map, new FunctionCallback<String>() {
            @Override
            public void done(String result, ParseException parseException) {
                if (parseException == null) {
                    textView.setText("Professor: " + result);
                } else {
                    Log.d("COURSE NOT FOUND", parseException.getMessage());
                }
            }
        });
    }
}
