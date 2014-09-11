package org.reclaimphilly.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;
import org.reclaimphilly.android.validator.Field;
import org.reclaimphilly.android.validator.FieldRadioGroup;
import org.reclaimphilly.android.validator.FormFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.concurrent.*;

// Parse


/**
 * Created by gosullivan on 8/12/13.
 */
public class PropertyDetailsFragment extends FormFragment {


    //radio button fields in the class
    private static final int RB1_ID = 1000;//first radio button id
    private static final int RB2_ID = 1001;//second radio button id
    private static final int RB3_ID = 1002;//third radio button id

    ProgressDialog pd;
    private static String tag = "PropertyDetailsFragment";

    private Property mProperty;
    private EditText mStreetAddressField;
    private TextView mLatitudeField;
    private TextView mLongitudeField;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

    public static final String EXTRA_PROPERTY_DATA = "org.reclaimphilly.reclaimphilly.property_data";
    public static final String TAG = "PropertyDetailsFragment";

    public static final String DIALOG_IMAGE = "image";
    public static final int REQUEST_PHOTO = 1;


    private RadioGroup mPropertyRadioGroup;
    private RadioButton mRadioBttn_lot;
    private RadioButton mRadioBttn_res;
    private RadioButton mRadioBttn_nrs;



    public PropertyDetailsFragment()	{
        super(tag);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        mProperty = getArguments().getParcelable(EXTRA_PROPERTY_DATA);

    }
    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View parentView = inflater.inflate(R.layout.fragment_property_details, container, false);


        initializeFormFields(parentView);

        Button button = (Button) parentView.findViewById(R.id.SubmitReportButton);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // do something
                submitReportButtonClick(parentView);
            }
        });


        return parentView;
    }


    //from form fields
    @Override
    protected void initializeFormFields(View v)
    {
        this.reportBack("form initialized");

        /*
         *  The Address Field
         */
        mStreetAddressField = (EditText)v.findViewById(R.id.property_street_address);
        mStreetAddressField.setText(mProperty.getStreetAddress());

         /*
         *  The Radio Buttons
         */
        mPropertyRadioGroup = (RadioGroup) v.findViewById(R.id.property_type_radio_group);
        mRadioBttn_lot = (RadioButton) v.findViewById(R.id.property_radio_lot_type_lot);
        mRadioBttn_lot.setId(RB1_ID);
        mRadioBttn_res = (RadioButton) v.findViewById(R.id.property_radio_lot_type_res);
        mRadioBttn_res.setId(RB2_ID);
        mRadioBttn_nrs = (RadioButton) v.findViewById(R.id.property_radio_lot_type_nrs);
        mRadioBttn_nrs.setId(RB3_ID);



         /*
         *  The Photo
         */
        mPhotoView = (ImageView)v.findViewById(R.id.property_imageView);
        registerForContextMenu(mPhotoView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.property_imageButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PropertyCameraActivity.class);
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });

//        mPhotoView = (ImageView) v.findViewById(R.id.property_imageView);
        mPhotoView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ParseFile photo = mProperty.getPhoto();
                if (photo == null)
                    return;

                // TO-DO
