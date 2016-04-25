package com.example.benjamin.tingle2;

import java.util.Calendar;
import java.util.Date;

public class Thing implements Comparable<Thing> {
    private int mId;
    private String mWhat = null;
    private String mWhere = null;
    private Date mDate = null;

    public Thing(String what, String where) {
        mWhat = what;
        mWhere = where;
        mDate = Calendar.getInstance().getTime();
    }
    public Thing(int id, String what, String where, Date date) {
        mId = id;
        mWhat = what;
        mWhere = where;
        mDate = date;
    }
    @Override
    public String toString() { return oneLine("Item: ","is here: "); }
    public int getId(){ return mId; }
    public String getWhat() { return mWhat; }
    public void setWhat(String what) { mWhat = what; }
    public String getWhere() { return mWhere; }
    public void setWhere(String where) { mWhere = where; }
    public Date getDate(){ return mDate; }
    public String oneLine(String pre, String post) {
        return pre+mWhat + " "+post + mWhere;
    }

    @Override
    public int compareTo(Thing another) {
        return mDate.compareTo(another.mDate);
    }
}
