package org.reclaimphilly.android;

import parse.network.ParseRestService;
import com.octo.android.robospice.SpiceManager;

/**
 * Created by gosullivan on 3/7/14.
 */
public abstract class BaseParseSpiceActivity extends BaseActivity {

    private SpiceManager spiceManager = new SpiceManager(ParseRestService.class);

//    public BaseParseSpiceActivity() { super("BaseParseSpiceActivity"); }

    public BaseParseSpiceActivity(String inTag)
    {
        super(inTag);
    }

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    protected SpiceManager getSpiceManager() {
        return spiceManager;
    }
}
