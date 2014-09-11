package org.reclaimphilly.android;

import com.parse.ParseGeoPoint;

import java.util.ArrayList;
import java.util.Random;

/**
 * Creates Property objects with test data.
 * 
 * @author gosullivan
 */
public class PropertyTestUtils {

	private static final String[] COORDINATES = {
		"39.1, -75.2", "39.1, -75.2", "39.1, -75.2", "39.1, -75.2",
        "39.1, -75.2", "39.1, -75.2", "39.1, -75.2", "39.1, -75.2"
	};

	private static final String[] TYPES = {
		"res", "res", "nrs", "nrd",
		"res", "lot", "lot", "res",
	};


	private static final String[] STREET_ADDRESSES = { 
		"2007 Bainbridge Street",
		"2001 Market Street",
		"200 Chestnut Street",
		"5959 Aggravated Drive NE. A905",
		"7010 Old Cedar Drive",
	};

	private final Random mRandom;

	/**
	 * Constructs a new PropertyTestUtils object with the specified seed
	 * 
	 * @param seed
	 *            long to seed the {@link java.util.Random} with
	 */
	public PropertyTestUtils(long seed) {
		mRandom = new Random(seed);
	}

	/**
	 * Returns ArrayList of Property objects
	 * 
	 * @param count
	 *            int number of Property objects to return
	 * @return ArrayList of Property objects
	 */
	public ArrayList<Property> getNewProperties(int count) {
		final ArrayList<Property> list = new ArrayList<Property>();
		for (int i = 0; i < count; i++) {
			list.add(getNewProperty());
		}
		return list;
	}

	/**
	 * Returns new Property filled with test data
	 * 
	 * @return new Property filled with test data
	 */
	public Property getNewProperty() {


	    int randomValue = mRandom.nextInt(COORDINATES.length);
        String[] myCoordinates = COORDINATES[randomValue].split(",");


        randomValue = mRandom.nextInt(TYPES.length);
        String type = TYPES[randomValue];

	    randomValue = mRandom.nextInt(STREET_ADDRESSES.length);
        String streetAddress = STREET_ADDRESSES[randomValue];



        ParseGeoPoint pgeo = new ParseGeoPoint(Double.parseDouble(myCoordinates[0]), Double.parseDouble(myCoordinates[1]));

        Property property = new Property(streetAddress, type, pgeo);

        return property;

	}
}
