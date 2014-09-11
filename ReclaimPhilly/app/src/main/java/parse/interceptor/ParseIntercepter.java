package parse.interceptor;

import org.reclaimphilly.android.BuildConfig;
import retrofit.RequestInterceptor;

/**
 * Created by gosullivan on 3/7/14.
 */
public class ParseIntercepter implements RequestInterceptor {

    @Override
    public void intercept(RequestFacade request) {
//        request.addHeader("X-Parse-Application-Id", "CzxhhRu2NJF9o1IM85xkRedx2ySoaA6G4rv4jRkc");
//        request.addHeader("X-Parse-REST-API-Key", "SyGe3rNcjaXOK8KAtNiXhKGwmPzpUfKo4El96P7d");
        request.addHeader("X-Parse-Application-Id", BuildConfig.PARSE_APPLICATION_ID);
        request.addHeader("X-Parse-REST-API-Key", BuildConfig.PARSE_REST_API_KEY);
        request.addHeader("Accept", "application/json");
        request.addHeader("Content-Type", "application/json");

    }
}
