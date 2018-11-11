package com.cudocomm.troubleticket.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by root on 13/06/16.
 */
public class SessionManagerGPS {
    // Shared Preferences reference
    SharedPreferences pref;

    // Editor reference for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREFER_NAME = "LA_PrefGPS";

    // All Shared Preferences Keys
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";

    // User name (make variable public to access from outside)
    public static final String LAT = "lat";
    public static final String LON = "lon";
    public static final String ACC = "acc";
    public static final String TIME = "time";
    //public static final String VERSI = "versidefinition";



    //public static final String _PASSWORD = "password";


    // Constructor
    public SessionManagerGPS(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //Create login session
    public void createSession(String acc, String lat, String lon, String time ){
        // Storing login value as TRUE
        editor.putBoolean(IS_USER_LOGIN, true);

        // Storing name in pref
        editor.putString(ACC, acc);
        editor.putString(LAT, lat);
        editor.putString(LON, lon);
        // Storing email in pref
        editor.putString(TIME, time);
        //editor.putString(VERSI, versidefinition);


        // Storing gerai_id in pref


        // commit changes
        editor.commit();
    }

    /**
     * Check login method will check user login status
     * If false it will redirect user to login page
     * Else do anything




    /**
     * Get stored session data
     * */
    public HashMap<String, String> getDetails(){

        //Use hashmap to store user credentials
        HashMap<String, String> user = new HashMap<>();
        user.put(ACC, pref.getString(ACC, null));
        user.put(LAT, pref.getString(LAT, null));
        user.put(LON, pref.getString(LON, null));
        user.put(TIME, pref.getString(TIME, null));

        // return user
        return user;
    }


}
