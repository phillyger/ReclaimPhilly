package org.reclaimphilly.android.listeners;

/**
 * Created by gosullivan on 8/19/13.
 */
import android.location.Address;

public interface AddressResultListener {
    public void onAddressAvailable(Address address);
}