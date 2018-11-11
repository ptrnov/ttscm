package com.cudocomm.troubleticket.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.adapter.TicketAdapter;
import com.cudocomm.troubleticket.model.Ticket;
import com.cudocomm.troubleticket.util.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyTicketList extends BaseFragment {

    private View rootView;
    private RecyclerView ticketListRV;

    private TicketAdapter ticketAdapter;
    private List<Ticket> tickets = new ArrayList<>();

//    private static final int REQUEST_CODE = 200;

    public static MyTicketList newInstance(List<Ticket> tickets) {
        MyTicketList fragment = new MyTicketList();
        Bundle args = new Bundle();
        args.putSerializable("list", (Serializable) tickets);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tickets = (List<Ticket>) getArguments().getSerializable("list");
        }
    }

    public MyTicketList() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_ticket_list, container, false);

        initComponent();
        updateComponent();

        return rootView;
    }

    private void initComponent() {
        ticketListRV = (RecyclerView) rootView.findViewById(R.id.ticketListRV);
    }

    private void updateComponent() {
        /*ticketAdapter = new TicketAdapter(tickets, new TicketAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Ticket ticket) {
                *//*Intent intent = new Intent(context, TicketActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.SELECTED_TICKET, ticket);
                intent.putExtras(bundle);
                getActivity().startActivityForResult(intent, Constants.REQUEST_CODE);*//*

                Fragment f = new TicketFragment();
                String page = Constants.TICKET_INFO_PAGE;
                Boolean flag = Boolean.valueOf(false);
                Bundle args = new Bundle();
                args.putString(Constants.PARAM_SECTION, Constants.MY_TASK_PAGE);
                args.putSerializable(Constants.SELECTED_TICKET, ticket);
//                args.putInt(Constants.SELECTED_TICKET_POSITION, tickets.indexOf(ticket));
                f.setArguments(args);
                preferences.savePreferences(Constants.ACTIVE_PAGE, page);
                mListener.onMenuSelected(page, f, flag);
            }
        });*/
                if(ticketAdapter == null) {
                    ticketAdapter = new TicketAdapter(tickets, new TicketAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Ticket ticket) {
                /*Intent intent = new Intent(context, TicketActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.SELECTED_TICKET, ticket);
                intent.putExtras(bundle);
                getActivity().startActivityForResult(intent, Constants.REQUEST_CODE);*/

                            Fragment f = new TicketFragment();
                            String page = Constants.TICKET_INFO_PAGE;
                            Boolean flag = Boolean.FALSE;
                            Bundle args = new Bundle();
                            args.putString(Constants.PARAM_SECTION, Constants.MY_TASK_PAGE);
                            args.putSerializable(Constants.SELECTED_TICKET, ticket);
//                args.putInt(Constants.SELECTED_TICKET_POSITION, tickets.indexOf(ticket));
                            f.setArguments(args);
                            preferences.savePreferences(Constants.ACTIVE_PAGE, page);
                            mListener.onMenuSelected(page, f, flag);
                        }
                    });
                } else {
                    ticketAdapter.swap(tickets);
                    ticketAdapter.notifyDataSetChanged();
                }
        ticketListRV.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ticketListRV.setLayoutManager(linearLayoutManager);

        ticketListRV.setAdapter(ticketAdapter);
//        ticketListRV.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                Ticket ticket = (Ticket) data.getExtras().get(Constants.SELECTED_TICKET);
                ((TicketAdapter) ticketListRV.getAdapter()).getmDataset().remove(ticket);
                ticketListRV.getAdapter().notifyDataSetChanged();
            }
        }

    }
}
