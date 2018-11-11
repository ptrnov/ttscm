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
import com.cudocomm.troubleticket.util.ApiClient;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.squareup.picasso.Picasso;

public class HomeKadepTS extends BaseFragment {

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

    public HomeKadepTS() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home_kadepts, container, false);

        initComponent();

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
                preferences.getPreferencesString(Constants.USER_NAME), preferences.getPreferencesString(Constants.POSITION_NAME), Constants.NASIONAL));
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

//        new
        widgetNeedApproval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = new RequestVisit();
                String page = Constants.REQUEST_VISIT_PAGE;
                preferences.savePreferences(Constants.ACTIVE_PAGE, page);
                mListener.onMenuSelected(page, f, false);
            }
        });

        widgetNeedAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = new MyTicketNew();
                String page = Constants.ASSIGNMENT_PAGE;
                preferences.savePreferences(Constants.ACTIVE_PAGE, page);
                mListener.onMenuSelected(page, f, false);
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
//        needApprovalCounterTV.setText(preferences.getPreferencesString(Constants.COUNTER_NEED_APPROVAL));
        needActionCounterTV.setText(preferences.getPreferencesString(Constants.COUNTER_MY_TASK));
//        totalTicketTV.setText(preferences.getPreferencesString(Constants.COUNTER_TOTAL));
    }

    @Override
    public void onPause() {
        super.onPause();
        ApiClient.cancel();
    }

}
