package org.reclaimphilly.android;

import android.net.Uri;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class StreetViewFetcher {
    public static final String TAG = "StreetViewFetcher";

    private static final String ENDPOINT = "http://maps.googleapis.com/maps/api/streetview";
    private static final String API_KEY = "AIzaSyAzuPnfgiw5T5dFeWoG5m3Flv7TNVweg6s";
//    private static final String IMG_SIZE = "200x200";
    private static final String SENSOR = "false";

    private int mImgWidth;
    private int mImgHeight;
    private double mLatitude;
    private double mLongitude;
    private String mUrlString;


    public StreetViewFetcher(String urlString){

        this(urlString, 200, 200);
    }

    public StreetViewFetcher(LatLng latLng){

        this(latLng.latitude, latLng.longitude, 200, 200);
    }

    public StreetViewFetcher(String urlString, int imgWidth, int imgHeight) {
        this.mUrlString = urlString;
        this.mImgHeight = imgHeight;
        this.mImgWidth = imgWidth;
    }

    public StreetViewFetcher(double latitude, double longitude, int imgWidth, int imgHeight) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mImgHeight = imgHeight;
        this.mImgWidth = imgWidth;
    }

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int bytesRead = 0;
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

    String getUrl() {

        String url;

        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(this.mLatitude))
                .append(',')
                .append(String.valueOf(this.mLongitude));
        String location = sb.toString();

        Log.i(TAG, "sb - point:" + location);

        sb = new StringBuilder();
        sb.append(String.valueOf(this.mImgWidth));
        sb.append("x");
        sb.append(String.valueOf(this.mImgHeight));
        String imgSize = sb.toString();

        url = Uri.parse(ENDPOINT).buildUpon()
        .appendQueryParameter("size", imgSize)
        .appendQueryParameter("location", location)
        .appendQueryParameter("sensor", SENSOR)
        .appendQueryParameter("key", API_KEY)
        .build().toString();

        Log.i(TAG, "Url is: " + url);
        return url;
    }


}
