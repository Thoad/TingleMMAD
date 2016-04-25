package com.example.benjamin.tingle2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.benjamin.tingle2.database.TingleBaseHelper;
import com.example.benjamin.tingle2.interfaces.OnListFragmentInteractionListener;

import java.util.Observable;
import java.util.Observer;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */

public class ListFragment extends Fragment implements Observer, OnListFragmentInteractionListener {

    // TODO: Customize parameters
    private int mColumnCount = 1;

    // Database
    private Context mContext;
    private TingleBaseHelper mDBHelper;
    private SQLiteDatabase mDatabase;

    // stuff
    private OnListFragmentInteractionListener mListener;
    private RecyclerView.Adapter mAdapter;

    // Views

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity().getApplicationContext();
        mDBHelper = new TingleBaseHelper(mContext);
        mDatabase = mDBHelper.getWritableDatabase();
        mDBHelper.getNotifyer().addObserver(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDBHelper.getNotifyer().deleteObserver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thing_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mAdapter = new MyThingRecyclerViewAdapter(getContext(), mDBHelper.getThings(mDatabase), mListener);
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = this;
        /* Not the activity in which this fragment lives, but this as listener
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void update(Observable observable, Object data) {

        ((MyThingRecyclerViewAdapter) mAdapter).getThingsAgain(mDBHelper, mDatabase);
        mAdapter.notifyDataSetChanged();
        System.out.println("Am i being Called? Yes, But how do i update myself?!? Barcode");
    }

    @Override
    public void onListFragmentInteraction(Thing thing) {
        mDBHelper.deleteThing(thing, mDatabase);
        System.out.println("Thing has been deleted from fragment! with id: " + thing.getId());
    }

}
