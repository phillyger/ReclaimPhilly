package org.reclaimphilly.android;


import android.annotation.TargetApi;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.SearchView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import org.reclaimphilly.android.adapters.maps.ClusterItemPopUpAdapter;
import parse.model.PropertyObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

/**
 * Created by gosullivan on 8/19/13.
 */
public class MainMapFragment extends SupportMapFragment implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener {


    //================================================================================
    // Properties
    //================================================================================

    // TAG info
    public static final String TAG = "MainMapFragment";

    private ClusterManager<PropertyObject> mClusterManager;

    public static final String PREF_SEARCH_QUERY = "searchQuery";

    private LocationClient mLocationClient;
    private MarkerOptions markerOptions;
    private PopupAdapter mPopUp;
    private ClusterItemPopUpAdapter mClusterItemPopUp;
    private ProgressBar mActivityIndicator;

    private static final String ARG_LOCATION_ID = "PROPERTY_ID";
//    static final double LAT_PHL = 39.9522;
//    static final double LNG_PHL = -75.1642;

    static final double LAT_PHL = 39.9441561;
    static final double LNG_PHL = -75.175665;


    static final double LAT_LOC_BOUNDARY_LOWER_LEFT = 39.867004000000000;
    static final double LNG_LOC_BOUNDARY_LOWER_LEFT = -74.955763000000000;
    static final double LAT_LOC_BOUNDARY_UPPER_RIGHT = 40.137992000000000;
    static final double LNG_LOC_BOUNDARY_UPPER_RIGHT = -75.280266000000000;

    static final int NTHREDS = 2;


    static final LatLng LOCATION_PHL = new LatLng(LAT_PHL, LNG_PHL);
    /**
     * Note that this may be null if the Google Play services APK is not available.
     */
    private GoogleMap mMap;



    // These settings are the same as the settings for the map. They will in fact give you updates at
    // the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    public static MainMapFragment newInstance(long propertyId) {
        Bundle args = new Bundle();
        args.putLong(ARG_LOCATION_ID, propertyId);
        MainMapFragment mmf = new MainMapFragment();
        mmf.setArguments(args);
        return mmf;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Parse.initialize(this.getActivity(), "eYOww0ZEGRtLUgVGJzAWvmOmgvwisq81c4TOHelB", "cElKQ8MalHETWbKcdElCuu6knq2W8UObXte9fDc0");

//        ParseObject testObject = new ParseObject("TestObject");
//        testObject.put("foo", "bar");
//        testObject.saveInBackground();

        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        setUpMapIfNeeded();
        setMapCamera();

        mPopUp = new PopupAdapter(inflater, getActivity());


