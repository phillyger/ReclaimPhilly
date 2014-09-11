package org.reclaimphilly.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.Button;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by gosullivan on 8/13/13.
 */
public class PropertyCameraFragment extends Fragment {


    public static final String TAG = "PropertyCameraFragment";

    public static final String EXTRA_PHOTO_FILENAME = "org.reclaimphilly.reclaimphilly.photo_filename";


    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private View mProgressContainer;

    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            // Display the progress indicator
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };

    private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // Create a filename
            String filename = UUID.randomUUID().toString() + ".jpg";
            // Save the jpg to disk
            FileOutputStream os = null;
            boolean success = true;
            try {
                os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                os.write(data);

            } catch (Exception e) {
                Log.e(TAG, "Error writing to file", e);
                success = false;
            } finally {
                try {
                    if (os != null)
                        os.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error closing file", e);
                    success = false;
                }
            }

            if (success) {
//                Log.i(TAG, "JPEG saved at " + filename);
                Intent i = new Intent();
                i.putExtra(EXTRA_PHOTO_FILENAME, filename);
                getActivity().setResult(Activity.RESULT_OK, i);
            } else {
                getActivity().setResult(Activity.RESULT_CANCELED);
            }
            getActivity().finish();
        }
    };


    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_property_camera, container, false);

        mProgressContainer = (View) v.findViewById(R.id.property_camera_progressContainer);
        mProgressContainer.setVisibility(View.INVISIBLE);

        Button takePictureButton = (Button)v.findViewById(R.id.property_camera_takePictureButton);

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getActivity().finish();
                if (mCamera != null) {
                    mCamera.takePicture(mShutterCallback, null, mJpegCallback);
                }
            }

        });

        mSurfaceView = (SurfaceView)v.findViewById(R.id.property_camera_surfaceView);

        SurfaceHolder holder = mSurfaceView.getHolder();
        // setType and SURFACE_TYPE_PUSH_BUFFERS are both deprecated
        // but are required for Camera preview to work on pre-3.0 devices.
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // tell the camera to use this surface as it preview area
                try {
                    if (mCamera != null)
                        mCamera.setPreviewDisplay(holder);
                } catch (IOException exception) {
                    Log.e(TAG, "Error setting up the preview display.", exception);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (mCamera == null) return;

                // The surface has changed size; update the camera preview size
                Camera.Parameters parameters = mCamera.getParameters();
//                Camera.Size s = null //To be set in the next section
                Camera.Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), width, height);
                parameters.setPreviewSize(s.width, s.height);
                s = getBestSupportedSize(parameters.getSupportedPictureSizes(), width, height);
                parameters.setPictureSize(s.width, s.height);
                mCamera.setParameters(parameters);
                try {
                    mCamera.startPreview();
                } catch (Exception e) {
                    Log.e(TAG, "could not start preview.", e);
                    mCamera.release();
                    mCamera = null;
                }

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // We can no longer display on this surface, so stop the preview.
                if (mCamera != null)
                    mCamera.stopPreview();
            }
        });
        return v;
    }

    @TargetApi(9)
    @Override
    public void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mCamera = Camera.open(0);
        } else {
            mCamera = Camera.open();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, int width, int height) {

        Camera.Size bestSize = sizes.get(0);
        int largestSize = bestSize.width * bestSize.height;
        for (Camera.Size s : sizes) {
            int area = s.width * s.height;
            if (area > largestSize) {
                bestSize = s;
                largestSize = area;
            }
        }
        return bestSize;
    }

}
