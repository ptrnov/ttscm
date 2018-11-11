package com.cudocomm.troubleticket.service;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.cudocomm.troubleticket.BuildConfig;

@android.support.annotation.RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationService extends NotificationListenerService {
    private static final String TAG = NotificationService.class.getSimpleName();
    Context context;

    public void onCreate() {
        Log.d(TAG, "onCreate: sadadasdsadasdasd");
        super.onCreate();
        this.context = getApplicationContext();
    }

    public void onNotificationPosted(StatusBarNotification sbn) {
        if (VERSION.SDK_INT >= 19) {
            String pkg = BuildConfig.VERSION_NAME;
            String title = BuildConfig.VERSION_NAME;
            String text = BuildConfig.VERSION_NAME;
//            String key = BuildConfig.VERSION_NAME;
//            String gkey = BuildConfig.VERSION_NAME;
            String tag = BuildConfig.VERSION_NAME;
            long posttime = 0;
            try {
                pkg = sbn.getPackageName();
                Bundle extras = sbn.getNotification().extras;
                title = extras.getString("android.title");
                text = extras.getCharSequence("android.text").toString();
                if (sbn.getTag() != null) {
                    tag = sbn.getTag();
                }
                posttime = sbn.getPostTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, "onNotificationPosted: " + pkg + "||" + title + "||" + text + "||" + tag + "||" + posttime);
            if (tag.contains("GCM-Notification")) {
                Intent msgrcv = new Intent("Msg");
                msgrcv.putExtra("title", title);
                msgrcv.putExtra("text", text);
                msgrcv.putExtra("tag", tag);
                LocalBroadcastManager.getInstance(this.context).sendBroadcast(msgrcv);
            }
        }
    }

    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg", "Notification Removed");
    }
}
