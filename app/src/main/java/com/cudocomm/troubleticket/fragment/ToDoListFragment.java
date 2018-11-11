package com.cudocomm.troubleticket.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.activity.TicketActivity;
import com.cudocomm.troubleticket.adapter.TicketAdapter;
import com.cudocomm.troubleticket.model.Ticket;
import com.cudocomm.troubleticket.util.ApiClient;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;

public class ToDoListFragment extends BaseFragment {

    private View rootView;

    private RecyclerView ticketListRV;
    List<Ticket> tickets;
    private SwipeRefreshLayout ticketSwiper;
    private TicketAdapter ticketAdapter;

    public static ToDoListFragment myTicket;

    public View emptyListLayout;

    private EditText searchET;

    String searchString = "";

    public ToDoListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_to_do_list, container, false);

        initComponent();
        updateComponent();

        myTicket = this;

        return rootView;
    }

    private void initComponent() {
        tickets = new ArrayList<>();
        searchET = (EditText) rootView.findViewById(R.id.searchET);
        emptyListLayout = rootView.findViewById(R.id.emptyListLayout);
        ticketSwiper = (SwipeRefreshLayout) rootView.findViewById(R.id.ticketSwiper);
        ticketListRV = (RecyclerView) rootView.findViewById(R.id.ticketListRV);
        ticketSwiper.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void updateComponent() {

        ticketSwiper.post(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        });

        ticketSwiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        searchET.setText("");
        searchET.addTextChangedListener(searchListener());

    }

    public TextWatcher searchListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                searchString = searchET.getText().toString();
                new ToDoListTask().execute();
            }
        };
    }

    public void refresh() {
        new ToDoListTask().execute();

    }

    private void processList() {
        if(tickets.size() > 0) {
            if(ticketAdapter == null) {
                ticketAdapter = new TicketAdapter(tickets, new TicketAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Ticket ticket) {
                        Intent intent = new Intent(context, TicketActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants.SELECTED_TICKET, ticket);
                        intent.putExtras(bundle);
                        getActivity().startActivityForResult(intent, Constants.REQUEST_CODE);

                    }
                });
                ticketAdapter.setHasStableIds(true);
                ticketListRV.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                ticketListRV.setLayoutManager(linearLayoutManager);


                ticketListRV.setAdapter(ticketAdapter);
            } else {
                ticketAdapter.swap(tickets);
            }
            emptyListLayout.setVisibility(View.GONE);
            ticketListRV.setVisibility(View.VISIBLE);
        } else {
            emptyListLayout.setVisibility(View.VISIBLE);
            ticketListRV.setVisibility(View.GONE);
        }


    }

    class ToDoListTask extends AsyncTask<Void, Void, Void> {

        String result;
        JSONObject object;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ticketSwiper.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(
                        CommonsUtil.getAbsoluteUrl("to_do_list"),
                        new FormBody.Builder().add("search", searchString).build());
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
                    Type type = new TypeToken<List<Ticket>>(){}.getType();
                    tickets = gson.fromJson(object.getString("data"), type);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            processList();
            ticketSwiper.setRefreshing(false);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        new ToDoListTask().execute();
    }
}
