package com.example.benjamin.tingle2;

import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
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
                    List<Thing> searchResult = simpleSearch(searchString, mDBHelper.getThings(mDatabase));    // searchlist
                    Collections.sort(searchResult); // Sort the list in ascending natural order based on date added

                    ListFragment fragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.list_container);
                    fragment.updateSearchResults(searchResult);
                    return true;
                }
                return false;
            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchBar.getText().length() == 0){
                    ListFragment fragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.list_container);
                    fragment.simpleUpdate();
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
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
     * Searches for the searchString in the List of strings
     * @param searchString String to search for
     * @param source The source List of Strings to search in
     * @return A List of passed strings
     */
    private List<Thing> simpleSearch(String searchString, List<Thing> source){
        List<Thing> result = new ArrayList<>();

        for (Thing thing: source) {
            if (thing.getWhat().contains(searchString) || thing.getWhere().contains(searchString)){
                result.add(thing);
            }
        }
        return result;
    }
}
