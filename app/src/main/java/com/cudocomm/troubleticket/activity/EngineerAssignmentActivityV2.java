package com.cudocomm.troubleticket.activity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.adapter.ViewPagerAdapter;
import com.cudocomm.troubleticket.component.CustomPopConfirm;
import com.cudocomm.troubleticket.component.PopupAssignmentTicket;
import com.cudocomm.troubleticket.component.PopupCloseTicket;
import com.cudocomm.troubleticket.component.PopupEscalationTicket;
import com.cudocomm.troubleticket.component.PopupRequestVisitTicket;
import com.cudocomm.troubleticket.fragment.TicketHistoryFragment;
import com.cudocomm.troubleticket.fragment.TicketInfoFragment;
import com.cudocomm.troubleticket.model.Assignment;
import com.cudocomm.troubleticket.model.Ticket;
import com.cudocomm.troubleticket.model.TicketLog;
import com.cudocomm.troubleticket.util.ApiClient;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Preferences;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import okhttp3.FormBody;

public class EngineerAssignmentActivityV2 extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener, LocationListener {

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    protected String latitude, longitude;
    protected boolean gps_enabled, network_enabled;


    private Preferences preferences;

    private SliderLayout photoPreviewLayout;
    private TabLayout tabLayout;
    private ViewPager contentPager;
    private ViewPagerAdapter viewPagerAdapter;

    private Assignment selectedAssignment;
    private Ticket returnTicket;
    private Ticket selectedTicket;
    private List<TicketLog> ticketLogs;

    private RelativeLayout actionLayout;
    private LinearLayout actionLL;
    private LinearLayout actionKadepTSLL;
    private Button escalatedBtn;
    private Button closedBtn;
    private Button assignmentBtn;

    private CustomPopConfirm confDialog;
    private PopupCloseTicket popupCloseTicket;
    private PopupEscalationTicket popupEscalationTicket;
    private PopupAssignmentTicket popupAssignmentTicket;

    private PopupRequestVisitTicket popupRequestVisitTicket;

    private SpotsDialog progressDialog;

    private String reasonVisit, additionalInfo;
    private Toolbar toolbar;

