package com.cudocomm.troubleticket.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.activity.ApprovalTicketActivity;
import com.cudocomm.troubleticket.adapter.CloseTicketAdapter;
import com.cudocomm.troubleticket.model.CloseTicket;
import com.cudocomm.troubleticket.model.CounterModel;
import com.cudocomm.troubleticket.util.ApiClient;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Logcat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import okhttp3.FormBody;

public class MyApproval extends BaseFragment {

    /*private View rootView;
    private TextView titleTV;
    private TextView statusTV;

    private CloseTicketAdapter closeTicketAdapter;
    private RecyclerView approvalTicketListRV;
    private SwipeRefreshLayout ticketSwiper;

    private List<CloseTicket> closeTickets;

    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gsona = this.gsonBuilder.create();

    private View emptyListLayout;*/
    public View rootView;
    public TextView titleTV;
    public TextView statusTV;

    public CloseTicketAdapter closeTicketAdapter;
    public RecyclerView approvalTicketListRV;
    public SwipeRefreshLayout ticketSwiper;

    public List<CloseTicket> closeTickets;
    private CounterModel myTaskCounter;

    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gsona = this.gsonBuilder.create();

    public View emptyListLayout;

    public MyApproval() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_approval, container, false);

        initComponent();
        updateComponent();

        return rootView;
    }

    private void initComponent() {
//        closeTickets = new ArrayList<>();
        emptyListLayout = rootView.findViewById(R.id.emptyListLayout);

        ticketSwiper = (SwipeRefreshLayout) rootView.findViewById(R.id.ticketSwiper);
        titleTV = (TextView) rootView.findViewById(R.id.positionTV);
        statusTV = (TextView) rootView.findViewById(R.id.dateTV);
        approvalTicketListRV = (RecyclerView) rootView.findViewById(R.id.approvalTicketListRV);

        ticketSwiper.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void updateComponent() {
        titleTV.setText(getResources().getString(R.string.label_header_position, preferences.getPreferencesString(Constants.STATION_NAME)));
        statusTV.setText(getResources().getString(R.string.label_header_date, CommonsUtil.dateToString(new Date())));

        ticketSwiper.post(new Runnable() {
            @Override
            public void run() {
                loadApprovals();
            }
        });

        ticketSwiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadApprovals();
            }
        });

    }

    public void loadApprovals() {
        ApiClient.setApplicationContext(context);
        RequestParams params = new RequestParams();
        Logcat.i("STATION_ID:" + preferences.getPreferencesString(Constants.STATION_ID));
        params.put(Constants.STATION_ID, preferences.getPreferencesString(Constants.STATION_ID));
        ApiClient.post("getrequestapproval2", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                ticketSwiper.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if(response.getString(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
//                        JSONObject object = response.getJSONObject("needapproval");
                        /*Logcat.i("NEED::" + response.getJSONObject("needapproval") );
                        preferences.savePreferences(Constants.COUNTER_NEED_APPROVAL,
                                getResources().getString(R.string.widget_need_approval_counter,
                                        object.get(Constants.CRITICAL), object.get(Constants.MAJOR),
                                        object.get(Constants.MINOR)));*/
                        JSONArray jsonArray = response.getJSONArray("new_tickets");

                        Type listType = new TypeToken<List<CloseTicket>>() {}.getType();
                        closeTickets = gsona.fromJson(jsonArray.toString(), listType);

                        /*Type counterType = new TypeToken<CounterModel>(){}.getType();
                        myTaskCounter = gson.fromJson(object.getString("mytaskcounter"), counterType);
                        if(myTaskCounter != null)
                            preferences.savePreferences(Constants.COUNTER_MY_TASK, getResources().getString(R.string.widget_need_approval_counter, myTaskCounter.getCritical(), myTaskCounter.getMajor(), myTaskCounter.getMinor()));
                        else
                            preferences.savePreferences(Constants.COUNTER_MY_TASK, getResources().getString(R.string.widget_need_approval_counter_null));
*/
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(closeTickets != null && closeTickets.size() > 0) {
                    if(approvalTicketListRV.getAdapter()==null) {
                        closeTicketAdapter = new CloseTicketAdapter(closeTickets, new CloseTicketAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(CloseTicket closeTicket) {
                                Intent intent = new Intent(getActivity(), ApprovalTicketActivity.class);
                                intent.putExtra(Constants.SELECTED_TICKET, closeTicket.getTicket());
                                getActivity().startActivityForResult(intent, Constants.REQUEST_CODE_APPROVAL);
                            }
                        });
                        approvalTicketListRV.setHasFixedSize(true);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        approvalTicketListRV.setLayoutManager(linearLayoutManager);
                        approvalTicketListRV.setAdapter(closeTicketAdapter);
                    } else {
//                                closeTicketAdapter = (CloseTicketAdapter) approvalTicketListRV.getAdapter();
                        closeTicketAdapter.swap(closeTickets);
                    }


                    emptyListLayout.setVisibility(View.GONE);
                    approvalTicketListRV.setVisibility(View.VISIBLE);
                } else {
                    emptyListLayout.setVisibility(View.VISIBLE);
                    approvalTicketListRV.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFinish() {
                super.onFinish();
                ticketSwiper.setRefreshing(false);
            }
        });
    }

    private class MyApprovalTask extends AsyncTask<Void, Void, Void> {

        String result;
        JSONObject object;
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gsona = this.gsonBuilder.create();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ticketSwiper.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(
                        CommonsUtil.getAbsoluteUrl(Constants.URL_REQUEST_APPROVE_CLOSE_NEW),
                        new FormBody.Builder()
                                .add(Constants.STATION_ID, String.valueOf(preferences.getPreferencesString(Constants.STATION_ID)))
                                .build());

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
//                JSONObject jsonObject = new JSONObject(result.toString());
                JSONObject jsonObject = new JSONObject(result);
                if(jsonObject.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
                    JSONArray jsonArray = jsonObject.getJSONArray("new_tickets");

                    Type listType = new TypeToken<List<CloseTicket>>() {}.getType();
                    closeTickets = gsona.fromJson(jsonArray.toString(), listType);

                    /*if(closeTickets.size() > 0) {
                        closeTicketAdapter = new CloseTicketAdapter(closeTickets, new CloseTicketAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(CloseTicket closeTicket) {
                                Fragment f = new MyApprovalDetail();
                                String page = Constants.MY_APPROVAL_DETAIL_PAGE;
                                Boolean flag = Boolean.valueOf(false);
                                Bundle args = new Bundle();
                                args.putString(Constants.PARAM_SECTION, Constants.MY_APPROVAL_PAGE);
                                args.putSerializable(Constants.SELECTED_TICKET, closeTicket.getTicket());
                                args.putInt(Constants.SELECTED_TICKET_POSITION, closeTickets.indexOf(closeTicket));
                                f.setArguments(args);
                                preferences.savePreferences(Constants.ACTIVE_PAGE, page);
                                mListener.onMenuSelected(page, f, flag);
                            }
                        });

                        approvalTicketListRV.setHasFixedSize(true);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        approvalTicketListRV.setLayoutManager(linearLayoutManager);

                        approvalTicketListRV.setAdapter(closeTicketAdapter);
                        emptyListLayout.setVisibility(View.GONE);
                        approvalTicketListRV.setVisibility(View.VISIBLE);
                    } else {
                        emptyListLayout.setVisibility(View.VISIBLE);
                        approvalTicketListRV.setVisibility(View.GONE);
                    }*/
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ticketSwiper.setRefreshing(false);
        }
    }

    public void initRecyclerView(RecyclerView approvalTicketListRV) {
        // reference
//        approvalTicketListRV = getRecyclerView();

        // set layout manager
        approvalTicketListRV.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        approvalTicketListRV.setLayoutManager(linearLayoutManager);


        new MyApprovalTask().execute();


        if(closeTickets.size() > 0) {
            if(approvalTicketListRV.getAdapter()==null) {
                closeTicketAdapter = new CloseTicketAdapter(closeTickets, new CloseTicketAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(CloseTicket closeTicket) {
                        Fragment f = new MyApprovalDetail();
                        String page = Constants.MY_APPROVAL_DETAIL_PAGE;
                        Boolean flag = Boolean.FALSE;
                        Bundle args = new Bundle();
                        args.putString(Constants.PARAM_SECTION, Constants.MY_APPROVAL_PAGE);
                        args.putSerializable(Constants.SELECTED_TICKET, closeTicket.getTicket());
                        args.putInt(Constants.SELECTED_TICKET_POSITION, closeTickets.indexOf(closeTicket));
                        f.setArguments(args);
                        preferences.savePreferences(Constants.ACTIVE_PAGE, page);
                        mListener.onMenuSelected(page, f, flag);
                    }
                });

            } else {
                closeTicketAdapter = (CloseTicketAdapter) approvalTicketListRV.getAdapter();
                closeTicketAdapter.swap(closeTickets);
            }

            approvalTicketListRV.setAdapter(closeTicketAdapter);
            emptyListLayout.setVisibility(View.GONE);
            approvalTicketListRV.setVisibility(View.VISIBLE);
        } else {
            emptyListLayout.setVisibility(View.VISIBLE);
            approvalTicketListRV.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ApiClient.cancel();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        loadApprovals();
    }

    @Override
    public void onResume() {
        super.onResume();
        Logcat.i("onPause");
        loadApprovals();
    }

    public RecyclerView getRecyclerView() {
        return (RecyclerView) this.rootView.findViewById(R.id.approvalTicketListRV);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.REQUEST_CODE_APPROVAL)
            loadApprovals();
    }
}
