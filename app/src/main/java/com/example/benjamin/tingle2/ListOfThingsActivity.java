package com.example.benjamin.tingle2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.example.benjamin.tingle2.database.TingleBaseHelper;
import com.example.benjamin.tingle2.interfaces.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;

public class ListOfThingsActivity extends AppCompatActivity {

    // Database
    private Context mContext;
    private TingleBaseHelper mDBHelper;
    private SQLiteDatabase mDatabase;

    // EditText View
    private EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mDBHelper = new TingleBaseHelper(mContext);
        mDatabase = mDBHelper.getWritableDatabase();

        // Views
        searchBar = (EditText) findViewById(R.id.search_field);
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null){
                    String searchString = v.getText().toString();


                    return true;
                }
                return false;
            }
        });

        setContentView(R.layout.activity_thing_list);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment =
                fm.findFragmentById(R.id.list_container);
        if (fragment == null) {
            fragment = new ListFragment();
            fm.beginTransaction()
                    .add(R.id.list_container, fragment)
                    .commit();
        }


    }

    private List<Thing> searchList(String searchString, List<Thing> source){
        List<Thing> result = new ArrayList<>();

        for (Thing thing: source) {
            if (thing.getWhat().equals(searchString) || thing.getWhere().equals(searchString)){
                result.add(thing);
            }
        }
        return result;
    }
}