    Location targetLocation, myLocation;
    double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engineer_assignment);
        initComponent();
        new MyAssignmentDetailTask().execute();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }

    private void initComponent() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        selectedAssignment = (Assignment) bundle.getSerializable(Constants.SELECTED_ASSIGNMENT);
        selectedTicket = selectedAssignment.getTicket();
        preferences = new Preferences(this);
        progressDialog = new SpotsDialog(this, R.style.progress_dialog_style);
        photoPreviewLayout = (SliderLayout) findViewById(R.id.photoPreviewLayout);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        contentPager = (ViewPager) findViewById(R.id.contentPager);

        actionLayout = (RelativeLayout) findViewById(R.id.actionLayout);
        actionLL = (LinearLayout) findViewById(R.id.actionLL);
        actionKadepTSLL = (LinearLayout) findViewById(R.id.actionKadepTSLL);
        escalatedBtn = (Button) findViewById(R.id.escalatedBtn);
        closedBtn = (Button) findViewById(R.id.closedBtn);
        assignmentBtn = (Button) findViewById(R.id.assignmentBtn);
    }

    private void updateComponent() {
//        add ticket position
        if(selectedTicket.getTicketStatus() == 1) {
            actionLayout.setVisibility(View.VISIBLE);
            if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.TECHNICIAN) {
                actionLL.setVisibility(View.VISIBLE);
                actionKadepTSLL.setVisibility(View.GONE);
            } else if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_TS) {
                actionLL.setVisibility(View.GONE);
                actionKadepTSLL.setVisibility(View.VISIBLE);
            }
        } else {
            actionLayout.setVisibility(View.GONE);
        }
        if(!selectedTicket.getTicketPhoto1().isEmpty() && !selectedTicket.getTicketPhoto1().equals("") && !selectedTicket.getTicketPhoto1().equals("null")) {
            TextSliderView textSliderView = new TextSliderView(getApplicationContext());
            String desc = "Photo 1 - " + selectedTicket.getTicketNo();
            textSliderView
                    .description(desc)
                    .image(CommonsUtil.getAbsoluteUrlImage(selectedTicket.getTicketPhoto1()))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", desc);

            photoPreviewLayout.addSlider(textSliderView);
        }

        if(!selectedTicket.getTicketPhoto2().isEmpty() && !selectedTicket.getTicketPhoto2().equals("") && !selectedTicket.getTicketPhoto2().equals("null")) {
            TextSliderView textSliderView = new TextSliderView(getApplicationContext());
            String desc = "Photo 2 - " + selectedTicket.getTicketNo();
            textSliderView
                    .description(desc)
                    .image(CommonsUtil.getAbsoluteUrlImage(selectedTicket.getTicketPhoto2()))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", desc);

            photoPreviewLayout.addSlider(textSliderView);
        }

        if(!selectedTicket.getTicketPhoto3().isEmpty() && !selectedTicket.getTicketPhoto3().equals("") && !selectedTicket.getTicketPhoto3().equals("null")) {
            TextSliderView textSliderView = new TextSliderView(getApplicationContext());
            String desc = "Photo 3 - " + selectedTicket.getTicketNo();
            textSliderView
                    .description(desc)
                    .image(CommonsUtil.getAbsoluteUrlImage(selectedTicket.getTicketPhoto3()))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", desc);

            photoPreviewLayout.addSlider(textSliderView);
        }



        photoPreviewLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
        photoPreviewLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        photoPreviewLayout.setCustomAnimation(new DescriptionAnimation());
        photoPreviewLayout.setDuration(8000);
        photoPreviewLayout.addOnPageChangeListener(this);

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.color_btn_negative));
        tabLayout.setTabTextColors(getResources().getColor(R.color.color_white), getResources().getColor(R.color.color_home_header));



        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        Map<String, Object> maps = new HashMap<>();
        maps.put(Constants.SELECTED_TICKET, selectedTicket);
        maps.put(Constants.TICKET_LOGS, ticketLogs);

        viewPagerAdapter.addFragment(TicketInfoFragment.newInstance(selectedTicket), "Ticket Info");
        viewPagerAdapter.addFragment(TicketHistoryFragment.newInstance(maps), "History");


        contentPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(contentPager);

        escalatedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupRequestVisitTicket = PopupRequestVisitTicket.newInstance("Request OnSite Visit","Process","Back");
                popupRequestVisitTicket.setBackListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupRequestVisitTicket.dismiss();
                    }
                });
                popupRequestVisitTicket.setProcessListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        reasonVisit = popupRequestVisitTicket.getReasonET().getText().toString();
                        if(TextUtils.isEmpty(reasonVisit)) {
                            popupRequestVisitTicket.getReasonET().requestFocus();
                            popupRequestVisitTicket.getReasonET().setError(getResources().getString(R.string.error_reason_req_visit));
                        } else {
                            String title = "Submission Confirmation";
                            String msg = "You will request on site visit for solved " + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType()) +" incident with detail : \nLocation : "+
                                    selectedTicket.getStationName() +
                                    "\nSuspect : " + selectedTicket.getSuspect1Name() + " - " + selectedTicket.getSuspect2Name() + " - " + selectedTicket.getSuspect3Name() +
                                    "\nSeverity : " + CommonsUtil.severityToString(selectedTicket.getTicketSeverity());
                            confDialog = CustomPopConfirm.newInstance(title,msg,"Yes","No");
                            confDialog.setBackListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    confDialog.dismiss();
                                }
                            });
                            confDialog.setProcessListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupRequestVisitTicket.dismiss();
                                    confDialog.dismiss();
                                    new SubmitRequestVisitTask().execute();

                                }
                            });
                            confDialog.show(getFragmentManager(), null);

                        }
