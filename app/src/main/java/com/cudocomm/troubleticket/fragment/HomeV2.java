package com.cudocomm.troubleticket.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.TTSApplication;
import com.cudocomm.troubleticket.util.BarChartItem;
import com.cudocomm.troubleticket.util.ChartItem;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.LineChartItem;
import com.cudocomm.troubleticket.util.Logcat;
import com.cudocomm.troubleticket.util.PieChartItem;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeV2 extends BaseFragment {

    private View rootView;

    private TextView userInfoTV;
    private TextView welcomeMsgTV;
    private TextView dateTimeTV;
    private ImageView profileImage;

    private String imageUrl;

    private ListView lv;
    private ArrayList<ChartItem> list;
    private ChartDataAdapter cda;

    private PieChart statusChart;
    private PieChart severityChart;
    private BarChart ovAllChart;
    private LineChart ocChart;
    private Typeface mTf;

    final List<PieEntry> statusEntries = new ArrayList<>();
    final List<PieEntry> severityEntries = new ArrayList<>();
    final List<Entry> openEntries = new ArrayList<>();
    final List<Entry> closeEntries = new ArrayList<>();

    final List<BarEntry> barOpenEntries = new ArrayList<>();
    final List<BarEntry> barCloseEntries = new ArrayList<>();
    final List<BarEntry> barCriticalEntries = new ArrayList<>();
    final List<BarEntry> barMajorEntries = new ArrayList<>();
    final List<BarEntry> barMinorEntries = new ArrayList<>();


    protected String[] mMonths = new String[]{
            "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", "Jan", "Feb"
    };

    List<String> months = new ArrayList<>();

    float groupSpace = 0.07f;
    float barSpace = 0.03f; // x5 DataSet
    float barWidth = 0.2f; // x5 DataSet

    String location;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        loadStatistics();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home_v2, container, false);

        initComponent();
