package org.reclaimphilly.android.listeners;

/**
 * Created by gosullivan on 8/19/13.
 */
import android.location.Location;

public interface LocationResultListener {
    public void onLocationResultAvailable(Location location);
}