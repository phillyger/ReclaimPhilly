package parse.network;

/**
 * Created by gosullivan on 3/7/14.
 */
import com.octo.android.robospice.exception.NetworkException;
import com.octo.android.robospice.persistence.exception.SpiceException;

import retrofit.RetrofitError;
import retrofit.client.Response;

import org.apache.http.HttpStatus;

/**
 * Customized retry policy based on {@link com.octo.android.robospice.retry.DefaultRetryPolicy}.
 * <p/>
 * Stop retrying in case of Http error.
 */
public class RetryPolicy implements com.octo.android.robospice.retry.RetryPolicy {

    /**
     * The default number of retry attempts.
     */
    public static final int RETRY_COUNT = 3;

    /**
     * The default delay before retry a request (in ms).
     */
    public static final long DELAY_BEFORE_RETRY = 1000;

    /**
     * The default backoff multiplier.
     */
    public static final float BACKOFF_MULT = 1.25f;

    /**
     * The number of retry attempts.
     */
    private int retryCount = RETRY_COUNT;

    /**
     * The delay to wait before next retry attempt. Will be multiplied by
     * {@link #BACKOFF_MULT} between every retry attempt.
     */
    private long delayBeforeRetry = DELAY_BEFORE_RETRY;

    @Override
    public int getRetryCount() {
        return retryCount;
    }

    @Override
    public void retry(SpiceException e) {
        if (doNotRetry(e)) {
            retryCount = 0;
        } else {
            retryCount--;
            delayBeforeRetry = (long) (delayBeforeRetry * BACKOFF_MULT);
        }
    }

    @Override
    public long getDelayBeforeRetry() {
        return delayBeforeRetry;
    }

    private boolean doNotRetry(SpiceException spiceException) {
        if (spiceException instanceof NetworkException) {
            Throwable networkException = spiceException.getCause();
            if (networkException instanceof RetrofitError) {
                RetrofitError retrofitError = (RetrofitError) networkException;
                Response response = retrofitError.getResponse();
                if (response != null) {
                    return checkStatus(response.getStatus());
                }
            }
        }
        return false;
    }

    private boolean checkStatus(int status) {
        switch (status) {
			/*
			 * Returned if the caller submits a badly formed request.
			 * For example, the caller can receive this return if you forget a required parameter.
			 */
            case HttpStatus.SC_BAD_REQUEST:
			/*
			 * Returned if the call requires authentication and either the credentials
			 * provided failed or no credentials were provided.
			 */
            case HttpStatus.SC_UNAUTHORIZED:
			/*
			 * Returned if the caller attempts to make a call or modify a resource for which
			 * the caller is not authorized. The request was a valid request, the caller's
			 * authentication credentials succeeded but those credentials do not grant the
			 * caller permission to access the resource.
			 */
            case HttpStatus.SC_FORBIDDEN:
			/*
			 * Returned if the specified resource does not exist.
			 */
            case HttpStatus.SC_NOT_FOUND:
                return true;
            default:
                return false;
        }
    }
}
