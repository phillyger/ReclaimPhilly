package parse.network;

/**
 * Created by gosullivan on 3/7/14.
 */

import parse.model.PropertyList;
import retrofit.http.GET;

/**
 * Parse REST API 1.0 as Java interface.
 */
public interface ParseApiV10 {

    @GET("/1/classes/PropertyObject")
    PropertyList getPropertyList();

}
