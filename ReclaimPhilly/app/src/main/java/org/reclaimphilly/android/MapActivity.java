package org.reclaimphilly.android;

import android.content.Intent;

import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationClient;


import android.location.Location;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Represents the map activity
 *
 * @author gosullivan
 */
public class MapActivity extends FragmentActivity
        implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener {

    static final String TAG = "MapActivity";

    static final double LAT_PHL = 39.9522;
    static final double LNG_PHL = -75.1642;
    static final int NTHREDS = 2;


    static final LatLng LOCATION_PHL = new LatLng(LAT_PHL, LNG_PHL);
    /**
     * Note that this may be null if the Google Play services APK is not available.
     */
    private GoogleMap mMap;
    private LocationClient mLocationClient;
    private MarkerOptions markerOptions;
    private PopupAdapter mPopUp;

//    ShowInfoWindowViewHolder holder;

    // These settings are the same as the settings for the map. They will in fact give you updates at
    // the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);
        setUpMapIfNeeded();

        mPopUp = new PopupAdapter(getLayoutInflater(), getBaseContext());

        mMap.setInfoWindowAdapter(mPopUp);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        setMapCamera();
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
    protected void onDestroy() {

        super.onDestroy();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.myMap))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    private void setUpLocationClientIfNeeded() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(
                    getApplicationContext(),
                    this,  // ConnectionCallbacks
                    this); // OnConnectionFailedListener
        }
    }

    private void setMapCamera() {
        // Move the camera instantly to hamburg with a zoom of 15.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LOCATION_PHL, 1));


        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11), 2000, null);
    }


    /**
     * Implementation of {@link LocationListener}.
     */
    @Override
    public void onLocationChanged(Location location) {
//        mMessageView.setText("Location = " + location);
    }

    /**
     * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        mLocationClient.requestLocationUpdates(
                REQUEST,
                this);  // LocationListener
    }

    /**
     * Callback called when disconnected from GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onDisconnected() {
        // Do nothing
    }

    /**
     * Implementation of {@link OnConnectionFailedListener}.
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

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                Property property = mPopUp.getPopupData(getBaseContext());


                if (property != null) {
                    Intent intent = new Intent(getBaseContext(),
                            PropertyDetailsActivity.class);
                    intent.putExtra("org.reclaimphilly.reclaimphilly.Property", property);
                    startActivity(intent);
                }

            }
        });
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

}