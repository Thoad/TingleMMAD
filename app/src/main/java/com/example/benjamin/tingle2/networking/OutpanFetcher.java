package com.example.benjamin.tingle2.networking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;




/**
 * Innerclass that does network stuff
 */
public class OutpanFetcher {
    Context mContext;
    // The following 2 members constitutes part of the URL to query. Must be put together correctly according to this:
    // https://api.outpan.com/v2/products/[GTIN]?apikey=[YOUR API KEY]
    String OutpanApiGet = "https://api.outpan.com/v2/products/";
    String OutpanApiKey = "?apikey=2cfd6985501afa1cd01ca4d9daf2ebce";

    /**
     * Create an OutpanFetcher that will query a given URL
     * @param context The context is used in some method calls
     */
    public OutpanFetcher(Context context){
        mContext = context;
    }

    /**
     * Fetch data from network source as byte array
     * @param code The barcode or QR-code to lookup
     * @return The result as an byte[]
     * @throws IOException
     */
    private byte[] getUrlBytes(String code) throws IOException {
        //if (!isOnline()){ throw new IOException("The app cannot connect to the internet"); }

        String UrlString = OutpanApiGet + code + OutpanApiKey;
        URL url = new URL(UrlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() !=
                    HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage()
                        +
                        ": with " +
                        UrlString);
            }
            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Fetch data from network source as string
     * @param code The barcode or QR-code to lookup
     * @return The result as an string
     * @throws IOException
     */
    public String getUrlString(String code) throws IOException {
        if (!isOnline()){ throw new IOException("The app cannot connect to the internet"); }

        return new String(getUrlBytes(code));
    }

    /**
     * Check if a network connection as available to the app
     * @return True is available false otherwise
     */
    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}


