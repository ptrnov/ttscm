package com.cudocomm.troubleticket.activity;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.database.DatabaseHelper;
import com.cudocomm.troubleticket.fragment.Home;
import com.cudocomm.troubleticket.fragment.HomeEngineer;
import com.cudocomm.troubleticket.fragment.HomeKadepInfra;
import com.cudocomm.troubleticket.fragment.HomeKadepTSV2;
import com.cudocomm.troubleticket.fragment.HomeKadiv;
import com.cudocomm.troubleticket.fragment.HomeV2;
import com.cudocomm.troubleticket.fragment.MyApproval;
import com.cudocomm.troubleticket.fragment.MyTicketV2;
import com.cudocomm.troubleticket.fragment.MyVisit;
import com.cudocomm.troubleticket.fragment.NavDrawFragment;
import com.cudocomm.troubleticket.model.Assignment;
import com.cudocomm.troubleticket.service.GpsService;
import com.cudocomm.troubleticket.service.NotificationService;
import com.cudocomm.troubleticket.service.ServiceGlobal;
import com.cudocomm.troubleticket.util.ApiClient;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Logcat;
import com.cudocomm.troubleticket.util.OnMenuSelected;
import com.cudocomm.troubleticket.util.Preferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import okhttp3.FormBody;

public class MainActivityOld extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener, OnMenuSelected {

    private TextView breadcrumb;
    private Boolean clearFragment = Boolean.FALSE;
    private View fragmentId;
    FragmentTransaction ft;
    private DrawerLayout mDrawerLayout;
    private NavDrawFragment navDraw;
    private Preferences pref;
    private Toolbar toolbar;

    private File propFile;

    boolean gpsBound=false;
    Service gpsService;
    GPSReciever gpsReciever;
    float GPSacc;


    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();
    Assignment selectedAssignment;

