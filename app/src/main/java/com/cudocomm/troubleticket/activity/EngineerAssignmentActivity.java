package com.cudocomm.troubleticket.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.TTSApplication;
import com.cudocomm.troubleticket.adapter.ViewPagerAdapter;
import com.cudocomm.troubleticket.component.CustomPopConfirm;
import com.cudocomm.troubleticket.component.PopupAssignmentTicket;
import com.cudocomm.troubleticket.component.PopupCloseTicketCustom;
import com.cudocomm.troubleticket.component.PopupEscalationTicket;
import com.cudocomm.troubleticket.component.PopupGuidanceTicket;
import com.cudocomm.troubleticket.component.PopupRequestVisitTicket;
import com.cudocomm.troubleticket.fragment.TicketHistoryFragment;
import com.cudocomm.troubleticket.fragment.TicketInfoFragment;
import com.cudocomm.troubleticket.model.Assignment;
import com.cudocomm.troubleticket.model.Ticket;
import com.cudocomm.troubleticket.model.TicketLog;
import com.cudocomm.troubleticket.util.ApiClient;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Logcat;
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

public class EngineerAssignmentActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

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
    private Button responseBtn;
    private Button escalatedBtn;
    private Button closedBtn;
    private Button assignmentBtn;
    private Button reportBtn;

    private CustomPopConfirm confDialog;
//    private PopupCloseTicket popupCloseTicket;
    private PopupCloseTicketCustom popupCloseTicket;
    private PopupEscalationTicket popupResponseTicket;
    private PopupAssignmentTicket popupAssignmentTicket;
    private PopupGuidanceTicket popupGuidanceTicket;

    private PopupRequestVisitTicket popupRequestVisitTicket;

    private SpotsDialog progressDialog;

    private String reasonVisit, additionalInfo, actionEng, remarksEng, prNo,actionDescribe;
    private int closedType = 1;
    private Toolbar toolbar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engineer_assignment);
        initComponent();
        new MyAssignmentDetailTask().execute();
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
        responseBtn = (Button) findViewById(R.id.responseBtn);
        escalatedBtn = (Button) findViewById(R.id.escalatedBtn);
        closedBtn = (Button) findViewById(R.id.closedBtn);
        reportBtn = (Button) findViewById(R.id.reportBtn);
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

        if (!String.valueOf(selectedTicket.getTicketPhoto2()).equals("null") ){
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
//        else if(!selectedTicket.getTicketPhoto2().isEmpty() && !selectedTicket.getTicketPhoto2().equals("") && !selectedTicket.getTicketPhoto2().equals("null") && !selectedTicket.getTicketPhoto2().equals(null) ) {
//            TextSliderView textSliderView = new TextSliderView(getApplicationContext());
//            String desc = "Photo 2 - " + selectedTicket.getTicketNo();
//            textSliderView
//                    .description(desc)
//                    .image(CommonsUtil.getAbsoluteUrlImage(selectedTicket.getTicketPhoto2()))
//                    .setScaleType(BaseSliderView.ScaleType.Fit)
//                    .setOnSliderClickListener(this);
//
//            //add your extra information
//            textSliderView.bundle(new Bundle());
//            textSliderView.getBundle()
//                    .putString("extra", desc);
//
//            photoPreviewLayout.addSlider(textSliderView);
//        }

        if (!String.valueOf(selectedTicket.getTicketPhoto3()).equals("null") ){
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
//        else if(!selectedTicket.getTicketPhoto3().isEmpty() && !selectedTicket.getTicketPhoto3().equals("") && !selectedTicket.getTicketPhoto3().equals("null") && !selectedTicket.getTicketPhoto2().equals(null)) {
//            TextSliderView textSliderView = new TextSliderView(getApplicationContext());
//            String desc = "Photo 3 - " + selectedTicket.getTicketNo();
//            textSliderView
//                    .description(desc)
//                    .image(CommonsUtil.getAbsoluteUrlImage(selectedTicket.getTicketPhoto3()))
//                    .setScaleType(BaseSliderView.ScaleType.Fit)
//                    .setOnSliderClickListener(this);
//
//            //add your extra information
//            textSliderView.bundle(new Bundle());
//            textSliderView.getBundle()
//                    .putString("extra", desc);
//
//            photoPreviewLayout.addSlider(textSliderView);
//        }



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

        responseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupResponseTicket = PopupEscalationTicket.newInstance("Response Engineer","Process","Back");
                popupResponseTicket.setBackListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupResponseTicket.dismiss();
                    }
                });
                popupResponseTicket.setProcessListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        actionEng = popupResponseTicket.getActionET().getText().toString();
                        remarksEng = popupResponseTicket.getRequireET().getText().toString();
                        if(TextUtils.isEmpty(actionEng)) {
                            popupResponseTicket.getActionET().requestFocus();
                            popupResponseTicket.getActionET().setError(getResources().getString(R.string.error_escalation_action));
                        } else if(TextUtils.isEmpty(remarksEng)) {
                            popupResponseTicket.getRequireET().requestFocus();
                            popupResponseTicket.getRequireET().setError(getResources().getString(R.string.error_remarks_eng));
                        } else {
                            String title = "Submission Confirmation";
                            String msg = "You will response ticket " + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType()) +" incident with detail : \nLocation : "+
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
                                    popupResponseTicket.dismiss();
                                    confDialog.dismiss();
                                    postEngineerResponse();
