package parse.exception;

import com.google.gson.JsonSyntaxException;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by gosullivan on 3/8/14.
 */
public class MyErrorHandler implements ErrorHandler {

    @Override
    public Throwable handleError(RetrofitError cause) {
        Response r = cause.getResponse();
        if (r != null && r.getStatus() == 401) {
            return new JsonSyntaxException(cause);
        }
        return cause;
    }
}
