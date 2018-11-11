package com.cudocomm.troubleticket.fragment;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.adapter.TicketAdapter;
import com.cudocomm.troubleticket.adapter.ViewPagerAdapter;
import com.cudocomm.troubleticket.component.PopupAssignmentTicket;
import com.cudocomm.troubleticket.component.PopupCloseTicket;
import com.cudocomm.troubleticket.component.PopupEscalationTicket;
import com.cudocomm.troubleticket.model.Ticket;
import com.cudocomm.troubleticket.model.TicketLog;
import com.cudocomm.troubleticket.model.UserModel;
import com.cudocomm.troubleticket.util.ApiClient;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Logcat;
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

public class TicketFragment extends BaseFragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private View rootView;

    private SliderLayout photoPreviewLayout;
    private TabLayout tabLayout;
    private ViewPager contentPager;
    private ViewPagerAdapter viewPagerAdapter;

    private Ticket returnTicket;
    private Ticket selectedTicket;
    private int selectedTicketPosition;
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

    private SpotsDialog progressDialog;

    private String additionalInfo;
    private String actionDescribe;
    private String requireSupport;

    private List<UserModel> engineers;

    public static TicketFragment newInstance(Ticket ticket) {
        TicketFragment fragment = new TicketFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.SELECTED_TICKET, ticket);
        fragment.setArguments(args);
        return fragment;
    }

    public TicketFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedTicket = (Ticket) getArguments().getSerializable(Constants.SELECTED_TICKET);
            selectedTicketPosition =  getArguments().getInt(Constants.SELECTED_TICKET_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ticket, container, false);

        initComponent();
        new TicketTask().execute();


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
        /*if(selectedTicket.getTicketStatus() == 1) {
            actionLayout.setVisibility(View.VISIBLE);
            if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.TECHNICIAN && preferences.getPreferencesInt(Constants.POSITION_ID) == selectedTicket.getTicketPosition()) {
                actionLL.setVisibility(View.VISIBLE);
                actionKadepTSLL.setVisibility(View.GONE);
            } else if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_TS && preferences.getPreferencesInt(Constants.POSITION_ID) == selectedTicket.getTicketPosition()) {
                actionLL.setVisibility(View.GONE);
                actionKadepTSLL.setVisibility(View.VISIBLE);
            } else if(preferences.getPreferencesInt(Constants.POSITION_ID) == selectedTicket.getTicketPosition()) {
                actionLL.setVisibility(View.VISIBLE);
                actionKadepTSLL.setVisibility(View.GONE);
            }
        } else {
            actionLayout.setVisibility(View.GONE);
        }*/

        if(selectedTicket.getTicketStatus() == 1) {
            if(preferences.getPreferencesInt(Constants.POSITION_ID) == selectedTicket.getTicketPosition()) {
                if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_TS) {
                    actionLayout.setVisibility(View.VISIBLE);
                    actionLL.setVisibility(View.GONE);
                    actionKadepTSLL.setVisibility(View.VISIBLE);
                } else {
                    actionLayout.setVisibility(View.VISIBLE);
                    actionLL.setVisibility(View.VISIBLE);
                    actionKadepTSLL.setVisibility(View.GONE);
                }

            }  else {
                actionLayout.setVisibility(View.GONE);
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
                        popupCloseTicket.dismiss();
                        additionalInfo = popupCloseTicket.getTicketInfoET().getText().toString();
                        new ClosedTicketTask().execute();
                    }
                });
                popupCloseTicket.show(getActivity().getFragmentManager(), null);
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
                        new EscalatedTicketTask().execute();
                    }
                });
                popupEscalationTicket.show(getActivity().getFragmentManager(), null);
            }
        });

        assignmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new GetEngineerTask().execute();

                Fragment f = new AssignmentFragment();
                String page = Constants.ASSIGNMENT_PAGE;
                Boolean flag = Boolean.FALSE;
                Bundle args = new Bundle();
                args.putString(Constants.PARAM_SECTION, Constants.TICKET_INFO_PAGE);
                args.putSerializable(Constants.SELECTED_TICKET, selectedTicket);
                f.setArguments(args);
                preferences.savePreferences(Constants.ACTIVE_PAGE, page);
                mListener.onMenuSelected(page, f, flag);

            }
        });
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
                            (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle(title)
                                    .setContentText(content);

                    Notification note = mBuilder.build();

                    mgr.notify(NotificationManager.IMPORTANCE_HIGH, note);

                    /*ComponentName componentName = getCallingActivity();

                    RecyclerView ticketListRV = (RecyclerView) findViewById(R.id.ticketListRV);
                    ((TicketAdapter) ticketListRV.getAdapter()).getmDataset().remove(ticket);
                    ((TicketAdapter) ticketListRV.getAdapter()).notifyDataSetChanged();*/




//                    finish();
                    getActivity().onBackPressed();
                    progressDialog.dismiss();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class EscalatedTicketTask extends AsyncTask<Void, Void, Void> {

        String result = "";
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gsona = this.gsonBuilder.create();

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
                switch (preferences.getPreferencesInt(Constants.POSITION_ID)) {
                    case Constants.TECHNICIAN:
                        url = "escalated_ticket";
                        result = ApiClient.post(CommonsUtil.getAbsoluteUrl(url), new FormBody.Builder()
                                .add("ticket_id", selectedTicket.getTicketId())
                                .add("from_user_id", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                                .add("from_station_id", String.valueOf(preferences.getPreferencesString(Constants.STATION_ID)))
                                .add("from_position_id", String.valueOf(preferences.getPreferencesInt(Constants.POSITION_ID)))
                                .add("action", actionDescribe)
                                .add("require", requireSupport)
                                .build());
                        break;
                    case Constants.KST:
                        url = "escalated_ticket_lv2";
                        result = ApiClient.post(CommonsUtil.getAbsoluteUrl(url), new FormBody.Builder()
                                .add("ticket_id", selectedTicket.getTicketId())
                                .add("from_user_id", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                                .add("from_station_id", String.valueOf(preferences.getPreferencesString(Constants.STATION_ID)))
                                .add("from_position_id", String.valueOf(preferences.getPreferencesInt(Constants.POSITION_ID)))
                                .add("action", actionDescribe)
                                .add("require", requireSupport)
                                .build());
                        break;
                    case Constants.KORWIL:
                        url = "escalated_ticket_lv3";
                        result = ApiClient.post(CommonsUtil.getAbsoluteUrl(url), new FormBody.Builder()
                                .add("ticket_id", selectedTicket.getTicketId())
                                .add("from_user_id", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                                .add("from_station_id", String.valueOf(preferences.getPreferencesString(Constants.REGION_ID)))
                                .add("from_position_id", String.valueOf(preferences.getPreferencesInt(Constants.POSITION_ID)))
                                .add("action", actionDescribe)
                                .add("require", requireSupport)
                                .build());
                        break;
                    case Constants.KADEP_WIL:
                        url = "escalated_ticket_lv4";
                        result = ApiClient.post(CommonsUtil.getAbsoluteUrl(url), new FormBody.Builder()
                                .add("ticket_id", selectedTicket.getTicketId())
                                .add("from_user_id", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                                .add("from_station_id", String.valueOf(preferences.getPreferencesString(Constants.DEPARTMENT_ID)))
                                .add("from_position_id", String.valueOf(preferences.getPreferencesInt(Constants.POSITION_ID)))
                                .add("action", actionDescribe)
                                .add("require", requireSupport)
                                .build());
                        break;
                    default:
                        break;
                }
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

                    /*MyTicket.myTicket.refresh();
                    getActivity().onBackPressed();*/

                    /*Fragment f = new MyTicket();
                    String page = Constants.MY_TASK_PAGE;
                    Boolean flag = Boolean.valueOf(false);
                    preferences.savePreferences(Constants.ACTIVE_PAGE, page);
                    mListener.onMenuSelected(page, f, flag);*/

                    Type type = new TypeToken<Ticket>(){}.getType();
                    Ticket returnTicket = gson.fromJson(object.getString("return"), type);

                    RecyclerView ticketListRV = (RecyclerView) getActivity().findViewById(R.id.ticketListRV);
                    /*((TicketAdapter) ticketListRV.getAdapter()).addItem(selectedTicketPosition, returnTicket);
                    ((TicketAdapter) ticketListRV.getAdapter()).notifyDataSetChanged();*/
                    ((TicketAdapter) ticketListRV.getAdapter()).getmDataset().add(selectedTicketPosition, returnTicket);
//                    ((TicketAdapter) ticketListRV.getAdapter()).notifyDataSetChanged();
                    ticketListRV.getAdapter().notifyItemChanged(selectedTicketPosition);

                    getActivity().onBackPressed();

                    progressDialog.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetEngineerTask extends AsyncTask<Void, Void, Void> {

        String result;
        JSONObject object;
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gsona = gsonBuilder.create();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(
                        CommonsUtil.getAbsoluteUrl(Constants.URL_GET_ENGINEER), new FormBody.Builder().build());
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
                    Type type = new TypeToken<List<UserModel>>(){}.getType();
                    try {
                        engineers = gsona.fromJson(object.getString("data"), type);
                        Logcat.i("engineer_size:" + engineers.size());

                        popupAssignmentTicket = PopupAssignmentTicket.newInstance("Assignment Ticket","Process","Back", engineers);
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
                            }
                        });

                        popupAssignmentTicket.show(getActivity().getSupportFragmentManager(), null);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
//            popupAssignmentTicket.show(getFragmentManager(), null);

        }
    }


}
