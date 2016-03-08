package it.gristeliti.smartu.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import it.gristeliti.smartu.R;

public class Board extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView listView = (ListView)findViewById(R.id.listView);
        String [] array = {"Antonio","Giovanni","Michele","Giuseppe", "Leonardo", "Alessandro"};
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this, R.layout.row, R.id.textViewRow, array);
        listView.setAdapter(arrayAdapter);

        queryUpdateBoard("Data Management");
    }

    private void queryUpdateBoard(String course) {
        HashMap<String, String> map = new HashMap<>();
        map.put("getName", course);
        ParseCloud.callFunctionInBackground("getMessages", map, new FunctionCallback<HashMap<ParseObject,String>>() {
            @Override
            public void done(HashMap<ParseObject,String> objects, ParseException parseException) {
                if (parseException == null) {
                    Log.d("BOARD", "update ok");
                    String result = "";
                    Iterator it = objects.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        it.remove(); // avoids a ConcurrentModificationException
                        result+=pair.getKey() + " = " + pair.getValue()+"|||||";
                    }
                    Log.d("BOARD", result);
                } else {
                    //Toast.makeText(Board.this, "Error board", Toast.LENGTH_SHORT).show();
                    Log.d("BOARD", parseException.getMessage());
                }
            }
        });
    }
}