//                        actionDescribe = popupEscalationTicket.getActionET().getText().toString();
//                        requireSupport = popupEscalationTicket.getRequireET().getText().toString();
//                        new TicketActivity.EscalatedTicketTask().execute();
                    }
                });
                popupRequestVisitTicket.show(getFragmentManager(), null);
            }
        });

        closedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupCloseTicket = PopupCloseTicket.newInstance("Close Ticket","Process","Back");
                popupCloseTicket.setBackListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupCloseTicket.dismiss();
                    }
                });
                popupCloseTicket.setProcessListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        additionalInfo = popupCloseTicket.getTicketInfoET().getText().toString();
                        if(TextUtils.isEmpty(additionalInfo)) {
                            popupCloseTicket.getTicketInfoET().requestFocus();
                            popupCloseTicket.getTicketInfoET().setError(getResources().getString(R.string.error_close_info));
                        } else {
                            String title = "Submission Confirmation";
                            String msg = "You will closed " + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType()) +" incident with detail : \nLocation : "+
                                    selectedTicket.getStationName() +
                                    "\nSuspect : " + selectedTicket.getSuspect1Name() + " - " + selectedTicket.getSuspect2Name() + " - " + selectedTicket.getSuspect3Name() +
                                    "\nSeverity : " + CommonsUtil.severityToString(selectedTicket.getTicketSeverity());
                            confDialog = CustomPopConfirm.newInstance(title,msg,"Yes","No");
                            confDialog.setBackListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    confDialog.dismiss();
                                }
                            });
                            confDialog.setProcessListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupCloseTicket.dismiss();
                                    confDialog.dismiss();
                                    new ClosedTicketTask().execute();

                                }
                            });
                            confDialog.show(getFragmentManager(), null);
                        }

                    }
                });
                popupCloseTicket.show(getFragmentManager(), null);
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        targetLocation = new Location("SITE");
        targetLocation.setLatitude(new Double(selectedTicket.getStationLat()));
        targetLocation.setLongitude(new Double(selectedTicket.getStationLong()));
        myLocation = location;
        distance=targetLocation.distanceTo(myLocation);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class MyAssignmentDetailTask extends AsyncTask<Void, Void, Void> {

        String result;
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(CommonsUtil.getAbsoluteUrl(Constants.URL_TICKET_HISTORY),
                        new FormBody.Builder().add(Constants.PARAM_TICKET_NO, selectedTicket.getTicketNo()).build());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Type type = new TypeToken<List<TicketLog>>(){}.getType();
            try {
                JSONObject object = new JSONObject(result);
                ticketLogs = gson.fromJson(object.getString("data"), type);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            updateComponent();

            progressDialog.dismiss();
        }
    }

    class ClosedTicketTask extends AsyncTask<Void, Void, Void> {

        String result = "";
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gsona = this.gsonBuilder.create();

        JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(CommonsUtil.getAbsoluteUrl("close_ticket"), new FormBody.Builder()
                        .add("ticket_id", selectedTicket.getTicketId())
                        .add("ticket_closedby", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                        .add("additional_info", additionalInfo)
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
                JSONObject object = new JSONObject(result);
                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
//                    returnTicket = gsona.fromJson(object.getString("data").toString(), Ticket.class);
                    returnTicket = gsona.fromJson(object.getString("data"), Ticket.class);
                    String title = returnTicket.getTicketId() + " - " + CommonsUtil.severityToString(returnTicket.getTicketSeverity()) + " - " + CommonsUtil.ticketTypeToString(returnTicket.getTicketType());
                    String content = "Down Time on site " + returnTicket.getStationName() + " has been solved.";
                    NotificationManager mgr=
                            (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle(title)
                                    .setContentText(content);

                    Notification note = mBuilder.build();

                    mgr.notify(NotificationManager.IMPORTANCE_HIGH, note);

                    finish();
                    progressDialog.dismiss();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class SubmitRequestVisitTask extends AsyncTask<Void, Void, Void> {

        String result = "";
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gsona = this.gsonBuilder.create();

        JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(CommonsUtil.getAbsoluteUrl("request_onsite_visit"), new FormBody.Builder()
                        .add(Constants.PARAM_ID, selectedAssignment.getAssignmentId())
                        .add(Constants.PARAM_REASON, reasonVisit)
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
                JSONObject object = new JSONObject(result);
                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
                    finish();
                    progressDialog.dismiss();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(getApplicationContext(), slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

}
