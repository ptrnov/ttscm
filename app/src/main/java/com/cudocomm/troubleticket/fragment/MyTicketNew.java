package com.cudocomm.troubleticket.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.activity.TicketActivity;
import com.cudocomm.troubleticket.adapter.TicketAdapter;
import com.cudocomm.troubleticket.model.CounterModel;
import com.cudocomm.troubleticket.model.Ticket;
import com.cudocomm.troubleticket.util.ApiClient;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Logcat;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;

public class MyTicketNew extends BaseFragment {

    private View rootView;
    private TextView titleTV;
    private TextView statusTV;

    private RecyclerView ticketListRV;
    List<Ticket> tickets;
    private CounterModel myTaskCounter;

    private String stationId;

    private SwipeRefreshLayout ticketSwiper;
    private TicketAdapter ticketAdapter;

    public static MyTicketNew myTicket;

    public View emptyListLayout;

    public MyTicketNew() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logcat.i("MyTicketNew.java for korwil onCreate()");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_ticket_new, container, false);

        Logcat.i("MyTicketNew.java for korwil onCreateView()");
        initComponent();
        updateComponent();

        myTicket = this;

        return rootView;
    }

    private void initComponent() {

        Logcat.i("MyTicketNew.java for korwil initComponent()");
        tickets = new ArrayList<>();
        emptyListLayout = rootView.findViewById(R.id.emptyListLayout);
        ticketSwiper = (SwipeRefreshLayout) rootView.findViewById(R.id.ticketSwiper);
        titleTV = (TextView) rootView.findViewById(R.id.positionTV);
        statusTV = (TextView) rootView.findViewById(R.id.dateTV);
        ticketListRV = (RecyclerView) rootView.findViewById(R.id.ticketListRV);
        ticketSwiper.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void updateComponent() {

        Logcat.i("MyTicketNew.java for korwil updateComponent()");
        if((preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.TECHNICIAN) || (preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KST))
            titleTV.setText(getResources().getString(R.string.label_header_position, preferences.getPreferencesString(Constants.STATION_NAME)));
        else if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KORWIL)
            titleTV.setText(getResources().getString(R.string.label_header_position_region, preferences.getPreferencesString(Constants.REGION_NAME)));
        else if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_WIL)
            titleTV.setText(getResources().getString(R.string.label_header_position_department, preferences.getPreferencesString(Constants.DEPARTMENT_NAME)));
        else if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_TS)
            titleTV.setText(getResources().getString(R.string.label_header_position_national));

        else
            titleTV.setText(getResources().getString(R.string.label_header_position, preferences.getPreferencesString(Constants.STATION_NAME)));

        statusTV.setText(getResources().getString(R.string.label_header_date, CommonsUtil.dateToString(new Date())));

        ticketSwiper.post(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        });

        ticketSwiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

    }

    public void refresh() {
        new MyTicketTask().execute();

    }

    private void processList() {
        if(tickets.size() > 0) {
            if(ticketAdapter == null) {
                ticketAdapter = new TicketAdapter(tickets, new TicketAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Ticket ticket) {
                        Intent intent = new Intent(context, TicketActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants.SELECTED_TICKET, ticket);
                        intent.putExtras(bundle);
                        getActivity().startActivityForResult(intent, Constants.REQUEST_CODE);

                    }
                });
                ticketAdapter.setHasStableIds(true);
                ticketListRV.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                ticketListRV.setLayoutManager(linearLayoutManager);


                ticketListRV.setAdapter(ticketAdapter);
            } else {
                ticketAdapter.swap(tickets);
            }
            emptyListLayout.setVisibility(View.GONE);
            ticketListRV.setVisibility(View.VISIBLE);
        } else {
            emptyListLayout.setVisibility(View.VISIBLE);
            ticketListRV.setVisibility(View.GONE);
        }


    }

    class MyTicketTask extends AsyncTask<Void, Void, Void> {

        String result;
        JSONObject object;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ticketSwiper.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                switch (preferences.getPreferencesInt(Constants.POSITION_ID)) {
                    case Constants.TECHNICIAN :
                        result = ApiClient.post(
                                CommonsUtil.getAbsoluteUrl(Constants.MY_TICKET),
                                new FormBody.Builder()
                                        .add(Constants.PARAM_STATION_ID, String.valueOf(preferences.getPreferencesString(Constants.STATION_ID)))
                                        .build());
                        break;
                    case Constants.KST :
                        /*result = ApiClient.post(
                                CommonsUtil.getAbsoluteUrl(Constants.URL_GET_ESCALATION_TICKET),
                                new FormBody.Builder()
                                        .add(Constants.PARAM_ID, String.valueOf(preferences.getPreferencesInt(Constants.USER_ID)))
                                        .add(Constants.POSITION_ID, String.valueOf(preferences.getPreferencesInt(Constants.POSITION_ID)))
                                        .build());*/
                        result = ApiClient.post(
                                CommonsUtil.getAbsoluteUrl(Constants.MY_TICKET),
                                new FormBody.Builder()
                                        .add(Constants.PARAM_STATION_ID, String.valueOf(preferences.getPreferencesString(Constants.STATION_ID)))
                                        .build());
                        break;
                    case Constants.KORWIL :
                        result = ApiClient.post(
                                CommonsUtil.getAbsoluteUrl(Constants.URL_GET_ESCALATION_TICKET),
                                new FormBody.Builder()
                                        .add(Constants.PARAM_ID, String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                                        .add(Constants.POSITION_ID, String.valueOf(preferences.getPreferencesInt(Constants.POSITION_ID)))
                                        .build());
                        break;
                    case Constants.KADEP_TS :
                        result = ApiClient.post(
                                CommonsUtil.getAbsoluteUrl(Constants.URL_GET_ESCALATION_TICKET),
                                new FormBody.Builder()
                                        .add(Constants.PARAM_ID, String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                                        .add(Constants.POSITION_ID, String.valueOf(preferences.getPreferencesInt(Constants.POSITION_ID)))
                                        .build());
                        break;
                    case Constants.KADEP_WIL :
                        result = ApiClient.post(
                                CommonsUtil.getAbsoluteUrl("get_active_tickets"),
                                new FormBody.Builder()
                                        .add(Constants.PARAM_ID, String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                                        .build());
                        break;
                    default:
                        result = ApiClient.post(
                                CommonsUtil.getAbsoluteUrl(Constants.URL_GET_ESCALATION_TICKET),
                                new FormBody.Builder()
                                        .add(Constants.PARAM_ID, String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                                        .add(Constants.POSITION_ID, String.valueOf(preferences.getPreferencesInt(Constants.POSITION_ID)))
                                        .build());
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

            if(isAdded()) {
                try {
                    object = new JSONObject(result);
                    if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
                        Type type = new TypeToken<List<Ticket>>(){}.getType();
                        tickets = gson.fromJson(object.getString("tickets"), type);

                        Type counterType = new TypeToken<CounterModel>(){}.getType();
                        myTaskCounter = gson.fromJson(object.getString("mytaskcounter"), counterType);
                        if(myTaskCounter != null)
                            preferences.savePreferences(Constants.COUNTER_MY_TASK, getResources().getString(R.string.widget_need_approval_counter, myTaskCounter.getCritical(), myTaskCounter.getMajor(), myTaskCounter.getMinor()));
                        else
                            preferences.savePreferences(Constants.COUNTER_MY_TASK, getResources().getString(R.string.widget_need_approval_counter_null));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                processList();
            }
            ticketSwiper.setRefreshing(false);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Logcat.i("MyTicketNew.java for korwil onAttach()");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Logcat.i("MyTicketNew.java for korwil onActivityResult()");
        new MyTicketTask().execute();

    }
}
