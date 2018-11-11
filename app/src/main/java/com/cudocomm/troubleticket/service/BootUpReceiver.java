package com.cudocomm.troubleticket.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Logcat;
import com.cudocomm.troubleticket.util.Preferences;

public class BootUpReceiver extends BroadcastReceiver {

    public Preferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        preferences = new Preferences(context);
        /****** For Start Activity *****/
       /* Intent i = new Intent(context, MyActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);*/

        /***** For start Service  ****/
        if(!preferences.getPreferencesString(Constants.USER_NAME).isEmpty())
        {
            Service mSensorService = new ServiceGlobal(context);
            if(!isMyServiceRunning(mSensorService.getClass(),context))
            {
                Intent myIntent = new Intent(context, ServiceGlobal.class);
                context.startService(myIntent);
            }
        }

    }
    private boolean isMyServiceRunning(Class<?> serviceClass,Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            // Log.d(TAG, "isMyServiceRunning: "+serviceClass.getName()+" =? "+service.service.getClassName());
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Logcat.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Logcat.i ("isMyServiceRunning?", false+"");
        return false;
    }
}