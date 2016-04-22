package com.example.benjamin.tingle2;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.benjamin.tingle2.database.TingleBaseHelper;
import com.example.benjamin.tingle2.networking.JsonConvert;
import com.example.benjamin.tingle2.networking.OutpanFetcher;

import org.json.JSONException;

import java.io.IOException;
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
    private TextView lastAdded, noServiceTextview;
    private EditText newWhat, newWhere;

    // Database
    private Context mContext;
    private TingleBaseHelper mDBHelper;
    private SQLiteDatabase mDatabase;

    // Parent view
    private View mParentView;

    // Toast layout
    View toastLayout;

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
        // Toast layout
        toastLayout = inflater.inflate(R.layout.no_internet_toast, (ViewGroup) getActivity().findViewById(R.id.toastLayout));

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
        newWhat = (EditText) mParentView.findViewById(R.id.what_text);
        newWhere = (EditText) mParentView.findViewById(R.id.where_text);
        noServiceTextview = (TextView) toastLayout.findViewById(R.id.noServiceTextview);

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
                // String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                newWhat.setText(contents);
                newWhat.invalidate();

                // Do Outpan Search on another thread
                Toast.makeText(getActivity().getApplicationContext(), "Checking code online", Toast.LENGTH_SHORT).show();

                FetchNetworkItemsTask fetcher = new FetchNetworkItemsTask(getContext(), contents);
                fetcher.execute();

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



    /**
     * InnerClass to run networking and JSONConvert in worker thread
     */
    public class FetchNetworkItemsTask extends AsyncTask<Void,Void,String> {
        Context mContext = null;
        String mCode = null;
        Exception mException = null;

        public FetchNetworkItemsTask(Context context, String code){
            mContext = context;
            mCode = code;
        }

        @Override
        protected String doInBackground(Void... params) {
            String itemName = null;
            try {
                OutpanFetcher of = new OutpanFetcher(mContext);
                String jsonstring = of.getUrlString(mCode);
                if (jsonstring == null){ throw new IOException("Result is empty"); }
                JsonConvert jc = new JsonConvert();
                itemName = jc.parseJsonString(jsonstring);

            } catch (IOException | JSONException ioe) {
                mException = ioe;
            }

            return itemName;
        }

        @Override
        protected void onPostExecute(String itemName){
            if (mException != null){
                // Display network error message
                mException.printStackTrace();
                System.out.println("Show error dialog / toast etc. that reflect right Exception");

                // Display toast with exception
                if (mException instanceof IOException){
                    noServiceTextview.setText("Problem with network connection");
                }else if(mException instanceof JSONException){
                    noServiceTextview.setText("Problem with outpan response JSON conversion");
                }else{
                    noServiceTextview.setText("Unknown problem... sucks.");
                }

                displayOutanFailureToast();

                return;
            }else{
                if (!uselessNameChecker(itemName)) {
                    noServiceTextview.setText("No data from Outpan");
                    displayOutanFailureToast();
                    return;
                }

                newWhat.setText(itemName);
            }
        }

        /**
         * Check is a name is useless
         * @param name string to check
         * @return True if name is USEFULL, false if name is USELESS
         */
        private boolean uselessNameChecker(String name){
            String[] uselessNames = {"null", "", "nil", "no data", " "};

            for (String uselessName: uselessNames) {
                if (name.equals(uselessName)){
                    return false;
                }
            }
            return true;
        }

        private void displayOutanFailureToast(){
            // Display error toast
            Toast toast = new Toast(getActivity().getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(toastLayout);
            toast.show();
        }


    }
}
