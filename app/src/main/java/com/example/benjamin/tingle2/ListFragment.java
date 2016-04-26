package com.example.benjamin.tingle2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.benjamin.tingle2.database.TingleBaseHelper;
import com.example.benjamin.tingle2.interfaces.OnListFragmentInteractionListener;

import java.text.DateFormat;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */

public class ListFragment extends Fragment implements Observer, OnListFragmentInteractionListener {

    private int mColumnCount = 1;

    // Database
    private Context mContext;
    private TingleBaseHelper mDBHelper;
    private SQLiteDatabase mDatabase;

    // stuff
    private OnListFragmentInteractionListener mListener;
    private RecyclerView.Adapter mAdapter;

    // Views
    Dialog thingDialog;

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
        ((MyThingRecyclerViewAdapter) mAdapter).setListContents(mDBHelper, mDatabase);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onListFragmentInteraction(Thing thing) {
        thingDialog = makeDialog(thing);
        thingDialog.show();
    }

    /**
     * Set mValues in Adapter to search results in {@param things}
     * @param things search results
     */
    public void updateSearchResults(List<Thing> things){
        ((MyThingRecyclerViewAdapter) mAdapter).setListContentsFromList(things);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Fetch data from default datasource
     */
    public void simpleUpdate(){
        ((MyThingRecyclerViewAdapter) mAdapter).setListContents(mDBHelper, mDatabase);
        mAdapter.notifyDataSetChanged();
    }
    private Dialog makeDialog(Thing thing){
        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.thing_dialog, null);

        TextView tvId = (TextView) view.findViewById(R.id.thing_id_tview);
            tvId.setText(String.format("%d",thing.getId()));
        TextView tvDate = (TextView) view.findViewById(R.id.thing_date_tview);
            DateFormat dformat = DateFormat.getDateTimeInstance();
            tvDate.setText(dformat.format(thing.getDate()));
        TextView tvWhat = (TextView) view.findViewById(R.id.thing_what_tview);
            tvWhat.setText(thing.getWhat());
        TextView tvWhere = (TextView) view.findViewById(R.id.thing_where_tview);
            tvWhere.setText(thing.getWhere());

        ImageButton iButton = (ImageButton) view.findViewById(R.id.delete_thing_ibutton);
            iButton.setOnClickListener(new MyClickListener(thing));

        ab.setTitle("Details!");
        ab.setView(view);

        return ab.create();
    }

    /**
     * Class to provide an OnClickListener that takes an argument
     */
    private class MyClickListener implements View.OnClickListener{
        private Thing thing;

        public MyClickListener(Thing thing){
            this.thing = thing;
        }
        @Override
        public void onClick(View v) {
            mDBHelper.deleteThing(thing, mDatabase);
            displayDeleteMessage(thing);
            thingDialog.dismiss();
            System.out.println("Thing has been deleted from fragment! with id: " + thing.getId());
        }
    }

    private void displayDeleteMessage(Thing thing){
        // Create the Snackbar
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.list_coordinator_layout), "", Snackbar.LENGTH_LONG);
        // Get the Snackbar's layout view
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        // Hide the text
        TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);

        // Inflate our custom view
        View snackView = getActivity().getLayoutInflater().inflate(R.layout.no_internet_layout, null);
        TextView tv = (TextView) snackView.findViewById(R.id.noServiceTextview);
        ImageView iv = (ImageView) snackView.findViewById(R.id.imageView);

        tv.setText("\"" + thing.getWhat() + "\" was deleted");
        iv.setImageResource(R.drawable.all_good5050);

        // Add the view to the Snackbar's layout
        layout.addView(snackView, 0);
        snackbar.show();
    }

}

