package parse.network.request;

import parse.model.PropertyList;
import roboguice.util.temp.Ln;

/**
 * Created by gosullivan on 3/23/14.
 */
public class RequestPropertyList extends ParseApiV10Request<PropertyList> {

    private static final String TAG = "RequestPropertyList";


    public RequestPropertyList() {

        super(PropertyList.class);

    }

    @Override
    public PropertyList loadDataFromNetwork() throws Exception {
        Ln.d("Call PropertyList Info web service ");

        return getService().getPropertyList();
    }
}

