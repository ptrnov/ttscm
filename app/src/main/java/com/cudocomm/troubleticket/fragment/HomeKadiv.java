package com.cudocomm.troubleticket.fragment;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.TTSApplication;
import com.cudocomm.troubleticket.activity.DashboardTicketActivity;
import com.cudocomm.troubleticket.adapter.Menu2Adapter;
import com.cudocomm.troubleticket.model.MenuModel;
import com.cudocomm.troubleticket.util.ApiClient;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Logcat;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class HomeKadiv extends BaseFragment {

    private static final String TAG = "HomeKadev";
    private View rootView;

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

    private TextView openTV, closeTV, criticalTV, majorTV, minorTV, acnTV, nacnTV;
    private LinearLayout widgetOpenClose;

    int open = 0;
    int close = 0;
    int critical = 0;
    int major = 0;
    int minor = 0;
    int acn = 0;
    int nacn = 0;

    RelativeLayout openTicketRL, closeTicketRL, criticalRL, majorRL, minorRL, acnRL, nacnRL;

    public HomeKadiv() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        loadStatistics();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_kadiv, container, false);

        initComponent();

        return rootView;
    }

    private void initComponent() {
        widgetOpenClose = (LinearLayout) rootView.findViewById(R.id.widgetOpenClose);
        openTV = (TextView) rootView.findViewById(R.id.openKadivTV);
        closeTV = (TextView) rootView.findViewById(R.id.closeKadivTV);
        criticalTV = (TextView) rootView.findViewById(R.id.criticalKadivTV);
        majorTV = (TextView) rootView.findViewById(R.id.majorKadivTV);
        minorTV = (TextView) rootView.findViewById(R.id.minorKadivTV);
        acnTV = (TextView) rootView.findViewById(R.id.acnTV);
        nacnTV = (TextView) rootView.findViewById(R.id.nacnTV);

        openTicketRL = (RelativeLayout) rootView.findViewById(R.id.openTicketRL);
        closeTicketRL = (RelativeLayout) rootView.findViewById(R.id.closeTicketRL);
        criticalRL = (RelativeLayout) rootView.findViewById(R.id.criticalRL);
        majorRL = (RelativeLayout) rootView.findViewById(R.id.majorRL);
        minorRL = (RelativeLayout) rootView.findViewById(R.id.minorRL);
        acnRL = (RelativeLayout) rootView.findViewById(R.id.acnRL);
        nacnRL = (RelativeLayout) rootView.findViewById(R.id.nacnRL);

        homeMenuRV = (RecyclerView) rootView.findViewById(R.id.homeMenuRV);
//        gridLayoutManager = new GridLayoutManager(TTSApplication.getContext(), 6);
        gridLayoutManager = new GridLayoutManager(TTSApplication.getContext(), 6,GridLayoutManager.VERTICAL,false);
        linearLayoutManager = new LinearLayoutManager(TTSApplication.getContext());
        LinearLayoutManager Ly = new LinearLayoutManager(TTSApplication.getContext(), LinearLayoutManager.HORIZONTAL, false);
        homeMenuRV.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        homeMenuRV.setNestedScrollingEnabled(false);
        homeMenuRV.setVerticalScrollBarEnabled(false);
        homeMenuRV.setHorizontalScrollBarEnabled(false);
        homeMenuRV.setHasFixedSize(true);
//        homeMenuRV.setLayoutManager(gridLayoutManager);
        homeMenuRV.setLayoutManager(Ly);

        this.menuTitle = getResources().getStringArray(R.array.menu_array_kadiv);
        this.menuIcon = getResources().obtainTypedArray(R.array.menu_icon_array_kadiv);
        this.menuIsTitle = getResources().getStringArray(R.array.menu_is_title_kadiv);

        loadMenu();

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

        loadStatistics();

        openTicketRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DashboardTicketActivity.class);
                intent.putExtra("param", Constants.DASHBOARD_OPEN_TICKET);
                intent.putExtra("title", "Open Tickets");
                startActivity(intent);
            }
        });
        closeTicketRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DashboardTicketActivity.class);
                intent.putExtra("param", Constants.DASHBOARD_CLOSE_TICKET);
                intent.putExtra("title", "Close Tickets");
                startActivity(intent);
            }
        });

        criticalRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DashboardTicketActivity.class);
                intent.putExtra("param", Constants.DASHBOARD_CRITICAL_TICKET);
                intent.putExtra("title", "Critical Tickets");
                startActivity(intent);
            }
        });
        majorRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DashboardTicketActivity.class);
                intent.putExtra("param", Constants.DASHBOARD_MAJOR_TICKET);
                intent.putExtra("title", "Major Tickets");
                startActivity(intent);
            }
        });
        minorRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DashboardTicketActivity.class);
                intent.putExtra("param", Constants.DASHBOARD_MINOR_TICKET);
                intent.putExtra("title", "Minor Tickets");
                startActivity(intent);
            }
        });

        acnRL.setVisibility(View.GONE);
        nacnRL.setVisibility(View.GONE);
        acnRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DashboardTicketActivity.class);
                intent.putExtra("param", Constants.DASHBOARD_ACN_TICKET);
                intent.putExtra("title", "AC Nielsen Downtime");
                startActivity(intent);
            }
        });
        nacnRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DashboardTicketActivity.class);
                intent.putExtra("param", Constants.DASHBOARD_NACN_TICKET);
                intent.putExtra("title", "Non AC Nielsen Downtime");
                startActivity(intent);
            }
        });
    }

    private void loadMenu() {
        if(menuModels.size() > 0)
            menuModels.clear();

        for (int i = 0; i < this.menuTitle.length; i++) {
            if (!menuIsTitle[i].equalsIgnoreCase("true") && !menuTitle[i].equalsIgnoreCase(Constants.HOME_PAGE)) {
                menuModels.add(new MenuModel(this.menuTitle[i], this.menuIcon.getResourceId(i, -1), false));
            }
        }

    menu2Adapter =
        new Menu2Adapter(
            getActivity(),
            menuModels,
            new Menu2Adapter.OnItemClickListener() {
              @Override
              public void onItemClick(MenuModel menuModel) {
                Fragment f;
                String page;
                Boolean flag = Boolean.FALSE;
                //                Bundle args = new Bundle();

                if (menuModel.getTitle().equalsIgnoreCase(Constants.TT_STATISTICS)) {
                  f = new StatisticFragment();
                  page = Constants.TT_STATISTICS;
                  preferences.savePreferences(Constants.ACTIVE_PAGE, page);
                  mListener.onMenuSelected(page, f, flag);
                }
                /*else if(menuModel.getTitle().equalsIgnoreCase(Constants.TT_TOP_TEN)) {
                    f = new TopTenFragment();
                    page = Constants.TT_TOP_TEN;
                    preferences.savePreferences(Constants.ACTIVE_PAGE, page);
                    mListener.onMenuSelected(page, f, flag);
                } */
                else if (menuModel.getTitle().equalsIgnoreCase(Constants.TT_NEWEST_TICKET)) {
                  f = new ToDoListFragment();
                  page = Constants.TT_NEWEST_TICKET;
                  preferences.savePreferences(Constants.ACTIVE_PAGE, page);
                  mListener.onMenuSelected(page, f, flag);
                } else if (menuModel.getTitle().equalsIgnoreCase(Constants.TT_AC_NIELASEN)) {
                  f = new ACNielsenFragment();
                  page = Constants.TT_AC_NIELASEN;
                  preferences.savePreferences(Constants.ACTIVE_PAGE, page);
                  mListener.onMenuSelected(page, f, flag);
                } else if (menuModel.getTitle().equalsIgnoreCase(Constants.TT_NON_AC_NIELASEN)) {
                  f = new NonACNielsenFragment();
                  page = Constants.TT_NON_AC_NIELASEN;
                  preferences.savePreferences(Constants.ACTIVE_PAGE, page);
                  mListener.onMenuSelected(page, f, flag);
                } else if (menuModel.getTitle().equalsIgnoreCase(Constants.TT_O_CHANNEL)) {
                  f = new OchannelFragment();
                  page = Constants.TT_O_CHANNEL;
                  preferences.savePreferences(Constants.ACTIVE_PAGE, page);
                  mListener.onMenuSelected(page, f, flag);
                } else if (menuModel.getTitle().equalsIgnoreCase(Constants.TT_NEXMEDIA)) {
                  f = new NexMediaFragment();
                  page = Constants.TT_NEXMEDIA;
                  preferences.savePreferences(Constants.ACTIVE_PAGE, page);
                  mListener.onMenuSelected(page, f, flag);
                } else if (menuModel.getTitle().equalsIgnoreCase(Constants.TT_SEND_REMINDER)) {
                    Log.d(TAG, "menu_test" + menuModel.getTitle().toString());;
                    f = new SendReminder();
                    page = Constants.TT_SEND_REMINDER;
                    preferences.savePreferences(Constants.ACTIVE_PAGE, page);
                    mListener.onMenuSelected(page, f, flag);
                }
              }
            });
        homeMenuRV.setAdapter(menu2Adapter);

    }



    private void loadStatistics() {

        ApiClient.setApplicationContext(context);
        RequestParams params = new RequestParams();
        ApiClient.post("statistics_data", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                Logcat.i("START statistics_data");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Logcat.i("RESPONSE::" + response.toString());

                try {
                    Logcat.i(response.toString());
                    if(response.getString(Constants.RESPONSE_STATUS).equalsIgnoreCase(Constants.RESPONSE_SUCCESS)) {

                        acnTV.setText(response.getString("acn_dt"));
                        nacnTV.setText(response.getString("nacn_dt"));

                        JSONArray statusArray = response.getJSONArray("status_percentages");
                        for(int i=0; i<statusArray.length(); i++) {
                            JSONObject object = statusArray.getJSONObject(i);
//                            statusEntries.add(new PieEntry(Float.parseFloat((String) object.get("total")), (String) object.get("ticket_status")));

                            if(((String) object.get("ticket_status")).equalsIgnoreCase("CLOSE")) {
                                close = close + Integer.parseInt((String) object.get("total"));
                            } else {
                                open = open + Integer.parseInt((String) object.get("total"));
                            }

                        }

                        JSONArray severityArray = response.getJSONArray("severity_percentages");
                        for(int i=0; i<severityArray.length(); i++) {
                            JSONObject object = severityArray.getJSONObject(i);
//                            severityEntries.add(new PieEntry(Float.parseFloat((String) object.get("total")), (String) object.get("ticket_severity")));

                            if(((String) object.get("ticket_severity")).equalsIgnoreCase("CRITICAL")) {
                                critical = critical + Integer.parseInt((String) object.get("total"));
                            } else if(((String) object.get("ticket_severity")).equalsIgnoreCase("MAJOR")) {
                                major = major + Integer.parseInt((String) object.get("total"));
                            } else if(((String) object.get("ticket_severity")).equalsIgnoreCase("MINOR")) {
                                minor = minor + Integer.parseInt((String) object.get("total"));
                            }

                        }

                    }


                } catch (JSONException e) {
                    Logcat.e("RESPONSE ERROR");
                    e.printStackTrace();
                }
                openTV.setText(String.valueOf(open));
                closeTV.setText(String.valueOf(close));
                criticalTV.setText(String.valueOf(critical));
                majorTV.setText(String.valueOf(major));
                minorTV.setText(String.valueOf(minor));
                widgetOpenClose.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Logcat.i("FAILURE statistics_data");
                throwable.printStackTrace();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Logcat.i("FINISH statistics_data");

            }
        });
    }

}
