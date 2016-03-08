package it.gristeliti.smartu.managers;

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
                }
            }
        });
    }

    /**
     * Retrieves the number of students from the given classroom
     */
    public synchronized static void queryNumberOfStudents(String classroomLabel, final TextView textView) {
        HashMap<String, String> map = new HashMap<>();
        map.put("getClassroom", classroomLabel);
        ParseCloud.callFunctionInBackground("getStudentsNumber", map, new FunctionCallback<Integer>() {
            @Override
            public void done(Integer result, ParseException parseException) {
                if (parseException == null) {
                    textView.setText("Number of students: " + String.valueOf(result));
                } else {
                    //Toast.makeText(Classroom.this, "Errore query numero studenti", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Retrieves the number of seats from the given classroom
     */
    public synchronized static void queryNumberOfSeats(String classroomLabel, final TextView textView) {
        HashMap<String, String> map = new HashMap<>();
        map.put("getSeats", classroomLabel);
        ParseCloud.callFunctionInBackground("getSeatsNumber", map, new FunctionCallback<Integer>() {
            @Override
            public void done(Integer result, ParseException parseException) {
                if (parseException == null) {
                    textView.setText("Number of seats: " + String.valueOf(result));
                } else {
                    //Toast.makeText(Classroom.this, "Errore query numero posti", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Retrieve the average noise of the given classroom
     */
    public synchronized static void queryAverageNoise(String classroomLabel, final TextView textView) {
        HashMap<String, String> map = new HashMap<>();
        map.put("getNoise", classroomLabel);
        ParseCloud.callFunctionInBackground("getClassroomNoise", map, new FunctionCallback<Object>() {
            @Override
            public void done(Object result, ParseException parseException) {
                if (parseException == null) {
                    if (result instanceof Integer) {
                        int res = (int) result;
                        textView.setText("Average Noise: " + String.valueOf(res) + " dB");
                    }
                    if (result instanceof Double) {
                        double res = (double) result;
                        int round = (int) Math.round(res);
                        textView.setText("Average Noise: " + String.valueOf(res) + " dB");
                    }
                } else {
                    //Toast.makeText(Classroom.this, "Errore query rumore medio", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
