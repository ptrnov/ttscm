package com.cudocomm.troubleticket.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.adapter.ViewPagerAdapter;
import com.cudocomm.troubleticket.util.ApiClient;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Logcat;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;

public class HomeKadepInfra extends BaseFragment {

    private View rootView;

    TabLayout chartTabLayout;
    ViewPager chartContentPager;
    private ViewPagerAdapter adapter;
    SwipeRefreshLayout chartSwiper;

    private TextView userInfoTV;
    private TextView welcomeMsgTV;
    private TextView dateTimeTV;
    private ImageView profileImage;

    private String imageUrl;

    final List<PieEntry> anStatusEntries = new ArrayList<>();
    final List<PieEntry> anSeverityEntries = new ArrayList<>();
    final List<Entry> anOpenEntries = new ArrayList<>();
    final List<Entry> anCloseEntries = new ArrayList<>();

    final List<BarEntry> anBarOpenEntries = new ArrayList<>();
    final List<BarEntry> anBarCloseEntries = new ArrayList<>();
    final List<BarEntry> anBarCriticalEntries = new ArrayList<>();
    final List<BarEntry> anBarMajorEntries = new ArrayList<>();
    final List<BarEntry> anBarMinorEntries = new ArrayList<>();

    final List<PieEntry> nanStatusEntries = new ArrayList<>();
    final List<PieEntry> nanSeverityEntries = new ArrayList<>();
    final List<Entry> nanOpenEntries = new ArrayList<>();
    final List<Entry> nanCloseEntries = new ArrayList<>();

    final List<BarEntry> nanBarOpenEntries = new ArrayList<>();
    final List<BarEntry> nanBarCloseEntries = new ArrayList<>();
    final List<BarEntry> nanBarCriticalEntries = new ArrayList<>();
    final List<BarEntry> nanBarMajorEntries = new ArrayList<>();
    final List<BarEntry> nanBarMinorEntries = new ArrayList<>();

    List<String> months = new ArrayList<>();

