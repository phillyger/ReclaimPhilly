package org.reclaimphilly.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SplashScreenActivity extends FragmentActivity implements DataLoaderFragment.ProgressListener {

    public static final String TAG_DATA_LOADER = "dataLoader";
    public static final String TAG_SPLASH_SCREEN = "splashScreen";

    private FragmentManager mFragMgr;
    private DataLoaderFragment mDataLoaderFragment;
    private SplashScreenFragment mSplashScreenFragment;


    /**
     * Note that this may be null if the Google Play services APK is not available.
     */
    private GoogleMap mMap;

    @Override
    public void onCompletion(Double result) {

//        TextView tv = new TextView(this);
//        tv.setText(String.valueOf(result));
//        setContentView(tv);


        if (mDataLoaderFragment != null) {
            mFragMgr.beginTransaction().remove(mDataLoaderFragment).commit();
            mDataLoaderFragment = null;
        }

        /**
         *  Start a new MainMapActivity intent
         */
        Intent i = new Intent(SplashScreenActivity.this, MainMapActivity.class);
        startActivity(i);


    }

    @Override
    public void onProgressUpdate(int value) {
        mSplashScreenFragment.setProgress(value);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFragMgr = getSupportFragmentManager();
        mDataLoaderFragment = (DataLoaderFragment) mFragMgr.findFragmentByTag(TAG_DATA_LOADER);
        if (mDataLoaderFragment == null) {
            mDataLoaderFragment = new DataLoaderFragment();
            mDataLoaderFragment.setProgressListener(this);
            mDataLoaderFragment.startLoading();
            mFragMgr.beginTransaction().add(mDataLoaderFragment,TAG_DATA_LOADER).commit();
        } else {
            if (checkCompletionStatus())
                return;
        }

        mSplashScreenFragment = (SplashScreenFragment) mFragMgr.findFragmentByTag(TAG_SPLASH_SCREEN);
        if (mSplashScreenFragment == null) {
            mSplashScreenFragment = new SplashScreenFragment();
            mFragMgr.beginTransaction().add(android.R.id.content, mSplashScreenFragment, TAG_SPLASH_SCREEN).commit();
        }



    }




    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not have been
     * completely destroyed during this process (it is likely that it would only be stopped or
     * paused), {@link #onCreate(android.os.Bundle)} may not be called again so we should call this method in
     * {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) mFragMgr.findFragmentById(R.id.myMap))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mDataLoaderFragment != null) {
            checkCompletionStatus();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDataLoaderFragment != null)
            mDataLoaderFragment.removeProgressListener();
    }


    /**
     * Checks to see if data is done loading, if it is, the result is handled.
     *
     *
     * @return true if data is done loading.
     */
    private boolean checkCompletionStatus() {
        if (mDataLoaderFragment.hasResult()) {
            onCompletion(mDataLoaderFragment.getResult());
            FragmentManager fm = getSupportFragmentManager();
            mSplashScreenFragment = (SplashScreenFragment) fm.findFragmentByTag(TAG_SPLASH_SCREEN);
            if (mSplashScreenFragment != null) {
                fm.beginTransaction().remove(mSplashScreenFragment).commit();
            }
            return true;
        }

        mDataLoaderFragment.setProgressListener(this);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}

