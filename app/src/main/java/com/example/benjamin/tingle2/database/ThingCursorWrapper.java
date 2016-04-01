package com.example.benjamin.tingle2.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.benjamin.tingle2.Thing;

/**
 * Created by Benjamin on 3/31/2016.
 */
public class ThingCursorWrapper extends CursorWrapper {
    public ThingCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Thing getThing(){
        String what = getString(getColumnIndex(TingleDBSchema.ThingTable.Cols.WHAT));
        String where = getString(getColumnIndex(TingleDBSchema.ThingTable.Cols.WHERE));

        Thing thing = new Thing(what, where);

        return thing;
    }

}