        return v;

    }

    private void respondToClick(Property property)
    {
        Intent intent = new Intent(getActivity(),
                PropertyDetailsActivity.class);
        intent.putExtra(Property.t_tablename, property);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        //setMapCamera();
        setUpLocationClientIfNeeded();
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mLocationClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLocationClient != null) {
            mLocationClient.disconnect();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_main_map, menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Pull out the SearchView
            MenuItem searchItem = menu.findItem(R.id.menu_item_search);
            SearchView searchView = (SearchView) searchItem.getActionView();
            // Get the data from our searchable.xml as a SearchableInfo
            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            ComponentName name = getActivity().getComponentName();
            SearchableInfo searchInfo = searchManager.getSearchableInfo(name);
            searchView.setSearchableInfo(searchInfo);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_search:
                getActivity().onSearchRequested();
                return true;
            case R.id.menu_item_clear:
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                        .putString(PREF_SEARCH_QUERY, null)
                        .commit();
                updateLocation();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);


            }
        }
    }

    private void plotClusterPoints()
    {
        mClusterManager = new ClusterManager<PropertyObject>(getActivity(), getMap());

        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);



        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<PropertyObject>() {
            @Override
            public boolean onClusterItemClick(PropertyObject propertyObject) {
                mClusterItemPopUp = new ClusterItemPopUpAdapter(getActivity().getApplicationContext(), propertyObject);
                mMap.setInfoWindowAdapter(mClusterItemPopUp);

                MarkerManager.Collection mClusterCollection = mClusterManager.getMarkerCollection();
                return false;
            }
        });

        mClusterManager.setOnClusterInfoWindowClickListener(new ClusterManager.OnClusterInfoWindowClickListener<PropertyObject>() {
            @Override
            public void onClusterInfoWindowClick(Cluster<PropertyObject> propertyObjectCluster) {
                MarkerManager.Collection mClusterCollection = mClusterManager.getMarkerCollection();
            }
        });

        mClusterManager.addItems(((MainMapActivity) getActivity()).getPropertyObjectList());
    }

    public void updateClusterPoints() {

        plotClusterPoints();
    }

    private void setUpLocationClientIfNeeded() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(
                    getActivity().getApplicationContext(),
                    this,  // ConnectionCallbacks
                    this); // OnConnectionFailedListener
        }
    }


    private void setMapCamera() {
        // Move the camera instantly to hamburg with a zoom of 15.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LOCATION_PHL, 2));


        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);

    }


    /**
     * Implementation of {@link LocationListener}.
     */
    @Override
    public void onLocationChanged(Location location) {
//        mMessageView.setText("Location = " + location);
    }

    /**
     * Callback called when connected to GCore. Implementation of {@link com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks}.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        mLocationClient.requestLocationUpdates(
                REQUEST,
                this);  // LocationListener
    }

    /**
     * Callback called when disconnected from GCore. Implementation of {@link com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks}.
     */
    @Override
    public void onDisconnected() {
        // Do nothing
    }

    /**
     * Implementation of {@link com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener}.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do nothing
    }


    @Override
    public void onMapLongClick(LatLng point) {

        // TODO Auto-generated method stub
//        Marker marker = mMap.addMarker(new MarkerOptions()
//                .position(point)
//                .title("You are here at ")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).draggable(true));
//
//        Log.i(TAG, "Drop Pin Position: " + marker.getPosition().toString());



        mMap.setInfoWindowAdapter(mPopUp);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                Property property = mPopUp.getPopupData(getActivity().getBaseContext());


                if (property != null) {
                    respondToClick(property);
                }

            }
        });


        // Getting the Latitude and Longitude of the touched location
        LatLng latLng = point;
        // Clears the previously touched position
        mMap.clear();
        // Animating to the touched position
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        // Creating a marker
        markerOptions = new MarkerOptions();
        // Setting the position for the marker
        markerOptions.position(latLng);

        // Adding Marker on the touched location with address
        mMap.addMarker(markerOptions).showInfoWindow();


    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Log.i(TAG, "Drag Start Position: " + marker.getPosition().toString());
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        // do nothing
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Log.i(TAG, "Drag End Position: " + marker.getPosition().toString());
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        MarkerManager.Collection clusterMarkerCollection = mClusterManager.getClusterMarkerCollection();
        return false;
    }

    public void updateLocation() {

        // You can also choose to place a point like so:
        String latitude = "0";
        String longitude = "0";
        String uri = "";
        String query = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(MainMapFragment.PREF_SEARCH_QUERY, null);

        if (query != null) {
            try {

                Log.i(TAG, "Received a new search query: " + URLEncoder.encode(query, "UTF-8"));

                uri = "geo:" + latitude + "," + longitude + "?q=" + URLEncoder.encode(query, "UTF-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            StringBuilder sb = new StringBuilder();
            sb.append(query);
            sb.append(", Philadelphia, PA");
//        getAddress("2007 Bainbridge Street, Philadelphia, PA 19146");
//        getAddress("1330 Pine Street, Philadelphia, PA");

            Log.i(TAG, "Search Address is : " + sb.toString());
            getAddress(sb.toString());
        }
    }


    /**
     * A subclass of AsyncTask that calls getFromLocation() in the
     * background. The class definition has these generic types:
     * Location - A Location object containing
     * the current location.
     * Void     - indicates that progress units are not used
     * String   - An address passed to onPostExecute()
     */
    private class GetAddressTask extends AsyncTask<String, Void, Address> {
        Context mContext;

        public GetAddressTask(Context context) {
            super();
            mContext = context;
        }

        /**
         * Get a Geocoder instance, get the latitude and longitude
         * look up the address, and return it
         *
         * @return A string containing the address of the current
         * location, or an empty string if no address can be found,
         * or an error message
         * @params params One or more Location objects
         */
        @Override
        protected Address doInBackground(String... params) {
            Geocoder geocoder =
                    new Geocoder(mContext, Locale.getDefault());

            String loc = params[0];
            // Create a list to contain the result address
            List<Address> addresses = null;
            try {
                /*
                 * Return 1 address.
                 */
                addresses = geocoder.getFromLocationName(loc, 1, LAT_LOC_BOUNDARY_LOWER_LEFT, LNG_LOC_BOUNDARY_LOWER_LEFT, LAT_LOC_BOUNDARY_UPPER_RIGHT, LNG_LOC_BOUNDARY_UPPER_RIGHT);
            } catch (IOException e1) {
                Log.e("LocationSampleActivity",
                        "IO Exception in getFromLocationName()");
                e1.printStackTrace();
                return null;
            } catch (IllegalArgumentException e2) {
                // Error message to post in the log
                String errorString = "Illegal arguments " +
                        loc +
                        " passed to address service";
                Log.e("LocationSampleActivity", errorString);
                e2.printStackTrace();
                return null;
            }
            // If the reverse geocode returned an address
            if (addresses != null && addresses.size() > 0) {
                // Get the first address
                Address address = addresses.get(0);

                return address;
            } else {
                return null;
            }
        }

        /**
         * A method that's called once doInBackground() completes. Turn
         * off the indeterminate activity indicator and set
         * the text of the UI element that shows the address. If the
         * lookup failed, display the error message.
         */
        @Override
        protected void onPostExecute(Address address) {
            // Set activity indicator visibility to "gone"
//            mActivityIndicator.setVisibility(View.GONE);


            LatLng newLatLng = new LatLng(address.getLatitude(), address.getLongitude());

            // Clears the previously touched position
            mMap.clear();

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, 1));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
//            mMap.animateCamera(CameraUpdateFactory.scrollBy(newLatLng.latitude, newLatLng.longitude), 2000, null);
//            mMap.animateCamera(CameraUpdateFactory.zoomIn());

            markerOptions = new MarkerOptions();
            // Setting the position for the marker
            markerOptions.position(newLatLng);

            // Adding Marker on the touched location with address
            mMap.addMarker(markerOptions).showInfoWindow();


        }
    }

    /**
     * The "Get Address" button in the UI is defined with
     * android:onClick="getAddress". The method is invoked whenever the
     * user clicks the button.
     *
     * @param location The view object associated with this method,
     *          in this case a Button.
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void getAddress(String location) {
        // Ensure that a Geocoder services is available
        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.GINGERBREAD
                &&
                Geocoder.isPresent()) {
            // Show the activity indicator

            /*View view = (View) getActivity().findViewById(R.id.fragmentContainer);*/
//            mActivityIndicator.setVisibility(View.VISIBLE);
            /*
             * Geocoding is long-running and synchronous.
             * Run it on a background thread.
             * Pass the current location to the background task.
             * When the task finishes,
             * onPostExecute() displays the address.
             */
            (new GetAddressTask(getActivity())).execute(location);
        }

    }



}
