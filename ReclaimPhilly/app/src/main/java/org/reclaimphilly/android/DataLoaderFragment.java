package org.reclaimphilly.android;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;

/**
 * Created by gosullivan on 7/2/13.
 */
public class DataLoaderFragment extends Fragment {

    /**
     *  Classes wishing to be notified of loading progress/completion
     *  implement this.
     */
    public interface ProgressListener {
        /**
         * Notifies that the task has completed
         *
         * @param result Double result of the task
         */
        public void onCompletion(Double result);

        /**
         *  Notifies of progress
         *
         *  @param value int value from 0-100
         */
        public void onProgressUpdate(int value);
    }

    private ProgressListener mProgressListener;
    private Double mResult = Double.NaN;
    private LoadingTask mTask;


    /**
     * Returns the result or {@value Double#Nan}
     *
     * @return result or {@value Double#Nan}
     */
    public Double getResult() {
        return mResult;
    }

    /**
     * Return true if a result has already been calculated
     *
     * @return true if a result has already been calculated
     * @see #getResult()
     */
    public boolean hasResult() {
        return !Double.isNaN(mResult);
    }

    /**
     * Removes Progress Listener
     *
     * @see #setProgressListener(org.reclaimphilly.android.DataLoaderFragment.ProgressListener)
     */
    public void removeProgressListener() {
        mProgressListener = null;
    }

    /**
     * Sets the progress listener to be notified of progress updates
     *
     * @param listener ProgressListener to notify
     * @see #removeProgressListener()
     */
    public void setProgressListener(ProgressListener listener) {
        mProgressListener = listener;
    }

    /**
     * Start loading the data
     */
    public void startLoading() {
        mTask = new LoadingTask();
        mTask.execute();
    }

    private class LoadingTask extends AsyncTask<Void, Integer, Double> {

        @Override
        protected void onPostExecute(Double result) {
            mResult = result;
            mTask = null;
            if (mProgressListener != null) {
                mProgressListener.onCompletion(mResult);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (mProgressListener != null) {
                mProgressListener.onProgressUpdate(values[0]);
            }
        }

        @Override
        protected Double doInBackground(Void... params) {
            double result = 0;

            for (int i=0; i < 100; i++) {
                try {
                    result += Math.sqrt(i);
                    Thread.sleep(50);
                    this.publishProgress(i);
                } catch (InterruptedException e) {
                    return null;
                }
            }
            return Double.valueOf(result);
        }
    }

}
