package org.reclaimphilly.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by gosullivan on 8/13/13.
 */
public class PropertyCameraActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new PropertyCameraFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Hide the window title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Hide the status bar and other OS-level flags
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
    }
}
