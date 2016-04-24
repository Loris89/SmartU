package it.gristeliti.smartu.managers;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.Date;
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

    public synchronized static void queryCourse(final String classroom, final TextView professorTextView, final TextView courseTextView) {
        HashMap<String, String> map = new HashMap<>();
        map.put("getClassroomName", classroom);
        ParseCloud.callFunctionInBackground("getCourseFromClassroom", map, new FunctionCallback<ParseObject>() {
            @Override
            public void done(ParseObject result, ParseException parseException) {
                if (parseException == null) {
                    ParseUser professor = result.getParseObject("Course").getParseUser("Professor");
                    courseTextView.setText(result.getParseObject("Course").getString("name"));
                    try {
                        professorTextView.setText(professor.fetchIfNeeded().getString("surname"));
                        // Start time
                        Calendar cal = Calendar.getInstance();
                        Date startTime =  result.getDate("start_time");
                        cal.setTime(startTime);
                        cal.add(Calendar.HOUR, -2);
                        int hours = cal.get(Calendar.HOUR_OF_DAY);
                        int min = cal.get(Calendar.MINUTE);
                        String smin = "";
                        if(min == 0) {
                            smin = "00";
                        } else {
                            smin = String.valueOf(min);
                        }
                        // --> edit textview
                        String start = hours + ":" + smin;
                        Log.d("Start lesson", start);
                        courseTextView.append("\nLesson started at: " + start);
                        // End time
                        Date endTime =  result.getDate("end_time");
                        cal.setTime(endTime);
                        cal.add(Calendar.HOUR, -2);
                        hours = cal.get(Calendar.HOUR_OF_DAY);
                        min = cal.get(Calendar.MINUTE);
                        smin = "";
                        if(min == 0) {
                            smin = "00";
                        } else {
                            smin = String.valueOf(min);
                        }
                        String end = hours + ":" + smin;
                        // --> edit textview
                        Log.d("End lesson", end);
                        courseTextView.append("\nLesson ends at: " + end);

                        // check if the professor of the lecture is in the classroom
                        isProfessorTeaching(professor.getUsername(), classroom, professorTextView);
                    } catch (ParseException e) {
                        Log.e("Queries Manager Prof", e.getMessage());
                    }
                } else {
                    Log.e("Queries Manager", parseException.getMessage());
                    professorTextView.setText("No Professor");
                    courseTextView.setText("No Lecture");
                }
            }
        });
    }

    private synchronized static void isProfessorTeaching(String professor, String classroom, final TextView professorTextView) {
        HashMap<String, String> map = new HashMap<>();
        map.put("getProfessorName", professor);
        map.put("getClassroom", classroom);
        ParseCloud.callFunctionInBackground("checkProfessor", map, new FunctionCallback<Boolean>() {
            @Override
            public void done(Boolean result, ParseException parseException) {
                if (parseException == null) {
                    if(result) {
                        professorTextView.append("\nProfessor is teaching");
                    }
                } else {
                    Log.e("Queries Manager", parseException.getMessage());
                }
            }
        });
    }

    public synchronized static void quietestClassroom(final Context context) {
        HashMap<String, String> map = new HashMap<>();
        ParseCloud.callFunctionInBackground("quietestClassroom", map, new FunctionCallback<String>() {
            @Override
            public void done(String result, ParseException parseException) {
                if (parseException == null) {
                    Toast.makeText(context, "The quietest classroom is " + result, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, parseException.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public synchronized static void emptyClassroom(final Context context) {
        HashMap<String, String> map = new HashMap<>();
        ParseCloud.callFunctionInBackground("getEmptyClassroom", map, new FunctionCallback<String>() {
            @Override
            public void done(String result, ParseException parseException) {
                if (parseException == null) {
                    Toast.makeText(context, "An empty classroom is " + result, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, parseException.getMessage(), Toast.LENGTH_LONG).show();
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

    public synchronized static void queryClassroom(String course, final Context context) {
        HashMap<String, String> map = new HashMap<>();
        map.put("getCourseName", course);
        ParseCloud.callFunctionInBackground("getClassroomFromCourse", map, new FunctionCallback<String>() {
            @Override
            public void done(String result, ParseException parseException) {
                if (parseException == null) {
                    Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                } else {
                    Log.e("Queries Manager", parseException.getMessage());
                    Toast.makeText(context, parseException.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
