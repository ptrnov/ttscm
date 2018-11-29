package com.cudocomm.troubleticket.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.activity.MainActivity;
import com.cudocomm.troubleticket.activity.PopupNotif;
import com.cudocomm.troubleticket.component.PopupNotification;
import com.cudocomm.troubleticket.util.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    public static String ACTION_MESSAGE ="messege";
    Intent godIntent;
    Builder notificationBuilder;

    Bitmap image;

    private PopupNotification popupNotification;

    @Override
    public void onCreate() {
        super.onCreate();
        godIntent = new Intent(ACTION_MESSAGE);
        Log.d(TAG, "onCreate: created ");
    }

    public class LocalBinder extends Binder {
        public MyFirebaseMessagingService getService() {
            return MyFirebaseMessagingService.this;
        }
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Map<String, String> data = remoteMessage.getData();
            sendNotification(data.get("tag"), data.get("body"), image, data.get("title"));
        }
    }

    public void sendNotification(String tag, String messageBody, Bitmap img, String title) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        String RESOURCE_PATH = ContentResolver.SCHEME_ANDROID_RESOURCE + "://";

        String path1 = RESOURCE_PATH + getPackageName() + "/raw/udivice_login";         //Login
        String path2 = RESOURCE_PATH + getPackageName() + "/raw/remainder_ticket";      //
        String path3 = RESOURCE_PATH + getPackageName() + "/raw/emergency_alert";
        String path4 = RESOURCE_PATH + getPackageName() + "/raw/kadiv_remainder";
        Uri defaultSoundUri1 = Uri.parse(path1);
        Uri defaultSoundUri2 = Uri.parse(path2);
        Uri defaultSoundUri3 = Uri.parse(path3);
        Uri defaultSoundUri4 = Uri.parse(path4);

        if (title.equals("Notification of new Device Login")) {
            notificationBuilder = new Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(img))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri1)
                    .setContentIntent(pendingIntent);
        }else if(title.equals("Escalation ticket")){
            notificationBuilder = new Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri3)
                    .setContentIntent(pendingIntent);
        }else if(title.equals("Do Action")){
            notificationBuilder = new Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri2)
                    .setContentIntent(pendingIntent);
        }else if(title.equals("Ticket Need Your Confirm")){
            notificationBuilder = new Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri2)
                    .setContentIntent(pendingIntent);
        }else if(title.equals("message from Kadiv")){
            notificationBuilder = new Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri4)
                    .setContentIntent(pendingIntent);
        }else{
            notificationBuilder = new Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri3)
                    .setContentIntent(pendingIntent);
        }

//        if (tag != null && tag.equalsIgnoreCase("image")) {
//            notificationBuilder = new Builder(this)
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setContentTitle(title)
//                    .setContentText(messageBody)
//                    .setStyle(new NotificationCompat.BigPictureStyle()
//                            .bigPicture(img))
//                    .setAutoCancel(true)
//                    .setSound(defaultSoundUri)
//                    .setContentIntent(pendingIntent);
//        } else {
//            notificationBuilder = new Builder(this)
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setContentTitle(title)
//                    .setContentText(messageBody)
//                    .setAutoCancel(true)
//                    .setSound(defaultSoundUri1)
//                    .setContentIntent(pendingIntent);
//        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(NotificationManager.IMPORTANCE_NONE, notificationBuilder.build());
        notificationManager.notify(NotificationManager.IMPORTANCE_MAX, notificationBuilder.build());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    private void launch_activity(String msg) {
        Intent i = new Intent();
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, i,0);

//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        i.putExtra(Constants.EXTRA_MESSAGE, msg);
        i.setAction("android.intent.action.MAIN");
        i.setClass(this, PopupNotif.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        startActivity(i);
    }

    private void sendNotification(String title, CharSequence message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(67108864);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(575857, new Builder(this).setStyle(
                new NotificationCompat.BigTextStyle().bigText(message)).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title).setAutoCancel(true).setSound(RingtoneManager.getDefaultUri(2)).setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)).build());
    }

}
