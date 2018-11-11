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

public class MyTicketV2 extends BaseFragment {

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
        rootView = inflater.inflate(R.layout.fragment_my_ticket_v2, container, false);
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

        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(new TicketListFragment().newInstance(Constants.MY_ACTIVE_TICKETS), Constants.MY_ACTIVE_TICKETS);
        viewPagerAdapter.addFragment(new TicketListFragment().newInstance(Constants.MY_OTHER_TICKETS), Constants.MY_OTHER_TICKETS);

        ticketViewer.setAdapter(viewPagerAdapter);
        ticketTabLayout.setupWithViewPager(ticketViewer);
        ticketTabLayout.setTabTextColors(getResources().getColor(R.color.md_grey_300), getResources().getColor(R.color.md_white_1000));

    }

}
