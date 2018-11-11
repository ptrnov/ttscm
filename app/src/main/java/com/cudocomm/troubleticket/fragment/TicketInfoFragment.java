package com.cudocomm.troubleticket.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.database.dao.SeverityDAO;
import com.cudocomm.troubleticket.database.model.SeverityModel;
import com.cudocomm.troubleticket.model.Ticket;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;

import java.sql.SQLException;

//public class TicketInfoFragment extends BaseFragment {
public class TicketInfoFragment extends Fragment {

    private View rootView;
    private TextView ticketNoTV;
    private TextView ticketTypeTV;
    private TextView ticketStationTV;
    private TextView ticketSeverityTV;
    private TextView ticketSuspectTV1;
    private TextView ticketSuspectTV2;
    private TextView ticketSuspectTV3;
    private TextView ticketCreatorTV;
    private TextView ticketRemarksTV;
    private TextView ticketStatusTV;
    private TextView ticketAgingTV;
    private TextView ticketSLATV;

    private Ticket selectedTicket;

    private SeverityModel severityModel;

    public static TicketInfoFragment newInstance(Ticket ticket) {
        TicketInfoFragment fragment = new TicketInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.SELECTED_TICKET, ticket);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedTicket = (Ticket) getArguments().getSerializable(Constants.SELECTED_TICKET);
        }
    }

    public TicketInfoFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ticket_info, container, false);
        initComponent();
        return rootView;
    }

    private void initComponent() {
        ticketNoTV = (TextView) rootView.findViewById(R.id.ticketNoTV);
        ticketTypeTV = (TextView) rootView.findViewById(R.id.ticketTypeTV);
        ticketStationTV = (TextView) rootView.findViewById(R.id.ticketStationTV);
        ticketSeverityTV = (TextView) rootView.findViewById(R.id.ticketSeverityTV);
        ticketSuspectTV1 = (TextView) rootView.findViewById(R.id.ticketSuspectTV1);
        ticketSuspectTV2 = (TextView) rootView.findViewById(R.id.ticketSuspectTV2);
        ticketSuspectTV3 = (TextView) rootView.findViewById(R.id.ticketSuspectTV3);
        ticketCreatorTV = (TextView) rootView.findViewById(R.id.ticketCreatorTV);
        ticketRemarksTV = (TextView) rootView.findViewById(R.id.ticketRemarksTV);
        ticketStatusTV = (TextView) rootView.findViewById(R.id.ticketStatusTV);
        ticketAgingTV = (TextView) rootView.findViewById(R.id.ticketAgingTV);
        ticketSLATV = (TextView) rootView.findViewById(R.id.ticketSLATV);

        ticketNoTV.setText(selectedTicket.getTicketNo());
        ticketTypeTV.setText(CommonsUtil.ticketTypeToString(selectedTicket.getTicketType()));
        ticketStationTV.setText(selectedTicket.getStationName());
        ticketSeverityTV.setText(CommonsUtil.severityToString(selectedTicket.getTicketSeverity()));
        ticketSuspectTV1.setText(selectedTicket.getSuspect1Name());
        ticketSuspectTV2.setText(selectedTicket.getSuspect2Name());
        ticketSuspectTV3.setText(selectedTicket.getSuspect3Name());
        ticketCreatorTV.setText(selectedTicket.getUserName());
        ticketRemarksTV.setText(selectedTicket.getTicketRemarks());
        ticketStatusTV.setText(CommonsUtil.statusToString(selectedTicket.getTicketStatus()));
        ticketAgingTV.setText(CommonsUtil.countAging(selectedTicket.getTicketDate()));

        try {
            severityModel = SeverityDAO.read(selectedTicket.getTicketSeverity());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ticketSLATV.setText(CommonsUtil.secondToTime(new Integer(severityModel.getSeverityTime())));

    }

}
