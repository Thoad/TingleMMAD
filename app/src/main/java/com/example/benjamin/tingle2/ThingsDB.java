package com.example.benjamin.tingle2;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by Benjamin on 2/27/2016.
 * TO BE REMOVED
 */
public class ThingsDB extends Observable {
    private static ThingsDB sThingsDB;
    private boolean invalid = false;
    //fake database
    private List<Thing> mThingsDB;
    public static ThingsDB get(Context context) {
        if (sThingsDB == null) {
            sThingsDB= new ThingsDB(context);
        }
        return sThingsDB;
    }
    public List<Thing> getThingsDB() {return mThingsDB; }
    public void addThing(Thing thing) {
        mThingsDB.add(thing);
        invalid = true;
        notifyObservers();
    }
    public int size() {return mThingsDB.size(); }
    public Thing get(int i){ return mThingsDB.get(i); }
    // Fill database for testing purposes
    private ThingsDB(Context context) {
        mThingsDB= new ArrayList<Thing>();
        mThingsDB.add(new Thing("Android Pnone", "Desk"));
        mThingsDB.add(new Thing("Hipster hat", "Closet"));
        mThingsDB.add(new Thing("Big Nerd book", "Desk"));
        mThingsDB.add(new Thing("Pic of myself", "In Bertelsens wallet"));
        mThingsDB.add(new Thing("Delete 2 x Activities", "Report"));
        mThingsDB.add(new Thing("Delete 2 x Activities", "Report"));
        mThingsDB.add(new Thing("Delete 2 x Activities", "Report"));
        mThingsDB.add(new Thing("Delete 2 x Activities", "Report"));
        mThingsDB.add(new Thing("Delete 2 x Activities", "Report"));
        mThingsDB.add(new Thing("Delete 2 x Activities", "Report"));
        mThingsDB.add(new Thing("Delete 2 x Activities", "Report"));
        mThingsDB.add(new Thing("Delete 2 x Activities", "Report"));
        mThingsDB.add(new Thing("Delete 2 x Activities", "Report"));
        mThingsDB.add(new Thing("Delete 2 x Activities", "Report"));
        mThingsDB.add(new Thing("Delete 2 x Activities", "Report"));
        mThingsDB.add(new Thing("Delete 2 x Activities", "Report"));
        mThingsDB.add(new Thing("Delete 2 x Activities", "Report"));
        mThingsDB.add(new Thing("Delete 2 x Activities", "Report"));

    }
    public void delete(Thing thing){
        if (thing == null){return;}
        mThingsDB.remove(thing);
        invalid = true;
        notifyObservers();

    }

    @Override
    public boolean hasChanged() {
        return invalid;
    }

    @Override
    protected void clearChanged() {
        super.clearChanged();
        invalid = false;
    }
}
