package org.reclaimphilly.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

/**
 * Created by gosullivan on 7/2/13.
 */
public class SplashScreenFragment extends Fragment {

    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.splash_screen, container, false);
        mProgressBar = (ProgressBar) view.findViewById (R.id.progress_bar);
        return view;
    }

    /**
     * Sets the progress of the ProgressBar
     *
     * @param progress int the new progress between 0 and 100
     */
    public void setProgress(int progress) {
        mProgressBar.setProgress(progress);
    }
}