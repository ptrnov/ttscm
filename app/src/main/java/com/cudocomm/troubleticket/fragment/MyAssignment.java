package com.cudocomm.troubleticket.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.activity.EngineerAssignmentActivity;
import com.cudocomm.troubleticket.adapter.AssignmentAdapter;
import com.cudocomm.troubleticket.model.Assignment;
import com.cudocomm.troubleticket.util.ApiClient;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;

public class MyAssignment extends BaseFragment {

    private View rootView;
    private SwipeRefreshLayout assignmentSwiper;
    private RecyclerView assignmentRV;

    private List<Assignment> assignments;
    private AssignmentAdapter assignmentAdapter;

    private TextView titleTV;
    private TextView statusTV;

    private View emptyListLayout;

    public MyAssignment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_assignment, container, false);

        initComponent();
        updateComponent();

        return rootView;
    }

    private void initComponent() {
        emptyListLayout = rootView.findViewById(R.id.emptyListLayout);
        assignmentSwiper = (SwipeRefreshLayout) rootView.findViewById(R.id.assignmentSwiper);
        assignmentRV = (RecyclerView) rootView.findViewById(R.id.assignmentRV);
        titleTV = (TextView) rootView.findViewById(R.id.positionTV);
        statusTV = (TextView) rootView.findViewById(R.id.dateTV);
        assignmentSwiper.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    private void updateComponent() {
        assignmentSwiper.post(new Runnable() {
            @Override
            public void run() {
                new MyAssignmentTask().execute();
            }
        });
        titleTV.setText(getResources().getString(R.string.label_header_position_national));

        statusTV.setText(getResources().getString(R.string.label_header_date, CommonsUtil.dateToString(new Date())));
        assignmentSwiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new MyAssignmentTask().execute();
            }
        });

    }

    private class MyAssignmentTask extends AsyncTask<Void, Void, Void> {
        String result;
        JSONObject object;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            assignmentSwiper.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(
                        CommonsUtil.getAbsoluteUrl(Constants.URL_GET_ASSIGNMENT_TICKET),
                        new FormBody.Builder()
                                .add(Constants.PARAM_ID, String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
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
                object = new JSONObject(result);
                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
                    Type type = new TypeToken<List<Assignment>>(){}.getType();
                    assignments = gson.fromJson(object.getString("new_tickets"), type);
                    if(assignments.size() > 0) {
                        assignmentAdapter = new AssignmentAdapter(assignments, new AssignmentAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(Assignment assignment) {

                                Intent intent = new Intent(context, EngineerAssignmentActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(Constants.SELECTED_ASSIGNMENT, assignment);
                                intent.putExtras(bundle);
                                getActivity().startActivityForResult(intent, Constants.ENGINEER_ASSIGNMENT);

                            }
                        });
                        assignmentRV.setHasFixedSize(true);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        assignmentRV.setLayoutManager(linearLayoutManager);

                        assignmentRV.setAdapter(assignmentAdapter);

                        emptyListLayout.setVisibility(View.GONE);
                        assignmentRV.setVisibility(View.VISIBLE);
                    } else {
                        emptyListLayout.setVisibility(View.VISIBLE);
                        assignmentRV.setVisibility(View.GONE);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            assignmentSwiper.setRefreshing(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        new MyAssignmentTask().execute();
    }
}
