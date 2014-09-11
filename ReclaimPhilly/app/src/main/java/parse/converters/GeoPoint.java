package parse.converters;

import android.os.Parcel;
import android.os.Parcelable;
import com.parse.ParseGeoPoint;

/**
 * Created by gosullivan on 2/26/14.
 */
public class GeoPoint implements Parcelable
{

    public double mLatitude;
    public double mLongitude;

    public GeoPoint(double latitude, double longitude){
        mLatitude = latitude;
        mLongitude = longitude;
    }


    public static final Parcelable.Creator<GeoPoint> CREATOR
            = new Parcelable.Creator<GeoPoint>() {
        public GeoPoint createFromParcel(Parcel in) {
            return new GeoPoint(in);
        }

        public GeoPoint[] newArray(int size) {
            return new GeoPoint[size];
        }
    }; //end of creator

    //
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
    }

    public GeoPoint(Parcel in){
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
    }

    public String toString()
    {
        return mLatitude + "," + mLongitude;
    }

    public static GeoPoint fromParseGeoPoint(ParseGeoPoint pgeo)
    {
        if (pgeo == null) return null;
        //pgeo is available
        double lat = pgeo.getLatitude();
        double lng = pgeo.getLongitude();

        return new GeoPoint(lat, lng);
    }

    public static ParseGeoPoint toParseGeoPoint(String s)
    {
        String [] geoPointComps = s.split(",");
        double lat = Double.parseDouble(geoPointComps[0]);
        double lng = Double.parseDouble(geoPointComps[1]);
        ParseGeoPoint parseGeoPoint = new ParseGeoPoint(lat,lng);
        return parseGeoPoint;
    }
}
