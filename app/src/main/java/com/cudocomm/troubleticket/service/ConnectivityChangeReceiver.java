package com.cudocomm.troubleticket.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.cudocomm.troubleticket.util.Logcat;

public class ConnectivityChangeReceiver extends BroadcastReceiver {
private static final String TAG = ConnectivityChangeReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        // Explicitly specify that which service class will handle the intent.
      /*  ComponentName comp = new ComponentName(context.getPackageName(),
                ServiceGlobalEzGG.class.getName());*/
        Logcat.d(TAG, "onReceive: "+isConnected(context));
       Intent i = new Intent();
        i.putExtra("data",isConnected(context));
        i.setAction(ServiceGlobal.ConnString);
        //startService(context, (intent.setComponent(comp)));
        context.sendBroadcast(i);
    }

    public  boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null)
        {

            Logcat.d(TAG, "isConnected: 1 "+networkInfo.getState());
            Logcat.d(TAG, "isConnected: 2 "+networkInfo.getExtraInfo());
            Logcat.d(TAG, "isConnected: 3  "+networkInfo.getDetailedState());
            Logcat.d(TAG, "isConnected: 4 "+networkInfo.getReason());
            Logcat.d(TAG, "isConnected: 5 "+networkInfo.getSubtypeName());

        }
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

}