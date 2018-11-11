package com.cudocomm.troubleticket.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.adapter.ViewPagerAdapter;
import com.cudocomm.troubleticket.component.PopupAssignmentTicket;
import com.cudocomm.troubleticket.component.PopupCloseTicket;
import com.cudocomm.troubleticket.component.PopupEscalationTicket;
import com.cudocomm.troubleticket.component.PopupRequestVisitTicket;
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

public class MyAssignmentDetail extends BaseFragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private View rootView;

    private SliderLayout photoPreviewLayout;
    private TabLayout tabLayout;
    private ViewPager contentPager;
    private ViewPagerAdapter viewPagerAdapter;

    private Assignment selectedAssignment;
    private Ticket selectedTicket;
    private List<TicketLog> ticketLogs;

    private RelativeLayout actionLayout;
    private LinearLayout actionLL;
    private LinearLayout actionKadepTSLL;
    private Button escalatedBtn;
    private Button closedBtn;
    private Button assignmentBtn;

    private PopupCloseTicket popupCloseTicket;
    private PopupEscalationTicket popupEscalationTicket;
    private PopupAssignmentTicket popupAssignmentTicket;

    private PopupRequestVisitTicket popupRequestVisitTicket;

    private SpotsDialog progressDialog;

    private String reasonVisit;

    public MyAssignmentDetail() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedAssignment = (Assignment) getArguments().getSerializable(Constants.SELECTED_ASSIGNMENT);
            selectedTicket = selectedAssignment.getTicket();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_assignment_detail, container, false);

        initComponent();
        new MyAssignmentDetailTask().execute();

        return rootView;
    }

    private void initComponent() {
        progressDialog = new SpotsDialog(getContext(), R.style.progress_dialog_style);
        photoPreviewLayout = (SliderLayout) rootView.findViewById(R.id.photoPreviewLayout);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        contentPager = (ViewPager) rootView.findViewById(R.id.contentPager);

        actionLayout = (RelativeLayout) rootView.findViewById(R.id.actionLayout);
        actionLL = (LinearLayout) rootView.findViewById(R.id.actionLL);
        actionKadepTSLL = (LinearLayout) rootView.findViewById(R.id.actionKadepTSLL);
        escalatedBtn = (Button) rootView.findViewById(R.id.escalatedBtn);
        closedBtn = (Button) rootView.findViewById(R.id.closedBtn);
        assignmentBtn = (Button) rootView.findViewById(R.id.assignmentBtn);
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
            TextSliderView textSliderView = new TextSliderView(context);
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
            TextSliderView textSliderView = new TextSliderView(context);
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
            TextSliderView textSliderView = new TextSliderView(context);
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



        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        Map<String, Object> maps = new HashMap<>();
        maps.put(Constants.SELECTED_TICKET, selectedTicket);
        maps.put(Constants.TICKET_LOGS, ticketLogs);

        viewPagerAdapter.addFragment(new TicketInfoFragment().newInstance(selectedTicket), "Ticket Info");
        viewPagerAdapter.addFragment(new TicketHistoryFragment().newInstance(maps), "History");


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
                        popupRequestVisitTicket.dismiss();
                        reasonVisit = popupRequestVisitTicket.getReasonET().getText().toString();
                        if(TextUtils.isEmpty(reasonVisit)) {
                            popupRequestVisitTicket.getReasonET().requestFocus();
                            popupRequestVisitTicket.getReasonET().setError(getResources().getString(R.string.error_reason_req_visit));
                        } else {
                            new SubmitRequestVisitTask().execute();
                        }
//                        actionDescribe = popupEscalationTicket.getActionET().getText().toString();
//                        requireSupport = popupEscalationTicket.getRequireET().getText().toString();
//                        new TicketActivity.EscalatedTicketTask().execute();
                    }
                });
                popupRequestVisitTicket.show(getActivity().getFragmentManager(), null);
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
                        popupCloseTicket.dismiss();
                        additionalInfo = popupCloseTicket.getTicketInfoET().getText().toString();
                        new TicketActivity.ClosedTicketTask().execute();
                    }
                });
                popupCloseTicket.show(getFragmentManager(), null);
            }
        });
        escalatedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupEscalationTicket = PopupEscalationTicket.newInstance("Escalation Ticket","Process","Back");
                popupEscalationTicket.setBackListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupEscalationTicket.dismiss();
                    }
                });
                popupEscalationTicket.setProcessListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupEscalationTicket.dismiss();
                        actionDescribe = popupEscalationTicket.getActionET().getText().toString();
                        requireSupport = popupEscalationTicket.getRequireET().getText().toString();
                        new TicketActivity.EscalatedTicketTask().execute();
                    }
                });
                popupEscalationTicket.show(getFragmentManager(), null);
            }
        });

        assignmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAssignmentTicket = PopupAssignmentTicket.newInstance("Assignment Ticket","Process","Back");
                popupAssignmentTicket.setBackListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupAssignmentTicket.dismiss();
                    }
                });
                popupAssignmentTicket.setProcessListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupAssignmentTicket.dismiss();
                        *//*actionDescribe = popupEscalationTicket.getActionET().getText().toString();
                        requireSupport = popupEscalationTicket.getRequireET().getText().toString();
                        new EscalatedTicketTask().execute();*//*
                    }
                });
                popupAssignmentTicket.show(getFragmentManager(), null);
            }
        });*/
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
            progressDialog.dismiss();

            try {
                JSONObject object = new JSONObject(result);
                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {

                    String page = Constants.MY_TASK_PAGE;
                    Boolean flag = Boolean.FALSE;
                    Fragment f = new MyAssignment();

                    preferences.savePreferences(Constants.ACTIVE_PAGE, page);
                    mListener.onMenuSelected(page, f, flag);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(context,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
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
