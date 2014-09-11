package org.reclaimphilly.android;

import android.app.Application;
import android.content.Context;
import com.parse.Parse;
import com.parse.ParseACL;

/**
 * Created by gosullivan on 3/23/14.
 */
public class ReclaimPhilly extends Application {

    private static String tag = "ReclaimPhilly";

    private static Context context;



    @Override
    public void onCreate() {
        super.onCreate();


        // Add your initialization code here
        Parse.initialize(this, BuildConfig.PARSE_APPLICATION_ID, BuildConfig.PARSE_CLIENT_KEY);

        //This will automatically create an annonymous user
        //The data associated to this user is abandoned when it is
        //logged out.
        //ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        // If you would like all objects to be private by default, remove this line.
        defaultACL.setPublicReadAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);

    }

    public static Context getAppContext() {
        return ReclaimPhilly.context;
    }

}
