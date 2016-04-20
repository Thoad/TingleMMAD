package com.example.benjamin.tingle2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.benjamin.tingle2.interfaces.OnListFragmentInteractionListener;                // Note to self; IDE puts dummy code in sneaky places.
import com.example.benjamin.tingle2.database.TingleBaseHelper;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Thing} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyThingRecyclerViewAdapter extends RecyclerView.Adapter<MyThingRecyclerViewAdapter.ViewHolder> {

    // Database

    private List<Thing> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyThingRecyclerViewAdapter(Context context, List<Thing> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    public void getThingsAgain(TingleBaseHelper dbH, SQLiteDatabase db){
        mValues = dbH.getThings(db);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_thing, parent, false); // Change from fragment_item
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mWhatView.setText(mValues.get(position).getWhat());
        holder.mWhereView.setText(mValues.get(position).getWhere());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mWhatView;
        public final TextView mWhereView;
        public Thing mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mWhatView = (TextView) view.findViewById(R.id.What);
            mWhereView = (TextView) view.findViewById(R.id.Where);
        }

        @Override
        public String toString() {
            //return super.toString() + " '" + mWhereView.getText() + "'";
            return super.toString() + " WTF?!? ViewAdapter";
        }
    }
}