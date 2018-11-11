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
import com.cudocomm.troubleticket.util.Logcat;
import com.cudocomm.troubleticket.util.Preferences;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;

public class NonACNielsenFragment extends BaseFragment {

    private Preferences preferences;
    private View rootView;

    private TabLayout topTenTabLayout;
    private ViewPager topTenPager;
    private ViewPagerAdapter adapter;

    List<TopTenActive> topTenActives;
    List<TopTenSuspect> topTenSuspects;
    List<Ticket> tickets;

    private SwipeRefreshLayout topTenSwiper;

    List<PieEntry> statusEntries, severityEntries;
    List<Entry> openEntries, closeEntries;

    List<BarEntry> barOpenEntries, barCloseEntries, barCriticalEntries, barMajorEntries, barMinorEntries;


    List<String> months = new ArrayList<>();
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

    private void setupViewPager(ViewPager viewPager, List<TopTenActive> topTenActives, List<TopTenSuspect> topTenSuspects, List<Ticket> tickets,
                                List<PieEntry> statusEntries, List<PieEntry> severityEntries, List<Entry> openEntries, List<Entry> closeEntries,
                                List<BarEntry> barOpenEntries, List<BarEntry> barCloseEntries, List<BarEntry> barCriticalEntries, List<BarEntry> barMajorEntries, List<BarEntry> barMinorEntries) {
//        adapter = new ViewPagerAdapter(getFragmentManager());
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new TTActiveTicketFragment().newInstance(topTenActives), "Active Tickets");
        adapter.addFragment(new TTLongestFragment().newInstance(tickets), "Longest Tickets");
        adapter.addFragment(new TTSuspectFragment().newInstance(topTenSuspects), "Top Suspects");
        adapter.addFragment(new ACStatisticFragment().newInstance(
                statusEntries, severityEntries, openEntries, closeEntries, barOpenEntries, barCloseEntries, barCriticalEntries, barMajorEntries, barMinorEntries, months), "Statistic");
        viewPager.setAdapter(adapter);
    }

    private class TopTenTask extends AsyncTask<Void, Void, Void> {
        List<PieEntry> statusEntries = new ArrayList<>();
        List<PieEntry> severityEntries = new ArrayList<>();
        List<Entry> openEntries = new ArrayList<>();
        List<Entry> closeEntries = new ArrayList<>();

        List<BarEntry> barOpenEntries = new ArrayList<>();
        List<BarEntry> barCloseEntries = new ArrayList<>();
        List<BarEntry> barCriticalEntries = new ArrayList<>();
        List<BarEntry> barMajorEntries = new ArrayList<>();
        List<BarEntry> barMinorEntries = new ArrayList<>();
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
                        CommonsUtil.getAbsoluteUrl("non_ac_nielsen"),
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


                    JSONArray statusArray = object.getJSONArray("status_percentages");
                    for(int i=0; i<statusArray.length(); i++) {
                        JSONObject object = statusArray.getJSONObject(i);
                        statusEntries.add(new PieEntry(Float.parseFloat((String) object.get("total")), (String) object.get("ticket_status")));
                    }

                    JSONArray severityArray = object.getJSONArray("severity_percentages");
                    for(int i=0; i<severityArray.length(); i++) {
                        JSONObject object = severityArray.getJSONObject(i);
                        severityEntries.add(new PieEntry(Float.parseFloat((String) object.get("total")), (String) object.get("ticket_severity")));
                    }

                    JSONArray overviewOpenArray = object.getJSONArray("overview_open");
                    for(int i=0; i<overviewOpenArray.length(); i++) {
                        JSONObject object = overviewOpenArray.getJSONObject(i);
                        Logcat.i("OPEN::" + object.toString());
                        months.add(object.getString("month"));
//                            openEntries.add(new Entry(i, Float.parseFloat((String) object.get("value"))));
                        openEntries.add(new Entry(i, object.getInt("value")));
                    }

                    JSONArray overviewCloseArray = object.getJSONArray("overview_close");
                    for(int i=0; i<overviewCloseArray.length(); i++) {
                        JSONObject object = overviewCloseArray.getJSONObject(i);
                        Logcat.i("CLOSE::" + object.toString());
//                            closeEntries.add(new Entry(i, Float.parseFloat((String) object.get("value"))));
                        closeEntries.add(new Entry(i, object.getInt("value")));
                    }

                    JSONArray barOpenArray = object.getJSONArray("bar_open");
                    for(int i=0; i<barOpenArray.length(); i++) {
                        JSONObject object = barOpenArray.getJSONObject(i);
                        barOpenEntries.add(new BarEntry(i, object.getInt("value")));
                    }
                    JSONArray barCloseArray = object.getJSONArray("bar_close");
                    for(int i=0; i<barCloseArray.length(); i++) {
                        JSONObject object = barCloseArray.getJSONObject(i);
                        barCloseEntries.add(new BarEntry(i, object.getInt("value")));
                    }
                    JSONArray barCriticalArray = object.getJSONArray("bar_critical");
                    for(int i=0; i<barCriticalArray.length(); i++) {
                        JSONObject object = barCriticalArray.getJSONObject(i);
                        barCriticalEntries.add(new BarEntry(i, object.getInt("value")));
                    }
                    JSONArray barMajorArray = object.getJSONArray("bar_major");
                    for(int i=0; i<barMajorArray.length(); i++) {
                        JSONObject object = barMajorArray.getJSONObject(i);
                        barMajorEntries.add(new BarEntry(i, object.getInt("value")));
                    }
                    JSONArray barMinorArray = object.getJSONArray("bar_minor");
                    for(int i=0; i<barMinorArray.length(); i++) {
                        JSONObject object = barMinorArray.getJSONObject(i);
                        barMinorEntries.add(new BarEntry(i, object.getInt("value")));
                    }
                    loadListToRV(statusEntries, severityEntries, openEntries, closeEntries, barOpenEntries, barCloseEntries, barCriticalEntries, barMajorEntries, barMinorEntries);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            topTenSwiper.setRefreshing(false);

        }
    }

    private void loadListToRV(List<PieEntry> statusEntries, List<PieEntry> severityEntries, List<Entry> openEntries, List<Entry> closeEntries,
                              List<BarEntry> barOpenEntries, List<BarEntry> barCloseEntries, List<BarEntry> barCriticalEntries, List<BarEntry> barMajorEntries, List<BarEntry> barMinorEntries) {
        setupViewPager(topTenPager, topTenActives, topTenSuspects, tickets, statusEntries, severityEntries, openEntries, closeEntries,
                barOpenEntries, barCloseEntries, barCriticalEntries, barMajorEntries, barMinorEntries);
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
