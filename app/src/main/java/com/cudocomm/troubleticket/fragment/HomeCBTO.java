package com.cudocomm.troubleticket.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.squareup.picasso.Picasso;

public class HomeCBTO extends BaseFragment {

    private View rootView;

    private TextView userInfoTV;
    private TextView welcomeMsgTV;

    private TextView dateTimeTV;
    private ImageView profileImage;

    private String imageUrl;
    private Intent intent;

    public HomeCBTO() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cbto, container, false);

        initComponent();

        return rootView;
    }

    private void initComponent() {

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
    }

}
