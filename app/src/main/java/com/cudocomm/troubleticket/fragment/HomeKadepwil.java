package com.cudocomm.troubleticket.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.model.CounterModel;
import com.cudocomm.troubleticket.util.ApiClient;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Logcat;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

public class HomeKadepwil extends BaseFragment {

    private View rootView;

    private TextView userInfoTV;
    private TextView welcomeMsgTV;
    private TextView dateTimeTV;
    private ImageView profileImage;

    private String imageUrl;

    private LinearLayout widgetNeedAction;
    private TextView needActionCounterTV;
    private TextView totalTicketTV;

    private CounterModel myTaskCounter, totalTicket;

    public HomeKadepwil() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home_korwil, container, false);

        initComponent();

        return rootView;
    }

    private void initComponent() {
        userInfoTV = (TextView) rootView.findViewById(R.id.userInfoTV);
        welcomeMsgTV = (TextView) rootView.findViewById(R.id.welcomeMsgTV);
        dateTimeTV = (TextView) rootView.findViewById(R.id.dateTimeTV);
        profileImage = (ImageView) rootView.findViewById(R.id.profile_image);
        widgetNeedAction = (LinearLayout) rootView.findViewById(R.id.widgetNeedAction);
        needActionCounterTV = (TextView) rootView.findViewById(R.id.needActionCounterTV);
        totalTicketTV = (TextView) rootView.findViewById(R.id.totalTicketTV);
        userInfoTV.setText(getResources().getString(R.string.label_user_info,
                preferences.getPreferencesString(Constants.USER_NAME), preferences.getPreferencesString(Constants.POSITION_NAME), preferences.getPreferencesString(Constants.DEPARTMENT_NAME)));
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
        params.put(Constants.DEPARTMENT_ID, preferences.getPreferencesString(Constants.DEPARTMENT_ID));
        params.put(Constants.POSITION_ID, preferences.getPreferencesInt(Constants.POSITION_ID));
        params.put(Constants.USER_ID, preferences.getPreferencesInt(Constants.ID_UPDRS));
        ApiClient.post(CommonsUtil.getAbsoluteUrl("countticketbydepartment_new"), params, new JsonHttpResponseHandler() {
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

                        Type counterType = new TypeToken<CounterModel>(){}.getType();
                        myTaskCounter = gson.fromJson(response.getString("mytaskcounter"), counterType);
                        if(myTaskCounter != null)
                            preferences.savePreferences(Constants.COUNTER_MY_TASK, getResources().getString(R.string.widget_need_approval_counter, myTaskCounter.getCritical(), myTaskCounter.getMajor(), myTaskCounter.getMinor()));
                        else
                            preferences.savePreferences(Constants.COUNTER_MY_TASK, getResources().getString(R.string.widget_need_approval_counter_null));


                        /*JSONObject myTaskCounterObj = response.getJSONObject("mytaskcounter");
                        String myTaskCounter = myTaskCounterObj.getString("critical") + " Critical - " + myTaskCounterObj.getString("major") + " Major - " + myTaskCounterObj.getString("minor") + " Minor";
                        preferences.savePreferences(Constants.COUNTER_MY_TASK, myTaskCounter);

                        needActionCounterTV.setText(myTaskCounter);*/
                        needActionCounterTV.setText(preferences.getPreferencesString(Constants.COUNTER_MY_TASK));

                        Type totalType = new TypeToken<CounterModel>(){}.getType();
                        totalTicket = gson.fromJson(response.getString("totalticket"), totalType);
                        if(totalTicket != null)
                            preferences.savePreferences(Constants.COUNTER_TOTAL, getResources().getString(R.string.widget_need_approval_counter, totalTicket.getCritical(), totalTicket.getMajor(), totalTicket.getMinor()));
                        else
                            preferences.savePreferences(Constants.COUNTER_TOTAL, getResources().getString(R.string.widget_need_approval_counter_null));

                        totalTicketTV.setText(preferences.getPreferencesString(Constants.COUNTER_TOTAL));
/*
                        JSONObject totTicketObj = response.getJSONObject("totalticket");
                        String totTicket = totTicketObj.getString("critical") + " Critical - " + totTicketObj.getString("major") + " Major - " + totTicketObj.getString("minor") + " Minor";
                        preferences.savePreferences(Constants.COUNTER_TOTAL, totTicket);

                        totalTicketTV.setText(totTicket);*/

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
        needActionCounterTV.setText(preferences.getPreferencesString(Constants.COUNTER_MY_TASK));
        totalTicketTV.setText(preferences.getPreferencesString(Constants.COUNTER_TOTAL));
    }

    @Override
    public void onPause() {
        super.onPause();
        ApiClient.cancel();
    }


}
