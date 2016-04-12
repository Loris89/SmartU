package it.gristeliti.smartu.managers;

import android.util.Log;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.HashMap;

public class QueriesManager {

    public synchronized static void queryLecture(String classroomLabel, final TextView textView) {
        HashMap<String, String> map = new HashMap<>();
        map.put("getLabel", classroomLabel);
        ParseCloud.callFunctionInBackground("getCurrentLesson", map, new FunctionCallback<String>() {
            @Override
            public void done(String result, ParseException parseException) {
                if (parseException == null) {
                    textView.setText(result);
                } else {
                    textView.setText("No lecture found");
                    Log.e("Queries Manager: ", parseException.getMessage());
                }
            }
        });
    }

    public synchronized static void queryNextLecture(String classroomLabel, final TextView textView) {
        HashMap<String, String> map = new HashMap<>();
        map.put("getLabel", classroomLabel);
        ParseCloud.callFunctionInBackground("getNextLesson", map, new FunctionCallback<String>() {
            @Override
            public void done(String result, ParseException parseException) {
                if (parseException == null) {
                    textView.setText(result);
                } else {
                    textView.setText("No lecture");
                    Log.e("Queries Manager: ", parseException.getMessage());
                }
            }
        });
    }

    public synchronized static void queryPrevoiusLecture(String classroomLabel, final TextView textView) {
        HashMap<String, String> map = new HashMap<>();
        map.put("getLabel", classroomLabel);
        ParseCloud.callFunctionInBackground("getPreviousLesson", map, new FunctionCallback<String>() {
            @Override
            public void done(String result, ParseException parseException) {
                if (parseException == null) {
                    textView.setText(result);
                } else {
                    textView.setText("No lecture");
                    Log.e("Queries Manager: ", parseException.getMessage());
                }
            }
        });
    }

    public synchronized static void querySeats(String classroomLabel, final TextView textView) {
        HashMap<String, String> map = new HashMap<>();
        map.put("getClassroomName", classroomLabel);
        ParseCloud.callFunctionInBackground("getSeats", map, new FunctionCallback<Integer>() {
            @Override
            public void done(Integer result, ParseException parseException) {
                if (parseException == null) {
                    textView.setText(String.valueOf(result));
                } else {
                    textView.setText("Error");
                    Log.e("Queries Manager: ", parseException.getMessage());
                }
            }
        });
    }

    public synchronized static void queryCourse(String classroom, final TextView professorTextView, final TextView courseTextView) {
        HashMap<String, String> map = new HashMap<>();
        map.put("getClassroomName", classroom);
        ParseCloud.callFunctionInBackground("getCourseFromClassroom", map, new FunctionCallback<ParseObject>() {
            @Override
            public void done(ParseObject result, ParseException parseException) {
                if (parseException == null) {
                    ParseUser professor = result.getParseUser("Professor");
                    try {
                        professorTextView.setText(professor.fetchIfNeeded().getString("surname"));
                    } catch (ParseException e) {
                        Log.e("Queries Manager Prof", e.getMessage());
                    }
                    courseTextView.setText(result.getString("name"));
                } else {
                    Log.e("Queries Manager", parseException.getMessage());
                    professorTextView.setText("No Professor");
                    courseTextView.setText("No Lecture");
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
                    textView.setText(result);
                } else {
                    textView.setText(parseException.getMessage());
                    Log.e("Queries Manager", parseException.getMessage());
                }
            }
        });
    }

    public synchronized static void queryNumberOfStudents(String classroom, final TextView textView) {
        HashMap<String, String> map = new HashMap<>();
        map.put("getClassroomName", classroom);
        ParseCloud.callFunctionInBackground("getStudentsNumber", map, new FunctionCallback<Integer>() {
            @Override
            public void done(Integer result, ParseException parseException) {
                if (parseException == null) {
                    textView.setText(String.valueOf(result));
                } else {
                    textView.setText("Error");
                    Log.e("Queries Manager", parseException.getMessage());
                }
            }
        });
    }

    public synchronized static void sendNoise(String classroom, int noise) {
        HashMap<String, String> map = new HashMap<>();
        map.put("getClassroomName", classroom);
        map.put("getNoise", String.valueOf(noise));
        ParseCloud.callFunctionInBackground("setNoise", map, new FunctionCallback<String>() {
            @Override
            public void done(String result, ParseException parseException) {
                if (parseException == null) {

                } else {
                    Log.e("Queries Manager", parseException.getMessage());
                }
            }
        });
    }

    public synchronized static void gueryNoise(String classroom, final TextView textView) {
        HashMap<String, String> map = new HashMap<>();
        map.put("getClassroomName", classroom);
        ParseCloud.callFunctionInBackground("getNoise", map, new FunctionCallback<String>() {
            @Override
            public void done(String result, ParseException parseException) {
                if (parseException == null) {
                    textView.setText(result);
                } else {
                    textView.setText("Error");
                    Log.e("Queries Manager", parseException.getMessage());
                }
            }
        });
    }
}
