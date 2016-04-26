package com.example.benjamin.tingle2;

import android.content.Context;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.benjamin.tingle2.database.TingleBaseHelper;
import com.example.benjamin.tingle2.interfaces.OnListFragmentInteractionListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements TingleFragment.OnFragmentInteractionListener{

    private FragmentManager fm = null;
    private Fragment mListFragment = new ListFragment();
    private Fragment mTingleFragment = new TingleFragment();

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

        setContentView(R.layout.activity_tingle);

        fm = getSupportFragmentManager();

        // Put fragment into layout, create landscape view
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            // If in landscape:
            onLandscape();
        } else{
            onPortrait();
        }
    }

    private void onPortrait(){
        // Put fragment into layout, create view
        Fragment fragment = fm.findFragmentById(R.id.fragment_tingle_container);
        if (fragment == null) {
            fm.beginTransaction()
                    .replace(R.id.fragment_tingle_container, mTingleFragment)
                    .commit();
        }
    }

    private void onLandscape(){
        // Put fragment into layout, create landscape view
        Fragment fragmentLeft = fm.findFragmentById(R.id.left_fragment_container);
        if (fragmentLeft == null) {
            fm.beginTransaction()
                    .replace(R.id.left_fragment_container, mTingleFragment)
                    .commit();
        }

        Fragment fragmentRight =
                fm.findFragmentById(R.id.right_fragment_container);
        if (fragmentRight == null) {
            fm.beginTransaction()
                    .replace(R.id.right_fragment_container, mListFragment)
                    .commit();
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {
        // Doesn't get called
        System.out.println("ONFRagment");
    }
}
