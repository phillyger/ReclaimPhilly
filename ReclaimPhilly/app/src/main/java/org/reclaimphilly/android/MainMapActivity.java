package org.reclaimphilly.android;

import android.app.SearchManager;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import parse.model.PropertyList;
import parse.model.PropertyObject;
import parse.network.MySpiceManager;
import parse.network.request.RequestPropertyList;

import java.util.List;

/**
 * Created by gosullivan on 8/19/13.
 */
public class MainMapActivity extends BaseParseSpiceActivity {



    //================================================================================
    // Properties
    //================================================================================

    // TAG info
    private static final String TAG = "MainMapActivity";

    private List<PropertyObject> mPropertyObjectList;

    RequestPropertyList mRequestPropertyList;

    // Handle on spiceManager
    protected MySpiceManager spiceManager = new MySpiceManager();


    /** A key for passing a run ID as a long */
    public static final String EXTRA_PROPERTY_ID = "org.reclaimphilly.reclaimphilly.property_id";

    //================================================================================
    // Constructors
    //================================================================================
    public MainMapActivity() { super(TAG); }

    //================================================================================
    // Implemented methods
    //================================================================================
    @Override
    public void onStart() {
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }


    @Override
    protected void onDestroy() {
//        spiceManager.removeAllDataFromCache();
        super.onDestroy();
    }

    @Override
    protected Fragment createFragment() {


        fetchPropertyList();

        long propertyId = getIntent(). getLongExtra( EXTRA_PROPERTY_ID, -1);

        if (propertyId != -1) {
            return MainMapFragment.newInstance(propertyId);
        } else {
            return new MainMapFragment();
        }

    }


    @Override
    protected void onNewIntent(Intent intent) {
        MainMapFragment fragment = (MainMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i(TAG, "Received a new search query: " + query);

            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putString(MainMapFragment.PREF_SEARCH_QUERY, query)
                    .commit();
        }

        fragment.updateLocation();

    }





    //================================================================================
    // Custom Methods
    //================================================================================



    /**
     * fetch the property list
     */
    void fetchPropertyList() {


        mRequestPropertyList = new RequestPropertyList();

        getSpiceManager().execute(mRequestPropertyList, "parsePropertyList", DurationInMillis.ONE_SECOND, new PropertyListListener());
        Log.d(TAG, "Pending Request Count A: " + getSpiceManager().getPendingRequestCount());
    }

    /**
     * Updates the cluster points instances
     */
    private void updateClusterPoints() {
        MainMapFragment fragment = (MainMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

        fragment.updateClusterPoints();
        fragment.getMap().animateCamera(CameraUpdateFactory.zoomIn());

    }


    // ============================================================================================
    // INNER CLASSES
    // ============================================================================================

    /**
     * Request Listener for MemberInfo
     */
    public final class PropertyListListener implements RequestListener<PropertyList> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(MainMapActivity.this, "failure", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(final PropertyList resultList) {
//            Toast.makeText(MainMapActivity.this, "success", Toast.LENGTH_SHORT).show();
            PropertyObject member = resultList.getResults().get(0);

            // Write results to ivar
            setPropertyObjectList(resultList.getResults());
            updateClusterPoints();
        }
    }

    // ============================================================================================
    // ACCESSORS
    // ============================================================================================

    public List<PropertyObject> getPropertyObjectList() {
        return mPropertyObjectList;
    }

    public void setPropertyObjectList(List<PropertyObject> propertyObjectList) {
        mPropertyObjectList = propertyObjectList;
    }

}
