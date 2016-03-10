package it.gristeliti.smartu.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.gristeliti.smartu.R;
import it.gristeliti.smartu.utils.BoardMessage;
import it.gristeliti.smartu.utils.CourseNotFollowed;

public class CoursesRegistration extends AppCompatActivity {

    private ListView coursesList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_registration);

        progressBar = (ProgressBar)findViewById(R.id.progress_bar_registration);

        coursesList = (ListView)findViewById(R.id.courses_listview);
        coursesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                CourseNotFollowed courseNotFollowed = (CourseNotFollowed)adapterView.getItemAtPosition(position);
                String course = courseNotFollowed.getCourse();
                registerToCourse(course);
            }
        });
    }

    private void registerToCourse(final String course) {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, String> map = new HashMap<>();
        map.put("getUserObjId", ParseUser.getCurrentUser().getObjectId());
        map.put("getCourseName", course);
        ParseCloud.callFunctionInBackground("registerToCourse", map, new FunctionCallback<String>() {
            @Override
            public void done(String result, ParseException parseException) {
                if (parseException == null) {
                    queryCourses();
                } else {
                    Log.d("COURSE REGISTRATION", parseException.getMessage());
                }
            }
        });
    }

    private void queryCourses() {
        Log.d("COURSE REGISTRAION", "queryCourses() called");
        HashMap<String, String> map = new HashMap<>();
        map.put("getUserObjId", ParseUser.getCurrentUser().getObjectId());
        ParseCloud.callFunctionInBackground("possibleCourses", map, new FunctionCallback<ArrayList<String>>() {
            @Override
            public void done(ArrayList<String> result, ParseException parseException) {
                if (parseException == null) {
                    Log.d("COURSE REGISTRATION", "Size: " + result.size());
                    extractCourses(result);
                    progressBar.setVisibility(View.GONE);
                } else {
                    Log.d("COURSE REGISTRATION", "Error querying courses");
                }
            }
        });
    }

    private void extractCourses(ArrayList<String> courses) {
        List<CourseNotFollowed> coursesNotFollowed = new ArrayList<>();
        for(String course : courses) {
            Log.d("COURSE REGISTRATION", course);
            CourseNotFollowed courseNotFollowed = new CourseNotFollowed(course);
            coursesNotFollowed.add(courseNotFollowed);
        }
        coursesList.setAdapter(new ListAdapter(CoursesRegistration.this, coursesNotFollowed));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // get the courses
        queryCourses();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private static class ListAdapter extends ArrayAdapter<CourseNotFollowed> {

        public ListAdapter(Context context, List<CourseNotFollowed> items) {
            super(context, R.layout.course_registration_row, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if(convertView == null) {
                // inflate the GridView item layout
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.course_registration_row, parent, false);

                // initialize the view holder
                viewHolder = new ViewHolder();
                viewHolder.course = (TextView) convertView.findViewById(R.id.course_registration_txt);
                convertView.setTag(viewHolder);
            } else {
                // recycle the already inflated view
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // update the item view
            CourseNotFollowed item = getItem(position);
            viewHolder.course.setText(item.getCourse());

            return convertView;
        }

        /**
         * The view holder design pattern prevents using findViewById()
         * repeatedly in the getView() method of the adapter.
         */
        private static class ViewHolder {
            TextView course;
        }
    }
}
