package com.example.benjamin.tingle2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.benjamin.tingle2.database.TingleBaseHelper;

public class ListOfThingsActivity extends AppCompatActivity implements ListFragment.OnListFragmentInteractionListener{

    // Database
    private Context mContext;
    private TingleBaseHelper mDBHelper;
    private SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mDBHelper = new TingleBaseHelper(mContext);
        mDatabase = mDBHelper.getWritableDatabase();

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

    @Override
    public void onListFragmentInteraction(Thing thing) {
        mDBHelper.deleteThing(thing, mDatabase);

        System.out.println("Thing has been deleted!");

        // Update / redraw view
        //refreshFragment();
    }

    private void refreshFragment(){
        Fragment fragment = new ListFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.list_container, fragment).commit();
    }
}
