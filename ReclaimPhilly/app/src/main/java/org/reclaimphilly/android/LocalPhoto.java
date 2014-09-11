package org.reclaimphilly.android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.io.ByteArrayOutputStream;

/**
 * Created by gosullivan on 2/28/14.
 */
public class LocalPhoto
{

    private static final String TAG = "Photo";

//    private static final String JSON_FILENAME = "filename";


    private String mFilename;
    private Bitmap mImageBitmap;

    /** Create a Photo representing an existing image on disk **/

    public LocalPhoto(String filename, Bitmap imageBitmap) {
        mFilename = filename;
        mImageBitmap = imageBitmap;

    }


    public LocalPhoto(String filename, byte[] bitmapdata) {
        mFilename = filename;
        mImageBitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

    }

    public LocalPhoto(Activity activity, String filename) {

        mFilename = filename;

        String path = activity.getFileStreamPath(filename).getAbsolutePath();
        mImageBitmap = BitmapFactory.decodeFile(path);

    }

    // Getters
    public String getFilename() {
        return mFilename;
    }

    public Bitmap getImageBitmap() { return mImageBitmap; }

    // Setters
    public void setFilename(String filename) { mFilename = filename; }

    public void setImageBitmap(Bitmap bitmap) { mImageBitmap = bitmap; }



    public static LocalPhoto fromParseFile(ParseFile pf)
    {
        LocalPhoto photo = null;

        if (pf == null) return null;
        //pf is available
        try {
            byte[] bitmapBytes = pf.getData();

            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);

            photo = new LocalPhoto(pf.getName(), bitmap);

        } catch (ParseException pe) {
            Log.e(TAG, "unable to create bitmap");
        }

        return photo;

    }



    public static byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer);
        return buffer.toByteArray();
    }

}
