package com.cudocomm.troubleticket.service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cudocomm.troubleticket.util.Logcat;

public class RestarterService extends BroadcastReceiver {
    private static final String TAG = RestarterService.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Logcat.i(RestarterService.class.getSimpleName(), "Service restarted! Oooooooooooooppppssssss!!!!");
       /* if (!isMyServiceRunning(ServiceGlobalEzGG.class,context)) {
            context.startService(new Intent(context, ServiceGlobalEzGG.class));
        }*/



        context.startService(new Intent(context, ServiceGlobal.class));
        //Toast.makeText(context, "Service Restarted", Toast.LENGTH_SHORT).show();
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