//        loadStatistics();


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
        else if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_TS)
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

        lv = (ListView) rootView.findViewById(R.id.chartLV);

        statusChart = (PieChart) rootView.findViewById(R.id.statusChart);
        severityChart = (PieChart) rootView.findViewById(R.id.severityChart);
        ovAllChart = (BarChart) rootView.findViewById(R.id.ovAllChart);
        ocChart = (LineChart) rootView.findViewById(R.id.ocChart);
        mTf = Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf");
    }

    /** adapter that supports 3 different item types */
    private class ChartDataAdapter extends ArrayAdapter<ChartItem> {

        public ChartDataAdapter(Context context, List<ChartItem> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getItem(position).getView(position, convertView, getContext());
        }

        @Override
        public int getItemViewType(int position) {
            // return the views type
            return getItem(position).getItemType();
        }

        @Override
        public int getViewTypeCount() {
            return 3; // we have 3 different item-types
        }
    }

    private LineData generateDataLine(List<Entry> opens, List<Entry> closes) {
        LineDataSet d1 = new LineDataSet(opens, "Open Ticket");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setColor(ContextCompat.getColor(context, R.color.md_red_300));
        d1.setDrawValues(false);

        LineDataSet d2 = new LineDataSet(closes, "Close Ticket");
        d2.setLineWidth(2.5f);
        d2.setCircleRadius(4.5f);
        d2.setHighLightColor(Color.rgb(244, 117, 117));
        d2.setColor(ContextCompat.getColor(context, R.color.md_green_300));
        d2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setDrawValues(false);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(d1);
        sets.add(d2);

        LineData cd = new LineData(sets);
        return cd;
    }

    private BarData generateDataBar(List<BarEntry> openEntries, List<BarEntry> closeEntries, List<BarEntry> criticalEntries,
                                    List<BarEntry> majorEntries, List<BarEntry> minorEntries) {

        BarDataSet openDS, closeDS, criticalDS, majorDS, minorDS;

        openDS = new BarDataSet(openEntries, "Open Ticket");
        openDS.setColor(ContextCompat.getColor(TTSApplication.getContext(), R.color.md_red_300));
        closeDS = new BarDataSet(closeEntries, "Close Ticket");
        closeDS.setColor(ContextCompat.getColor(TTSApplication.getContext(), R.color.md_green_300));
        criticalDS = new BarDataSet(criticalEntries, "Critical");
        criticalDS.setColor(ContextCompat.getColor(TTSApplication.getContext(), R.color.red_900));
        majorDS = new BarDataSet(majorEntries, "Major");
        majorDS.setColor(ContextCompat.getColor(TTSApplication.getContext(), R.color.md_orange_500));
        minorDS = new BarDataSet(minorEntries, "Minor");
        minorDS.setColor(ContextCompat.getColor(TTSApplication.getContext(), R.color.md_yellow_600));

        BarData data = new BarData(openDS, closeDS, criticalDS, majorDS, minorDS);
        data.setValueFormatter(new LargeValueFormatter());
        return data;
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return
     */
    private PieData generateDataPie(List<PieEntry> entries, int[] color) {
        PieDataSet d = new PieDataSet(entries, "");

        // space between slices
        d.setSliceSpace(2f);
//        d.setColors(ColorTemplate.MATERIAL_COLORS);
        d.setColors(color);

        PieData cd = new PieData(d);
        return cd;
    }


    private void loadStat() {
        final int[] STATUS_COLOR = { ContextCompat.getColor(context, R.color.md_green_900), ContextCompat.getColor(context, R.color.md_red_900), ContextCompat.getColor(context, R.color.md_yellow_600) };
        final int[] SEVERITY_COLOR = { ContextCompat.getColor(context, R.color.md_red_900), ContextCompat.getColor(context, R.color.md_orange_600), ContextCompat.getColor(context, R.color.md_yellow_600) };

        final JSONObject items = new JSONObject();
        try {
            items.put("param_1", String.valueOf(preferences.getPreferencesInt(Constants.POSITION_ID)));
            if(preferences.getPreferencesInt(Constants.POSITION_ID) == 1 || preferences.getPreferencesInt(Constants.POSITION_ID) == 2)
                items.put("param_2", String.valueOf(preferences.getPreferencesString(Constants.STATION_ID)));
            else if(preferences.getPreferencesInt(Constants.POSITION_ID) == 3)
                items.put("param_2", String.valueOf(preferences.getPreferencesString(Constants.REGION_ID)));
            else if(preferences.getPreferencesInt(Constants.POSITION_ID) == 4)
                items.put("param_2", String.valueOf(preferences.getPreferencesString(Constants.DEPARTMENT_ID)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringRequest request = new StringRequest(Request.Method.POST, CommonsUtil.getAbsoluteUrl("all_statistics_data"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        try {
                            Logcat.i(res.toString());
                            JSONObject response = new JSONObject(res);
                            if(response.getString(Constants.RESPONSE_STATUS).equalsIgnoreCase(Constants.RESPONSE_SUCCESS)) {

                                statusEntries.clear();
                                severityEntries.clear();
                                months.clear();
                                openEntries.clear();
                                closeEntries.clear();
                                barOpenEntries.clear();
                                barCloseEntries.clear();
                                barCriticalEntries.clear();
                                barMajorEntries.clear();
                                barMinorEntries.clear();

                                JSONArray statusArray = response.getJSONArray("status_percentages");
                                for(int i=0; i<statusArray.length(); i++) {
                                    JSONObject object = statusArray.getJSONObject(i);
                                    statusEntries.add(new PieEntry(Float.parseFloat((String) object.get("total")), (String) object.get("ticket_status")));
                                }

                                JSONArray severityArray = response.getJSONArray("severity_percentages");
                                for(int i=0; i<severityArray.length(); i++) {
                                    JSONObject object = severityArray.getJSONObject(i);
                                    severityEntries.add(new PieEntry(Float.parseFloat((String) object.get("total")), (String) object.get("ticket_severity")));
                                }

                                JSONArray overviewOpenArray = response.getJSONArray("overview_open");
                                for(int i=0; i<overviewOpenArray.length(); i++) {
                                    JSONObject object = overviewOpenArray.getJSONObject(i);
                                    Logcat.i("OPEN::" + object.toString());
                                    months.add(object.getString("month"));
                                    openEntries.add(new Entry(i, object.getInt("value")));
                                }

                                JSONArray overviewCloseArray = response.getJSONArray("overview_close");
                                for(int i=0; i<overviewCloseArray.length(); i++) {
                                    JSONObject object = overviewCloseArray.getJSONObject(i);
                                    Logcat.i("CLOSE::" + object.toString());
                                    closeEntries.add(new Entry(i, object.getInt("value")));
                                }

                                JSONArray barOpenArray = response.getJSONArray("bar_open");
                                for(int i=0; i<barOpenArray.length(); i++) {
                                    JSONObject object = barOpenArray.getJSONObject(i);
                                    barOpenEntries.add(new BarEntry(i, object.getInt("value")));
                                }
                                JSONArray barCloseArray = response.getJSONArray("bar_close");
                                for(int i=0; i<barCloseArray.length(); i++) {
                                    JSONObject object = barCloseArray.getJSONObject(i);
                                    barCloseEntries.add(new BarEntry(i, object.getInt("value")));
                                }
                                JSONArray barCriticalArray = response.getJSONArray("bar_critical");
                                for(int i=0; i<barCriticalArray.length(); i++) {
                                    JSONObject object = barCriticalArray.getJSONObject(i);
                                    barCriticalEntries.add(new BarEntry(i, object.getInt("value")));
                                }
                                JSONArray barMajorArray = response.getJSONArray("bar_major");
                                for(int i=0; i<barMajorArray.length(); i++) {
                                    JSONObject object = barMajorArray.getJSONObject(i);
                                    barMajorEntries.add(new BarEntry(i, object.getInt("value")));
                                }
                                JSONArray barMinorArray = response.getJSONArray("bar_minor");
                                for(int i=0; i<barMinorArray.length(); i++) {
                                    JSONObject object = barMinorArray.getJSONObject(i);
                                    barMinorEntries.add(new BarEntry(i, object.getInt("value")));
                                }

                            }
                        } catch (JSONException e) {
                            Logcat.e("RESPONSE ERROR");
                            e.printStackTrace();
                        }

                        list = new ArrayList<>();
                        Logcat.i("STATUS_ENTRIES::" + severityEntries.size());
                        list.add(new PieChartItem(generateDataPie(statusEntries, STATUS_COLOR), generateCenterText("Trouble Ticket\nper\nStatus"), context));
                        list.add(new PieChartItem(generateDataPie(severityEntries, SEVERITY_COLOR), generateCenterText("Trouble Ticket\nper\nSeverity"), context));
                        list.add(new BarChartItem(generateDataBar(barOpenEntries, barCloseEntries, barCriticalEntries, barMajorEntries, barMinorEntries), getContext()));
                        list.add(new LineChartItem(generateDataLine(openEntries, closeEntries), getContext()));

                        cda = new ChartDataAdapter(getContext(), list);
                        lv.setAdapter(cda);

                        buildChart(statusChart, generateDataPie(statusEntries, STATUS_COLOR), generateCenterText("Trouble Ticket\nper\nStatus"));
                        buildChart(severityChart, generateDataPie(severityEntries, SEVERITY_COLOR), generateCenterText("Trouble Ticket\nper\nSeverity"));
                        buildBarChart(ovAllChart, generateDataBar(barOpenEntries, barCloseEntries, barCriticalEntries, barMajorEntries, barMinorEntries));
                        buildLineChart(ocChart, generateDataLine(openEntries, closeEntries));

                    }
        },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Logcat.e("Volley error: " + error.getMessage() + ", code: " + error.networkResponse);
                        Toast.makeText(getContext(), "Terjadi Kesalahan. Umumnya karena masalah jaringan.", Toast.LENGTH_SHORT).show();
                    }
        }

        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("param_1", String.valueOf(preferences.getPreferencesInt(Constants.POSITION_ID)));
                if(preferences.getPreferencesInt(Constants.POSITION_ID) == 1 || preferences.getPreferencesInt(Constants.POSITION_ID) == 2)
                    params.put("param_2", String.valueOf(preferences.getPreferencesString(Constants.STATION_ID)));
                else if(preferences.getPreferencesInt(Constants.POSITION_ID) == 3)
                    params.put("param_2", String.valueOf(preferences.getPreferencesString(Constants.REGION_ID)));
                else if(preferences.getPreferencesInt(Constants.POSITION_ID) == 4)
                    params.put("param_2", String.valueOf(preferences.getPreferencesString(Constants.DEPARTMENT_ID)));
                Logcat.e("params: " + params.toString());
                return params;
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

    /*private void loadStatistics() {
        final int[] STATUS_COLOR = { ContextCompat.getColor(context, R.color.md_green_900), ContextCompat.getColor(context, R.color.md_red_900), ContextCompat.getColor(context, R.color.md_yellow_600) };
        final int[] SEVERITY_COLOR = { ContextCompat.getColor(context, R.color.md_red_900), ContextCompat.getColor(context, R.color.md_orange_600), ContextCompat.getColor(context, R.color.md_yellow_600) };

        ApiClient.setApplicationContext(context);
        RequestParams params = new RequestParams();
        params.put("param_1", preferences.getPreferencesInt(Constants.POSITION_ID));
        if(preferences.getPreferencesInt(Constants.POSITION_ID) == 1 || preferences.getPreferencesInt(Constants.POSITION_ID) == 2)
            params.put("param_2", preferences.getPreferencesInt(Constants.STATION_ID));
        else if(preferences.getPreferencesInt(Constants.POSITION_ID) == 3)
            params.put("param_2", preferences.getPreferencesInt(Constants.REGION_ID));
        else if(preferences.getPreferencesInt(Constants.POSITION_ID) == 4)
            params.put("param_2", preferences.getPreferencesInt(Constants.DEPARTMENT_ID));
        ApiClient.post("all_statistics_data", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                Logcat.i("START statistics_data");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Logcat.i("RESPONSE::" + response.toString());
                try {
                    Logcat.i(response.toString());
                    if(response.getString(Constants.RESPONSE_STATUS).equalsIgnoreCase(Constants.RESPONSE_SUCCESS)) {
                        JSONArray statusArray = response.getJSONArray("status_percentages");
                        for(int i=0; i<statusArray.length(); i++) {
                            JSONObject object = statusArray.getJSONObject(i);
                            statusEntries.add(new PieEntry(Float.parseFloat((String) object.get("total")), (String) object.get("ticket_status")));
                        }

                        JSONArray severityArray = response.getJSONArray("severity_percentages");
                        for(int i=0; i<severityArray.length(); i++) {
                            JSONObject object = severityArray.getJSONObject(i);
                            severityEntries.add(new PieEntry(Float.parseFloat((String) object.get("total")), (String) object.get("ticket_severity")));
                        }

                        JSONArray overviewOpenArray = response.getJSONArray("overview_open");
                        for(int i=0; i<overviewOpenArray.length(); i++) {
                            JSONObject object = overviewOpenArray.getJSONObject(i);
                            Logcat.i("OPEN::" + object.toString());
                            months.add(object.getString("month"));
                            openEntries.add(new Entry(i, object.getInt("value")));
                        }

                        JSONArray overviewCloseArray = response.getJSONArray("overview_close");
                        for(int i=0; i<overviewCloseArray.length(); i++) {
                            JSONObject object = overviewCloseArray.getJSONObject(i);
                            Logcat.i("CLOSE::" + object.toString());
                            closeEntries.add(new Entry(i, object.getInt("value")));
                        }

                        JSONArray barOpenArray = response.getJSONArray("bar_open");
                        for(int i=0; i<barOpenArray.length(); i++) {
                            JSONObject object = barOpenArray.getJSONObject(i);
                            barOpenEntries.add(new BarEntry(i, object.getInt("value")));
                        }
                        JSONArray barCloseArray = response.getJSONArray("bar_close");
                        for(int i=0; i<barCloseArray.length(); i++) {
                            JSONObject object = barCloseArray.getJSONObject(i);
                            barCloseEntries.add(new BarEntry(i, object.getInt("value")));
                        }
                        JSONArray barCriticalArray = response.getJSONArray("bar_critical");
                        for(int i=0; i<barCriticalArray.length(); i++) {
                            JSONObject object = barCriticalArray.getJSONObject(i);
                            barCriticalEntries.add(new BarEntry(i, object.getInt("value")));
                        }
                        JSONArray barMajorArray = response.getJSONArray("bar_major");
                        for(int i=0; i<barMajorArray.length(); i++) {
                            JSONObject object = barMajorArray.getJSONObject(i);
                            barMajorEntries.add(new BarEntry(i, object.getInt("value")));
                        }
                        JSONArray barMinorArray = response.getJSONArray("bar_minor");
                        for(int i=0; i<barMinorArray.length(); i++) {
                            JSONObject object = barMinorArray.getJSONObject(i);
                            barMinorEntries.add(new BarEntry(i, object.getInt("value")));
                        }

                    }
                } catch (JSONException e) {
                    Logcat.e("RESPONSE ERROR");
                    e.printStackTrace();
                }

                list = new ArrayList<>();
                Logcat.i("STATUS_ENTRIES::" + severityEntries.size());
                list.add(new PieChartItem(generateDataPie(statusEntries, STATUS_COLOR), generateCenterText("Trouble Ticket\nper\nStatus"), context));
                list.add(new PieChartItem(generateDataPie(severityEntries, SEVERITY_COLOR), generateCenterText("Trouble Ticket\nper\nSeverity"), context));
                list.add(new BarChartItem(generateDataBar(barOpenEntries, barCloseEntries, barCriticalEntries, barMajorEntries, barMinorEntries), getContext()));
                list.add(new LineChartItem(generateDataLine(openEntries, closeEntries), getContext()));

                cda = new ChartDataAdapter(getContext(), list);
                lv.setAdapter(cda);

                buildChart(statusChart, generateDataPie(statusEntries, STATUS_COLOR), generateCenterText("Trouble Ticket\nper\nStatus"));
                buildChart(severityChart, generateDataPie(severityEntries, SEVERITY_COLOR), generateCenterText("Trouble Ticket\nper\nSeverity"));
                buildBarChart(ovAllChart, generateDataBar(barOpenEntries, barCloseEntries, barCriticalEntries, barMajorEntries, barMinorEntries));
                buildLineChart(ocChart, generateDataLine(openEntries, closeEntries));

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Logcat.i("FAILURE statistics_data");
                throwable.printStackTrace();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Logcat.i("FINISH statistics_data");

            }
        });
    }*/

    private SpannableString generateCenterText(String str) {
        SpannableString s = new SpannableString(str);
        s.setSpan(new RelativeSizeSpan(1.6f), 0, 14, 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.VORDIPLOM_COLORS[0]), 0, 14, 0);
        s.setSpan(new RelativeSizeSpan(.9f), 14, 25, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, 25, 0);
        s.setSpan(new RelativeSizeSpan(1.4f), 25, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 25, s.length(), 0);
        return s;
    }

    private void buildChart(PieChart chart, ChartData<?> mChartData, SpannableString mCenterText) {
        chart.getDescription().setEnabled(false);
        chart.setHoleRadius(52f);
        chart.setTransparentCircleRadius(57f);
        chart.setCenterText(mCenterText);
        chart.setCenterTextTypeface(mTf);
        chart.setCenterTextSize(9f);

//        mChartData.setValueFormatter(new PercentFormatter());
        mChartData.setValueTypeface(mTf);
        mChartData.setValueTextSize(11f);
        mChartData.setValueTextColor(Color.BLACK);

        // set data
        chart.setData((PieData) mChartData);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // do not forget to refresh the chart
        // chart.invalidate();
        chart.animateY(900);
        chart.invalidate();
    }

    private void buildBarChart(BarChart chart, ChartData<?> mChartData) {
        // apply styling
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);

//        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTf);
        xAxis.setDrawGridLines(false);
        /*xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mMonths[(int) value % mMonths.length];
            }
        });*/
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return months.get(((int) value % months.size()));
//                return months.get((int)value);
            }
        });

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(5, false);
        leftAxis.setSpaceTop(20f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setTypeface(mTf);
        rightAxis.setLabelCount(5, false);
        rightAxis.setSpaceTop(20f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        mChartData.setValueTypeface(mTf);


        // set data
        chart.setData((BarData) mChartData);
//        chart.setFitBars(true);

        // do not forget to refresh the chart
//        chart.invalidate();
        chart.animateY(700);

        chart.setDoubleTapToZoomEnabled(false);
        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);

        // specify the width each bar should have
        chart.getBarData().setBarWidth(0.2f);
        chart.groupBars(0, groupSpace, barSpace);
        chart.invalidate();
    }

    private void buildLineChart(LineChart chart, ChartData<?> mChartData) {
        // apply styling
        // chart.setValueTypeface(mTf);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);

        /*XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTf);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);*/

//        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTf);
        xAxis.setDrawGridLines(false);
        /*xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mMonths[(int) value % mMonths.length];
            }
        });*/
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return months.get((int) value % months.size());
//                return months.get((int) value);
            }
        });

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(5, false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setTypeface(mTf);
        rightAxis.setLabelCount(5, false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        // set data
        chart.setData((LineData) mChartData);

        // do not forget to refresh the chart
        // chart.invalidate();
        chart.animateX(750);

        chart.setDoubleTapToZoomEnabled(false);
        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);

    }

    @Override
    public void onResume() {
        super.onResume();
//        loadStatistics();
        loadStat();
    }
}
