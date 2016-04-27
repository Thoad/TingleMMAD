package com.example.benjamin.tingle2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.benjamin.tingle2.database.TingleBaseHelper;
import com.example.benjamin.tingle2.networking.JsonConvert;
import com.example.benjamin.tingle2.networking.OutpanFetcher;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

    private String outpanFailureMessage;
    private Dialog scanDialog;

    // GUI variables
    private Button addThing, scanButton, showThings;
    private TextView lastAdded;
    private EditText newWhat, newWhere;

    // Database
    private Context mContext;
    private TingleBaseHelper mDBHelper;
    private SQLiteDatabase mDatabase;

    // Parent view
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
        newWhat = (EditText) mParentView.findViewById(R.id.what_text);
        newWhere = (EditText) mParentView.findViewById(R.id.where_text);

        // Click event
        addThing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((newWhat.getText().length() >0) && (newWhere.getText().length() >0
                )){
                    Thing thing = new Thing(
                            newWhat.getText().toString(),
                            newWhere.getText().toString()
                    );
                    mDBHelper.addThing( thing, mDatabase
                    );
                    newWhat.setText(""); newWhere.setText("");
                    updateUI();
                    displayAddMessage(thing);
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
                    scanDialog = showScanDialog();
                    scanDialog.show();
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
        if (scanDialog != null){scanDialog.dismiss();}
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                // Handle successful scan
                System.out.println("Scanning action was succesfull");

                String contents = intent.getStringExtra("SCAN_RESULT");
                // String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                newWhat.setText(contents);
                newWhat.invalidate();

                // Do Outpan Search on another thread
                Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.coordinator_layout),
                                                    R.string.check_online,
                                                    Snackbar.LENGTH_LONG);
                snackbar.show();

                FetchNetworkItemsTask fetcher = new FetchNetworkItemsTask(getContext(), contents);
                fetcher.execute();

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Handle cancel
                System.out.println("Scanning action was cancelled");
            }
        }
    }

    private Dialog showScanDialog() throws ActivityNotFoundException{
        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.scan_dialog, null);

        Button qrButton = (Button) dialogView.findViewById(R.id.scan_button_qr);
        Button barcodeButton = (Button) dialogView.findViewById(R.id.scan_button_barcode);

        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");

                startActivityForResult(intent, 0);
            }
        });
        barcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "PRODUCT_MODE");

                startActivityForResult(intent, 0);
            }
        });

        ab.setTitle("Scan options");
        ab.setView(dialogView);
        return ab.create();
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

    private void displayAddMessage(Thing thing){
        // Create the Snackbar
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), "", Snackbar.LENGTH_LONG);
        // Get the Snackbar's layout view
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        // Hide the text
        TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);

        // Inflate our custom view
        View snackView = getActivity().getLayoutInflater().inflate(R.layout.no_internet_layout, null);
        TextView tv = (TextView) snackView.findViewById(R.id.noServiceTextview);
        ImageView iv = (ImageView) snackView.findViewById(R.id.imageView);

        tv.setText("\"" + thing.getWhat() + "\" was added");
        iv.setImageResource(R.drawable.all_good5050);

        // Add the view to the Snackbar's layout
        layout.addView(snackView, 0);
        snackbar.show();
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

                // Display message with exception
                if (mException instanceof java.io.FileNotFoundException){ // Apparently this is the exception thrown for this case...
                    outpanFailureMessage = "Outpan failed to understand what was scanned";
                }else if (mException instanceof IOException){
                    outpanFailureMessage = "Problem with network connection";
                }else if(mException instanceof JSONException){
                    outpanFailureMessage = "Problem with Outpan response JSON conversion";
                }else{
                    outpanFailureMessage = "Unknown problem... sucks.";
                }

                displayNetworkMessage(false);

                return;
            }else{
                if (!uselessNameChecker(itemName)) {
                    outpanFailureMessage = "No data from Outpan";
                    displayNetworkMessage(false);
                    return;
                }

                newWhat.setText(itemName);
                displayNetworkMessage(true);

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

        private void displayNetworkMessage(boolean positive){
            // Create the Snackbar
            Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), "", Snackbar.LENGTH_INDEFINITE);
            // Get the Snackbar's layout view
            Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
            // Hide the text
            TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
            textView.setVisibility(View.INVISIBLE);

            // Inflate our custom view
            View snackView = getActivity().getLayoutInflater().inflate(R.layout.no_internet_layout, null);
            TextView tv = (TextView) snackView.findViewById(R.id.noServiceTextview);
            ImageView iv = (ImageView) snackView.findViewById(R.id.imageView);

            if (positive){
                tv.setText("Outpan reply replaced scanned code");
                iv.setImageResource(R.drawable.all_good5050);
            }else {
                tv.setText(outpanFailureMessage);
                iv.setImageResource(R.drawable.sucks5050);
            }

            // Add the view to the Snackbar's layout
            layout.addView(snackView, 0);
            snackbar.show();
        }
    }
}
