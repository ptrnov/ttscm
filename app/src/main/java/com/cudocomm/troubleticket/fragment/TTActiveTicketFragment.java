package com.cudocomm.troubleticket.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.adapter.TopTenActiveAdapter;
import com.cudocomm.troubleticket.model.TopTenActive;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TTActiveTicketFragment extends BaseFragment {

    private View rootView;
    private RecyclerView topTenActiveRV;

    private TopTenActiveAdapter topTenActiveAdapter;
    private List<TopTenActive> topTenActives = new ArrayList<>();

    BarChart ttActiveChart;
    private Typeface mTf;
    final List<BarEntry> entries = new ArrayList<>();
    final List<String> headers = new ArrayList<>();

//    private static final int REQUEST_CODE = 200;

    public static TTActiveTicketFragment newInstance(List<TopTenActive> topTenActives) {
        TTActiveTicketFragment fragment = new TTActiveTicketFragment();
        Bundle args = new Bundle();
        args.putSerializable("list", (Serializable) topTenActives);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            topTenActives = (List<TopTenActive>) getArguments().getSerializable("list");
        }
    }

    public TTActiveTicketFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tt_active_ticket, container, false);

        initComponent();
        updateComponent();

        return rootView;
    }

    private void initComponent() {
        topTenActiveRV = (RecyclerView) rootView.findViewById(R.id.topTenActiveRV);

        ttActiveChart = (BarChart) rootView.findViewById(R.id.ttActiveChart);
        mTf = Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf");
    }

    private void updateComponent() {

                if(topTenActiveAdapter == null) {
                    topTenActiveAdapter = new TopTenActiveAdapter(topTenActives);
                } else {
                    topTenActiveAdapter.swap(topTenActives);
                    topTenActiveAdapter.notifyDataSetChanged();
                }
        topTenActiveRV.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        topTenActiveRV.setLayoutManager(linearLayoutManager);

        topTenActiveRV.setAdapter(topTenActiveAdapter);

        for(int i=0; i<topTenActives.size(); i++) {
            if(!headers.contains(topTenActives.get(i).getStationName()))
                headers.add(topTenActives.get(i).getStationName());
            entries.add(new BarEntry(i, Float.parseFloat(topTenActives.get(i).getTotal())));
        }

        buildBarChart(ttActiveChart, generateDataBar(entries), headers);
        ttActiveChart.setVisibility(View.VISIBLE);

        ttActiveChart.setDoubleTapToZoomEnabled(false);
        ttActiveChart.setPinchZoom(false);
        ttActiveChart.setScaleEnabled(false);

    }

    private void buildBarChart(BarChart chart, ChartData<?> mChartData, final List<String> header) {
        // apply styling
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);

        chart.getLegend().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(-90);
        xAxis.setLabelCount(header.size(), false);
        xAxis.setTypeface(mTf);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return header.get((int) value % header.size());
            }
        });

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(10, false);
        leftAxis.setSpaceTop(20f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setTypeface(mTf);
        rightAxis.setLabelCount(10, false);
        rightAxis.setSpaceTop(20f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        mChartData.setValueTypeface(mTf);


        // set data
        chart.setData((BarData) mChartData);
        chart.getBarData().setBarWidth(0.4f);
//        chart.setFitBars(true);

        // do not forget to refresh the chart
//        chart.invalidate();
        chart.animateY(700);
        chart.invalidate();
    }

    private BarData generateDataBar(List<BarEntry> entries) {
        BarDataSet dataSet;
        dataSet = new BarDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData data = new BarData(dataSet);
        data.setValueFormatter(new LargeValueFormatter());
        return data;
    }

}
