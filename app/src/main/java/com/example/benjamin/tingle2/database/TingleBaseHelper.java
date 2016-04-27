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

    private static List<Thing> _things = null;

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
        value.put(ThingTable.Cols.DATE, thing.getDate().getTime()); // Convert to unix time

        return value;
    }

    /**
     * Add the given thing to the given database, by transforming to ContentValues first. Db adds id itself
     * @param thing Thing to add
     * @param db DB to add thing into
     */
    public void addThing(Thing thing, SQLiteDatabase db){
        ContentValues value = toContentValue(thing);

        db.insert(ThingTable.NAME, null, value);

        _things = getThings(db);

        notifyer.invalidate();
        notifyer.notifyObs();
    }

    public void updateThing(Thing thing, SQLiteDatabase db){
        int id = thing.getId();
        ContentValues value = toContentValue(thing);

        db.update(ThingTable.NAME,
                value,
                ThingTable.Cols.ID + " = ?",
                new String[]{Integer.toString(id)});

        notifyer.invalidate();
        notifyer.notifyObs();
    }

    public void deleteThing(Thing thing, SQLiteDatabase db){
        int id = thing.getId();
        db.delete(ThingTable.NAME,
                  ThingTable.Cols.ID + " = ?",
                  new String[]{Integer.toString(id)} );

        _things = getThings(db);

        notifyer.invalidate();
        notifyer.notifyObs();
    }

    public ThingCursorWrapper queryThings(String whereClause, String[] whereArgs, SQLiteDatabase db){
        return queryThingsPrivate(whereClause, whereArgs, db);
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

    public Thing getThing(int id, SQLiteDatabase db){
        ThingCursorWrapper cursor = queryThingsPrivate(
                ThingTable.Cols.ID + " = ?",
                new String[] {Integer.toString(id)},
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
        _things = new ArrayList<>();

        ThingCursorWrapper cursor = queryThingsPrivate(null, null, db);

        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                _things.add(cursor.getThing());
                cursor.moveToNext();
            }
        }
        finally {
            cursor.close();
        }
        return _things;
    }




    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table " + ThingTable.NAME + "(" + ThingTable.Cols.ID + " integer primary key autoincrement, " +
                        ThingTable.Cols.WHAT + ", " +
                        ThingTable.Cols.WHERE + ", " +
                        ThingTable.Cols.DATE +
                        ")"
        );
        dbInsertTestData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }

    /**
     * Populate db with dummy data
     */
    private void dbInsertTestData(SQLiteDatabase db){
        Thing[] things = new Thing[]{
            new Thing("Keys", "Desk drawer"),
            new Thing("Wrench", "Toolbox"),
            new Thing("Smartphone", "... In your hand"),
            new Thing("Glasses", "IDK"),
            new Thing("Bike", "Garage"),
            new Thing("Wallet", "In your pocket"),
            new Thing("The cake", "There is no cake, it was a lie"),
            new Thing("Donald Trump", "www.trumpdonald.org"),
            new Thing("Multiple search result", "8"),
            new Thing("Multiple search result", "7"),
            new Thing("Multiple search result", "6"),
            new Thing("Multiple search result", "5"),
            new Thing("Multiple search result", "4"),
            new Thing("Multiple search result", "3"),
            new Thing("Multiple search result", "2"),
            new Thing("Multiple search result", "1"),
        };
        for (Thing thing: things) {
            addThing(thing, db);
        }
    }

}
