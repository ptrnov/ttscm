package com.cudocomm.troubleticket.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.activity.LoginActivity;
import com.cudocomm.troubleticket.util.Logcat;
import com.cudocomm.troubleticket.util.SessionManagerGPS;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by adsxg on 6/15/2017.
 */

public class ServiceGlobal extends Service {

    Intent intentFirebase ;
    Intent intentConnectivy;
    private NotificationManager mNM;
    Service messageService;
    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    public int NOTIFICATION = 1174;
    boolean serviceBound=false;
    boolean constatusBound=false;
    Notification notification;
    public int counter=0;
    Service gpsService;
    boolean gpsBound=false;
    // GPSReciever gpsReciever;
    MessegeReciever msreciever;
    List<String> ntfMsg = new ArrayList<>();
    public static String GPSSENDFromGGTY = "GPSSENDggty";
    ConnReceiver connReceiver;
    public static String ConnString="GANZSTRING";
    //gps
    String tag = "";
    SessionManagerGPS smGPS;
    public Location mLastLocation;
    private static final int TWO_MINUTES = 1000 * 30;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */

    public ServiceGlobal(Context applicationContext) {
        super();
        Logcat.i("HERE", "here I am!");
    }

    public ServiceGlobal(){

    }
    public class LocalBinder extends Binder {
        public ServiceGlobal getService() {
            return ServiceGlobal.this;
        }
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        // Display a notification about us starting.  We put an icon in the status bar.
        intentFirebase = new Intent(this, MyFirebaseMessagingService.class);
        intentConnectivy = new Intent(this, ConnectivityChangeReceiver.class);
        initMessageService();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logcat.i("onStartCommand Received start id " + startId + ": " + intent+" : "+gpsBound);
        //startTimer();
        return START_STICKY;
    }
    public boolean isForeground(String myPackage) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        Logcat.d("onStartCommand isForeground: "+componentInfo.getPackageName()+" "+myPackage);
        return componentInfo.getPackageName().equals(myPackage);
    }
    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Logcat.i("in timer", "in timer ++++  "+ (counter++));
                // showNotification(String.valueOf(counter));
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            messageService = ((MyFirebaseMessagingService.LocalBinder)service).getService();
            serviceBound=true;
            Logcat.d("onServiceConnected: message");

        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Logcat.d("onDisconnected: message");
            stopService(intentFirebase);
            serviceBound=false;
            mConnection = null;
        }
    };


    void initMessageService(){
        if (!serviceBound) {
            Logcat.d("initMessageService: created");
            msreciever = new MessegeReciever();

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(MyFirebaseMessagingService.ACTION_MESSAGE);
            registerReceiver(msreciever, intentFilter);

            startService(intentFirebase);
            bindService(intentFirebase, mConnection, Context.BIND_AUTO_CREATE);
            serviceBound = true;
            buildNotification("Connected");
            startForeground(NOTIFICATION, notification);
            initConnectionStatus();
            LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));

        }
    }
    void initConnectionStatus(){
        if (!constatusBound) {
            connReceiver = new ConnReceiver();

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnString);
            startService(intentConnectivy);
            registerReceiver(connReceiver, intentFilter);

            constatusBound = true;
            //buildNotification("Connected");
            //startForeground(NOTIFICATION,notification);
        }
    }

    void stopMessageService(){
        if(serviceBound)
        {
            try {
                stopService(intentFirebase);
                unregisterReceiver(msreciever);
                unbindService(mConnection);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                serviceBound=false;
            }
        }
    }
    void stopConnService(){
        if(constatusBound)
        {

            try {
                stopService(intentConnectivy);
                unregisterReceiver(connReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private BroadcastReceiver onNotice= new WakefulBroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String title = intent.getStringExtra("title");
                String body = intent.getStringExtra("text");

                boolean cansend =false;
                if(!tag.equals(intent.getStringExtra("tag")))
                {
                    tag=intent.getStringExtra("tag");
                    cansend=true;
                }
                Logcat.d("onReceive: notice"+"||"+title+"||"+body);
                if(cansend && (!body.equals("Connected") || !body.equals("offline mode")))
                {

                    ntfMsg.add(title+", "+body);
                    Logcat.d("onReceive: "+ntfMsg.size());

                    if(ntfMsg.size()>1)
                    {
                        sendNotificationV2(ntfMsg);
                    }
                    else
                    {
                        sendNotification(title,body);
                    }
//                    cansend=false;

                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    };

    private class MessegeReciever extends WakefulBroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub

            //int datapassed = arg1.getIntExtra("DATAPASSED", 0);

            Logcat.d("onReceive: MyFirebaseMsgService > Servicew"+arg1.getExtras());

            if(arg1.getStringExtra("title")!=null &&arg1.getStringExtra("body")!=null)
            {

                String title= arg1.getStringExtra("title");
                String body= arg1.getStringExtra("body");
                ntfMsg.add(title+", "+body);
                Logcat.d("onReceive: "+ntfMsg.size());

                if(ntfMsg.size()>1)
                {
                    sendNotificationV2(ntfMsg);
                }
                else
                {
                    sendNotification(title,body);
                }

            }else
            {
                if(arg1.getStringExtra("body")!=null)
                {
                    String title= getString(R.string.app_name);
                    String body= arg1.getStringExtra("body");
                    ntfMsg.add(title+", "+body);
                    Logcat.d("onReceive: "+ntfMsg.size());

                    if(ntfMsg.size()>1)
                    {
                        sendNotificationV2(ntfMsg);
                    }
                    else
                    {
                        AlarmManager alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, arg1, PendingIntent.FLAG_CANCEL_CURRENT);
                        alarmMgr.cancel(alarmIntent);
                        completeWakefulIntent(arg1);
                        sendNotification(title,body);
                    }
                }
            }

            //GPSacc=Float.parseFloat(loc);
            //UpdateGPS(GPSacc);


        }
    }
    private class ConnReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            Logcat.d("onReceive: con rece trigger"+intent.getExtras());
            String sv= "Offline Mode";
            if(intent.getExtras()!=null && intent.getBooleanExtra("data",false))
            {
                Logcat.d("onReceive: con rece trigger"+intent.getBooleanExtra("data",false));
                sv="Connected";
            }
            if(ntfMsg.size()==0)
            {
                buildNotification(sv);
                mNM.notify(NOTIFICATION,notification);
            }

        }

    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);
        // Tell the user we stopped.
        stoptimertask();

        // Tell the user we stopped.

        Intent broadcastIntent = new Intent("RestartSensor");
        sendBroadcast(broadcastIntent);
        stopMessageService();
        stopConnService();
        //stopForeground(true);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
  /*  @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();*/

    /**
     * Show a notification while this service is running.
     */

    void buildNotification(String textBody) {
        Logcat.d("buildNotification: builded");
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("notif","ada");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String RESOURCE_PATH = ContentResolver.SCHEME_ANDROID_RESOURCE + "://";

//        String path = RESOURCE_PATH + getPackageName() + "/raw/emergency_alert";
//        Uri defaultSoundUri = Uri.parse(path);
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);
        CharSequence text = "Service started";
        notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(getResources().getColor(R.color.md_white_1000))
                .setLargeIcon(icon)// the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(getString(R.string.app_name))  // the label of the entry
                .setContentText(textBody)  // the contents of the entry
                .setContentIntent(pendingIntent)  // The intent to send when the entry is clicked
