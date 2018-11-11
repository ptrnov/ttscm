package com.cudocomm.troubleticket.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.adapter.ViewPagerAdapter;
import com.cudocomm.troubleticket.model.Ticket;
import com.cudocomm.troubleticket.util.ApiClient;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;

public class MyTicket extends BaseFragment {

    private View rootView;
    private TextView titleTV;
    private TextView statusTV;

    List<Ticket> openTickets;
    List<Ticket> confTickets;

    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter adapter;

    private String stationId;

    private SwipeRefreshLayout ticketSwiper;

    public static MyTicket myTicket;

    public MyTicket() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_ticket, container, false);

        initComponent();
        updateComponent();

        myTicket = this;

        return rootView;
    }

    private void initComponent() {
        ticketSwiper = (SwipeRefreshLayout) rootView.findViewById(R.id.ticketSwiper);
        titleTV = (TextView) rootView.findViewById(R.id.positionTV);
        statusTV = (TextView) rootView.findViewById(R.id.dateTV);
        mViewPager = (ViewPager) rootView.findViewById(R.id.container);
        ticketSwiper.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.color_btn_negative));
        tabLayout.setTabTextColors(getResources().getColor(R.color.color_white), getResources().getColor(R.color.color_home_header));
    }

    private void updateComponent() {
        titleTV.setText(getResources().getString(R.string.label_header_position, preferences.getPreferencesString(Constants.STATION_NAME)));
        statusTV.setText(getResources().getString(R.string.label_header_date, CommonsUtil.dateToString(new Date())));

        /*ticketSwiper.post(new Runnable() {
            @Override
            public void run() {
                new MyTicketTask().execute();
            }
        });*/
        ticketSwiper.post(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        });

        /*ticketSwiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new MyTicketTask().execute();
            }
        });*/
        ticketSwiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

    }

    private void setupViewPager(ViewPager viewPager, List<Ticket> openTickets, List<Ticket> confirmTickets) {
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new MyTicketList().newInstance(openTickets), "Open Ticket");
        adapter.addFragment(new MyTicketList().newInstance(confirmTickets), "Need Confirmation");
        viewPager.setAdapter(adapter);
    }

    public void refresh() {
        new MyTicketTask().execute();
        /*loadListToRV();*/
    }

    /*private class MyTicketTask extends AsyncTask<Void, Void, Void> {

        String result;
        JSONObject object;
        List<Ticket> openTickets;
        List<Ticket> confTickets;



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
                                        .add(Constants.PARAM_STATION_ID, String.valueOf(preferences.getPreferencesInt(Constants.STATION_ID)))
                                        .build());
                        break;
                    case Constants.KST :
                        result = ApiClient.post(
                                CommonsUtil.getAbsoluteUrl(Constants.URL_GET_ESCALATION_TICKET),
                                new FormBody.Builder()
                                        .add(Constants.PARAM_ID, String.valueOf(preferences.getPreferencesInt(Constants.USER_ID)))
                                        .add(Constants.POSITION_ID, String.valueOf(preferences.getPreferencesInt(Constants.POSITION_ID)))
                                        .build());
                        break;
                    case Constants.KORWIL :
                        result = ApiClient.post(
                                CommonsUtil.getAbsoluteUrl(Constants.URL_GET_ESCALATION_TICKET),
                                new FormBody.Builder()
                                        .add(Constants.PARAM_ID, String.valueOf(preferences.getPreferencesInt(Constants.USER_ID)))
                                        .add(Constants.POSITION_ID, String.valueOf(preferences.getPreferencesInt(Constants.POSITION_ID)))
                                        .build());
                        break;
                    case Constants.KADEP_TS :
                        result = ApiClient.post(
                                CommonsUtil.getAbsoluteUrl(Constants.URL_GET_ESCALATION_TICKET),
                                new FormBody.Builder()
                                        .add(Constants.PARAM_ID, String.valueOf(preferences.getPreferencesInt(Constants.USER_ID)))
                                        .add(Constants.POSITION_ID, String.valueOf(preferences.getPreferencesInt(Constants.POSITION_ID)))
                                        .build());
                        break;
                    default:
                        result = ApiClient.post(
                                CommonsUtil.getAbsoluteUrl(Constants.URL_GET_ESCALATION_TICKET),
                                new FormBody.Builder()
                                        .add(Constants.PARAM_ID, String.valueOf(preferences.getPreferencesInt(Constants.USER_ID)))
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

            try {
                object = new JSONObject(result);
                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
                    if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.TECHNICIAN) {
                        Type type = new TypeToken<List<Ticket>>(){}.getType();
                        openTickets = gson.fromJson(object.getString(Constants.RESPONSE_OPEN_TICKET), type);
                        confTickets = gson.fromJson(object.getString(Constants.RESPONSE_CONF_TICKET), type);
                        setupViewPager(mViewPager, openTickets, confTickets);
                        tabLayout.setupWithViewPager(mViewPager);
                    } else  {
                        Type type = new TypeToken<List<Ticket>>(){}.getType();
                        openTickets = gson.fromJson(object.getString("tickets"), type);
                        confTickets = new ArrayList<>();
                        setupViewPager(mViewPager, openTickets, confTickets);
                        tabLayout.setupWithViewPager(mViewPager);
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ticketSwiper.setRefreshing(false);
        }
    }*/

    private class MyTicketTask extends AsyncTask<Void, Void, Void> {

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
                        result = ApiClient.post(
                                CommonsUtil.getAbsoluteUrl(Constants.URL_GET_ESCALATION_TICKET),
                                new FormBody.Builder()
                                        .add(Constants.PARAM_ID, String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                                        .add(Constants.POSITION_ID, String.valueOf(preferences.getPreferencesInt(Constants.POSITION_ID)))
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

            try {
                object = new JSONObject(result);
                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
                    if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.TECHNICIAN) {
                        Type type = new TypeToken<List<Ticket>>(){}.getType();
                        openTickets = gson.fromJson(object.getString(Constants.RESPONSE_OPEN_TICKET), type);
                        confTickets = gson.fromJson(object.getString(Constants.RESPONSE_CONF_TICKET), type);
                    } else  {
                        Type type = new TypeToken<List<Ticket>>(){}.getType();
                        openTickets = gson.fromJson(object.getString("tickets"), type);
                        confTickets = new ArrayList<>();
//                        setupViewPager(mViewPager, openTickets, confTickets);
//                        tabLayout.setupWithViewPager(mViewPager);
                    }
                    loadListToRV();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ticketSwiper.setRefreshing(false);
        }
    }

    private void loadListToRV() {
        setupViewPager(mViewPager, openTickets, confTickets);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        Logcat.i("onAttach ticketSwiper");
//        new MyTicketTask().execute();
    }
}