//                                    new SubmitRequestVisitTask().execute();

                                }
                            });
                            confDialog.show(getFragmentManager(), null);

                        }
//                        actionDescribe = popupEscalationTicket.getActionET().getText().toString();
//                        requireSupport = popupEscalationTicket.getRequireET().getText().toString();
//                        new TicketActivity.EscalatedTicketTask().execute();
                    }
                });
                popupResponseTicket.show(getFragmentManager(), null);
            }
        });

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

        /*closedBtn.setOnClickListener(new View.OnClickListener() {
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
        });*/
        closedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupCloseTicket = PopupCloseTicketCustom.newInstance("Close Ticket","Process","Back");
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
                        prNo = popupCloseTicket.getPrNoET().getText().toString();
                        popupCloseTicket.getFixTypeSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(popupCloseTicket.getFixTypeSpinner().getSelectedItem().toString().equals("Closed by PR")) {
                                    popupCloseTicket.getPrNoET().setVisibility(View.VISIBLE);
                                    closedType = 2;
                                } else {
                                    popupCloseTicket.getPrNoET().setVisibility(View.GONE);
                                    closedType = 1;
                                }

                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                        String regexStr = "^[0-9]*$";
                        if (popupCloseTicket.getFixTypeSpinner().getSelectedItemPosition() == 0) {
                            popupCloseTicket.getFixTypeSpinner().getSelectedView().requestFocus();
                            popupCloseTicket.getFixTypeSpinner().setError(getResources().getString(R.string.error_fix_type));
                        }else if (prNo.equals("")) {
                            popupCloseTicket.getPrNoET().requestFocus();
                            popupCloseTicket.getPrNoET().setError(getResources().getString(R.string.error_pr_no));
                        }else if (prNo.length()!=9 ) {
                            popupCloseTicket.getPrNoET().requestFocus();
                            popupCloseTicket.getPrNoET().setError(getResources().getString(R.string.error_prNo_action_digit));
                        }else if (!prNo.trim().matches(regexStr)) {
                            popupCloseTicket.getPrNoET().requestFocus();
                            popupCloseTicket.getPrNoET().setError(getResources().getString(R.string.error_prNo_action_number));
                        }else if(TextUtils.isEmpty(additionalInfo)) {
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

        // Edit By ptr.nov
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupGuidanceTicket = PopupGuidanceTicket.newInstance("Report Ticket","Process","Back");
                popupGuidanceTicket.setBackListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupGuidanceTicket.dismiss();
                    }
                });
                popupGuidanceTicket.setProcessListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        actionDescribe = popupGuidanceTicket.getActionET().getText().toString();
                        if(TextUtils.isEmpty(actionDescribe) || actionDescribe.length() < 60) {
                            popupGuidanceTicket.getActionET().requestFocus();
                            popupGuidanceTicket.getActionET().setError(getResources().getString(R.string.error_guidance_action));
                        } else {
                            String title = "Submission Confirmation";
                            String msg = "You will Report " + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType()) +" incident with detail : \nLocation : "+
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
                                    popupGuidanceTicket.dismiss();
                                    confDialog.dismiss();
                                    new GuidanceTicketTask().execute();

                                }
                            });
                            confDialog.show(getFragmentManager(), null);

                        }
                    }
                });
                popupGuidanceTicket.show(getFragmentManager(), null);
            }
        });
    }

    class GuidanceTicketTask extends AsyncTask<Void, Void, Void> {

        String result = "";
        JSONObject jsonObject;
        String url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(CommonsUtil.getAbsoluteUrl("guidance_to"), new FormBody.Builder()
                        .add("ticket_id", selectedTicket.getTicketId())
                        .add("id_updrs", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
//                        .add("from_position_id", String.valueOf(preferences.getPreferencesInt(Constants.POSITION_ID)))
                        .add("remarks", actionDescribe)
//                        .add("require", requireSupport)
                        .build());

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            Logcat.i(result);
            Log.d("Log123123",String.valueOf(result));
            try {
                JSONObject object = new JSONObject(result);
                Log.d("Log123123",String.valueOf(object.get(Constants.RESPONSE_STATUS)));
                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
                    finish();
                    progressDialog.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void postEngineerResponse() {
        progressDialog.show();
        final JSONObject items = new JSONObject();
        try {
            items.put("ticket_no", selectedTicket.getTicketNo());
            items.put("action_eng", actionEng);
            items.put("remarks_eng", remarksEng);
            items.put("user_id", preferences.getPreferencesInt(Constants.ID_UPDRS));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringRequest request = new StringRequest(Request.Method.POST, CommonsUtil.getAbsoluteUrl("response_engineer"),
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Logcat.e("response: " + response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            if (jObj.getString("status").equalsIgnoreCase("success")) {
                                finish();
                            }

                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Logcat.e("Volley error: " + error.getMessage() + ", code: " + error.networkResponse);
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Terjadi Kesalahan. Umumnya karena masalah jaringan.", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> items = new HashMap<>();
                items.put("ticket_no", selectedTicket.getTicketNo());
                items.put("action_eng", actionEng);
                items.put("remarks_eng", remarksEng);
                items.put("user_id", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)));
                Logcat.e("params: " + items.toString());
                return items;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        TTSApplication.getInstance().addToRequestQueue(request);
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

    /*class ClosedTicketTask extends AsyncTask<Void, Void, Void> {

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
                        .add("ticket_closedby", String.valueOf(preferences.getPreferencesInt(Constants.USER_ID)))
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
    }*/

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
                result = ApiClient.post(CommonsUtil.getAbsoluteUrl("close_ticket_kerusakan_with_input_pr_no"), new FormBody.Builder()
                        .add("ticket_id", selectedTicket.getTicketId())
                        .add("ticket_closedby", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                        .add("additional_info", additionalInfo)
                        .add("closed_type", String.valueOf(closedType))
                        .add("pr_no", prNo)
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
                Log.d("ticketID11111",selectedTicket.getTicketId());
                result = ApiClient.post(CommonsUtil.getAbsoluteUrl("request_onsite_visit"), new FormBody.Builder()
//                        .add(Constants.PARAM_ID, selectedAssignment.getAssignmentId())
                        .add(Constants.PARAM_ID, selectedTicket.getTicketId())
                        .add(Constants.ID_UPDRS, String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                        .add(Constants.PARAM_REASON, reasonVisit)
                        .build());

                Log.d("idupdrs11111",String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)));
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
