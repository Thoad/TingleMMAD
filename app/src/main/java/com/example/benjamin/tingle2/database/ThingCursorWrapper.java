package com.example.benjamin.tingle2.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.benjamin.tingle2.Thing;

import java.util.Calendar;
import java.util.Date;

public class ThingCursorWrapper extends CursorWrapper {
    public ThingCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Thing getThing(){
        int id = getInt(getColumnIndex(TingleDBSchema.ThingTable.Cols.ID));
        String what = getString(getColumnIndex(TingleDBSchema.ThingTable.Cols.WHAT));
        String where = getString(getColumnIndex(TingleDBSchema.ThingTable.Cols.WHERE));
        Long unixDate = getLong(getColumnIndex(TingleDBSchema.ThingTable.Cols.DATE));
            Date date = new Date(unixDate); // Conversion to date object

        return new Thing(id, what, where, date);
    }

}