    Intent mServiceIntent;
    Service mSensorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fabric.with(this, new Crashlytics());

        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);


        setContentView(R.layout.activity_main);

        pref = new Preferences(this);

        CommonsUtil.appPermission(this);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        breadcrumb = (TextView) findViewById(R.id.breadcrumb);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        setupNavDraw();
        if (savedInstanceState == null) {
            if(pref.getPreferencesInt(Constants.POSITION_ID) == Constants.TECHNICIAN) {
                setPage(Constants.HOME_PAGE, new Home(), Boolean.TRUE);
            } else if(pref.getPreferencesInt(Constants.POSITION_ID) == Constants.KST
                    || pref.getPreferencesInt(Constants.POSITION_ID) == Constants.KORWIL
                    || pref.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_WIL) {
                setPage(Constants.HOME_PAGE, new HomeV2(), Boolean.TRUE);
            }
            /*else if(pref.getPreferencesInt(Constants.POSITION_ID) == Constants.KORWIL) {
                setPage(Constants.HOME_PAGE, new HomeKorwil(), Boolean.valueOf(true));
            }  else if(pref.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_WIL) {
                setPage(Constants.HOME_PAGE, new HomeKadepwil(), Boolean.valueOf(true));
            } */
            else if(pref.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_TS) {
                setPage(Constants.HOME_PAGE, new HomeKadepTSV2(), Boolean.TRUE);
            } else if(pref.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_INFRA) {
                setPage(Constants.HOME_PAGE, new HomeKadepInfra(), Boolean.TRUE);
            } else if(pref.getPreferencesInt(Constants.POSITION_ID) == Constants.ENGINEER) {
                setPage(Constants.HOME_PAGE, new HomeEngineer(), Boolean.TRUE);
            } else if(pref.getPreferencesInt(Constants.POSITION_ID) == Constants.KADIV) {
                setPage(Constants.HOME_PAGE, new HomeKadiv(), Boolean.TRUE);
            } else if(pref.getPreferencesInt(Constants.POSITION_ID) == Constants.CBTO) {
                setPage(Constants.HOME_PAGE, new HomeKadiv(), Boolean.TRUE);
            }
        }

        refreshNotif();

    }

    private void runNotificationService(){
        Log.d(getClass().getSimpleName(), "Setting alarm!!");

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Calendar cur_cal = Calendar.getInstance();
        cur_cal.setTimeInMillis(System.currentTimeMillis());

        Intent intent = new Intent(this, NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,intent,0);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                15 * 60 * 1000,
                pendingIntent);
    }

    void refreshNotif() {
        try {
            stopServiceGGty();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void startServiceGGty(){
        try {
            mSensorService = new ServiceGlobal(getApplicationContext());
            mServiceIntent = new Intent(getApplicationContext(), mSensorService.getClass());
            if (!isMyServiceRunning(mSensorService.getClass())) {
                /*Intent firebase = new Intent(this,MyFirebaseMessagingService.class);
                startService(firebase);*/
                startService(mServiceIntent);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void stopServiceGGty()
    {
        try {
            mSensorService = new ServiceGlobal(getApplicationContext());
            mServiceIntent = new Intent(getApplicationContext(), mSensorService.getClass());
            if (isMyServiceRunning(mSensorService.getClass())) {
                /*Intent firebase = new Intent(this,MyFirebaseMessagingService.class);
                startService(firebase);*/
                stopService(mServiceIntent);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupNavDraw() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navDraw = (NavDrawFragment) getSupportFragmentManager().findFragmentById(R.id.navDrawer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        fragmentId = findViewById(R.id.navDrawer);
        navDraw.setup(fragmentId, mDrawerLayout, toolbar);
    }

    private void setPage(String page, Fragment f, Boolean flag) {
        setBreadCrumb(page);
        ft = getSupportFragmentManager().beginTransaction();

            if(f != null) {
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.replace(R.id.container, f, page);
                ft.commit();
            }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout :
                stopGPSservice();
                pref.clearAllPreferences();
                DatabaseHelper.getInstance().commitClearDatabase();
                DatabaseHelper.getInstance().clearDatabase();
                Intent intent = new Intent(MainActivityOld.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onMenuSelected(String page, Fragment f, Boolean flag) {
        setPage(page, f, flag);
    }

    public void setBreadCrumb(String page) {
        this.breadcrumb.setText(page);
    }

    public void clear(Boolean flag) {
        this.clearFragment = flag;
    }

    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(fragmentId)) {
            mDrawerLayout.closeDrawer(fragmentId);
        } else if (getFragmentCount() > 1) {
            if(pref.getPreferencesInt(Constants.POSITION_ID) == Constants.KADIV || pref.getPreferencesInt(Constants.POSITION_ID) == Constants.CBTO) {
                if(pref.getPreferencesString(Constants.ACTIVE_PAGE).equalsIgnoreCase(Constants.TT_STATISTICS) ||
                        pref.getPreferencesString(Constants.ACTIVE_PAGE).equalsIgnoreCase(Constants.TT_TOP_TEN) ||
                        pref.getPreferencesString(Constants.ACTIVE_PAGE).equalsIgnoreCase(Constants.TT_NEWEST_TICKET)) {
                    setPage(Constants.HOME_PAGE, new HomeKadiv(), Boolean.TRUE);
                }
            } else if(pref.getPreferencesInt(Constants.POSITION_ID) == Constants.TECHNICIAN) {
                if(pref.getPreferencesString(Constants.ACTIVE_PAGE).equalsIgnoreCase(Constants.TT_ACTIVITY_PAGE)) {
                    setPage(Constants.HOME_PAGE, new Home(), Boolean.TRUE);
                } else if (!onBackPressed(getSupportFragmentManager())) {

                }
            } else {
                if(pref.getPreferencesString(Constants.ACTIVE_PAGE).equalsIgnoreCase(Constants.MY_TASK_PAGE)) {
                    setPage(Constants.HOME_PAGE, new Home(), Boolean.TRUE);
                } else if(pref.getPreferencesString(Constants.ACTIVE_PAGE).equalsIgnoreCase(Constants.MY_VISIT_DETAIL_PAGE)) {
                    setPage(Constants.MY_VISIT_PAGE, new MyVisit(), Boolean.TRUE);
                } else if(pref.getPreferencesString(Constants.ACTIVE_PAGE).equalsIgnoreCase(Constants.MY_APPROVAL_DETAIL_PAGE)) {
                    setPage(Constants.MY_APPROVAL_PAGE, new MyApproval(), Boolean.TRUE);
                } else if(pref.getPreferencesString(Constants.ACTIVE_PAGE).equalsIgnoreCase(Constants.TICKET_INFO_PAGE) || pref.getPreferencesString(Constants.ACTIVE_PAGE).equalsIgnoreCase(Constants.MY_TASK_DETAIL_PAGE)) {
//                setPage(Constants.MY_TASK_PAGE, new MyTicket(), Boolean.TRUE);
                } else if (!onBackPressed(getSupportFragmentManager())) {

                }
            }
        } else {
        }
    }

    protected int getFragmentCount() {
        return getSupportFragmentManager().getBackStackEntryCount();
    }

    private Fragment getFragmentAt(int index) {
        return getFragmentCount() > 0 ? getSupportFragmentManager().findFragmentByTag(Integer.toString(index)) : null;
    }

    protected Fragment getCurrentFragment() {
        return getFragmentAt(getFragmentCount() - 1);
    }

    private boolean onBackPressed(FragmentManager fm) {
        if (fm != null) {
            if (fm.getBackStackEntryCount() > 1) {
                fm.popBackStack();
                return true;
            }
            List<Fragment> fragList = fm.getFragments();
            if (fragList != null && fragList.size() > 0) {
                for (Fragment frag : fragList) {
                    if (frag != null && frag.isVisible()) {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }

    @Override
    public void back() {
        onBackPressed();
    }

    @Override
    public void onBackStackChanged() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1)
            setPage(Constants.TT_ACTIVITY_PAGE, new MyTicketV2(), Boolean.FALSE);
        else if(requestCode == Constants.REQUEST_NEW_TICKET)
            setPage(Constants.TT_ACTIVITY_PAGE, new MyTicketV2(), Boolean.FALSE);
        else if(requestCode == Constants.REQUEST_CODE_APPROVAL)
            setPage(Constants.MY_APPROVAL_PAGE, new MyApproval(), Boolean.FALSE);

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            // Log.d(TAG, "isMyServiceRunning: "+serviceClass.getName()+" =? "+service.service.getClassName());
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
     /*   unregisterReceiver(gpsReciever);
        stopGPSservice();*/
        stopGPSservice();
        super.onStop();
    }
    @Override
    protected void onStart() {
        startServiceGGty();
        initGPSservice();
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(gpsBound) {
            stopGPSservice();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
//        cus.checkLogin();

        if(!gpsBound) {
            initGPSservice();
        }

//        Logcat.d("onResume: ");
        refreshNotif();

//        if(ready){
//            new getDataFirstBlood().execute();
//        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshNotif();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopGPSservice();

        try {
            //Intent bb = new Intent(this,ServiceGlobalEzGG.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (isMyServiceRunning(ServiceGlobal.class))
                    stopService(mServiceIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void stopGPSservice(){
        if(pref.getPreferencesInt(Constants.POSITION_ID) == Constants.ENGINEER) {
            try {
                Logcat.d("stopGPSservice: " + gpsBound);
                if (gpsBound) {
                    unbindService(mConnection);
                    gpsBound = false;
                    Intent intent = new Intent(MainActivityOld.this, GpsService.class);
                    stopService(intent);
                    unregisterReceiver(gpsReciever);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            gpsService = ((GpsService.LocalBinder)service).getService();
            gpsBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mConnection = null;
        }
    };

    void initGPSservice(){
        if(pref.getPreferencesInt(Constants.POSITION_ID) == Constants.ENGINEER) {
            new EngineerVisitTarget().execute();

        }
    }

    private class GPSReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub

            //int datapassed = arg1.getIntExtra("DATAPASSED", 0);

            if(arg1.getStringExtra("GPS")!=null){

                String loc= arg1.getStringExtra("GPS");
                String dis= arg1.getStringExtra("DISTANCE");
                GPSacc=Float.parseFloat(loc);
                UpdateGPS(GPSacc);

                Double dDis = new Double(dis);
                if(dDis <= 100) {
                    new ResumeClock().execute();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "GPS detected!\n"
                                    + "Accurate location: " + String.valueOf(loc)+"Meter\n"
                                    + "Distance: " + String.valueOf(dis)+"Meter",
                            Toast.LENGTH_SHORT).show();
                }

            }


        }
    }

    public void UpdateGPS(float loc){


        /*
        0.0 as No Gps signal

        1-25 - 4 bars

        26-50 - 3 bars

        51-75 - 2 bars

        75-100 - 1 bar
        */


        if (loc>100){
            Toast.makeText(getApplicationContext(), "GPS > 100", Toast.LENGTH_SHORT).show();
//            gpsImg.setImageResource(R.drawable.circle_red);
        } else
        if (loc <=100 && 50<=loc) {
//            gpsImg.setImageResource(R.drawable.circle_orange);
            Toast.makeText(getApplicationContext(), "50 < GPS < 100", Toast.LENGTH_SHORT).show();
        } else if (loc < 50 && loc >25) {
            Toast.makeText(getApplicationContext(), "25 < GPS < 50", Toast.LENGTH_SHORT).show();
//            gpsImg.setImageResource(R.drawable.circle_yellow);
        } else if (loc>0&&loc<=25){
            Toast.makeText(getApplicationContext(), "0 < GPS < 25", Toast.LENGTH_SHORT).show();
//            gpsImg.setImageResource(R.drawable.circle_green);
        } else if (loc==0.0){
            Toast.makeText(getApplicationContext(), "GPS 0.0", Toast.LENGTH_SHORT).show();
//            gpsImg.setImageResource(R.drawable.circle_grey);
        }


    }

    private class EngineerVisitTarget extends AsyncTask<Void, Void, Void> {
        String result;
        JSONObject object;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(
                        CommonsUtil.getAbsoluteUrl("getonsitevisit_active"),
                        new FormBody.Builder()
                                .add(Constants.PARAM_ID, String.valueOf(pref.getPreferencesInt(Constants.ID_UPDRS)))
                                .build());

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                object = new JSONObject(result);
                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
                    Type type = new TypeToken<Assignment>(){}.getType();
                    selectedAssignment = gson.fromJson(object.getString("new_tickets"), type);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(selectedAssignment != null) {
                pref.savePreferences(Constants.SELECTED_STATION_LAT, selectedAssignment.getTicket().getStationLat());
                pref.savePreferences(Constants.SELECTED_STATION_LONG, selectedAssignment.getTicket().getStationLong());
                try {
                    gpsReciever = new GPSReciever();

                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction(GpsService.GPSSEND);
                    registerReceiver(gpsReciever, intentFilter);
                    Intent i = new Intent(MainActivityOld.this,
                            GpsService.class);
                    startService(i);
                    bindService(i, mConnection, Context.BIND_AUTO_CREATE);
                    gpsBound = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ResumeClock extends AsyncTask<Void, Void, Void> {
        String result;
        JSONObject object;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(
                        CommonsUtil.getAbsoluteUrl("start_clock"),
                        new FormBody.Builder()
                                .add(Constants.PARAM_TICKET_NO, selectedAssignment.getTicket().getTicketNo())
                                .build());

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                object = new JSONObject(result);
                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
                    stopGPSservice();
                    Toast.makeText(getApplicationContext(),
                            "clock resume",
                            Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