//                .setSound(defaultSoundUri)
                .build();
    }

    private void sendNotification(String title,String messageBody) {
        Logcat.d("sendNotification: ");
//        Intent intent = new Intent(this, MainActivity.class);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String RESOURCE_PATH = ContentResolver.SCHEME_ANDROID_RESOURCE + "://";

        String path = RESOURCE_PATH + getPackageName() + "/raw/reminder_tiket";
        Uri defaultSoundUri = Uri.parse(path);
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);
//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(icon)
                .setPriority(2)
                .setColor(getResources().getColor(R.color.md_white_1000))
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        startForeground(NOTIFICATION,notification.build());
    }
    private void sendNotificationV2(List<String> msg) {
        Logcat.d("sendNotification: ");
        String msgbody = "You have "+msg.size()+" Task";
//        Intent intent = new Intent(this, MainActivity.class);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("notif","ada");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);
//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String RESOURCE_PATH = ContentResolver.SCHEME_ANDROID_RESOURCE + "://";

        String path = RESOURCE_PATH + getPackageName() + "/raw/reminder_tiket";
        Uri defaultSoundUri = Uri.parse(path);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(icon)
                .setPriority(2)
                .setColor(getResources().getColor(R.color.md_white_1000))
                .setContentTitle(getString(R.string.app_name))
                .setContentText(msgbody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        /* Add Big View Specific Configuration */
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();



        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle(msgbody);

        // Moves events into the big view
        for (int i=0; i < msg.size(); i++) {

            String value = msg.get(i);
//            Spannable sb = new SpannableString(value);
            /*
            sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), 8, 40, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/
            inboxStyle.addLine(value);
            //inboxStyle.addLine(msg.get(i));
            Logcat.d("sendNotificationV2: "+msg.get(i));
        }

        notification.setStyle(inboxStyle);

        // NotificationCompat.MessagingStyle msgStyle = new NotificationCompat.MessagingStyle();

       /* NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);*/

        //mNM.notify(NOTIFICATION, notification);
        startForeground(NOTIFICATION,notification.build());
    }
    
}
