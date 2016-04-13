package com.example.benjamin.tingle2;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.example.benjamin.tingle2.database.TingleBaseHelper;

import java.util.List;
import java.util.Observable;
import java.util.Observer;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TingleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TingleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TingleFragment extends Fragment implements Observer {

    // GUI variables
    private Button addThing, scanButton, showThings;
    private TextView lastAdded, newWhat, newWhere;

    // Database
    private Context mContext;
    private TingleBaseHelper mDBHelper;
    private SQLiteDatabase mDatabase;

    private View mParentView;

    // Listener
    private OnFragmentInteractionListener mListener;


    public TingleFragment() { /* Required empty public constructor */ }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TingleFragment.
     */
    public static TingleFragment newInstance() {
        TingleFragment fragment = new TingleFragment();
        return fragment;
    }

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
        // Inflate the layout for this fragment
        mParentView = inflater.inflate(R.layout.fragment_tingle, container, false);

        //Accessing GUI element. Update last text changed
        lastAdded = (TextView) mParentView.findViewById(R.id.last_thing);
        updateUI();

        // Buttons. Get buttons
        addThing = (Button) mParentView.findViewById(R.id.add_button);
        showThings = (Button) mParentView.findViewById(R.id.show_things_button);
        scanButton = (Button) mParentView.findViewById(R.id.scan_button);

        // Textfields for describing a thing
        newWhat = (TextView) mParentView.findViewById(R.id.what_text);
        newWhere = (TextView) mParentView.findViewById(R.id.where_text);

        // Click event
        addThing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((newWhat.getText().length() >0) && (newWhere.getText().length() >0
                )){
                    mDBHelper.addThing( new Thing(
                                    newWhat.getText().toString(),
                                    newWhere.getText().toString()
                            ), mDatabase
                    );
                    newWhat.setText(""); newWhere.setText("");
                    updateUI();
                }
            }
        });

        showThings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // Start ListActivity
                Intent intent = new Intent(getContext(), ListOfThingsActivity.class);                       // Start ListActivity / fragment the right way
                startActivity(intent);
            }
        });

        scanButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // Start scan application with implicit intent
                System.out.println("Scan pressed. OnClickListener");

                try{
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
                    startActivityForResult(intent, 0);
                }catch (ActivityNotFoundException anfe){
                    System.out.println("Scanner not found");
                    scanButton.setBackgroundColor(Color.RED);
                }

            }
        });

        return mParentView;
    }

    //@Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                // Handle successful scan
                System.out.println("Scanning action was succesfull");

                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                newWhat.setText(contents);
                mParentView.invalidate();
                 // System.out.println("This is content: " + contents + " And this is format: " + format);

            } else if (resultCode == Activity.RESULT_CANCELED) {
            // Handle cancel
                System.out.println("Scanning action was cancelled");
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void update(Observable observable, Object data) {
        updateUI();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void updateUI(){
        List<Thing> s= mDBHelper.getThings(mDatabase);
        if (s.size()>0) {
            lastAdded.setText(s.get(s.size()-1).toString());
        }
    }
}
