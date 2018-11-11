package com.cudocomm.troubleticket.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.TTSApplication;
import com.cudocomm.troubleticket.util.BarChartItem;
import com.cudocomm.troubleticket.util.ChartItem;
import com.cudocomm.troubleticket.util.LineChartItem;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ACStatisticFragment extends BaseFragment {
    
//    private View rootView;

    private ListView lv;
//    private ArrayList<ChartItem> list;
//    private ChartDataAdapter cda;

    private PieChart statusChart;
    private PieChart severityChart;
    private BarChart ovAllChart;
    private LineChart ocChart;
    private Typeface mTf;

    List<PieEntry> statusEntries, severityEntries;
    List<Entry> openEntries, closeEntries;

    List<BarEntry> barOpenEntries, barCloseEntries, barCriticalEntries, barMajorEntries, barMinorEntries;


    protected String[] mMonths = new String[]{
            "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", "Jan", "Feb"
    };

    float groupSpace = 0.07f;
    float barSpace = 0.03f; // x5 DataSet
    float barWidth = 0.2f; // x5 DataSet

    List<String> months;


    public static ACStatisticFragment newInstance(List<PieEntry> statusEntries, List<PieEntry> severityEntries,
                                                  List<Entry> openEntries, List<Entry> closeEntries,
                                                  List<BarEntry> barOpenEntries, List<BarEntry> barCloseEntries,
                                                  List<BarEntry> barCriticalEntries, List<BarEntry> barMajorEntries, List<BarEntry> barMinorEntries, List<String> months) {
        ACStatisticFragment fragment = new ACStatisticFragment();
        Bundle args = new Bundle();
        args.putSerializable("statusEntries", (Serializable) statusEntries);
        args.putSerializable("severityEntries", (Serializable) severityEntries);
        args.putSerializable("openEntries", (Serializable) openEntries);
        args.putSerializable("closeEntries", (Serializable) closeEntries);
        args.putSerializable("barOpenEntries", (Serializable) barOpenEntries);
        args.putSerializable("barCloseEntries", (Serializable) barCloseEntries);
        args.putSerializable("barCriticalEntries", (Serializable) barCriticalEntries);
        args.putSerializable("barMajorEntries", (Serializable) barMajorEntries);
        args.putSerializable("barMinorEntries", (Serializable) barMinorEntries);
        args.putSerializable("months", (Serializable) months);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            statusEntries = (List<PieEntry>) getArguments().getSerializable("statusEntries");
            severityEntries = (List<PieEntry>) getArguments().getSerializable("severityEntries");
            openEntries = (List<Entry>) getArguments().getSerializable("openEntries");
            closeEntries = (List<Entry>) getArguments().getSerializable("closeEntries");
            barOpenEntries = (List<BarEntry>) getArguments().getSerializable("barOpenEntries");
            barCloseEntries = (List<BarEntry>) getArguments().getSerializable("barCloseEntries");
            barCriticalEntries = (List<BarEntry>) getArguments().getSerializable("barCriticalEntries");
            barMajorEntries = (List<BarEntry>) getArguments().getSerializable("barMajorEntries");
            barMinorEntries = (List<BarEntry>) getArguments().getSerializable("barMinorEntries");
            months = (List<String>) getArguments().getSerializable("months");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_statistic, container, false);

        lv = (ListView) rootView.findViewById(R.id.chartLV);

        statusChart = (PieChart) rootView.findViewById(R.id.statusChart);
        severityChart = (PieChart) rootView.findViewById(R.id.severityChart);
        ovAllChart = (BarChart) rootView.findViewById(R.id.ovAllChart);
        ocChart = (LineChart) rootView.findViewById(R.id.ocChart);
        mTf = Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf");
        loadStatistic(statusEntries, severityEntries, openEntries, closeEntries, barOpenEntries, barCloseEntries, barCriticalEntries, barMajorEntries, barMinorEntries);
        
        return rootView;
    }

    /** adapter that supports 3 different item types */
    private class ChartDataAdapter extends ArrayAdapter<ChartItem> {

        public ChartDataAdapter(Context context, List<ChartItem> objects) {
            super(context, 0, objects);
        }

        @Override
        @NonNull
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


    private void loadStatistic(List<PieEntry> statusEntries, List<PieEntry> severityEntries,
                               List<Entry> openEntries, List<Entry> closeEntries,
                               List<BarEntry> barOpenEntries, List<BarEntry> barCloseEntries,
                               List<BarEntry> barCriticalEntries, List<BarEntry> barMajorEntries, List<BarEntry> barMinorEntries) {
        final int[] STATUS_COLOR = { ContextCompat.getColor(context, R.color.md_green_900), ContextCompat.getColor(context, R.color.md_red_900), ContextCompat.getColor(context, R.color.md_yellow_600) };
        final int[] SEVERITY_COLOR = { ContextCompat.getColor(context, R.color.md_red_900), ContextCompat.getColor(context, R.color.md_orange_600), ContextCompat.getColor(context, R.color.md_yellow_600) };

        ArrayList<ChartItem> list = new ArrayList<>();
        list.add(new PieChartItem(generateDataPie(statusEntries, STATUS_COLOR), generateCenterText("Trouble Ticket\nper\nStatus"), context));
        list.add(new PieChartItem(generateDataPie(severityEntries, SEVERITY_COLOR), generateCenterText("Trouble Ticket\nper\nSeverity"), context));
        list.add(new BarChartItem(generateDataBar(barOpenEntries, barCloseEntries, barCriticalEntries, barMajorEntries, barMinorEntries), getContext()));
        list.add(new LineChartItem(generateDataLine(openEntries, closeEntries), getContext()));

        ChartDataAdapter cda = new ChartDataAdapter(getContext(), list);
        lv.setAdapter(cda);

        buildChart(statusChart, generateDataPie(statusEntries, STATUS_COLOR), generateCenterText("Trouble Ticket\nper\nStatus"));
        buildChart(severityChart, generateDataPie(severityEntries, SEVERITY_COLOR), generateCenterText("Trouble Ticket\nper\nSeverity"));
        buildBarChart(ovAllChart, generateDataBar(barOpenEntries, barCloseEntries, barCriticalEntries, barMajorEntries, barMinorEntries));
        buildLineChart(ocChart, generateDataLine(openEntries, closeEntries));

    }

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
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
//                return mMonths[(int) value % mMonths.length];
                return months.get((int) value % months.size());
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
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
//                return mMonths[(int) value % mMonths.length];
                return months.get((int) value % months.size());
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

}
