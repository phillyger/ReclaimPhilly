package parse.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gosullivan on 3/23/14.
 */
public class PropertyList {


    @Expose
    private List<PropertyObject> results = new ArrayList<PropertyObject>();

    public List<PropertyObject> getResults() {
        return results;
    }
}
