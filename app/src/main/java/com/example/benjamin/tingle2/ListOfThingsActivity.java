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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH;

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

        setContentView(R.layout.activity_thing_list);

        // Views
        searchBar = (EditText) findViewById(R.id.search_field);
        searchBar.setSingleLine(true);

        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == IME_ACTION_SEARCH){
                    String searchString = v.getText().toString();
                    List<Thing> searchResult = searchList(searchString, mDBHelper.getThings(mDatabase));    // searchlist
                    Collections.sort(searchResult); // Sort the list in ascending natural order based on date added

                    ListFragment fragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.list_container);
                    fragment.updateSearchResults(searchResult);
                    return true;
                }
                return false;
            }
        });

        FragmentManager fm = getSupportFragmentManager();
        Fragment listFragment = fm.findFragmentById(R.id.list_container);
        if (listFragment == null) {
            listFragment = new ListFragment();
            fm.beginTransaction()
                    .add(R.id.list_container, listFragment)
                    .commit();

        }
    }

    /**
     * Weak matching of strings
     * @param searchString String to search for
     * @param source The source List of Strings to search in
     * @return A List of passed strings
     */
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
