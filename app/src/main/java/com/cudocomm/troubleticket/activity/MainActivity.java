package com.cudocomm.troubleticket.activity;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

import com.crashlytics.android.Crashlytics;
import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.database.DatabaseHelper;
import com.cudocomm.troubleticket.fragment.Home;
import com.cudocomm.troubleticket.fragment.HomeEngineerV2;
import com.cudocomm.troubleticket.fragment.HomeKadepInfra;
import com.cudocomm.troubleticket.fragment.HomeKadepTSV2;
import com.cudocomm.troubleticket.fragment.HomeKadiv;
import com.cudocomm.troubleticket.fragment.HomeV2;
import com.cudocomm.troubleticket.fragment.MyApproval;
import com.cudocomm.troubleticket.fragment.MyAssignmentV2;
import com.cudocomm.troubleticket.fragment.MyTicketV2;
import com.cudocomm.troubleticket.fragment.MyVisit;
import com.cudocomm.troubleticket.fragment.NavDrawFragment;
import com.cudocomm.troubleticket.model.Assignment;
import com.cudocomm.troubleticket.service.NotificationService;
import com.cudocomm.troubleticket.service.ServiceGlobal;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.OnMenuSelected;
import com.cudocomm.troubleticket.util.Preferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends BaseActivity implements FragmentManager.OnBackStackChangedListener, OnMenuSelected {

    @BindView(R.id.app_bar)
    Toolbar toolbar;
    @BindView(R.id.breadcrumb)
    TextView breadcrumb;

    private Boolean clearFragment = Boolean.FALSE;
    private View fragmentId;
    FragmentTransaction ft;
    private DrawerLayout mDrawerLayout;
    private NavDrawFragment navDraw;

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

        ButterKnife.bind(this);

//        pref = new Preferences(this);

        CommonsUtil.appPermission(this);

//        toolbar = (Toolbar) findViewById(R.id.app_bar);
//        breadcrumb = (TextView) findViewById(R.id.breadcrumb);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        setupNavDraw();
        if (savedInstanceState == null) {
            if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.TECHNICIAN) {
                setPage(Constants.HOME_PAGE, new Home(), Boolean.TRUE);
            } else if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KST
                    || preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KORWIL
                    || preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_WIL) {
                setPage(Constants.HOME_PAGE, new HomeV2(), Boolean.TRUE);
            }
            /*else if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KORWIL) {
                setPage(Constants.HOME_PAGE, new HomeKorwil(), Boolean.valueOf(true));
            }  else if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_WIL) {
                setPage(Constants.HOME_PAGE, new HomeKadepwil(), Boolean.valueOf(true));
            } */
            else if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_TS) {
                setPage(Constants.HOME_PAGE, new HomeKadepTSV2(), Boolean.TRUE);
            } else if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_INFRA) {
                setPage(Constants.HOME_PAGE, new HomeKadepInfra(), Boolean.TRUE);
            } else if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.ENGINEER) {
//                setPage(Constants.HOME_PAGE, new HomeEngineer(), Boolean.valueOf(true));
                setPage(Constants.HOME_PAGE, new HomeEngineerV2(), Boolean.TRUE);
            } else if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADIV) {
                setPage(Constants.HOME_PAGE, new HomeKadiv(), Boolean.TRUE);
            } else if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.CBTO) {
                setPage(Constants.HOME_PAGE, new HomeKadiv(), Boolean.TRUE);
            }
        }

        refreshNotif();

    }

    /*private void runNotificationService(){
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
    }*/

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
//                stopGPSservice();
                preferences.clearAllPreferences();
                DatabaseHelper.getInstance().commitClearDatabase();
                DatabaseHelper.getInstance().clearDatabase();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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
            if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADIV || preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.CBTO) {
                if(preferences.getPreferencesString(Constants.ACTIVE_PAGE).equalsIgnoreCase(Constants.TT_STATISTICS) ||
                        preferences.getPreferencesString(Constants.ACTIVE_PAGE).equalsIgnoreCase(Constants.TT_TOP_TEN) ||
                        preferences.getPreferencesString(Constants.ACTIVE_PAGE).equalsIgnoreCase(Constants.TT_NEWEST_TICKET)) {
                    setPage(Constants.HOME_PAGE, new HomeKadiv(), Boolean.TRUE);
                }
            } else if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.TECHNICIAN) {
                if(preferences.getPreferencesString(Constants.ACTIVE_PAGE).equalsIgnoreCase(Constants.TT_ACTIVITY_PAGE)) {
                    setPage(Constants.HOME_PAGE, new Home(), Boolean.TRUE);
                } else if (!onBackPressed(getSupportFragmentManager())) {

                }
            } else {
                if(preferences.getPreferencesString(Constants.ACTIVE_PAGE).equalsIgnoreCase(Constants.MY_TASK_PAGE)) {
                    setPage(Constants.HOME_PAGE, new Home(), Boolean.TRUE);
                } else if(preferences.getPreferencesString(Constants.ACTIVE_PAGE).equalsIgnoreCase(Constants.MY_VISIT_DETAIL_PAGE)) {
                    setPage(Constants.MY_VISIT_PAGE, new MyVisit(), Boolean.TRUE);
                } else if(preferences.getPreferencesString(Constants.ACTIVE_PAGE).equalsIgnoreCase(Constants.MY_APPROVAL_DETAIL_PAGE)) {
                    setPage(Constants.MY_APPROVAL_PAGE, new MyApproval(), Boolean.TRUE);
                } else if(preferences.getPreferencesString(Constants.ACTIVE_PAGE).equalsIgnoreCase(Constants.TICKET_INFO_PAGE) || preferences.getPreferencesString(Constants.ACTIVE_PAGE).equalsIgnoreCase(Constants.MY_TASK_DETAIL_PAGE)) {
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

        else if(requestCode == Constants.REQUEST_ENGINEER_ONSITE_CLOSE)
            setPage(Constants.MY_VISIT_PAGE, new MyVisit(), Boolean.FALSE);

        else if(requestCode == Constants.REQUEST_VIEW_ASSIGNMENT || requestCode == Constants.ENGINEER_ASSIGNMENT)
            setPage(Constants.TT_ACTIVITY_PAGE, new MyAssignmentV2(), Boolean.FALSE);

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
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
        super.onStop();
    }
    @Override
    protected void onStart() {
        startServiceGGty();
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        refreshNotif();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshNotif();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            //Intent bb = new Intent(this,ServiceGlobalEzGG.class);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (isMyServiceRunning(ServiceGlobal.class))
                    stopService(mServiceIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
