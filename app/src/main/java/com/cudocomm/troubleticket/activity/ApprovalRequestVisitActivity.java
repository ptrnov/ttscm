package com.cudocomm.troubleticket.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.adapter.ViewPagerAdapter;
import com.cudocomm.troubleticket.component.CustomPopConfirm;
import com.cudocomm.troubleticket.component.PopupApprovalRequestVisitTicket;
import com.cudocomm.troubleticket.fragment.TicketHistoryFragment;
import com.cudocomm.troubleticket.fragment.TicketInfoFragment;
import com.cudocomm.troubleticket.model.Assignment;
import com.cudocomm.troubleticket.model.Ticket;
import com.cudocomm.troubleticket.model.TicketLog;
import com.cudocomm.troubleticket.util.ApiClient;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
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

public class ApprovalRequestVisitActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private SliderLayout photoPreviewLayout;
    private TabLayout tabLayout;
    private ViewPager contentPager;
//    private ViewPagerAdapter viewPagerAdapter;

    private Assignment selectedAssignment;
    private Ticket selectedTicket;
    private List<TicketLog> ticketLogs;

//    private RelativeLayout actionLayout;
//    private LinearLayout actionLL;
    private Button rejectBtn;
    private Button rejectBtns;
    private Button approveBtn;

    private CustomPopConfirm popConfirm;

    private PopupApprovalRequestVisitTicket popupApprovalRequestVisitTicket;

    private SpotsDialog progressDialog;

    private String notesForEng;
    private String dateVisit;
    
//    private Preferences preferences;
    private CustomPopConfirm confDialog;

    public ApprovalRequestVisitActivity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_ticket);
//        preferences = new Preferences(this);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        selectedTicket = (Ticket) bundle.getSerializable(Constants.SELECTED_TICKET);

        initComponent();
        new RequestVisitDetailTask().execute();
    }

    private void initComponent() {
        progressDialog = new SpotsDialog(this, R.style.progress_dialog_style);
        photoPreviewLayout = (SliderLayout) findViewById(R.id.photoPreviewLayout);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        contentPager = (ViewPager) findViewById(R.id.contentPager);

//        actionLayout = (RelativeLayout) findViewById(R.id.actionLayout);
//        actionLL = (LinearLayout) findViewById(R.id.actionLL);
        rejectBtn = (Button) findViewById(R.id.rejectBtn);
        rejectBtns = (Button) findViewById(R.id.rejectBtns);
        rejectBtns.setVisibility(View.GONE);
        approveBtn = (Button) findViewById(R.id.approveBtn);
    }

    private void updateComponent() {
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



        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        Map<String, Object> maps = new HashMap<>();
        maps.put(Constants.SELECTED_TICKET, selectedTicket);
        maps.put(Constants.TICKET_LOGS, ticketLogs);

        viewPagerAdapter.addFragment(TicketInfoFragment.newInstance(selectedTicket), "Ticket Info");
        viewPagerAdapter.addFragment(TicketHistoryFragment.newInstance(maps), "History");


        contentPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(contentPager);

        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popConfirm = CustomPopConfirm.newInstance("Reject Request", "Are you sure reject request visit onsite?", "Yes", "No");
                popConfirm.setProcessListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popConfirm.dismiss();
                        new RejectRequestVisitTask().execute();
                    }
                });
                popConfirm.setBackListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popConfirm.dismiss();
                    }
                });
                popConfirm.show(getFragmentManager(), null);
            }
        });
        approveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupApprovalRequestVisitTicket = PopupApprovalRequestVisitTicket.newInstance("Approval Request OnSite Visit","Process","Back");
                popupApprovalRequestVisitTicket.setBackListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupApprovalRequestVisitTicket.dismiss();
                    }
                });
                popupApprovalRequestVisitTicket.setProcessListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notesForEng = popupApprovalRequestVisitTicket.getInfoET().getText().toString();
                        dateVisit = popupApprovalRequestVisitTicket.getDateET().getText().toString();
                        if(TextUtils.isEmpty(notesForEng)) {
                            popupApprovalRequestVisitTicket.getInfoET().requestFocus();
                            popupApprovalRequestVisitTicket.getInfoET().setError(getResources().getString(R.string.error_notes_for_engineer));
                        } else if(TextUtils.isEmpty(dateVisit)) {
                            popupApprovalRequestVisitTicket.getDateET().requestFocus();
                            popupApprovalRequestVisitTicket.getDateET().setError(getResources().getString(R.string.error_label_date_visit));
                        } else {
                            String title = "Submission Confirmation";
                            String msg = "You will approved request visit for solved " + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType()) +" incident with detail : \nLocation : "+
                                    selectedTicket.getStationName() +
                                    "\nSuspect : " + selectedTicket.getSuspect1Name() + " - " + selectedTicket.getSuspect2Name() + " - " + selectedTicket.getSuspect3Name() +
                                    "\nSeverity : " + CommonsUtil.severityToString(selectedTicket.getTicketSeverity());
                            confDialog = CustomPopConfirm.newInstance(title,msg,"Process","Back");
                            confDialog.setBackListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    confDialog.dismiss();
                                }
                            });
                            confDialog.setProcessListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupApprovalRequestVisitTicket.dismiss();
                                    confDialog.dismiss();
                                    new SubmitApprovalRequestVisitTask().execute();

                                }
                            });
                            confDialog.show(getFragmentManager(), null);

                        }
                    }
                });
                popupApprovalRequestVisitTicket.show(getSupportFragmentManager(), null);
            }
        });
    }


    private class RequestVisitDetailTask extends AsyncTask<Void, Void, Void> {

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

    private class SubmitApprovalRequestVisitTask extends AsyncTask<Void, Void, Void> {

        String result = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(CommonsUtil.getAbsoluteUrl(Constants.URL_APPROVE_REQUEST_VISIT), new FormBody.Builder()
                        .add(Constants.PARAM_ID, selectedAssignment.getAssignmentId())
                        .add(Constants.PARAM_INFO, notesForEng)
                        .add(Constants.PARAM_DATE_VISIT, dateVisit)
                        .build());

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            try {
                JSONObject object = new JSONObject(result);
                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {

                    finish();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class RejectRequestVisitTask extends AsyncTask<Void, Void, Void> {

        String result = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(CommonsUtil.getAbsoluteUrl(Constants.URL_REJECT_REQUEST_VISIT), new FormBody.Builder()
                        .add(Constants.PARAM_ID, selectedAssignment.getAssignmentId())
                        .build());

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            try {
                JSONObject object = new JSONObject(result);
                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {

                    finish();

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
