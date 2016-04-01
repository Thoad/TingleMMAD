package com.example.benjamin.tingle2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.benjamin.tingle2.Thing;
import com.example.benjamin.tingle2.database.TingleDBSchema.ThingTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin on 3/30/2016.
 * THIS IS CHANGE
 */
public class TingleBaseHelper extends SQLiteOpenHelper{
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "thingsBase.db";
    private DBObservable notifyer = null;

    public TingleBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
        notifyer = DBObservable.get();
    }

    public DBObservable getNotifyer(){
        return notifyer;
    }

    public static ContentValues toContentValue(Thing thing){
        ContentValues value = new ContentValues();
        value.put(ThingTable.Cols.WHAT, thing.getWhat());
        value.put(ThingTable.Cols.WHERE, thing.getWhere());

        return value;
    }

    /**
     * Add the givin thing to the givin database, by transforming to ContentValues first.
     * No exception handling... at all :)
     * @param thing
     * @param db
     */
    public void addThing(Thing thing, SQLiteDatabase db){
        ContentValues value = toContentValue(thing);

        db.insert(ThingTable.NAME, null, value);

        notifyer.invalidate();
        notifyer.notifyObs();
    }

    public void updateThing(Thing thing, SQLiteDatabase db){
        String what = thing.getWhat();
        ContentValues value = toContentValue(thing);

        db.update(ThingTable.NAME,
                value,
                ThingTable.Cols.WHAT + " = ?",
                new String[]{what});

        notifyer.invalidate();
        notifyer.notifyObs();
    }

    public void deleteThing(Thing thing, SQLiteDatabase db){
        String what = thing.getWhat();
        db.delete(ThingTable.NAME,
                  ThingTable.Cols.WHAT + " = ?",
                  new String[]{what} );

        notifyer.invalidate();
        notifyer.notifyObs();
    }

    public ThingCursorWrapper queryThings(String whereClause, String[] whereArgs, SQLiteDatabase db){
        ThingCursorWrapper cs = queryThingsPrivate(whereClause, whereArgs, db);
        return cs;
    }
    private ThingCursorWrapper queryThingsPrivate(String whereClause, String[] whereArgs, SQLiteDatabase db){
        Cursor cursor = db.query(
                ThingTable.NAME,
                null, //Colums - null selects all colums
                whereClause,
                whereArgs,
                null, // groupby
                null, // having
                null  // orderby
        );
        return new ThingCursorWrapper(cursor);
    }

    public Thing getThing(String what, SQLiteDatabase db){
        ThingCursorWrapper cursor = queryThingsPrivate(
                ThingTable.Cols.WHAT + " = ?",
                new String[] {what},
                db
        );

        try{
            if (cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getThing();
        }
        finally {
            cursor.close();
        }
    }

    public List<Thing> getThings(SQLiteDatabase db){
        List<Thing> things = new ArrayList<>();

        ThingCursorWrapper cursor = queryThingsPrivate(null, null, db);

        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                things.add(cursor.getThing());
                cursor.moveToNext();
            }
        }
        finally {
            cursor.close();
        }
        return things;
    }




    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table " + ThingTable.NAME +
                        "(" + " _id integer primary key autoincrement, " +
                        ThingTable.Cols.WHAT + ", " +
                        ThingTable.Cols.WHERE +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }

}
