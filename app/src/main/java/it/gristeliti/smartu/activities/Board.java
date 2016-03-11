package it.gristeliti.smartu.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.gristeliti.smartu.R;
import it.gristeliti.smartu.utils.BoardMessage;

public class Board extends AppCompatActivity {

    private Button sendButton;
    private EditText insertText;
    private ListView listView;

    private ProgressBar progressBar;

    private String course;

    private final Handler h = new Handler();
    private final int delay = 10000; //milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.listView);

        progressBar = (ProgressBar)findViewById(R.id.progress_bar_board);

        Intent intent = getIntent();
        course = intent.getStringExtra(MyCourses.COURSE_KEY);

        insertText=(EditText)findViewById(R.id.editText);
        sendButton = (Button)findViewById(R.id.button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                querySendMessage(insertText.getText().toString(), course, ParseUser.getCurrentUser().getObjectId());
                insertText.getText().clear();
            }
        });
    }

    protected void onStart() {
        super.onStart();

        queryUpdateBoard(course);

        h.postDelayed(new Runnable() {
            public void run() {
                queryUpdateBoard(course);
                h.postDelayed(this, delay);
            }
        }, delay);
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    private void queryUpdateBoard(String course) {
        HashMap<String, String> map = new HashMap<>();
        map.put("getName", course);
        final ArrayList<ParseObject> messages=new ArrayList<ParseObject>();
        ParseCloud.callFunctionInBackground("getMessages", map, new FunctionCallback<ArrayList<String>>() {
            @Override
            public void done(ArrayList<String> result, ParseException parseException) {
                if (parseException == null) {
                    progressBar.setVisibility(View.GONE);
                    extractMessages(result);
                } else {
                    Log.d("BOARD", parseException.getMessage());
                }
            }
        });
    }

    private void extractMessages(ArrayList<String> messages) {
        List<BoardMessage> boardMessageList = new ArrayList<>();
        for(String mex : messages)  {
            String[] splitted = mex.split(";");
            BoardMessage boardMessage = new BoardMessage(splitted[0], splitted[1], splitted[2]);
            boardMessageList.add(boardMessage);
        }
        listView.setAdapter(new ListAdapter(Board.this, boardMessageList));
    }

    private void querySendMessage(String message, String course, String userObjId) {
        HashMap<String, String> map = new HashMap<>();
        map.put("getCourseName", course);
        map.put("getMessage", message);
        map.put("getUserObjId", userObjId);
        ParseCloud.callFunctionInBackground("sendMessage", map, new FunctionCallback<String>() {
            @Override
            public void done(String result, ParseException parseException) {
                if (parseException == null) {
                } else {
                    Toast.makeText(Board.this, parseException.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private static class ListAdapter extends ArrayAdapter<BoardMessage> {

        public ListAdapter(Context context, List<BoardMessage> items) {
            super(context, R.layout.board_row, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if(convertView == null) {
                // inflate the GridView item layout
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.board_row, parent, false);

                // initialize the view holder
                viewHolder = new ViewHolder();
                viewHolder.nickname = (TextView) convertView.findViewById(R.id.nickname_txt);
                viewHolder.message = (TextView) convertView.findViewById(R.id.message_txt);
                viewHolder.date = (TextView) convertView.findViewById(R.id.date_txt);
                convertView.setTag(viewHolder);
            } else {
                // recycle the already inflated view
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // update the item view
            BoardMessage item = getItem(position);
            viewHolder.nickname.setText(item.getNickname());
            viewHolder.message.setText(item.getMessage());
            viewHolder.date.setText(item.getDate());

            return convertView;
        }

        /**
         * The view holder design pattern prevents using findViewById()
         * repeatedly in the getView() method of the adapter.
         */
        private static class ViewHolder {
            TextView nickname;
            TextView message;
            TextView date;
        }
    }
}
