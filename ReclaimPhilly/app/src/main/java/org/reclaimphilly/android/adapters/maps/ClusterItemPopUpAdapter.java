package org.reclaimphilly.android.adapters.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import org.reclaimphilly.android.R;
import org.reclaimphilly.android.StreetViewFetcher;
import parse.model.PropertyObject;

import java.util.concurrent.*;


/**
 * Created by gosullivan on 4/1/14.
 */
public class ClusterItemPopUpAdapter implements GoogleMap.InfoWindowAdapter {

    static final String TAG = "ClusterItemPopUpAdapter";
    static final int NTHREDS = 2;

    PropertyObject mPropertyObject = null;
    Context context = null;

    ShowInfoWindowViewHolder holder;
    ExecutorService executor;


    public ClusterItemPopUpAdapter(Context context, PropertyObject propertyObject) {
        this.mPropertyObject = propertyObject;
        this.context = context;

    }

    @Override
    public View getInfoContents(Marker marker) {

        executor = Executors.newFixedThreadPool(NTHREDS);

        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        View view = inflater.inflate(R.layout.cluster_item_info_window, null);

        holder = new ShowInfoWindowViewHolder(view);

        view.setTag(holder);


        holder.address.setText(String.valueOf(mPropertyObject.getAddress()));

//        holder.latitude.setText(String.valueOf(mPropertyObject.getGeopoint().getLatitude()));
//        holder.longitude.setText(String.valueOf(mPropertyObject.getGeopoint().getLongitude()));


        if (executor != null) {

            Future<Bitmap> streetViewFuture = executor.submit(new FetchStreetView<Bitmap>(mPropertyObject.getPhoto().getUrl()));

            // This will make the executor accept no new threads
            // and finish all existing threads in the queue
            executor.shutdown();

            // Wait until all threads are finish
            while (!executor.isTerminated()) {
            }

            try {

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
        return view;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }



    /**
     * A class that holds view references for showWindowInfo
     *
     * @author gosullivan
     */
    private static class ShowInfoWindowViewHolder {

        final TextView address;
//        final TextView longitude;
//        final TextView latitude;
        final ImageView streetView;
        final Button viewLocation;

        ShowInfoWindowViewHolder(View v) {
            // Getting reference to the TextView to set address
            address = (TextView) v.findViewById(R.id.address_cluster_item);

//            // Getting reference to the TextView to set longitude
//            longitude = (TextView) v.findViewById(R.id.longitude_cluster_item);
//
//            // Getting reference to the TextView to set latitude
//            latitude = (TextView) v.findViewById(R.id.latitude_cluster_item);

            // Getting reference to the ImageView to set street view
            streetView = (ImageView) v.findViewById(R.id.thumbnail_cluster_item);

            // Getting reference to the ImageView to set street view
            viewLocation = (Button) v.findViewById(R.id.bttn_view_location);

        }

    }

        private static class FetchStreetView<Bitmap> implements Callable<Bitmap> {

            Bitmap bitmap = null;
            String mUrlSpec;

            public FetchStreetView(String urlSpec) {
                super();
                this.mUrlSpec = urlSpec;
            }

            @Override
            public Bitmap call() throws Exception {
                StreetViewFetcher gsv = new StreetViewFetcher(mUrlSpec);
                Log.i(TAG, "FetchStreetView::url : " + mUrlSpec);
                byte[] bitmapBytes = gsv.getUrlBytes(mUrlSpec);
                bitmap = (Bitmap) BitmapFactory
                        .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);

                return bitmap;
            }

        }

}
