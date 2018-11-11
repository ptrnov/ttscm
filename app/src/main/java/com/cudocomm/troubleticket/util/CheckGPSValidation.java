package com.cudocomm.troubleticket.util;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import com.cudocomm.troubleticket.component.CustomPopConfirm;

import java.util.Calendar;
import java.util.TimeZone;

public class CheckGPSValidation {
    Context context;
    SessionManagerGPS smGPS;
    CustomPopConfirm popConfirm;
//    DialogOeganz dialoGanz;
    private static final String TAG = CheckGPSValidation.class.getSimpleName();
    public CheckGPSValidation(Context context)
    {
        this.context=context;
        smGPS = new SessionManagerGPS(context);
//        dialoGanz = new DialogOeganz(context);
    }

    public boolean isValidAcc(){
        double acc =100000;
        long gpsTime=0;
        if (smGPS.getDetails().get(SessionManagerGPS.ACC)!=null) {
            try {
                 acc = Double.parseDouble(smGPS.getDetails().get(SessionManagerGPS.ACC));
                 gpsTime= Long.parseLong(smGPS.getDetails().get(SessionManagerGPS.TIME));
                Calendar cal = Calendar.getInstance();
                TimeZone tz = cal.getTimeZone();
                long thistime =System.currentTimeMillis()+tz.getRawOffset();
                Logcat.d("isValidAcc: from ses "+(thistime-gpsTime)+"<= now"+Constants.C_VALID_GPS_TIME);
                if((thistime-gpsTime)<Constants.C_VALID_GPS_TIME)
                {
                    if(acc<=Constants.C_GPS_ACC){
                        return true;
                    }
                    else
                    {
                        showToast("Can't get valid GPS 1");
                        return false;
                    }

                }

                else
                {
                    showToast("Can't get valid GPS 2 ");
                    return false;
                }


            } catch (NumberFormatException e) {
                showToast("Error,"+e.getMessage());
                e.printStackTrace();
                showToast("Can't get valid GPS 3");
                return false;
            }
        }
        else
        {
            return false;
        }

    }
    //distance
    public boolean isValidDistance(float dLat,float dLon,float dRad){
        try {

            float distance;
            float cLat = Float.parseFloat(smGPS.getDetails().get(SessionManagerGPS.LAT));
            float cLon = Float.parseFloat(smGPS.getDetails().get(SessionManagerGPS.LON));

            Location locCurrent=new Location("Current");
            Location data = new Location("data");

            locCurrent.setLatitude(cLat);
            locCurrent.setLongitude(cLon);
            data.setLatitude(dLat);
            data.setLongitude(dLon);
            distance=radiusFromSite(locCurrent,data);
            if(distance<=dRad)
            {
                return true;
            }
            else
            {
                showToast("your location is too far..<br>  Your distance From location "+distance+"m"+"<br>" + "your location is "+cLat+","+cLon);
//                dialoGanz.showAlert(SweetAlertDialog.ERROR_TYPE,"your location is too far..<br>  Your distance From location "+distance+"m"+"<br>" +
//                        "your location is "+cLat+","+cLon);
                return false;
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }
    Float radiusFromSite(Location current,Location Site){

        return current.distanceTo(Site);
    }

    void showToast(String text){

        Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
    }
}
