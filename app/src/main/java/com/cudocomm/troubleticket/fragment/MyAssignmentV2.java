package com.cudocomm.troubleticket.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.adapter.ViewPagerAdapter;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;

import java.util.Date;

public class MyAssignmentV2 extends BaseFragment {

    private View rootView;
    private TextView titleTV;
    private TextView statusTV;

    TabLayout ticketTabLayout;
    ViewPager ticketViewer;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_assignment_v2, container, false);
        initComponent();
        updateComponent();
        return rootView;
    }

    private void initComponent() {
        titleTV = (TextView) rootView.findViewById(R.id.positionTV);
        statusTV = (TextView) rootView.findViewById(R.id.dateTV);
        ticketTabLayout = (TabLayout) rootView.findViewById(R.id.ticket_tab_layout);
        ticketViewer = (ViewPager) rootView.findViewById(R.id.ticket_viewer);
    }

    private void updateComponent() {
        titleTV.setText(getResources().getString(R.string.label_header_position_national));

        statusTV.setText(getResources().getString(R.string.label_header_date, CommonsUtil.dateToString(new Date())));
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(new AssignmentListFragment().newInstance(Constants.MY_ACTIVE_TICKETS), Constants.MY_ACTIVE_TICKETS);
        viewPagerAdapter.addFragment(new AssignmentListFragment().newInstance(Constants.MY_OTHER_TICKETS), Constants.MY_OTHER_TICKETS);

        ticketViewer.setAdapter(viewPagerAdapter);
        ticketTabLayout.setupWithViewPager(ticketViewer);
        ticketTabLayout.setTabTextColors(getResources().getColor(R.color.md_grey_300), getResources().getColor(R.color.md_white_1000));

    }

}
