package com.cudocomm.troubleticket.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.util.ApiClient;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Logcat;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;
import okhttp3.FormBody;

public class HomeKST extends BaseFragment {

    private View rootView;

    private TextView userInfoTV;
    private TextView welcomeMsgTV;
    private TextView dateTimeTV;
    private ImageView profileImage;

    private String imageUrl;

    private LinearLayout widgetNeedApproval;
    private LinearLayout widgetNeedAction;
    private TextView needApprovalCounterTV;
    private TextView needActionCounterTV;
    private TextView totalTicketTV;

    public HomeKST() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        loadStatistics();

        new KSTLoadStatistics().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home_kst, container, false);

        initComponent();
        loadDashboard();


        return rootView;
    }

    private void initComponent() {
        widgetNeedApproval = (LinearLayout) rootView.findViewById(R.id.widgetNeedApproval);
        widgetNeedAction = (LinearLayout) rootView.findViewById(R.id.widgetNeedAction);
        needApprovalCounterTV = (TextView) rootView.findViewById(R.id.needApprovalCounterTV);
        needActionCounterTV = (TextView) rootView.findViewById(R.id.needActionCounterTV);
        totalTicketTV = (TextView) rootView.findViewById(R.id.totalTicketTV);
        userInfoTV = (TextView) rootView.findViewById(R.id.userInfoTV);
        welcomeMsgTV = (TextView) rootView.findViewById(R.id.welcomeMsgTV);
        dateTimeTV = (TextView) rootView.findViewById(R.id.dateTimeTV);
        profileImage = (ImageView) rootView.findViewById(R.id.profile_image);

        userInfoTV.setText(getResources().getString(R.string.label_user_info,
                preferences.getPreferencesString(Constants.USER_NAME), preferences.getPreferencesString(Constants.POSITION_NAME), preferences.getPreferencesString(Constants.STATION_NAME)));
        welcomeMsgTV.setText(getResources().getString(R.string.label_welcome_msg, preferences.getPreferencesString(Constants.USER_NAME)));


        if(!preferences.getPreferencesString(Constants.USER_PICTURE).isEmpty() && !preferences.getPreferencesString(Constants.USER_PICTURE).equals("")) {
            if(preferences.getPreferencesString(Constants.USER_PICTURE).contains("/assets/images/")) {
                imageUrl = CommonsUtil.getAbsoluteUrlImage(preferences.getPreferencesString(Constants.USER_PICTURE));
            } else {
                imageUrl = CommonsUtil.getAbsoluteUrlImage("/assets/images/user_picture/" + preferences.getPreferencesString(Constants.USER_PICTURE));
            }
            Picasso.with(getContext()).load(imageUrl).error(R.drawable.ic_no_image).into(profileImage);
        }


        dateTimeTV.setText(CommonsUtil.getToday());

//        loadStatistics();
            new KSTLoadStatistics().execute();


        widgetNeedApproval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = new MyApproval();
                String page = Constants.MY_APPROVAL_PAGE;
                preferences.savePreferences(Constants.ACTIVE_PAGE, page);
                mListener.onMenuSelected(page, f, false);
            }
        });

        widgetNeedAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = new MyTicketNew();
                String page = Constants.TT_ACTIVITY_PAGE;
                preferences.savePreferences(Constants.ACTIVE_PAGE, page);
                mListener.onMenuSelected(page, f, false);
            }
        });
    }

    class KSTLoadStatistics extends AsyncTask<Void, Void, Void> {

        String request;
        JSONObject response;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                request = ApiClient.post(
                        CommonsUtil.getAbsoluteUrl("countticketbystation_new"),
                        new FormBody.Builder()
                                .add(Constants.PARAM_STATION_ID, String.valueOf(preferences.getPreferencesString(Constants.STATION_ID)))
                                .add(Constants.POSITION_ID, String.valueOf(preferences.getPreferencesInt(Constants.POSITION_ID)))
                                .add(Constants.USER_ID, String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                                .build());

                response = new JSONObject(request);

                Logcat.i(response.toString());
                if(response.getString(Constants.RESPONSE_STATUS).equalsIgnoreCase(Constants.RESPONSE_SUCCESS)) {
                    /*JSONObject object = response.getJSONObject("needapproval");
                    String counter = object.getString("critical") + " Critical - " + object.getString("major") + " Major - " + object.getString("minor") + " Minor";
                    preferences.savePreferences(Constants.COUNTER_NEED_APPROVAL, counter);*/

//                    Logcat.i("NEED_APPROVAL::" + response.getString("needapproval"));

                    if(!response.getString("needapproval").equals("null")) {
                        JSONObject object = response.getJSONObject("needapproval");
                        String counter = object.getString("critical") + " Critical - " + object.getString("major") + " Major - " + object.getString("minor") + " Minor";
                        preferences.savePreferences(Constants.COUNTER_NEED_APPROVAL, counter);

                    } else {
                        String counter = "0 Critical - 0 Major - 0 Minor";
                        preferences.savePreferences(Constants.COUNTER_NEED_APPROVAL, counter);

                    }

                    if(!response.getString("mytaskcounter").equals("null")) {
                        JSONObject myTaskCounterObj = response.getJSONObject("mytaskcounter");
                        String myTaskCounter = myTaskCounterObj.getString("critical") + " Critical - " + myTaskCounterObj.getString("major") + " Major - " + myTaskCounterObj.getString("minor") + " Minor";
                        preferences.savePreferences(Constants.COUNTER_MY_TASK, myTaskCounter);
                    } else {
                        String myTaskCounter = "0 Critical - 0 Major - 0 Minor";
                        preferences.savePreferences(Constants.COUNTER_MY_TASK, myTaskCounter);
                    }


                    if(!response.getString("totalticket").equals("null")) {
                        JSONObject totTicketObj = response.getJSONObject("totalticket");
                        String totTicket = totTicketObj.getString("critical") + " Critical - " + totTicketObj.getString("major") + " Major - " + totTicketObj.getString("minor") + " Minor";
                        preferences.savePreferences(Constants.COUNTER_TOTAL, totTicket);
                    } else {
                        String totTicket = "0 Critical - 0 Major - 0 Minor";
                        preferences.savePreferences(Constants.COUNTER_TOTAL, totTicket);
                    }

                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

    private void loadStatistics() {
        ApiClient.setApplicationContext(context);
        RequestParams params = new RequestParams();
        params.put(Constants.STATION_ID, preferences.getPreferencesString(Constants.STATION_ID));
        params.put(Constants.POSITION_ID, preferences.getPreferencesInt(Constants.POSITION_ID));
        params.put(Constants.USER_ID, preferences.getPreferencesInt(Constants.ID_UPDRS));
        ApiClient.post(CommonsUtil.getAbsoluteUrl("countticketbystation_new"), params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Logcat.i(response.toString());
                    if(response.getString(Constants.RESPONSE_STATUS).equalsIgnoreCase(Constants.RESPONSE_SUCCESS)) {
                        JSONObject object = response.getJSONObject("needapproval");
                        String counter = object.getString("critical") + " Critical - " + object.getString("major") + " Major - " + object.getString("minor") + " Minor";
                        preferences.savePreferences(Constants.COUNTER_NEED_APPROVAL, counter);

//                        needApprovalCounterTV.setText(counter);

                        JSONObject myTaskCounterObj = response.getJSONObject("mytaskcounter");
                        String myTaskCounter = myTaskCounterObj.getString("critical") + " Critical - " + myTaskCounterObj.getString("major") + " Major - " + myTaskCounterObj.getString("minor") + " Minor";
                        preferences.savePreferences(Constants.COUNTER_MY_TASK, myTaskCounter);

//                        needActionCounterTV.setText(myTaskCounter);

                        JSONObject totTicketObj = response.getJSONObject("totalticket");
                        String totTicket = totTicketObj.getString("critical") + " Critical - " + totTicketObj.getString("major") + " Major - " + totTicketObj.getString("minor") + " Minor";
                        preferences.savePreferences(Constants.COUNTER_TOTAL, totTicket);

//                        totalTicketTV.setText(totTicket);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                /*statisticRV.setVisibility(View.VISIBLE);
                statisticPB.setVisibility(View.GONE);*/
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Logcat.e(throwable.toString());
            }
        });
    }

    private void loadDashboard() {
        needApprovalCounterTV.setText(preferences.getPreferencesString(Constants.COUNTER_NEED_APPROVAL));
        needActionCounterTV.setText(preferences.getPreferencesString(Constants.COUNTER_MY_TASK));
        totalTicketTV.setText(preferences.getPreferencesString(Constants.COUNTER_TOTAL));
    }

    @Override
    public void onResume() {
        super.onResume();
        /*needApprovalCounterTV.setText(preferences.getPreferencesString(Constants.COUNTER_NEED_APPROVAL));
        needActionCounterTV.setText(preferences.getPreferencesString(Constants.COUNTER_MY_TASK));
        totalTicketTV.setText(preferences.getPreferencesString(Constants.COUNTER_TOTAL));*/
        loadStatistics();
        loadDashboard();
    }

    @Override
    public void onPause() {
        super.onPause();
        ApiClient.cancel();
    }
}
