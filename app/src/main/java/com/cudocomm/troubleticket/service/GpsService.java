package com.cudocomm.troubleticket.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Logcat;
import com.cudocomm.troubleticket.util.Preferences;
import com.cudocomm.troubleticket.util.SessionManagerGPS;

import java.util.Calendar;

public class GpsService extends Service {

    public Preferences preferences = new Preferences(this);

    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 500;
    private static final float LOCATION_DISTANCE = 10f;
    public float acc = 0;
    public int process = 0;
    SessionManagerGPS smGPS;
    public Location mLastLocation;
    public static String GPSSEND = "GPSSEND";
    private final IBinder mBinder = new LocalBinder();
    private static final int TWO_MINUTES = 1000 * 30;

    public Location targetLocation;
    public Location myLocation;

    Intent intent;

    public class LocationListener implements android.location.LocationListener {


        public LocationListener(String provider) {
            Logcat.e("LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            if (isBetterLocation(location, mLastLocation)) {
                Calendar cal = Calendar.getInstance();
//                TimeZone tz = cal.getTimeZone();

                long havetime = 0;
                float acc = 0;
                try {
                    havetime = Long.parseLong(smGPS.getDetails().get(SessionManagerGPS.TIME));
                    acc = Float.parseFloat(smGPS.getDetails().get(SessionManagerGPS.ACC));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (isValidtoSession(location.getTime(), havetime, location.getAccuracy(), acc)) {
                    smGPS.createSession(String.valueOf(location.getAccuracy()), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), String.valueOf(location.getTime()));
                    Logcat.d("onReceive: update gps " + String.valueOf(location.getAccuracy()) + "||" + String.valueOf(location.getLatitude()) + "||" + String.valueOf(location.getLongitude()) + "||" + String.valueOf(location.getTime()));

                } else

                {
                    Logcat.d("onLocationChanged: not put to session" + String.valueOf(location.getAccuracy()) + "||" + String.valueOf(location.getLatitude()) + "||" + String.valueOf(location.getLongitude()) + "||" + String.valueOf(location.getTime()));
                }

//                kuru
                targetLocation = new Location("SITE");
                /*targetLocation.setLatitude(-6.3285737);
                targetLocation.setLongitude(106.680796);*/
                targetLocation.setLatitude(new Double(preferences.getPreferencesString(Constants.SELECTED_STATION_LAT)));
                targetLocation.setLongitude(new Double(preferences.getPreferencesString(Constants.SELECTED_STATION_LONG)));
                myLocation = location;
                double distance=targetLocation.distanceTo(myLocation);
//                kuru

                intent.putExtra("GPS", String.valueOf(location.getAccuracy()));
                intent.putExtra("DISTANCE", String.valueOf(distance));
                sendBroadcast(intent);
                mLastLocation = location;
            }


        }

        boolean isValidtoSession(Long current, Long havetime, float currentAcc, float haveAcc) {

            long millse = current - havetime;
            long mills = Math.abs(millse);

            int Hours = (int) (mills / (1000 * 60 * 60));
            int Mins = (int) (mills / (1000 * 60)) % 60;
            long Secs = (int) (mills / 1000) % 60;

            if (currentAcc <= Constants.C_GPS_ACC) {
                Logcat.d("isValidtoSession: " + Hours + ":" + ":" + Mins + ":" + Secs);
                if (currentAcc <= haveAcc) {
                    return true;
                } else
                    return mills > (15 * 60 * 1000);
            } else {
                return false;
            }


        }

        @Override
        public void onProviderDisabled(String provider) {
            mLastLocation.reset();
            Toast.makeText(getApplicationContext(), "Gps Disabled Please Make Sure Your GPS is Enable", Toast.LENGTH_SHORT).show();
            Logcat.e("onProviderDisabled: " + provider);

        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();

            Logcat.e("onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Logcat.e("onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public GpsService getService() {
            return GpsService.this;
        }
    }

    @Override
    public void onCreate() {
        Logcat.e("onCreate");
        intent = new Intent(GPSSEND);
        initializeLocationManager();
        smGPS = new SessionManagerGPS(this);
        getLocation();
    }


    public void getLocation() {
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Logcat.i("fail to request location update, ignore : " + ex);
        } catch (IllegalArgumentException ex) {
            Logcat.d("network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Logcat.i("fail to request location update, ignore : " + ex);
        } catch (IllegalArgumentException ex) {
            Logcat.d("gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Logcat.e("onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Logcat.i("fail to remove location listners, ignore : " + ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Logcat.e("initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
