package com.cudocomm.troubleticket.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.adapter.ViewPagerAdapter;
import com.cudocomm.troubleticket.model.Ticket;
import com.cudocomm.troubleticket.model.TopTenActive;
import com.cudocomm.troubleticket.model.TopTenSuspect;
import com.cudocomm.troubleticket.util.ApiClient;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Preferences;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.FormBody;

public class TopTenFragment extends BaseFragment {

    private Preferences preferences;
    private View rootView;

    private TabLayout topTenTabLayout;
    private ViewPager topTenPager;
    private ViewPagerAdapter adapter;

    List<TopTenActive> topTenActives;
    List<TopTenSuspect> topTenSuspects;
    List<Ticket> tickets;

    private SwipeRefreshLayout topTenSwiper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_top_ten, container, false);

        initComponent();
        updateComponent();

        return rootView;
    }

    private void initComponent() {
        topTenPager = (ViewPager) rootView.findViewById(R.id.topTenPager);
        topTenSwiper = (SwipeRefreshLayout) rootView.findViewById(R.id.topTenSwiper);
        topTenSwiper.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        topTenTabLayout = (TabLayout) rootView.findViewById(R.id.topTenTabLayout);
        topTenTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.color_btn_negative));
        topTenTabLayout.setTabTextColors(getResources().getColor(R.color.color_home_header), getResources().getColor(R.color.color_white));

    }

    private void updateComponent() {
        topTenSwiper.post(new Runnable() {
            @Override
            public void run() {
                new TopTenTask().execute();
            }
        });

        topTenSwiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new TopTenTask().execute();
            }
        });
    }

    private void refresh() {

    }

    private void setupViewPager(ViewPager viewPager, List<TopTenActive> topTenActives, List<TopTenSuspect> topTenSuspects, List<Ticket> tickets) {
//        adapter = new ViewPagerAdapter(getFragmentManager());
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new TTActiveTicketFragment().newInstance(topTenActives), "Top 10 Active");
        adapter.addFragment(new TTSuspectFragment().newInstance(topTenSuspects), "Top 10 Suspects");
        adapter.addFragment(new TTLongestFragment().newInstance(tickets), "Top 10 Longest");
        viewPager.setAdapter(adapter);
    }

    private class TopTenTask extends AsyncTask<Void, Void, Void> {

        String result;
        JSONObject object;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            topTenSwiper.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(
                        CommonsUtil.getAbsoluteUrl("top_ten"),
                        new FormBody.Builder().build());
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
                    Type type = new TypeToken<List<TopTenActive>>(){}.getType();
                    topTenActives = gson.fromJson(object.getString("top_ten_active_tickets"), type);
                    Type typeS = new TypeToken<List<TopTenSuspect>>(){}.getType();
                    topTenSuspects = gson.fromJson(object.getString("top_ten_suspects"), typeS);
                    Type typeT = new TypeToken<List<Ticket>>(){}.getType();
                    tickets = gson.fromJson(object.getString("top_ten_longest_tickets"), typeT);
                    loadListToRV();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            topTenSwiper.setRefreshing(false);

        }
    }

    private void loadListToRV() {
        setupViewPager(topTenPager, topTenActives, topTenSuspects, tickets);
        topTenTabLayout.setupWithViewPager(topTenPager);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        new TopTenTask().execute();
    }
}
