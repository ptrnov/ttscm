package com.cudocomm.troubleticket.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.adapter.ViewPagerAdapter;
import com.cudocomm.troubleticket.component.CustomPopConfirm;
import com.cudocomm.troubleticket.component.PopupConfirmTicket;
import com.cudocomm.troubleticket.component.PopupConfirmTicketvisit;
import com.cudocomm.troubleticket.fragment.TicketHistoryFragment;
import com.cudocomm.troubleticket.fragment.TicketInfoFragment;
import com.cudocomm.troubleticket.model.Assignment;
import com.cudocomm.troubleticket.model.CounterModel;
import com.cudocomm.troubleticket.model.Ticket;
import com.cudocomm.troubleticket.model.TicketLog;
import com.cudocomm.troubleticket.model.UserModel;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import okhttp3.FormBody;

public class ApprovalTicketActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private static final String TAG = "APPROVE TICKET";
    private Preferences preferences;

    private SliderLayout photoPreviewLayout;
    private TabLayout tabLayout;
    private ViewPager contentPager;
//    private ViewPagerAdapter viewPagerAdapter;

//    private Ticket returnTicket;
    private Ticket selectedTicket;
    private List<TicketLog> ticketLogs;

//    private RelativeLayout actionLayout;
//    private LinearLayout actionLL;
//    private LinearLayout actionKadepTSLL;
    private Button approveBtn;
    private Button rejectBtn;

    private PopupConfirmTicket popupConfirmTicket;
    private PopupConfirmTicketvisit popupConfirmTicketvisit;
    private CustomPopConfirm confDialog;

    private SpotsDialog progressDialog;

    private String additionalInfo;
    private String requireTglDapature;
    private String requireVasselNo;
//    private CounterModel myTaskCounter;
//    private String actionDescribe;
//    private String requireSupport;
    private int guide; // filter visit ot not visit
    private List<UserModel> engineers;
//    private Toolbar toolbar;

    private CustomPopConfirm popConfirm;
    private Assignment selectedAssignment;
    private String guiteSerializer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_ticket);
        preferences = new Preferences(this);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        selectedTicket = (Ticket) bundle.getSerializable(Constants.SELECTED_TICKET);
        Log.d(TAG, "approve: " +selectedTicket);
        initComponent();
