package com.example.benjamin.tingle2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class ThingListActivity extends AppCompatActivity implements ThingFragment.OnListFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thing_list);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment =
                fm.findFragmentById(R.id.list_container);
        if (fragment == null) {
            fragment = new ThingFragment();
            fm.beginTransaction()
                    .add(R.id.list_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onListFragmentInteraction(Thing thing) {
        ThingsDB.get(this).delete(thing);

        System.out.println("Thing has been deleted!");

        // Update / redraw view
        refreshFragment();
    }

    private void refreshFragment(){
        Fragment fragment = new ThingFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.list_container, fragment).commit();
    }
}
