package parse.network.request;

import parse.network.RetryPolicy;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;


/**
 * Common base class of common implementation for a {@link RetrofitSpiceRequest}.
 *
 * @param <T> The result type of this request
 * @param <R> The retrofited interface used by this request.
 */
public abstract class BaseParseRequest<T, R> extends RetrofitSpiceRequest<T, R> {

    public BaseParseRequest(Class<T> clazz, Class<R> retrofitedInterfaceClass) {
        super(clazz, retrofitedInterfaceClass);
        setRetryPolicy(new RetryPolicy());
    }

    /**
     * Get the key used to store and retrieve the result of the request in the cache.
     * <p/>
     * Recommended key value structure: request identifier (unique name) + hash
     *
     * @return The key value.
     */
    public Object getCacheKey() {
        return null;
    }

    public int hashCode() {
        return 0;
    }

    /**
     * Get the duration in milliseconds after which the cached value of the request will be
     * considered to be expired.
     *
     * @return The cache expire duration in milliseconds.
     */
    public long getCacheExpireDuration() {
        return DurationInMillis.ALWAYS_EXPIRED;
    }
}