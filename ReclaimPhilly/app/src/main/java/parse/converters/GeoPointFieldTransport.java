package parse.converters;

import android.os.Parcel;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

/**
 * Created by gosullivan on 2/26/14.
 */
public class GeoPointFieldTransport
implements IFieldTransport
{

    ParseObject po;
    Parcel p;
    int d = IFieldTransport.DIRECTION_FORWARD;

    public GeoPointFieldTransport(ParseObject inpo, Parcel inp)
    {
        this(inpo,inp,DIRECTION_FORWARD);
    }

    public GeoPointFieldTransport(ParseObject inpo, Parcel inp, int direction)
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

            String s = p.readString();
            ParseGeoPoint parseGeoPoint = GeoPoint.toParseGeoPoint(s);


            po.put(f.name, parseGeoPoint);
        }
        else
        {
            //forward
            //parseobject to parcel

            ParseGeoPoint parseGeoPoint = po.getParseGeoPoint(f.name);

            String s = GeoPoint.fromParseGeoPoint(parseGeoPoint).toString();
            p.writeString(s);




        }
    }


}
