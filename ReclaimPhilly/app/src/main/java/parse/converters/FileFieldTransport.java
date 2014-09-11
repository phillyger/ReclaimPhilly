package parse.converters;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import com.parse.ParseFile;
import com.parse.ParseObject;
import org.reclaimphilly.android.LocalPhoto;

import java.io.ByteArrayOutputStream;

/**
 * Created by gosullivan on 2/28/14.
 */
public class FileFieldTransport
implements IFieldTransport
{

    ParseObject po;
    Parcel p;
    int d = IFieldTransport.DIRECTION_FORWARD;

    public FileFieldTransport(ParseObject inpo, Parcel inp)
    {
        this(inpo,inp,DIRECTION_FORWARD);
    }

    public FileFieldTransport(ParseObject inpo, Parcel inp, int direction)
    {
        po = inpo;
        p = inp;
        d = direction;
    }
    @Override
    public void transfer(ValueField f)
    {
        //1
        if (d == DIRECTION_BACKWARD)
        {
            //parcel to parseobject

            Bitmap bitmap = (Bitmap)p.readParcelable(Bitmap.class.getClassLoader());
            if (bitmap != null) {

                ParseFile parseFile = new ParseFile("street_view.png", bitmapToByteArray(bitmap));
                po.put(f.name, parseFile);
            }
        }
        else
        {
            //forward
            //parseobject to parcel

            ParseFile parseFile = po.getParseFile(f.name);

            if (parseFile != null) {
            LocalPhoto photo = LocalPhoto.fromParseFile(parseFile);

            Bitmap bitmap = photo.getImageBitmap();

            // hey, BitmapDrawable class implements Parcelable.
            // so we can write the object directly
            p.writeParcelable(bitmap, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
            } else {
                p.writeValue(null);
            }


        }
    }

    public static byte[] bitmapToByteArray(Bitmap bmp)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }


}
