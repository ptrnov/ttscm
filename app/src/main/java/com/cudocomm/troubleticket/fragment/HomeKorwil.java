package com.cudocomm.troubleticket.fragment;

import android.app.Activity;
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

import cz.msebera.android.httpclient.Header;

public class HomeKorwil extends BaseFragment {

    private View rootView;

    private TextView userInfoTV;
    private TextView welcomeMsgTV;
    private TextView dateTimeTV;
    private ImageView profileImage;

    private String imageUrl;

    private LinearLayout widgetNeedAction;
    private TextView needActionCounterTV;
    private TextView totalTicketTV;

    public HomeKorwil() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logcat.i("HomeKorwil.java onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home_korwil, container, false);
        Logcat.i("HomeKorwil.java onCreateView()");
        initComponent();



        return rootView;
    }

    private void initComponent() {
        Logcat.i("HomeKorwil.java initComponent()");
        widgetNeedAction = (LinearLayout) rootView.findViewById(R.id.widgetNeedAction);
        needActionCounterTV = (TextView) rootView.findViewById(R.id.needActionCounterTV);
        totalTicketTV = (TextView) rootView.findViewById(R.id.totalTicketTV);
        userInfoTV = (TextView) rootView.findViewById(R.id.userInfoTV);
        welcomeMsgTV = (TextView) rootView.findViewById(R.id.welcomeMsgTV);
        dateTimeTV = (TextView) rootView.findViewById(R.id.dateTimeTV);
        profileImage = (ImageView) rootView.findViewById(R.id.profile_image);

        userInfoTV.setText(getResources().getString(R.string.label_user_info,
                preferences.getPreferencesString(Constants.USER_NAME), preferences.getPreferencesString(Constants.POSITION_NAME), preferences.getPreferencesString(Constants.REGION_NAME)));
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

        loadStatistics();

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

    private void loadStatistics() {
        ApiClient.setApplicationContext(context);
        RequestParams params = new RequestParams();
//        params.put(Constants.REGION_ID, preferences.getPreferencesInt(Constants.STATION_ID));
        params.put(Constants.REGION_ID, preferences.getPreferencesString(Constants.REGION_ID));
        params.put(Constants.POSITION_ID, preferences.getPreferencesInt(Constants.POSITION_ID));
        params.put(Constants.USER_ID, preferences.getPreferencesInt(Constants.ID_UPDRS));
        ApiClient.post(CommonsUtil.getAbsoluteUrl("countticketbyregion_new"), params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                /*statisticRV.setVisibility(View.GONE);
                statisticPB.setVisibility(View.VISIBLE);*/
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Logcat.i(response.toString());
                    if(response.getString(Constants.RESPONSE_STATUS).equalsIgnoreCase(Constants.RESPONSE_SUCCESS)) {

                        JSONObject myTaskCounterObj = response.getJSONObject("mytaskcounter");
                        String myTaskCounter = myTaskCounterObj.getString("critical") + " Critical - " + myTaskCounterObj.getString("major") + " Major - " + myTaskCounterObj.getString("minor") + " Minor";
                        preferences.savePreferences(Constants.COUNTER_MY_TASK, myTaskCounter);

                        needActionCounterTV.setText(myTaskCounter);

                        JSONObject totTicketObj = response.getJSONObject("totalticket");
                        String totTicket = totTicketObj.getString("critical") + " Critical - " + totTicketObj.getString("major") + " Major - " + totTicketObj.getString("minor") + " Minor";
                        preferences.savePreferences(Constants.COUNTER_TOTAL, totTicket);

                        totalTicketTV.setText(totTicket);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Logcat.i("HomeKorwil.java onResume()");
        needActionCounterTV.setText(preferences.getPreferencesString(Constants.COUNTER_MY_TASK));
        totalTicketTV.setText(preferences.getPreferencesString(Constants.COUNTER_TOTAL));
    }

    @Override
    public void onPause() {
        super.onPause();
        Logcat.i("HomeKorwil.java onPause()");
        ApiClient.cancel();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Logcat.i("HomeKorwil.java onAttach()");
    }
}
