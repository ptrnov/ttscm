package com.cudocomm.troubleticket.fragment;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.util.Log;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.TTSApplication;
import com.cudocomm.troubleticket.activity.DownTimeActivity;
import com.cudocomm.troubleticket.activity.DownTimeSpcActivity;
import com.cudocomm.troubleticket.activity.GangguanActivity;
import com.cudocomm.troubleticket.activity.KerusakanActivity;
import com.cudocomm.troubleticket.activity.KerusakanSpcActivity;
import com.cudocomm.troubleticket.activity.MainActivity;
import com.cudocomm.troubleticket.adapter.MenuAdapter;
import com.cudocomm.troubleticket.database.dao.UserDAO;
import com.cudocomm.troubleticket.database.model.UserLoginModel;
import com.cudocomm.troubleticket.model.MenuModel;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Preferences;
import com.cudocomm.troubleticket.util.SessionManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NavDrawFragment extends BaseFragment {

    private static final String TAG = "SideNav";
    private View containerView;
    private boolean fromSavedInstantState;
    private boolean islearned;
    private DrawerLayout mDrawerLayout;
    private SessionManager sessionManager;
    private List<UserLoginModel> userLoginModels;
    private Spinner userSpinner;
    private List<String> userList = new ArrayList<String>();
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private TypedArray menuIcon;
    private String[] menuIsTitle;
    private String[] menuTitle;
    private Preferences prefs;
    private View rootView;
    private DialogFragment dialogFragment;
    private Intent intent;

    private int userType = 0;

    public NavDrawFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new Preferences(getActivity());
        islearned = prefs.getPreferencesBoolean(Constants.IS_LEARNED);
        userType = prefs.getPreferencesInt(Constants.POSITION_ID);
        if (savedInstanceState != null) {
            this.fromSavedInstantState = true;
        }
        sessionManager = new SessionManager(TTSApplication.getContext());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_nav_draw, container, false);
        userSpinner = (Spinner) rootView.findViewById(R.id.user_spinner);
        mDrawerList = (ListView) rootView.findViewById(R.id.left_drawer);
        return rootView;
    }

    public void setup(View fragmentId, DrawerLayout drawerlayout, Toolbar toolbar) {
        containerView = fragmentId;
        drawerlayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout = drawerlayout;
        if(userType == Constants.TECHNICIAN) {
            menuTitle = getResources().getStringArray(R.array.menu_array_technician);
            menuIcon = getResources().obtainTypedArray(R.array.menu_icon_array_technician);
            menuIsTitle = getResources().getStringArray(R.array.menu_is_title_technician);
        } else if(userType == Constants.KADEP_TS) {
            menuTitle = getResources().getStringArray(R.array.menu_array_kadep_ts);
            menuIcon = getResources().obtainTypedArray(R.array.menu_icon_array_kadep_ts);
            menuIsTitle = getResources().getStringArray(R.array.menu_is_title_kadep_ts);
        } else if(userType == Constants.KADEP_INFRA) {
            menuTitle = getResources().getStringArray(R.array.menu_array_kadep_infra);
            menuIcon = getResources().obtainTypedArray(R.array.menu_icon_array_kadep_infra);
            menuIsTitle = getResources().getStringArray(R.array.menu_is_title_kadep_infra);
        } else if(userType == Constants.ENGINEER) {
            menuTitle = getResources().getStringArray(R.array.menu_array_engineer);
            menuIcon = getResources().obtainTypedArray(R.array.menu_icon_array_engineer);
            menuIsTitle = getResources().getStringArray(R.array.menu_is_title_engineer);
        } else if(userType == Constants.KST) {
            menuTitle = getResources().getStringArray(R.array.menu_array_kst);
            menuIcon = getResources().obtainTypedArray(R.array.menu_icon_array_kst);
            menuIsTitle = getResources().getStringArray(R.array.menu_is_title_kst);
        } else if(userType == Constants.KORWIL) {
            menuTitle = getResources().getStringArray(R.array.menu_array_korwil);
            menuIcon = getResources().obtainTypedArray(R.array.menu_icon_array_korwil);
            menuIsTitle = getResources().getStringArray(R.array.menu_is_title_korwil);
        } else if(userType == Constants.KADEP_WIL) {
            menuTitle = getResources().getStringArray(R.array.menu_array_kadeptn);
            menuIcon = getResources().obtainTypedArray(R.array.menu_icon_array_kadeptn);
            menuIsTitle = getResources().getStringArray(R.array.menu_is_title_kadeptn);
        } else if(userType == Constants.KADIV || userType == Constants.CBTO) {
            menuTitle = getResources().getStringArray(R.array.menu_array_kadiv);
            menuIcon = getResources().obtainTypedArray(R.array.menu_icon_array_kadiv);
            menuIsTitle = getResources().getStringArray(R.array.menu_is_title_kadiv);
        } else {
            menuTitle = getResources().getStringArray(R.array.menu_array_kst);
            menuIcon = getResources().obtainTypedArray(R.array.menu_icon_array_kst);
            menuIsTitle = getResources().getStringArray(R.array.menu_is_title_kst);
        }

        MenuAdapter adapter = MenuAdapter.get_instance(getActivity(), R.layout.menu_row_list);
        for (int i = 0; i < this.menuTitle.length; i++) {
            if (menuIsTitle[i].equalsIgnoreCase("true")) {
                adapter.add(new MenuModel(menuTitle[i], menuIcon.getResourceId(i, -1), true));
            } else {
                adapter.add(new MenuModel(menuTitle[i], menuIcon.getResourceId(i, -1), false));
            }
        }

        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position != 0) {
                    mDrawerList.setItemChecked(position, true);
                    routePage(userType, position);
                }
            }
        });

        UserLoginModel userSession = sessionManager.getUserLoginModel();

        int position = 0;
        try {
            userLoginModels = UserDAO.readAll(-11, -11);
            for (int i = 0 ; i < userLoginModels.size(); i++) {
                UserLoginModel userLoginModel = userLoginModels.get(i);
                if (userLoginModel.getPositionId() == Constants.TECHNICIAN || userLoginModel.getPositionId() == Constants.KST) {
                    userList.add(userLoginModel.getPositionName() + " " + userLoginModel.getStationName());
                } else if (userLoginModel.getPositionId() == Constants.KORWIL) {
                    userList.add(userLoginModel.getPositionName() + " " + userLoginModel.getRegionName());
                } else if (userLoginModel.getPositionId() == Constants.KADEP_WIL) {
                    userList.add(userLoginModel.getPositionName() + " " + userLoginModel.getDepartmentName());
                } else {
                    userList.add(userLoginModel.getPositionName());
                }
                if (userSession.getIdUpdrs().equals(userLoginModel.getIdUpdrs())) {
                    position = i;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getActivity(),
                R.layout.support_simple_spinner_dropdown_item, userList);
        userSpinner.setAdapter(adapterSpinner);
        Log.d(TAG, "userList" + userLoginModels.size());
        userSpinner.setSelection(position);
        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            boolean first_trigger = true;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(first_trigger){
                    first_trigger = false;
                } else {
                    UserLoginModel newSession = userLoginModels.get(position);
                    sessionManager.createLoginSession(newSession);
//                    getActivity().recreate();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
//                    getActivity().finish();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerlayout, toolbar, 0, 0) {
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!islearned) {
                    islearned = true;
                    prefs.savePreferences(Constants.IS_LEARNED, islearned);
                }
            }

            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    private void routePage(int userType, int index) {
        String page = null;
        Fragment pageFragment = null;
        Boolean flag = Boolean.FALSE;
        if (mDrawerLayout.isDrawerOpen(containerView)) {
            mDrawerLayout.closeDrawer(containerView);
        }
//        Bundle args;
        if(userType == Constants.TECHNICIAN) {
            switch (index) {
                case 1:
                    pageFragment = new Home();
                    page = Constants.HOME_PAGE;
                    break;
                case 2:
                    intent = new Intent(getActivity(), DownTimeActivity.class);
                    getActivity().startActivityForResult(intent, Constants.REQUEST_NEW_TICKET);
                    break;
                case 3:
                    intent = new Intent(getActivity(), KerusakanActivity.class);
                    getActivity().startActivityForResult(intent, Constants.REQUEST_NEW_TICKET);
                    break;
                case 4:
                    intent = new Intent(getActivity(), GangguanActivity.class);
                    getActivity().startActivityForResult(intent, Constants.REQUEST_NEW_TICKET);
                    break;
                case 5:
//                    pageFragment = new MyTicketNew();
                    pageFragment = new MyTicketV2();
                    page = Constants.TT_ACTIVITY_PAGE;
                    break;
                default:
                    break;
            }
        } else if(userType == Constants.KST) {
            switch (index) {
                case 1:
//                    pageFragment = new HomeKST();
                    pageFragment = new HomeV2();
                    page = Constants.HOME_PAGE;
                    break;

                case 2:
//                    pageFragment = new MyTicketNew();
                    pageFragment = new MyTicketV2();
                    page = Constants.TT_ACTIVITY_PAGE;
                    break;
                case 3:
                    pageFragment = new MyApproval();
                    page = Constants.MY_APPROVAL_PAGE;
                    break;
                default:
                    break;
            }
        } else if(userType == Constants.KORWIL) {
            switch (index) {
                case 1:
//                    pageFragment = new HomeKorwil();
                    pageFragment = new HomeV2();
                    page = Constants.HOME_PAGE;
                    break;
                case 2:
//                    pageFragment = new MyTicketNew();
                    pageFragment = new MyTicketV2();
                    page = Constants.TT_ACTIVITY_PAGE;
                    break;
                default:
                    break;
            }
        }
        else if(userType == Constants.KADEP_WIL) {
            switch (index) {
                case 1:
//                    pageFragment = new HomeKadepwil();
                    pageFragment = new HomeV2();
                    page = Constants.HOME_PAGE;
                    break;
                case 2:
                    intent = new Intent(getActivity(), DownTimeSpcActivity.class);
                    getActivity().startActivityForResult(intent, Constants.REQUEST_NEW_TICKET);
                    break;
                case 3:
                    intent = new Intent(getActivity(), KerusakanSpcActivity.class);
                    getActivity().startActivityForResult(intent, Constants.REQUEST_NEW_TICKET);
                    break;
                case 4:
//                    pageFragment = new MyTicketNew();
                    pageFragment = new MyTicketV2();
                    page = Constants.TT_ACTIVITY_PAGE;
                    break;
                default:
                    break;
            }
        }
        else if(userType == Constants.KADEP_TS) {
            switch (index) {
                case 1:
                    pageFragment = new HomeKadepTSV2();
                    page = Constants.HOME_PAGE;
                    break;
                case 2:
//                    pageFragment = new MyTicketNew();
                    pageFragment = new MyTicketV2();
                    page = Constants.ASSIGNMENT_PAGE;
                    break;
                case 3:
                    pageFragment = new RequestVisit();
                    page = Constants.REQUEST_VISIT_PAGE;
                    break;

                default:
                    break;
            }
        }
        else if(userType == Constants.KADEP_INFRA) {
            /*switch (index) {
                case 1:
                    pageFragment = new HomeKadepInfra();
                    page = Constants.HOME_PAGE;
                    break;
                case 2:
                    pageFragment = new MyTicketV2();
                    page = Constants.ASSIGNMENT_PAGE;
                    break;
                case 3:
                    pageFragment = new RequestVisit();
                    page = Constants.REQUEST_VISIT_PAGE;
                    break;

                default:
                    break;
            }*/

            switch (index) {
                case 1:
                    pageFragment = new HomeKadepInfra();
                    page = Constants.HOME_PAGE;
                    break;
                case 2:
                    pageFragment = new MyTicketV2();
                    page = Constants.TT_ACTIVITY_PAGE;
                    break;
                default:
                    break;
            }
        }
        else if(userType == Constants.ENGINEER) {
            switch (index) {
                case 1:
//                    pageFragment = new HomeEngineer();
                    pageFragment = new HomeEngineerV2();
                    page = Constants.HOME_PAGE;
                    break;
                case 2:
//                    pageFragment = new MyAssignment();
                    pageFragment = new MyAssignmentV2();
                    page = Constants.TT_ACTIVITY_PAGE;
                    break;
                case 3:
                    pageFragment = new MyVisit();
                    page = Constants.MY_VISIT_PAGE;
                    break;
                default:
                    break;
            }
        } else if(userType == Constants.KADIV) {
            /*switch (index) {
                case 1:
                    pageFragment = new HomeKadiv();
                    page = Constants.HOME_PAGE;
                    break;
                case 2:
                    pageFragment = new StatisticFragment();
                    page = Constants.TT_STATISTICS;
                    break;
                case 3:
                    pageFragment = new TopTenFragment();
                    page = Constants.TT_TOP_TEN;
                    break;
                case 4:
                    pageFragment = new ToDoListFragment();
                    page = Constants.TT_NEWEST_TICKET;
                    break;
                case 5:
                    pageFragment = new ACNielsenFragment();
                    page = Constants.TT_AC_NIELASEN;
                    break;
                case 6:
                    pageFragment = new NonACNielsenFragment();
                    page = Constants.TT_NON_AC_NIELASEN;
                    break;
                default:
                    break;
            }*/
            switch (index) {
                case 1:
                    pageFragment = new HomeKadiv();
                    page = Constants.HOME_PAGE;
                    break;
                case 2:
                    pageFragment = new StatisticFragment();
                    page = Constants.TT_STATISTICS;
                    break;
                case 3:
                    pageFragment = new ToDoListFragment();
                    page = Constants.TT_NEWEST_TICKET;
                    break;
                case 4:
                    pageFragment = new ACNielsenFragment();
                    page = Constants.TT_AC_NIELASEN;
                    break;
                case 5:
                    pageFragment = new NonACNielsenFragment();
                    page = Constants.TT_NON_AC_NIELASEN;
                    break;
                case 6:
                    pageFragment = new OchannelFragment();
                    page = Constants.TT_O_CHANNEL;
                    break;
                case 7:
                    pageFragment = new NexMediaFragment();
                    page = Constants.TT_NEXMEDIA;
                    break;
                default:
                    break;
            }
        } else if(userType == Constants.CBTO) {
            /*switch (index) {
                case 1:
                    pageFragment = new HomeKadiv();
                    page = Constants.HOME_PAGE;
                    break;
                case 2:
                    pageFragment = new StatisticFragment();
                    page = Constants.TT_STATISTICS;
                    break;
                case 3:
                    pageFragment = new TopTenFragment();
                    page = Constants.TT_TOP_TEN;
                    break;
                case 4:
                    pageFragment = new ToDoListFragment();
                    page = Constants.TT_NEWEST_TICKET;
                    break;
                case 5:
                    pageFragment = new ACNielsenFragment();
                    page = Constants.TT_AC_NIELASEN;
                    break;
                case 6:
                    pageFragment = new NonACNielsenFragment();
                    page = Constants.TT_NON_AC_NIELASEN;
                    break;
                default:
                    break;
            }*/
            switch (index) {
                case 1:
                    pageFragment = new HomeKadiv();
                    page = Constants.HOME_PAGE;
                    break;
                case 2:
                    pageFragment = new StatisticFragment();
                    page = Constants.TT_STATISTICS;
                    break;
                case 3:
                    pageFragment = new ToDoListFragment();
                    page = Constants.TT_NEWEST_TICKET;
                    break;
                case 4:
                    pageFragment = new ACNielsenFragment();
                    page = Constants.TT_AC_NIELASEN;
                    break;
                case 5:
                    pageFragment = new NonACNielsenFragment();
                    page = Constants.TT_NON_AC_NIELASEN;
                    break;
                default:
                    break;
            }
        }
        if (pageFragment != null) {
            this.mListener.onMenuSelected(page, pageFragment, flag);
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void invalidateMenu(int position) {
        mDrawerList.setItemChecked(position, true);
    }

    public void clearChoice() {
        mDrawerList.clearChoices();
    }
}
