package com.cudocomm.troubleticket.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.adapter.TicketLogAdapter;
import com.cudocomm.troubleticket.model.Ticket;
import com.cudocomm.troubleticket.model.TicketLog;
import com.cudocomm.troubleticket.util.Constants;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

//public class TicketHistoryFragment extends BaseFragment {
public class TicketHistoryFragment extends Fragment {

    private View rootView;

    private RecyclerView ticketHistoryRV;

    private Ticket selectedTicket;
    private List<TicketLog> ticketLogs;
    private TicketLogAdapter adapter;

    public static TicketHistoryFragment newInstance(Map<String, Object> maps) {
        TicketHistoryFragment fragment = new TicketHistoryFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.SELECTED_TICKET, (Serializable) maps.get(Constants.SELECTED_TICKET));
        args.putSerializable(Constants.TICKET_LOGS, (Serializable) maps.get(Constants.TICKET_LOGS));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedTicket = (Ticket) getArguments().getSerializable(Constants.SELECTED_TICKET);
            ticketLogs = (List<TicketLog>) getArguments().getSerializable(Constants.TICKET_LOGS);
        }
    }

    public TicketHistoryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ticket_history, container, false);

        ticketHistoryRV = (RecyclerView) rootView.findViewById(R.id.ticketHistoryRV);
        if(ticketLogs != null && !ticketLogs.isEmpty())
            updateComponent();
        return rootView;
    }

    private void updateComponent() {
        ticketHistoryRV.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        ticketHistoryRV.setLayoutManager(linearLayoutManager);
        adapter = new TicketLogAdapter(ticketLogs, new TicketLogAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TicketLog ticket) {

            }
        });
        ticketHistoryRV.setAdapter(adapter);
    }

}
