package it.gristeliti.smartu.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.gristeliti.smartu.R;
import it.gristeliti.smartu.utils.CourseFollowed;

/**
 * Da questa Activity è possibile  selezionare i corsi che l'utente vuole
 * seguire. Visualizza i corsi attualmente seguiti. Cliccando sui corsi
 * seguiti è possibile rimuoverli.
 *
 * Usa list view per visualizzare i corsi seguiti. Ascolta il click degli
 * item per gestire la loro rimozione. Sfrutta il concetto di channel di Parse
 * per segnarsi/togliersi da un corso.
 */
public class MyCourses extends AppCompatActivity {

    private ListView myCoursesListView;
    public static final String COURSE_KEY = "COURSE";
    private ProgressBar progressBar;
    private TextView messageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycourses);

        messageTextView = (TextView)findViewById(R.id.warning_message_txt);

        progressBar = (ProgressBar)findViewById(R.id.progress_bar_unregistration);

        myCoursesListView = (ListView)findViewById(R.id.mycourses_listview);
        myCoursesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                CourseFollowed courseFollowed = (CourseFollowed) adapterView.getItemAtPosition(position);
                String course = courseFollowed.getCourse();
                Intent intent = new Intent(MyCourses.this, CourseActivity.class);
                intent.putExtra(COURSE_KEY, course);
                startActivity(intent);
                //finish();
            }
        });
    }

    private void queryCourses() {
        HashMap<String, String> map = new HashMap<>();
        map.put("getUserObjId", ParseUser.getCurrentUser().getObjectId());
        ParseCloud.callFunctionInBackground("getMyCourses", map, new FunctionCallback<ArrayList<String>>() {
            @Override
            public void done(ArrayList<String> result, ParseException parseException) {
                if (parseException == null) {
                    if(result.size() == 0) {
                        messageTextView.setVisibility(View.VISIBLE);
                    }
                    extractCourses(result);
                    progressBar.setVisibility(View.GONE);
                } else {
                    Log.d("MY COURSE", "Error querying courses ");
                }
            }
        });
    }

    private void extractCourses(ArrayList<String> courses) {
        List<CourseFollowed> coursesFollowed = new ArrayList<>();
        for(String course : courses) {
            CourseFollowed courseNotFollowed = new CourseFollowed(course);
            coursesFollowed.add(courseNotFollowed);
        }
        myCoursesListView.setAdapter(new ListAdapter(MyCourses.this, coursesFollowed));
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

    private static class ListAdapter extends ArrayAdapter<CourseFollowed> {

        public ListAdapter(Context context, List<CourseFollowed> items) {
            super(context, R.layout.mycourses_row, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if(convertView == null) {
                // inflate the GridView item layout
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.mycourses_row, parent, false);

                if (position % 2 == 0) {
                    convertView.setBackgroundResource(R.color.main_data_color);
                }

                // initialize the view holder
                viewHolder = new ViewHolder();
                viewHolder.course = (TextView) convertView.findViewById(R.id.my_course_txt);
                convertView.setTag(viewHolder);
            } else {
                // recycle the already inflated view
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // update the item view
            CourseFollowed item = getItem(position);
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
