package org.reclaimphilly.android;

import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import parse.converters.ValueField;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single Property such as a vacant lot, residential or non-residential property.
 * 
 * This is thread safe and implements Parcelable for passing between Activities.
 * In this case, the Property simply reuses the raw JSON String to pass around
 * so that JSONObject can do the heavy lifting.
 * 
 * In the real world, the data for each of these objects would be coming from
 * a server somewhere, so this object is modeled after a possible JSON
 * representation.
 * 
 * @author gosullivan
 */
public class Property extends ParseObjectWrapper {
	private static final String TAG = "Property";

    public static String t_tablename = "PropertyObject";

    public static String PARCELABLE_PROPERTY_ID = "PropertyObjectId";

//	public static final String JSON_KEY_STREET_ADDRESS = "address";
//    public static final String JSON_KEY_LONGITUDE = "longitude";
//    public static final String JSON_KEY_LATITUDE = "latitude";
//    public static final String JSON_KEY_TYPE = "type";
//    public static final String JSON_KEY_PHOTO = "photo";


    public static final String f_street_address = "address";
    public static final String f_geopoint = "geopoint";
    public static final String f_type = "type";
    public static final String f_photo = "photo";
    public static final String f_description = "description";




    /**
	 * The longitude of the location
	 */
//	private double mLongitude;


    /**
	 * The latitude of the location
	 */
//	private double mLatitude;


    /**
     * The GeoPoint of the location
     */
//    private ParseGeoPoint mGeoPoint;

	
	/**
	 * The type of property
	 */
//	private String mType;
	
	/**
	 * A photo/image of the property
	 */
	private LocalPhoto mLocalPhoto;
	
	/**
	 * The raw JSON representation of the object
	 */
//	private final String mRawJson;

    /**
	 * The street address like "123 Main St."
	 */
//	private String mStreetAddress;


    public Property(String streetAddress, String propertyType, String description, ParseGeoPoint geoPoint, ParseFile photo)
    {
        super(t_tablename);
        setStreetAddress(streetAddress);
        setType(propertyType);
        setGeoPoint(geoPoint);
        setPhoto(photo);
        setDescription(description);
    }

    public Property(String streetAddress, String propertyType, ParseGeoPoint geoPoint)
    {
        super(t_tablename);
        setStreetAddress(streetAddress);
        setType(propertyType);
        setGeoPoint(geoPoint);
    }

    public Property(String streetAddress, ParseGeoPoint geoPoint)
    {
        super(t_tablename);
        setStreetAddress(streetAddress);
        setGeoPoint(geoPoint);
    }

    public Property(String streetAddress, ParseGeoPoint geoPoint, ParseFile photo)
    {
        super(t_tablename);
        setStreetAddress(streetAddress);
        setPhoto(photo);
        setGeoPoint(geoPoint);
    }

    public Property(ParseObject po)
    {
        super(po);
    }

    public Property(ParseObjectWrapper inPow)
    {
        super(inPow);
    }
//
//	public Property(JSONObject json) throws JSONException {
//		mLongitude = json.getDouble(JSON_KEY_LONGITUDE);
//        mLatitude = json.getDouble(JSON_KEY_LATITUDE);
//        if (json.has(JSON_KEY_TYPE))
//		    mType = json.getString(JSON_KEY_TYPE);
//        if (json.has(JSON_KEY_PHOTO))
//            mPhoto = new Photo(json.getJSONObject(JSON_KEY_TYPE));
//		mStreetAddress = json.getString(JSON_KEY_STREET_ADDRESS);
//		mRawJson = json.toString();
//	}

	@Override
    public int describeContents() {
	    return 0;
    }


    /**
     * Set the street address of the property
     *
     * @return ParseGeoPoint
     */
    public ParseGeoPoint getGeoPoint() {
        return po.getParseGeoPoint(f_geopoint);
    }

    /**
     * Returns the type of the property (i.e. lot, res, non-res)
     *
     * @return the type
     */
    public String getType() {

        return po.getString(f_type);
    }

    /**
     * Returns the photo of the property
     *
     * @return the photo
     */
    public ParseFile getPhoto() {
        return po.getParseFile(f_photo);
    }

	/**
	 * Returns the street address like "123 Main St."
	 * 
	 * @return the streetAddress
	 */
	public String getStreetAddress() {
        return po.getString(f_street_address);
	}

    /**
     * Returns the description
     *
     * @return the description
     */
    public String getDescription() {
        return po.getString(f_description);
    }

    /**
     * Set the type of the property
     *
     * @param type
     */
    public void setType(String type) {
        po.put(f_type, type);
    }

    /**
     * Set the photo of the property
     *
     * @param photo
     */
    public void setPhoto(ParseFile photo) {
        po.put(f_photo, photo);
    }

    /**
     * Set the street address of the property
     *
     * @param streetAddress
     */
    public void setStreetAddress(String streetAddress) {
        po.put(f_street_address, streetAddress);
    }

    /**
     * Set the street address of the property
     *
     * @param description
     */
    public void setDescription(String description) {
        po.put(f_description, description);
    }


    /**
     * Set the gep point of the property
     *
     * @param geoPoint
     */
    public void setGeoPoint(ParseGeoPoint geoPoint) {

        po.put(f_geopoint, geoPoint);
    }




    public LocalPhoto getLocalPhoto() {
        return mLocalPhoto;
    }

    public void setLocalPhoto(LocalPhoto localPhoto) {
        mLocalPhoto = localPhoto;
    }


    public void deleteLocalPhoto() {
        mLocalPhoto = null;
    }

    public String toString()
    {
        String streetAddress = getStreetAddress();
        String type =(getType()!=null) ? getType() : "" ;
        String lat = String.valueOf(getGeoPoint().getLatitude());
        String lng = String.valueOf(getGeoPoint().getLongitude());

        return streetAddress + "/" + type + "/" + lat + "/" + lng;
    }

    //have the children override this
    @Override
    public List<ValueField> getFieldList()
    {
        ArrayList<ValueField> fields = new ArrayList<ValueField>();
        fields.add(ValueField.getStringField(Property.f_street_address));
        fields.add(ValueField.getGeoPointField(Property.f_geopoint));

//        fields.add(ValueField.getStringField(Property.f_type));
//        fields.add(ValueField.getFileField(Property.f_photo));
        fields.add(ValueField.getStringField(Property.f_description));

        return fields;
    }
}