//                FragmentManager fm = getActivity().getSupportFragmentManager();
//                String path = getActivity().getFileStreamPath(photo.getFilename()).getAbsolutePath();
//                ImageFragment.newInstance(path).show(fm, DIALOG_IMAGE);
            }
        });


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            registerForContextMenu(mPhotoView);
        } else {

            mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.i(TAG, "Long pressed imageView");
                    getActivity().openContextMenu(v);
                    return true;
                }
            });
        }


        // If device does not have camera, disable camera functionality
        PackageManager pm = getActivity().getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) &&
                (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)))
            mPhotoButton.setEnabled(false);



       //Setup the validators
        addValidator(new Field(mStreetAddressField));
        addValidator(new FieldRadioGroup(mPropertyRadioGroup));

    }


    private String getStreetAddressField(View v)	{
        return getStringValue(v, R.id.property_street_address);
    }

    private String getDescriptionField(View v)	{
        return getStringValue(v, R.id.property_description);
    }

    private String getPropertyType(View v)	{
        return getRadioValue(v, R.id.property_type_radio_group);
    }

    private Double getLongitude()	{
        return mProperty.getGeoPoint().getLongitude();
    }

    private Double getLatitude()	{
        return mProperty.getGeoPoint().getLatitude();
    }

    private String getStringValue(View v, int controlId)
    {
        EditText tv = (EditText)v.findViewById(controlId);
        if (tv == null)
        {
            throw new RuntimeException("Sorry Can't find the control id");
        }
        //view available

        return tv.getText().toString();
    }

    private String getRadioValue(View v, int controlId)
    {
        RadioGroup rg = (RadioGroup)v.findViewById(controlId);
        if (rg == null)
        {
            throw new RuntimeException("Sorry Can't find the control id");
        }
        //view available
        int radioBttnSelectedIdx = rg.getCheckedRadioButtonId();

        String radioBttnValue = "";

        switch (radioBttnSelectedIdx) {
            case RB1_ID:
                // the first RadioButton is checked.
                radioBttnValue = "lot";
                break;
            case RB2_ID:
                // the second RadioButton is checked.
                radioBttnValue = "res";
                break;
            case RB3_ID:
                // the third RadioButton is checked.
                radioBttnValue = "nrs";
                break;
            //other checks for the other RadioButtons ids from the RadioGroup
            case -1:
                // no RadioButton is checked in the Radiogroup
                break;
        }

        return radioBttnValue;

    }

    public void submitReportButtonClick(View v)
    {
        if (validateForm() == false)
        {
            reportTransient("Make sure all fields have valid values");
            return;
        }
        //everything is good
        String streetAddressValue = getStreetAddressField(v);
        mProperty.setStreetAddress(streetAddressValue);

        String propertyTypeValue = getPropertyType(v);
        mProperty.setType(propertyTypeValue);

        String description = getDescriptionField(v);
        mProperty.setDescription(description);

        byte [] bytesOfImage = convertImageViewImageToBytes(mPhotoView);
        String filename = addFileExtIfNecessary(mProperty.getStreetAddress().replaceAll(" ", "_").toLowerCase(), ".png");
        mProperty.setPhoto(new ParseFile(filename, bytesOfImage));

        Double latitudeValue = getLatitude();
        Double longitudeValue = getLongitude();
        reportTransient("Going to submit to Parse now...");


        StringBuilder strBuilder = new StringBuilder(streetAddressValue);
        strBuilder.append(" : ");
        strBuilder.append(propertyTypeValue);
        strBuilder.append(" : ");
        strBuilder.append(latitudeValue.toString());
        strBuilder.append(" : ");
        strBuilder.append(longitudeValue.toString());


        Toast.makeText(getActivity(), strBuilder.toString(),
                Toast.LENGTH_LONG).show();
        saveToParse();
    }
    private void saveToParse()
    {
//        gotoActivity(WelcomeActivity.class);


        turnOnProgressDialog("Saving a Property", "We will be right back");
        mProperty.po.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                turnOffProgressDialog();
                if (e == null)	{
                    //no exception
                    propertySavedSuccessfully();
                }
                else	{
                    propertySaveFailed(e);
                }

            }
        });

    }//publish-method


    private void propertySaveFailed(ParseException e)
    {
        String error = e.getMessage();
        alert("Saving word failed", error);
    }
    private void propertySavedSuccessfully()
    {
        Toast.makeText(getActivity(), "Saved to Parse",
                Toast.LENGTH_LONG).show();

         gotoActivity(MainMapActivity.class);
        //Don't finish it as back button is valid
        //finish();
    }


    private void showPhoto() {
        // (re) set the image button's image based upon our photo


        LocalPhoto localPhoto = mProperty.getLocalPhoto();

        if (localPhoto!=null) {


            Bitmap bitmap = localPhoto.getImageBitmap();

            if (bitmap != null) {

//            BitmapDrawable bitmap = null;

//            Bitmap b = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                mPhotoView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 500, 350, false));


                //                String path = getActivity().getFileStreamPath(photo.getFilename()).getAbsolutePath();
//                bitmap = PictureUtils.getScaledDrawable(getActivity(), path);


            }

        } else {
            ExecutorService executor = Executors.newFixedThreadPool(1);

            if (executor!= null) {
                Future<Bitmap> streetViewFuture = executor.submit(new FetchStreetView<Bitmap>(mProperty.getGeoPoint().getLatitude(), mProperty.getGeoPoint().getLongitude()));


                // This will make the executor accept no new threads
                // and finish all existing threads in the queue
                executor.shutdown();  // moved to onDestroy

                // Wait until all threads are finish
                while (!executor.isTerminated()) {
                }

                try {

                    if (streetViewFuture.get() != null) {
                        mPhotoView.setImageBitmap(streetViewFuture.get());
                        Log.i(TAG, streetViewFuture.get().toString());
                    }
                } catch (InterruptedException e) {

                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public static PropertyDetailsFragment newInstance(Property property) {
        Bundle args = new Bundle();
//        args.putSerializable(EXTRA_PROPERTY_DATA, json);
        args.putParcelable(EXTRA_PROPERTY_DATA, property);

        PropertyDetailsFragment fragment = new PropertyDetailsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_PHOTO) {

            if (mProperty.getLocalPhoto() != null) {
                deletePhoto();
            }

            // create a new Photo object and attach it to the crime
            String filename = data.getStringExtra(PropertyCameraFragment.EXTRA_PHOTO_FILENAME);
            if (filename != null) {
                LocalPhoto localPhoto = new LocalPhoto(getActivity(),filename);
                mProperty.setLocalPhoto(localPhoto);

                showPhoto();
            }
        }
    }


    public void deletePhoto() {

        String filePath = mProperty.getLocalPhoto().getFilename();

        if (filePath != null) {
            // Delete local image
            String path = getActivity().getFileStreamPath(mProperty.getLocalPhoto().getFilename()).getAbsolutePath();
            // delete image on disk
            Log.i(TAG, "Found existing photo at path " + path);
            File file = new File(path);
            file.delete();

            // delete model
            mProperty.deleteLocalPhoto();

            // clean imageview
            Log.i(TAG, "Deleted existing photo!");
            Log.i(TAG, "Clean Image view");

        }

        PictureUtils.cleanImageView(mPhotoView);


    }

    @Override
    public void onPause() {
        super.onPause();
//        CrimeLab.get(getActivity()).saveCrimes();
    }

    @Override
    public void onStart() {
        super.onStart();


         showPhoto();

    }

    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanImageView(mPhotoView);
    }

    private static class FetchStreetView<Bitmap> implements Callable<Bitmap> {


        Bitmap bitmap = null;
        double mLatitude;
        double mLongitude;

        public FetchStreetView(double latitude, double longitude) {
            super();
            this.mLatitude = latitude;
            this.mLongitude = longitude;
        }

        @Override
        public Bitmap call() throws Exception {
            StreetViewFetcher gsv = new StreetViewFetcher(mLatitude, mLongitude, 950, 350);
            String url = gsv.getUrl();
            Log.i(TAG, "FetchStreetView::url : " + url);
            byte[] bitmapBytes = gsv.getUrlBytes(url);
            bitmap = (Bitmap) BitmapFactory
                    .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);

            return (Bitmap) bitmap;
        }

    }


    /**
     * Converts the current imageView resource to a series of bytes
     * @param imageView file to check
     * @return an image bytes
     */
    private byte[] convertImageViewImageToBytes(ImageView imageView)
    {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();

    }


    /**
     * Add extension to a file that doesn't have yet an extension
     * this method is useful to automatically add an extension in the savefileDialog control
     * @param file file to check
     * @param ext extension to add
     * @return file with extension (e.g. 'test.doc')
     */
    private String addFileExtIfNecessary(String file,String ext) {
        if(file.lastIndexOf('.') == -1)
            file += ext;

        return file;
    }
}
