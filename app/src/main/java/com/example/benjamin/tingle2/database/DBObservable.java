package com.example.benjamin.tingle2.database;

import java.util.Observable;

/**
 * Created by Benjamin on 4/1/2016.
 */
public class DBObservable extends Observable {
    private boolean invalid = false;
    private static DBObservable observer;

    public static DBObservable get() {
        if (observer == null) {
            observer= new DBObservable();
        }
        return observer;
    }
    private DBObservable(){ }

    public void notifyObs(){
        notifyObservers();
    }

    public void invalidate(){
        invalid = true;
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
