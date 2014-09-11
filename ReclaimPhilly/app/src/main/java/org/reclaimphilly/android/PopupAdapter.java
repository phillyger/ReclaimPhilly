package org.reclaimphilly.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.parse.ParseGeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;

/**
 * Created by gosullivan on 8/19/13.
 */
public class PopupAdapter implements GoogleMap.InfoWindowAdapter {

    static final String TAG = "PopupAdapter";
    static final int NTHREDS = 2;

    LayoutInflater inflater=null;
    Context context = null;

    ExecutorService executor;
    ShowInfoWindowViewHolder holder;

    PopupAdapter(LayoutInflater inflater, Context context) {
        this.inflater=inflater;
        this.context = context;

    }


    // Use default InfoWindow frame
    @Override
    public View getInfoWindow(Marker arg0) {
        return null;
    }

    // Defines the contents of the InfoWindow
    @Override
    public View getInfoContents(Marker arg0) {

        // Getting view from the layout file info_window_layout
        View v = inflater.inflate(R.layout.info_window, null);


        holder = new ShowInfoWindowViewHolder(v);

        v.setTag(holder);



        // Getting the position from the marker
        LatLng latLng = arg0.getPosition();

        //Convert LatLng to Location
        Location location = new Location("temp");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        location.setTime(new java.util.Date().getTime()); //Set time as current Date


        executor = Executors.newFixedThreadPool(NTHREDS);

        if (executor != null) {
           Future<String> reverseGeocodingFuture = executor.submit(new ReverseGeocodingTask(context, latLng));



            Future<Bitmap> streetViewFuture = executor.submit(new FetchStreetView<Bitmap>(latLng));

            // This will make the executor accept no new threads
            // and finish all existing threads in the queue
            executor.shutdown();

            // Wait until all threads are finish
            while (!executor.isTerminated()) {
            }

            try {
                if (reverseGeocodingFuture.get() != null) {

                    // Setting the latitude
                    holder.address.setText(reverseGeocodingFuture.get());
                    Log.i(TAG, reverseGeocodingFuture.get());
                }

                if (latLng != null) {
                    holder.latitude.setText(String.valueOf(latLng.latitude));
                    Log.i(TAG, "latitude: " +String.valueOf(latLng.latitude));
                    holder.longitude.setText(String.valueOf(latLng.longitude));
                    Log.i(TAG, "longitude: " +String.valueOf(latLng.longitude));
                }


                if (streetViewFuture.get() != null) {
                    holder.streetView.setImageBitmap(streetViewFuture.get());
                    Log.i(TAG, streetViewFuture.get().toString());
                }
            } catch (InterruptedException e) {

                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }


        // Returning the view containing InfoWindow contents
        return v;

    }


    private static class FetchStreetView<Bitmap> implements Callable<Bitmap> {

        Bitmap bitmap = null;
        LatLng mLatLng;

        public FetchStreetView(LatLng latLng) {
            super();
            mLatLng = latLng;
        }

        @Override
        public Bitmap call() throws Exception {
            StreetViewFetcher gsv = new StreetViewFetcher(mLatLng);
            String url = gsv.getUrl();
            Log.i(TAG, "FetchStreetView::url : " + url);
            byte[] bitmapBytes = gsv.getUrlBytes(url);
            bitmap = (Bitmap) BitmapFactory
                    .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);

            return (Bitmap) bitmap;
        }

    }

    private static class ReverseGeocodingTask implements Callable<String> {


        LatLng mLatLng;
        Context mContext;

        public ReverseGeocodingTask(Context context, LatLng latLng) {
            super();
            mContext = context;
            mLatLng = latLng;

        }

        @Override
        public String call() throws Exception {
            Geocoder geocoder = new Geocoder(mContext, Locale.US);
            double latitude = mLatLng.latitude;
            double longitude = mLatLng.longitude;
            List<Address> addresses;
            String addressText = null;

            try {
                if (geocoder.isPresent()) {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    Thread.sleep(500);


                    if (addresses != null && addresses.size() > 0) {
                        Address address = addresses.get(0);
//                    Formatter formatter = new Formatter();
                        addressText = String.format("%s", address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : address.getPremises());

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return addressText;
        }

    }

    /**
     * A class that holds view references for showWindowInfo
     *
     * @author gosullivan
     */
    private static class ShowInfoWindowViewHolder {

        final TextView address;
        final TextView longitude;
        final TextView latitude;
        final ImageView streetView;
        final Button report;

        ShowInfoWindowViewHolder(View v) {
            // Getting reference to the TextView to set address
            address = (TextView) v.findViewById(R.id.address);

            // Getting reference to the TextView to set longitude
            longitude = (TextView) v.findViewById(R.id.longitude);

            // Getting reference to the TextView to set latitude
            latitude = (TextView) v.findViewById(R.id.latitude);

            // Getting reference to the ImageView to set street view
            streetView = (ImageView) v.findViewById(R.id.thumbnail);

            // Getting reference to the ImageView to set street view
            report = (Button) v.findViewById(R.id.bttn_report_location);
        }

    }


    public Property getPopupData(Context context) {

//        JSONObject json = new JSONObject();
        Property property = null;

        ParseGeoPoint geoPoint = new ParseGeoPoint(Double.parseDouble(holder.latitude.getText().toString()), Double.parseDouble(holder.longitude.getText().toString()));


//        Drawable d = holder.streetView.getDrawable();
//        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//        byte[] bitmapdata = stream.toByteArray();
//        String filename = "street_view.png";

//        Photo localPhoto = new Photo("street_view.png", bitmapdata, context);

//        if (localPhoto != null) {
//            ParseFile parsePhoto = localPhoto.getImageFile();
//            property = new Property(holder.address.getText().toString(), geoPoint, parsePhoto);
//        } else {
//            property = new Property(holder.address.getText().toString(), geoPoint);
//        }

//        ParseFile parsePhoto = new ParseFile(filename, bitmapdata);
        property = new Property(holder.address.getText().toString(), geoPoint);





//        try {

//            Log.i(TAG, holder.address.getText().toString());
//            Log.i(TAG, holder.latitude.getText().toString());
//            Log.i(TAG, holder.longitude.getText().toString());

//            json.put(Property.JSON_KEY_STREET_ADDRESS, holder.address.getText().toString());
//                    json.put(Property.JSON_KEY_TYPE, null);
//                    json.put(Property.JSON_KEY_PHOTO, null);
//            json.put(Property.JSON_KEY_LATITUDE, Double.parseDouble(holder.latitude.getText().toString()));
//            json.put(Property.JSON_KEY_LONGITUDE, Double.parseDouble(holder.longitude.getText().toString()));



//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        return property;
    }

}
