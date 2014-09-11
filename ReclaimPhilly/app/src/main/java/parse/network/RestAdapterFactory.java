package parse.network;

import org.reclaimphilly.android.ReclaimPhilly;
import parse.exception.MyErrorHandler;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RestAdapter;
import retrofit.android.AndroidApacheClient;
import retrofit.client.Client;
import retrofit.converter.Converter;
import parse.interceptor.ParseIntercepter;

/**
 * Defines a factory which is responsible for creating a {@link retrofit.RestAdapter}.
 */
public class RestAdapterFactory {

    /**
     * Log the request method and URL and the response status code and execution time.
     */
    private static final RestAdapter.LogLevel logLevel = RestAdapter.LogLevel.HEADERS;

    /**
     * API Server base URL.
     */
    public static final String parseApiUrl = "https://api.parse.com";

    /**
     * Creates a {@link retrofit.RestAdapter.Builder}.
     *
     * @param rePHL       {@link org.reclaimphilly.android.ReclaimPhilly} application
     * @param endPoint    API Server
     * @param converter Converter used for serialization and deserialization of objects
     * @return RestAdapter.Builder object
     */
    static RestAdapter.Builder provideBuilder(ReclaimPhilly rePHL,
                                              Endpoint endPoint, Converter converter) {
        Client client = new AndroidApacheClient();
        return new RestAdapter.Builder()
                .setEndpoint(endPoint)
//                .setConverter(converter)
                .setClient(client)
                .setErrorHandler(new MyErrorHandler())
                .setRequestInterceptor(new ParseIntercepter())
                .setLogLevel(logLevel);
    }

    /**
     * Creates a {@link retrofit.RestAdapter.Builder}.
     *
     * @param rePHL       {@link org.reclaimphilly.android.ReclaimPhilly} application
     * @param serverUrl API Server base URL
     * @param converter Converter used for serialization and deserialization of objects
     * @return RestAdapter.Builder object
     */
    public static RestAdapter.Builder provideBuilder(ReclaimPhilly rePHL,
                                              String serverUrl, Converter converter) {
        Endpoint endPoint = Endpoints.newFixedEndpoint(serverUrl);
        return provideBuilder(rePHL, endPoint, converter);
    }

    /**
     * Creates a {@link retrofit.RestAdapter} instance.
     *
     * @param rePHL       {@link org.reclaimphilly.android.ReclaimPhilly} application
     * @param serverUrl API Server
     * @param converter Converter used for serialization and deserialization of objects
     * @return RestAdapter object
     */
    static RestAdapter provideAdapter(ReclaimPhilly rePHL,
                                      String serverUrl, Converter converter) {
        return provideBuilder(rePHL, serverUrl, converter).build();
    }

    /**
     * Creates a {@link retrofit.RestAdapter} instance with Parse API URL and Gson converter.
     *
     * @param rePHL       {@link org.reclaimphilly.android.ReclaimPhilly} application
     * @return RestAdapter object
     */
    public static RestAdapter provideAdapter(ReclaimPhilly rePHL) {
        return provideAdapter(rePHL, parseApiUrl, GsonFactory.provideGsonConverter());
    }
}