package org.reclaimphilly.android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.util.Log;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.io.ByteArrayOutputStream;

/**
 * Created by gosullivan on 7/17/13.
 */
public class Photo
{

    private static final String TAG = "Photo";

//    private static final String JSON_FILENAME = "filename";


    private String mFilename;
    private Bitmap mImageBitmap;
    private ParseFile mImageFile;

    /** Create a Photo representing an existing image on disk **/

    public Photo(String filename, Bitmap imageBitmap, ParseFile imageFile) {
        mFilename = filename;
        mImageBitmap = imageBitmap;
//        mImageFile = imageFile;
    }


    public Photo(String filename, Bitmap imageBitmap, Activity activity) {
        mFilename = filename;
        mImageBitmap = imageBitmap;

        mImageFile = setImageFile(filename, getImageBitmap(), activity.getBaseContext());
    }

    public Photo(String filename, Bitmap imageBitmap, Context context) {
        mFilename = filename;
        mImageBitmap = imageBitmap;

        mImageFile = setImageFile(filename, getImageBitmap(), context);
    }

    public Photo(String filename, byte[] bitmapdata, Context context) {
        mFilename = filename;
        mImageBitmap = BitmapFactory.decodeByteArray(bitmapdata , 0, bitmapdata .length);

        mImageFile = setImageFile(filename, getImageBitmap(), context);
    }

    public Photo(Activity activity, String filename) {

        mFilename = filename;

        String path = activity.getFileStreamPath(filename).getAbsolutePath();
        mImageBitmap = BitmapFactory.decodeFile(path);

        mImageFile = setImageFile(filename, getImageBitmap(), activity.getBaseContext());
    }

    // Getters
    public String getFilename() {
        return mFilename;
    }

    public Bitmap getImageBitmap() { return mImageBitmap; }

    public ParseFile getImageFile() {
        return mImageFile;
    }

    // Setters
    public void setFilename(String filename) { mFilename = filename; }

    public void setImageBitmap(Bitmap bitmap) { mImageBitmap = bitmap; }

    public void setImageFile(ParseFile parseFile) {
        mImageFile = parseFile;
    }


    public Photo(Parcel in)
    {
        mImageBitmap = (Bitmap) in.readParcelable(Bitmap.class.getClassLoader());
    }


    public static Photo fromParseFile(ParseFile pf)
    {
        Photo photo = null;

        if (pf == null) return null;
        //pf is available
        try {
            byte[] bitmapBytes = pf.getData();
//            ByteArrayInputStream is = new ByteArrayInputStream(bitmapBytes, 0, bitmapBytes.length);
//            Drawable d = new BitmapDrawable(is);

//            Bitmap bitmap = BitmapFactory.decodeStream(is);

            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);

            photo = new Photo(pf.getName(), bitmap, pf);

        } catch (ParseException pe) {
            Log.e(TAG, "unable to create bitmap");
        }

        return photo;

    }

    public ParseFile setImageFile(String filename, Bitmap bitmap, Context context)
    {

        return new ParseFile(filename, convertBitmapToByteArray(bitmap, context));
    }



    public static byte[] convertBitmapToByteArray(Bitmap bitmap, Context context) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer);
        return buffer.toByteArray();
    }

}
