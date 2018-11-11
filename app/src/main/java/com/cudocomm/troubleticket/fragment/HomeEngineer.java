package com.cudocomm.troubleticket.fragment;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.TTSApplication;
import com.cudocomm.troubleticket.adapter.Menu2Adapter;
import com.cudocomm.troubleticket.model.MenuModel;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeEngineer extends BaseFragment {

    private View rootView;

//    private TextView userInfoTV;
//    private TextView welcomeMsgTV;

    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView homeMenuRV;

    private Menu2Adapter menu2Adapter;
    private List<MenuModel> menuModels = new ArrayList<>();

    private TextView userInfoTV;
    private TextView welcomeMsgTV;

    private TypedArray menuIcon;
    private String[] menuIsTitle;
    private String[] menuTitle;
    private TextView dateTimeTV;
    private ImageView profileImage;

    private String imageUrl;
    private Intent intent;

    public HomeEngineer() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home_engineer, container, false);

        initComponent();

        return rootView;
    }

    private void initComponent() {

        userInfoTV = (TextView) rootView.findViewById(R.id.userInfoTV);
        welcomeMsgTV = (TextView) rootView.findViewById(R.id.welcomeMsgTV);

        userInfoTV.setText(getResources().getString(R.string.label_user_info,
                preferences.getPreferencesString(Constants.USER_NAME), preferences.getPreferencesString(Constants.POSITION_NAME), Constants.NASIONAL));
        welcomeMsgTV.setText(getResources().getString(R.string.label_welcome_msg, preferences.getPreferencesString(Constants.USER_NAME)));

        homeMenuRV = (RecyclerView) rootView.findViewById(R.id.homeMenuRV);
        gridLayoutManager = new GridLayoutManager(TTSApplication.getContext(), 2);
        linearLayoutManager = new LinearLayoutManager(TTSApplication.getContext());
        homeMenuRV.setHasFixedSize(true);
        homeMenuRV.setLayoutManager(gridLayoutManager);

        this.menuTitle = getResources().getStringArray(R.array.menu_array_engineer);
        this.menuIcon = getResources().obtainTypedArray(R.array.menu_icon_array_engineer);
        this.menuIsTitle = getResources().getStringArray(R.array.menu_is_title_engineer);

        loadMenu();

//        userInfoTV = (TextView) rootView.findViewById(R.id.userInfoTV);
//        welcomeMsgTV = (TextView) rootView.findViewById(R.id.welcomeMsgTV);
        dateTimeTV = (TextView) rootView.findViewById(R.id.dateTimeTV);
        profileImage = (ImageView) rootView.findViewById(R.id.profile_image);

//        userInfoTV.setText(getResources().getString(R.string.label_user_info,
//                preferences.getPreferencesString(Constants.USER_NAME), preferences.getPreferencesString(Constants.POSITION_NAME), preferences.getPreferencesString(Constants.STATION_NAME)));
//        welcomeMsgTV.setText(getResources().getString(R.string.label_welcome_msg, preferences.getPreferencesString(Constants.USER_NAME)));

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

    private void loadMenu() {
        if(menuModels.size() > 0)
            menuModels.clear();

        for (int i = 0; i < this.menuTitle.length; i++) {
            if (!menuIsTitle[i].equalsIgnoreCase("true") && !menuTitle[i].equalsIgnoreCase(Constants.HOME_PAGE)) {
                menuModels.add(new MenuModel(this.menuTitle[i], this.menuIcon.getResourceId(i, -1), false));
            }
        }

        menu2Adapter = new Menu2Adapter(getActivity(), menuModels, new Menu2Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(MenuModel menuModel) {
                /*Fragment f = null;
                String page = "";
                Boolean flag = Boolean.valueOf(false);
                Bundle args = new Bundle();

                if(menuModel.getTitle().equalsIgnoreCase(Constants.DOWN_TIME)) {
                    intent = new Intent(getActivity(), DownTimeActivity.class);
                    getActivity().startActivityForResult(intent, Constants.REQUEST_NEW_TICKET);
                } else if(menuModel.getTitle().equalsIgnoreCase(Constants.KERUSAKAN)) {
                    intent = new Intent(getActivity(), KerusakanActivity.class);
                    getActivity().startActivityForResult(intent, Constants.REQUEST_NEW_TICKET);
                } else if(menuModel.getTitle().equalsIgnoreCase(Constants.TT_ACTIVITY_PAGE)) {
                    f = new MyTicketNew();
                    page = Constants.TT_ACTIVITY_PAGE;
                    args.putString(Constants.STATION_ID, String.valueOf(preferences.getPreferencesInt(Constants.STATION_ID)));
                    f.setArguments(args);
                    f.setArguments(args);
                    preferences.savePreferences(Constants.ACTIVE_PAGE, page);
                    mListener.onMenuSelected(page, f, flag);
                }*/


            }
        });
        homeMenuRV.setAdapter(menu2Adapter);

    }

}