    String location;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home_kadep_infra, container, false);

        initComponent();


        return rootView;
    }

    private void initComponent() {
        userInfoTV = (TextView) rootView.findViewById(R.id.userInfoTV);
        welcomeMsgTV = (TextView) rootView.findViewById(R.id.welcomeMsgTV);
        dateTimeTV = (TextView) rootView.findViewById(R.id.dateTimeTV);
        profileImage = (ImageView) rootView.findViewById(R.id.profile_image);



        if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.TECHNICIAN || preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KST)
            location = preferences.getPreferencesString(Constants.STATION_NAME);
        else if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KORWIL)
            location = preferences.getPreferencesString(Constants.REGION_NAME);
        else if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_WIL)
            location = preferences.getPreferencesString(Constants.DEPARTMENT_NAME);
        else if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_TS || preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_INFRA)
            location = "Nasional";

        userInfoTV.setText(getResources().getString(R.string.label_user_info,
                preferences.getPreferencesString(Constants.USER_NAME),
                preferences.getPreferencesString(Constants.POSITION_NAME),
                location));
        welcomeMsgTV.setText(getResources().getString(R.string.label_welcome_msg, preferences.getPreferencesString(Constants.USER_NAME)));


        if(!preferences.getPreferencesString(Constants.USER_PICTURE).isEmpty() && !preferences.getPreferencesString(Constants.USER_PICTURE).equals("")) {
            if(preferences.getPreferencesString(Constants.USER_PICTURE).contains("/assets/images/")) {
                imageUrl = CommonsUtil.getAbsoluteUrlImage(preferences.getPreferencesString(Constants.USER_PICTURE));
            } else {
                imageUrl = CommonsUtil.getAbsoluteUrlImage("/assets/images/user_picture/" + preferences.getPreferencesString(Constants.USER_PICTURE));
            }
            Picasso.with(getContext()).load(imageUrl).error(R.drawable.ic_no_image).into(profileImage);
        }


        dateTimeTV.setText(CommonsUtil.getToday());


        chartTabLayout = (TabLayout) rootView.findViewById(R.id.chartTabLayout);
        chartContentPager = (ViewPager) rootView.findViewById(R.id.chartContentPager);
        chartSwiper = (SwipeRefreshLayout) rootView.findViewById(R.id.chartSwiper);
        chartSwiper.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        chartTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.color_btn_negative));
        chartTabLayout.setTabTextColors(getResources().getColor(R.color.color_home_header), getResources().getColor(R.color.color_white));

        chartSwiper.post(new Runnable() {
            @Override
            public void run() {
                new StatisticTask().execute();
            }
        });

        chartSwiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new StatisticTask().execute();
            }
        });
    }

    private class StatisticTask extends AsyncTask<Void, Void, Void> {

        String result;
        JSONObject object;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            chartSwiper.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(
                        CommonsUtil.getAbsoluteUrl("all_nielsen"),
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

                    JSONArray statusArray = object.getJSONArray("an_status_percentages");
                    for(int i=0; i<statusArray.length(); i++) {
                        JSONObject object = statusArray.getJSONObject(i);
                        anStatusEntries.add(new PieEntry(Float.parseFloat((String) object.get("total")), (String) object.get("ticket_status")));
                    }

                    JSONArray severityArray = object.getJSONArray("an_severity_percentages");
                    for(int i=0; i<severityArray.length(); i++) {
                        JSONObject object = severityArray.getJSONObject(i);
                        anSeverityEntries.add(new PieEntry(Float.parseFloat((String) object.get("total")), (String) object.get("ticket_severity")));
                    }

                    JSONArray overviewOpenArray = object.getJSONArray("an_overview_open");
                    for(int i=0; i<overviewOpenArray.length(); i++) {
                        JSONObject object = overviewOpenArray.getJSONObject(i);
                        Logcat.i("OPEN::" + object.toString());
                        months.add(object.getString("month"));
//                            openEntries.add(new Entry(i, Float.parseFloat((String) object.get("value"))));
                        anOpenEntries.add(new Entry(i, object.getInt("value")));
                    }

                    JSONArray overviewCloseArray = object.getJSONArray("an_overview_close");
                    for(int i=0; i<overviewCloseArray.length(); i++) {
                        JSONObject object = overviewCloseArray.getJSONObject(i);
                        Logcat.i("CLOSE::" + object.toString());
//                            closeEntries.add(new Entry(i, Float.parseFloat((String) object.get("value"))));
                        anCloseEntries.add(new Entry(i, object.getInt("value")));
                    }

                    JSONArray barOpenArray = object.getJSONArray("an_bar_open");
                    for(int i=0; i<barOpenArray.length(); i++) {
                        JSONObject object = barOpenArray.getJSONObject(i);
                        anBarOpenEntries.add(new BarEntry(i, object.getInt("value")));
                    }
                    JSONArray barCloseArray = object.getJSONArray("an_bar_close");
                    for(int i=0; i<barCloseArray.length(); i++) {
                        JSONObject object = barCloseArray.getJSONObject(i);
                        anBarCloseEntries.add(new BarEntry(i, object.getInt("value")));
                    }
                    JSONArray barCriticalArray = object.getJSONArray("an_bar_critical");
                    for(int i=0; i<barCriticalArray.length(); i++) {
                        JSONObject object = barCriticalArray.getJSONObject(i);
                        anBarCriticalEntries.add(new BarEntry(i, object.getInt("value")));
                    }
                    JSONArray barMajorArray = object.getJSONArray("an_bar_major");
                    for(int i=0; i<barMajorArray.length(); i++) {
                        JSONObject object = barMajorArray.getJSONObject(i);
                        anBarMajorEntries.add(new BarEntry(i, object.getInt("value")));
                    }
                    JSONArray barMinorArray = object.getJSONArray("an_bar_minor");
                    for(int i=0; i<barMinorArray.length(); i++) {
                        JSONObject object = barMinorArray.getJSONObject(i);
                        anBarMinorEntries.add(new BarEntry(i, object.getInt("value")));
                    }

                    JSONArray nstatusArray = object.getJSONArray("nan_status_percentages");
                    for(int i=0; i<nstatusArray.length(); i++) {
                        JSONObject object = nstatusArray.getJSONObject(i);
                        nanStatusEntries.add(new PieEntry(Float.parseFloat((String) object.get("total")), (String) object.get("ticket_status")));
                    }

                    JSONArray nseverityArray = object.getJSONArray("nan_severity_percentages");
                    for(int i=0; i<nseverityArray.length(); i++) {
                        JSONObject object = nseverityArray.getJSONObject(i);
                        nanSeverityEntries.add(new PieEntry(Float.parseFloat((String) object.get("total")), (String) object.get("ticket_severity")));
                    }

                    JSONArray noverviewOpenArray = object.getJSONArray("nan_overview_open");
                    for(int i=0; i<noverviewOpenArray.length(); i++) {
                        JSONObject object = noverviewOpenArray.getJSONObject(i);
                        nanOpenEntries.add(new Entry(i, object.getInt("value")));
                    }

                    JSONArray noverviewCloseArray = object.getJSONArray("nan_overview_close");
                    for(int i=0; i<noverviewCloseArray.length(); i++) {
                        JSONObject object = noverviewCloseArray.getJSONObject(i);
                        nanCloseEntries.add(new Entry(i, object.getInt("value")));
                    }

                    JSONArray nbarOpenArray = object.getJSONArray("nan_bar_open");
                    for(int i=0; i<nbarOpenArray.length(); i++) {
                        JSONObject object = nbarOpenArray.getJSONObject(i);
                        nanBarOpenEntries.add(new BarEntry(i, object.getInt("value")));
                    }
                    JSONArray nbarCloseArray = object.getJSONArray("nan_bar_close");
                    for(int i=0; i<nbarCloseArray.length(); i++) {
                        JSONObject object = nbarCloseArray.getJSONObject(i);
                        nanBarCloseEntries.add(new BarEntry(i, object.getInt("value")));
                    }
                    JSONArray nbarCriticalArray = object.getJSONArray("nan_bar_critical");
                    for(int i=0; i<nbarCriticalArray.length(); i++) {
                        JSONObject object = nbarCriticalArray.getJSONObject(i);
                        nanBarCriticalEntries.add(new BarEntry(i, object.getInt("value")));
                    }
                    JSONArray nbarMajorArray = object.getJSONArray("nan_bar_major");
                    for(int i=0; i<nbarMajorArray.length(); i++) {
                        JSONObject object = nbarMajorArray.getJSONObject(i);
                        nanBarMajorEntries.add(new BarEntry(i, object.getInt("value")));
                    }
                    JSONArray nbarMinorArray = object.getJSONArray("nan_bar_minor");
                    for(int i=0; i<nbarMinorArray.length(); i++) {
                        JSONObject object = nbarMinorArray.getJSONObject(i);
                        nanBarMinorEntries.add(new BarEntry(i, object.getInt("value")));
                    }

                    adapter = new ViewPagerAdapter(getChildFragmentManager());
                    adapter.addFragment(ACStatisticFragment.newInstance(
                            anStatusEntries, anSeverityEntries, anOpenEntries, anCloseEntries, anBarOpenEntries, anBarCloseEntries,
                            anBarCriticalEntries, anBarMajorEntries, anBarMinorEntries, months), "AC Nielsen");

                    adapter.addFragment(ACStatisticFragment.newInstance(
                            nanStatusEntries, nanSeverityEntries, nanOpenEntries, nanCloseEntries, nanBarOpenEntries, nanBarCloseEntries,
                            nanBarCriticalEntries, nanBarMajorEntries, nanBarMinorEntries, months), "Non AC Nielsen");
                    chartContentPager.setAdapter(adapter);
                    chartTabLayout.setupWithViewPager(chartContentPager);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            chartSwiper.setRefreshing(false);

        }
    }

}
