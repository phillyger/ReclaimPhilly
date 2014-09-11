package parse.network;

/**
 * Created by gosullivan on 3/7/14.
 */
import android.util.Log;
import com.octo.android.robospice.SpiceManager;
import roboguice.util.temp.Ln;

public class MySpiceManager extends SpiceManager {
    public MySpiceManager() {
        super(ParseRestService.class);
        Ln.getConfig().setLoggingLevel(Log.ERROR);
    }

    @Override
    protected int getThreadCount() {
        return 3;
    }


}