//        updateComponent();
        new TicketTask().execute();
    }

    private void initComponent() {
//        setTitle("Approval Ticket Information");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new SpotsDialog(this, R.style.progress_dialog_style);
        photoPreviewLayout = (SliderLayout) findViewById(R.id.photoPreviewLayout);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        contentPager = (ViewPager) findViewById(R.id.contentPager);

//        actionLayout = (RelativeLayout) findViewById(R.id.actionLayout);
//        actionLL = (LinearLayout) findViewById(R.id.actionLL);
//        actionKadepTSLL = (LinearLayout) findViewById(R.id.actionKadepTSLL);
        approveBtn = (Button) findViewById(R.id.approveBtn);
        rejectBtn = (Button) findViewById(R.id.rejectBtns);


        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupConfirmTicket = PopupConfirmTicket.newInstance("Reject Ticket","Process","Back");
                popupConfirmTicket.setBackListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupConfirmTicket.dismiss();
                    }
                });
                popupConfirmTicket.setProcessListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        additionalInfo = popupConfirmTicket.getTicketInfoET().getText().toString();
                        if(TextUtils.isEmpty(additionalInfo)) {
                            popupConfirmTicket.getTicketInfoET().requestFocus();
                            popupConfirmTicket.getTicketInfoET().setError(getResources().getString(R.string.error_close_info));
                        } else {
                            String title = "Submission Confirmation";
                            String msg = "You will approved " + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType()) +" incident with detail : \nLocation : "+
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
                                    popupConfirmTicket.dismiss();
                                    confDialog.dismiss();
                                    new RejectClosedTicketTask().execute();

                                }
                            });
                            confDialog.show(getFragmentManager(), null);


                        }

                    }
                });
                popupConfirmTicket.show(getFragmentManager(), null);
            }
        });
    }

    private class RejectClosedTicketTask extends AsyncTask<Void, Void, Void> {

        String result = "";
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gsona = this.gsonBuilder.create();
//
//        JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(
                        CommonsUtil.getAbsoluteUrl("reject_approval"),
                        new FormBody.Builder()
                                .add(Constants.PARAM_TICKET_ID, selectedTicket.getTicketId())
                                .add(Constants.PARAM_TICKET_CONFIRM_BY, String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                                .add(Constants.PARAM_INFO, additionalInfo)
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

                    Type counterType = new TypeToken<CounterModel>(){}.getType();
                    CounterModel myTaskCounter = gsona.fromJson(object.getString("needapproval"), counterType);
                    if(myTaskCounter != null)
                        preferences.savePreferences(Constants.COUNTER_NEED_APPROVAL, getResources().getString(R.string.widget_need_approval_counter, myTaskCounter.getCritical(), myTaskCounter.getMajor(), myTaskCounter.getMinor()));
                    else
                        preferences.savePreferences(Constants.COUNTER_NEED_APPROVAL, getResources().getString(R.string.widget_need_approval_counter_null));


                    finish();
                    progressDialog.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateComponent() {

        if(selectedTicket.getTicketPhoto1() !=null) {
            if (!selectedTicket.getTicketPhoto1().isEmpty()) {
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
        }

        if(selectedTicket.getTicketPhoto2() !=null) {
            if (!selectedTicket.getTicketPhoto2().isEmpty()) {
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
        }

        if(selectedTicket.getTicketPhoto3() !=null) {
            if (!selectedTicket.getTicketPhoto3().isEmpty()) {
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
        }



        photoPreviewLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
        photoPreviewLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        photoPreviewLayout.setCustomAnimation(new DescriptionAnimation());
        photoPreviewLayout.setDuration(8000);
        photoPreviewLayout.addOnPageChangeListener(this);

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.color_btn_negative));
        tabLayout.setTabTextColors(getResources().getColor(R.color.color_white), getResources().getColor(R.color.color_home_header));

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        Map<String, Object> maps = new HashMap<>();
        maps.put(Constants.SELECTED_TICKET, selectedTicket);
        maps.put(Constants.TICKET_LOGS, ticketLogs);

        viewPagerAdapter.addFragment(TicketInfoFragment.newInstance(selectedTicket), "Ticket Info");
        viewPagerAdapter.addFragment(TicketHistoryFragment.newInstance(maps), "History");


        contentPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(contentPager);

        try {
            guiteSerializer = ApiClient.post(
                    CommonsUtil.getAbsoluteUrl("cek_close_visit"),
                    new FormBody.Builder()
                            .add(Constants.PARAM_TICKET_ID, selectedTicket.getTicketId())
                            .build());
            JSONObject obj = null;

            try {
                obj = new JSONObject(guiteSerializer);
                Log.d(TAG, "check_guide: " +obj.getString("guide"));
                guide=Integer.parseInt(obj.getString("guide"));
//            guide=guide1;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Log.d(TAG, "check_guide: " +guiteSerializer);

//    guide=0;
        //int foo = Integer.parseInt(guide);
    if (guide!=2){
            approveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupConfirmTicket = PopupConfirmTicket.newInstance("Confirm Ticket","Process","Back");
                popupConfirmTicket.setBackListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupConfirmTicket.dismiss();
                    }
                });
                popupConfirmTicket.setProcessListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        additionalInfo = popupConfirmTicket.getTicketInfoET().getText().toString();
                        if(TextUtils.isEmpty(additionalInfo)) {
                            popupConfirmTicket.getTicketInfoET().requestFocus();
                            popupConfirmTicket.getTicketInfoET().setError(getResources().getString(R.string.error_close_info));
                        } else {
                            String title = "Submission Confirmation";
                            String msg = "You will approved " + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType()) +" incident with detail : \nLocation : "+
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
                                    popupConfirmTicket.dismiss();
                                    confDialog.dismiss();
                                    new ApproveClosedTicketTask().execute();

                                }
                            });
                            confDialog.show(getFragmentManager(), null);


                        }

                    }
                });
                popupConfirmTicket.show(getFragmentManager(), null);
            }
        });

        //Edit by ptr.nov
      }else{
      approveBtn.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                assignDate = popupReAssignmentTicket.getAssignmentDateET().getText().toString();
////                                            else if (TextUtils.isEmpty(assignDate)) {
////                            popupReAssignmentTicket.getAssignmentDateET().requestFocus();
////                            popupReAssignmentTicket.getAssignmentDateET().setError(getResources().getString(R.string.error_assignment_date));
              popupConfirmTicketvisit =
                  PopupConfirmTicketvisit.newInstance("Confirm Ticket", "Process", "Back");
              popupConfirmTicketvisit.setBackListener(
                  new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      popupConfirmTicketvisit.dismiss();
                    }
                  });
              popupConfirmTicketvisit.setProcessListener(
                  new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar c = Calendar.getInstance();
                        String tglNow = sdf.format(c.getTime());

                        Date testDate;
                        Date testDate1;
                        Date testDate3;
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                        additionalInfo =popupConfirmTicketvisit.getTicketInfoET().getText().toString();
                        requireTglDapature =popupConfirmTicketvisit.getTicketTglDepature().getText().toString();
                        requireVasselNo =popupConfirmTicketvisit.getTicketVasselNo().getText().toString();
                        try{
                            testDate = df.parse(requireTglDapature);
                            testDate1 = df.parse(tglNow);

                              if (TextUtils.isEmpty(additionalInfo)) {
                                popupConfirmTicketvisit.getTicketInfoET().requestFocus();
                                popupConfirmTicketvisit.getTicketInfoET().setError(getResources().getString(R.string.error_close_info));
                              }else if (TextUtils.isEmpty(requireTglDapature)) {
                                  popupConfirmTicketvisit.getTicketTglDepature().requestFocus();
                                  popupConfirmTicketvisit.getTicketTglDepature().setError(getResources().getString(R.string.error_dapature));
                              }else if (testDate1.after(testDate)){
                                  popupConfirmTicketvisit.getTicketTglDepature().requestFocus();
                                  popupConfirmTicketvisit.getTicketTglDepature().setError(getResources().getString(R.string.out_dated));
                              }else if (TextUtils.isEmpty(requireVasselNo)) {
                                  popupConfirmTicketvisit.getTicketVasselNo().requestFocus();
                                  popupConfirmTicketvisit.getTicketVasselNo().setError(getResources().getString(R.string.error_rassel_no));
                              }  else {
                                String title = "Submission Confirmation";
                                String msg =
                                    "You will approved "
                                        + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType())
                                        + " incident with detail : \nLocation : "
                                        + selectedTicket.getStationName()
                                        + "\nSuspect : "
                                        + selectedTicket.getSuspect1Name()
                                        + " - "
                                        + selectedTicket.getSuspect2Name()
                                        + " - "
                                        + selectedTicket.getSuspect3Name()
                                        + "\nSeverity : "
                                        + CommonsUtil.severityToString(selectedTicket.getTicketSeverity())
                                        + "\nDate Depature : "
                                         + requireTglDapature
                                        + "\nVassel.No : "
                                        + requireVasselNo;
                                confDialog = CustomPopConfirm.newInstance(title, msg, "Yes", "No");
                                confDialog.setBackListener(
                                    new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                        confDialog.dismiss();
                                      }
                                    });
                                confDialog.setProcessListener(
                                    new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                        popupConfirmTicketvisit.dismiss();
                                        confDialog.dismiss();
                                        new ApproveClosedTicketvisitTask().execute();
                                      }
                                    });
                                confDialog.show(getFragmentManager(), null);
                              }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        };
                    }
                  });
              popupConfirmTicketvisit.show(getSupportFragmentManager(), null);
            }
          });
        }
    }


    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(getApplicationContext(),slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    private class TicketTask extends AsyncTask<Void, Void, Void> {

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
                Log.d(TAG, "approve1: " +result);
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

    private class ApproveClosedTicketTask extends AsyncTask<Void, Void, Void> {

        String result = "";
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gsona = this.gsonBuilder.create();
//
//        JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(
                        CommonsUtil.getAbsoluteUrl("submitapproval"),
                        new FormBody.Builder()
                                .add(Constants.PARAM_TICKET_ID, selectedTicket.getTicketId())
                                .add(Constants.PARAM_TICKET_CONFIRM_BY, String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                                .add(Constants.PARAM_INFO, additionalInfo)
//                                .add(Constants.PARAM_TGLDAPATURE, "0")
//                                .add(Constants.PARAM_VESSELNO, "0")
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

                    Type counterType = new TypeToken<CounterModel>(){}.getType();
                    CounterModel myTaskCounter = gsona.fromJson(object.getString("needapproval"), counterType);
                    if(myTaskCounter != null)
                        preferences.savePreferences(Constants.COUNTER_NEED_APPROVAL, getResources().getString(R.string.widget_need_approval_counter, myTaskCounter.getCritical(), myTaskCounter.getMajor(), myTaskCounter.getMinor()));
                    else
                        preferences.savePreferences(Constants.COUNTER_NEED_APPROVAL, getResources().getString(R.string.widget_need_approval_counter_null));


                    finish();
                    progressDialog.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class ApproveClosedTicketvisitTask extends AsyncTask<Void, Void, Void> {

        String result = "";
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gsona = this.gsonBuilder.create();
//
//        JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(
                        CommonsUtil.getAbsoluteUrl("submitapprovalv2"),
                        new FormBody.Builder()
                                .add(Constants.PARAM_TICKET_ID, selectedTicket.getTicketId())
                                .add(Constants.PARAM_TICKET_CONFIRM_BY, String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                                .add(Constants.PARAM_INFO, additionalInfo)
                                .add(Constants.PARAM_TGLDAPATURE, requireTglDapature)
                                .add(Constants.PARAM_VESSELNO, requireVasselNo)
                                .build());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            try {
//                JSONObject object = new JSONObject(result);
//                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
//
//                    Type counterType = new TypeToken<CounterModel>(){}.getType();
//                    CounterModel myTaskCounter = gsona.fromJson(object.getString("needapproval"), counterType);
//                    if(myTaskCounter != null)
//                        preferences.savePreferences(Constants.COUNTER_NEED_APPROVAL, getResources().getString(R.string.widget_need_approval_counter, myTaskCounter.getCritical(), myTaskCounter.getMajor(), myTaskCounter.getMinor()));
//                    else
//                        preferences.savePreferences(Constants.COUNTER_NEED_APPROVAL, getResources().getString(R.string.widget_need_approval_counter_null));
//
//
//                    finish();
//                    progressDialog.dismiss();
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
    }

}
