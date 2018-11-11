package com.cudocomm.troubleticket.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.adapter.ViewPagerAdapter;
import com.cudocomm.troubleticket.component.CustomPopConfirm;
import com.cudocomm.troubleticket.component.PopupConfirmTicket;
import com.cudocomm.troubleticket.model.Ticket;
import com.cudocomm.troubleticket.model.TicketLog;
import com.cudocomm.troubleticket.util.ApiClient;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import okhttp3.FormBody;

public class MyApprovalDetail extends BaseFragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private View rootView;

    private SliderLayout photoPreviewLayout;
    private TabLayout tabLayout;
    private ViewPager contentPager;
    private ViewPagerAdapter viewPagerAdapter;

    private Ticket selectedTicket;
    private int selectedTicketPosition;
    private Ticket returnTicket;
    private List<TicketLog> ticketLogs;

    private RelativeLayout actionLayout;
    private LinearLayout actionLL;
    private Button approveBtn;

    private CustomPopConfirm popConfirm;
    private PopupConfirmTicket popupConfirmTicket;
    private SpotsDialog progressDialog;

    private String additionalInfo;

    public MyApprovalDetail() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedTicket = (Ticket) getArguments().getSerializable(Constants.SELECTED_TICKET);
            selectedTicketPosition = getArguments().getInt(Constants.SELECTED_TICKET_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_approval_detail, container, false);

        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.approvalRootView);
        if (fragment != null) {
            fragmentManager.beginTransaction().detach(fragment).commit();
            fragmentManager.executePendingTransactions();
        }

        initComponent();
        new MyApprovalDetailTask().execute();

        return rootView;
    }

    private void initComponent() {
        progressDialog = new SpotsDialog(getContext(), R.style.progress_dialog_style);
        photoPreviewLayout = (SliderLayout) rootView.findViewById(R.id.photoPreviewLayout);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        contentPager = (ViewPager) rootView.findViewById(R.id.contentPager);

        actionLayout = (RelativeLayout) rootView.findViewById(R.id.actionLayout);
        actionLL = (LinearLayout) rootView.findViewById(R.id.actionLL);
        approveBtn = (Button) rootView.findViewById(R.id.approveBtn);
    }

    private void updateComponent() {
        if(!selectedTicket.getTicketPhoto1().isEmpty() && !selectedTicket.getTicketPhoto1().equals("") && !selectedTicket.getTicketPhoto1().equals("null")) {
            TextSliderView textSliderView = new TextSliderView(context);
            String desc = "Photo 1 - " + selectedTicket.getTicketNo();
            textSliderView
                    .description(desc)
                    .image(CommonsUtil.getAbsoluteUrlImage(selectedTicket.getTicketPhoto1()))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", desc);

            photoPreviewLayout.addSlider(textSliderView);
        }

        if(!selectedTicket.getTicketPhoto2().isEmpty() && !selectedTicket.getTicketPhoto2().equals("") && !selectedTicket.getTicketPhoto2().equals("null")) {
            TextSliderView textSliderView = new TextSliderView(context);
            String desc = "Photo 2 - " + selectedTicket.getTicketNo();
            textSliderView
                    .description(desc)
                    .image(CommonsUtil.getAbsoluteUrlImage(selectedTicket.getTicketPhoto2()))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", desc);

            photoPreviewLayout.addSlider(textSliderView);
        }

        if(!selectedTicket.getTicketPhoto3().isEmpty() && !selectedTicket.getTicketPhoto3().equals("") && !selectedTicket.getTicketPhoto3().equals("null")) {
            TextSliderView textSliderView = new TextSliderView(context);
            String desc = "Photo 3 - " + selectedTicket.getTicketNo();
            textSliderView
                    .description(desc)
                    .image(CommonsUtil.getAbsoluteUrlImage(selectedTicket.getTicketPhoto3()))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", desc);

            photoPreviewLayout.addSlider(textSliderView);
        }



        photoPreviewLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
        photoPreviewLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        photoPreviewLayout.setCustomAnimation(new DescriptionAnimation());
        photoPreviewLayout.setDuration(8000);
        photoPreviewLayout.addOnPageChangeListener(this);

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.color_btn_negative));
        tabLayout.setTabTextColors(getResources().getColor(R.color.color_white), getResources().getColor(R.color.color_home_header));



        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        Map<String, Object> maps = new HashMap<>();
        maps.put(Constants.SELECTED_TICKET, selectedTicket);
        maps.put(Constants.TICKET_LOGS, ticketLogs);

        viewPagerAdapter.addFragment(new TicketInfoFragment().newInstance(selectedTicket), "Ticket Info");
        viewPagerAdapter.addFragment(new TicketHistoryFragment().newInstance(maps), "History");


        contentPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(contentPager);


        approveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupConfirmTicket = PopupConfirmTicket.newInstance("Confirm Ticket","Process","Back");
                popupConfirmTicket.setBackListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupConfirmTicket.dismiss();
                    }
                });
                popupConfirmTicket.setProcessListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        additionalInfo = popupConfirmTicket.getTicketInfoET().getText().toString();
                        if(TextUtils.isEmpty(additionalInfo)) {
                            popupConfirmTicket.getTicketInfoET().requestFocus();
                            popupConfirmTicket.getTicketInfoET().setError(getResources().getString(R.string.error_close_info));
                        } else {
                            popupConfirmTicket.dismiss();
                            new ApproveClosedTicketTask().execute();
                        }

                    }
                });
                popupConfirmTicket.show(getActivity().getFragmentManager(), null);

                /*popConfirm = CustomPopConfirm.newInstance(
                        getResources().getString(R.string.popup_approval_title),
                        getResources().getString(R.string.popup_approval_message, CommonsUtil.ticketTypeToString(selectedTicket.getTicketType()), selectedTicket.getStationName(), selectedTicket.getSuspect1Name(), CommonsUtil.severityToString(selectedTicket.getTicketSeverity())),
                        "Process","Back");
                popConfirm.setBackListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popConfirm.dismiss();
                    }
                });
                popConfirm.setProcessListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popConfirm.dismiss();
                        new ApproveClosedTicketTask().execute();
                    }
                });
                popConfirm.show(getActivity().getFragmentManager(), null);*/
            }
        });

    }


    private class MyApprovalDetailTask extends AsyncTask<Void, Void, Void> {

        String result;
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(CommonsUtil.getAbsoluteUrl(Constants.URL_TICKET_HISTORY),
                        new FormBody.Builder().add(Constants.PARAM_TICKET_NO, selectedTicket.getTicketNo()).build());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Type type = new TypeToken<List<TicketLog>>(){}.getType();
            try {
                JSONObject object = new JSONObject(result);
                ticketLogs = gson.fromJson(object.getString("data"), type);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            updateComponent();

            progressDialog.dismiss();
        }
    }

    class ApproveClosedTicketTask extends AsyncTask<Void, Void, Void> {

        String result = "";
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gsona = this.gsonBuilder.create();

        JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(
                        CommonsUtil.getAbsoluteUrl("submitapproval"),
                        new FormBody.Builder()
                                .add(Constants.PARAM_TICKET_ID, selectedTicket.getTicketId())
                                .add(Constants.PARAM_TICKET_CONFIRM_BY, String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                                .add(Constants.PARAM_INFO, additionalInfo)
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
                JSONObject object = new JSONObject(result);
                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {

                    /*String page = Constants.MY_APPROVAL_PAGE;
                    Boolean flag = Boolean.valueOf(false);
                    Fragment f = new MyApproval();

                    preferences.savePreferences(Constants.ACTIVE_PAGE, page);
                    mListener.onMenuSelected(page, f, flag);*/

                    /*RecyclerView list = (RecyclerView) getActivity().findViewById(R.id.ticketListRV);
                    CloseTicketAdapter adapter = (CloseTicketAdapter) list.getAdapter();
                    List<CloseTicket> closeTickets = adapter.getmDataset();
                    closeTickets.remove(selectedTicket);
                    adapter.setmDataset(closeTickets);
                    adapter.notifyDataSetChanged();
                    list.setAdapter(adapter);*/

//                    RecyclerView ticketListRV = (RecyclerView) getActivity().findViewById(R.id.ticketListRV);
//                    ((CloseTicketAdapter) ticketListRV.getAdapter()).getmDataset().remove(selectedTicketPosition);
//                    ((CloseTicketAdapter) ticketListRV.getAdapter()).notifyDataSetChanged();


//                    getActivity().onBackPressed();

//                    RecyclerView ticketListRV = (RecyclerView) getActivity().findViewById(R.id.approvalTicketListRV);
                    MyApproval f = new MyApproval();
                    String page = Constants.MY_APPROVAL_PAGE;
                    Boolean flag = Boolean.FALSE;
                    preferences.savePreferences(Constants.ACTIVE_PAGE, page);
                    mListener.onMenuSelected(page, f, flag);

                    progressDialog.dismiss();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(context,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

}
