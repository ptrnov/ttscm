package com.cudocomm.troubleticket.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.TTSApplication;
import com.cudocomm.troubleticket.adapter.ViewPagerAdapter;
import com.cudocomm.troubleticket.component.CustomPopConfirm;
import com.cudocomm.troubleticket.component.PopupAssignToTicket;
import com.cudocomm.troubleticket.component.PopupAssignmentTicket;
import com.cudocomm.troubleticket.component.PopupReAssignmentTicket;
import com.cudocomm.troubleticket.component.PopupApprovalReplacment;
import com.cudocomm.troubleticket.component.PopupCloseTicket;
import com.cudocomm.troubleticket.component.PopupCloseTicketCustom;
import com.cudocomm.troubleticket.component.PopupCloseTicketCustomReplacment;
import com.cudocomm.troubleticket.component.PopupCloseTicketV2;
import com.cudocomm.troubleticket.component.PopupEscalationTicket;
import com.cudocomm.troubleticket.component.PopupGuidanceTicket;
import com.cudocomm.troubleticket.fragment.TicketHistoryFragment;
import com.cudocomm.troubleticket.fragment.TicketInfoFragment;
import com.cudocomm.troubleticket.model.Ticket;
import com.cudocomm.troubleticket.model.TicketLog;
import com.cudocomm.troubleticket.model.UserModel;
import com.cudocomm.troubleticket.util.ApiClient;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Logcat;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import dmax.dialog.SpotsDialog;
import fr.ganfra.materialspinner.MaterialSpinner;
import okhttp3.FormBody;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TicketActivity extends BaseActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    public static final String TAG = TicketActivity.class.getSimpleName();

    @BindView(R.id.photoPreviewLayout)
    SliderLayout photoPreviewLayout;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.contentPager)
    ViewPager contentPager;
    private ViewPagerAdapter viewPagerAdapter;

    private Ticket returnTicket;
    private Ticket selectedTicket;
    private List<TicketLog> ticketLogs;

    @BindView(R.id.actionLayout)
    RelativeLayout actionLayout;
    @BindView(R.id.actionLL)
    LinearLayout actionLL;
    @BindView(R.id.actionKadepTSLL)
    LinearLayout actionKadepTSLL;
    @BindView(R.id.actionAssign)
//     LinearLayout actionApprovedRep;
//    @BindView(R.id.actionApprovedRep)
    LinearLayout actionAssign;
    @BindView(R.id.escalatedBtn)
    Button escalatedBtn;
    @BindView(R.id.closedBtn)
    Button closedBtn;
    @BindView(R.id.assignmentBtn)
    Button assignmentBtn;
    @BindView(R.id.closedAssignmentBtn)
    Button closedAssignmentBtn;
    @BindView(R.id.assignmentToBtn)
    Button assignmentToBtn;
    @BindView(R.id.assignmentToBtn2)
    Button assignmentToBtn2;
    @BindView(R.id.guidanceBtn)
    Button guidanceBtn;
    @BindView(R.id.guidanceBtnKadepTs)
    Button guidanceBtnKadepTs;
    @BindView(R.id.replacemmentApprovedBtn)
    Button replacemmentApprovedBtn;

    private CustomPopConfirm popConfirm;
    private PopupApprovalReplacment popupApprovalReplacment;
    private PopupCloseTicket popupCloseTicket;
    private PopupCloseTicketV2 popupCloseTicketV2;
    private PopupCloseTicketCustom popupCloseTicketCustom;
    private PopupCloseTicketCustomReplacment popupCloseTicketCustomReplacment;
    private PopupEscalationTicket popupEscalationTicket;
    private PopupAssignmentTicket popupAssignmentTicket;
    private PopupReAssignmentTicket popupReAssignmentTicket;
    private PopupAssignToTicket popupAssignToTicket;
    private PopupGuidanceTicket popupGuidanceTicket;

    public int closedType = 1;
    public String assignType;
    private SpotsDialog progressDialog;

    private String additionalInfo;
    String prNo;
    String replacmentNo;
    String partNo;
    String serialNo;
    private String actionDescribe;
    private String requireSupport;
    private String assignDate, assignAction;
    private String penyebab, program;
    private String startTime, duration;
    private String closedTypes;
    private int totalSassign;
    private String sassignSerializer;
    private String reasonAssign;
    private String prNoomor;
//    private String ticketInfoET;
    private EditText ticketInfoET;

    private List<UserModel> engineers;
    private CustomPopConfirm confDialog;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        selectedTicket = (Ticket) bundle.getSerializable(Constants.SELECTED_TICKET);

        initComponent();
//        updateComponent();
        new TicketTask().execute();
    }

    private void initComponent() {
//        setTitle("Ticket Information");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new SpotsDialog(this, R.style.progress_dialog_style);
            try {
                sassignSerializer = ApiClient.post(
                        CommonsUtil.getAbsoluteUrl("cek_total_assign"),
                        new FormBody.Builder()
                                .add(Constants.PARAM_TICKET_ID, selectedTicket.getTicketId())
                                .build());
                JSONObject obj = null;
                try {
                    obj = new JSONObject(sassignSerializer);
                    Log.d(TAG, "check_assign: " +obj.getString("assign"));
                    totalSassign=Integer.parseInt(obj.getString("assign"));
                    if (totalSassign > 0) {
                        assignmentBtn.setText("ReAssignment");
                    }else{
                        assignmentBtn.setText("Assignment");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

//        Date currentTime = Calendar.getInstance().getTime();

//        DateFormat dateFormatter = onNewIntent();
//        dateFormatter = new SimpleDateFormat("yyyyMMdd hhmmss");
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
//        dateFormatter.setLenient(false);
//        Date today = new Date();
//        String s = dateFormatter.format(today);
    }

    private void updateComponent() {
        Logcat.d("chk_status : " + selectedTicket.getTicketStatus());
        if(selectedTicket.getTicketStatus() == 1) {
            if(preferences.getPreferencesInt(Constants.ID_UPDRS) == selectedTicket.getTicketPosition()) {
//                if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_TS || preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_INFRA) {
                if (selectedTicket.getHasAssign().equals("1")){
                    actionLayout.setVisibility(View.VISIBLE);
                    actionLL.setVisibility(View.GONE);
                    actionKadepTSLL.setVisibility(View.GONE);
                    actionAssign.setVisibility(View.VISIBLE);
                    assignmentToBtn.setVisibility(View.VISIBLE);
                    replacemmentApprovedBtn.setVisibility(View.GONE);
//                    guidanceBtn.setVisibility(View.GONE);
                }else {
                    if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_TS) {
                        actionLayout.setVisibility(View.VISIBLE);
                        actionLL.setVisibility(View.GONE);
                        assignmentToBtn2.setVisibility(View.GONE);
                        actionKadepTSLL.setVisibility(View.VISIBLE);
                    } else if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_INFRA) {
                        actionLayout.setVisibility(View.VISIBLE);
                        actionLL.setVisibility(View.VISIBLE);
                        actionKadepTSLL.setVisibility(View.GONE);
                    }else if (preferences.getPreferencesInt(Constants.POSITION_ID) == 1){
                        guidanceBtn.setVisibility(View.GONE);
                        actionLayout.setVisibility(View.VISIBLE);
                        actionLL.setVisibility(View.VISIBLE);
                        actionKadepTSLL.setVisibility(View.GONE);
                        assignmentToBtn2.setVisibility(View.GONE);
                    }else {
                        actionLayout.setVisibility(View.VISIBLE);
                        actionLL.setVisibility(View.VISIBLE);
                        actionKadepTSLL.setVisibility(View.GONE);
                        assignmentToBtn2.setVisibility(View.GONE);

                        if(selectedTicket.getTicketType() == 3) {
                            escalatedBtn.setVisibility(View.GONE);
                        }
                    }
                }

            }  else {
                actionLayout.setVisibility(View.GONE);
            }
        } else if (selectedTicket.getTicketStatus() == 2){
            actionLayout.setVisibility(View.VISIBLE);
            actionAssign.setVisibility(View.VISIBLE);
//            if (selectedTicket.getAssetNno()!=null){
            if(!String.valueOf(selectedTicket.getAssetNno()).isEmpty()){
                if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_TS) {
                    replacemmentApprovedBtn.setVisibility(View.VISIBLE);
                    assignmentToBtn.setVisibility(View.GONE);
                } else if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_INFRA) {
                    replacemmentApprovedBtn.setVisibility(View.VISIBLE);
                    assignmentToBtn.setVisibility(View.GONE);
                }else{
                    assignmentToBtn.setVisibility(View.GONE);
                    replacemmentApprovedBtn.setVisibility(View.GONE);
                }
            }else{
                if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_TS) {
                    assignmentToBtn.setVisibility(View.VISIBLE);
                    replacemmentApprovedBtn.setVisibility(View.GONE);
                } else if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_INFRA) {
                    assignmentToBtn.setVisibility(View.VISIBLE);
                    replacemmentApprovedBtn.setVisibility(View.GONE);
                }else{
                    assignmentToBtn.setVisibility(View.GONE);
                    replacemmentApprovedBtn.setVisibility(View.GONE);
                }
            }

        }else {
            actionLayout.setVisibility(View.GONE);
        }

        Logcat.d("tiketphoto : " + selectedTicket.getTicketPhoto1());
        if(selectedTicket.getTicketPhoto1() !=null) {
            if (!selectedTicket.getTicketPhoto1().isEmpty()) {
                TextSliderView textSliderView = new TextSliderView(getApplicationContext());
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
        }

        if(selectedTicket.getTicketPhoto2() !=null) {
            if (!selectedTicket.getTicketPhoto2().isEmpty()) {
                TextSliderView textSliderView = new TextSliderView(getApplicationContext());
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
        }

        if(selectedTicket.getTicketPhoto3() !=null) {
            if (!selectedTicket.getTicketPhoto3().isEmpty()) {
                TextSliderView textSliderView = new TextSliderView(getApplicationContext());
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
        }

        photoPreviewLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
        photoPreviewLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        photoPreviewLayout.setCustomAnimation(new DescriptionAnimation());
        photoPreviewLayout.setDuration(8000);
        photoPreviewLayout.addOnPageChangeListener(this);

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.color_btn_negative));
        tabLayout.setTabTextColors(getResources().getColor(R.color.color_home_header), getResources().getColor(R.color.color_white));

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        Map<String, Object> maps = new HashMap<>();
        maps.put(Constants.SELECTED_TICKET, selectedTicket);
        maps.put(Constants.TICKET_LOGS, ticketLogs);

        viewPagerAdapter.addFragment(new TicketInfoFragment().newInstance(selectedTicket), "Ticket Info");
        viewPagerAdapter.addFragment(new TicketHistoryFragment().newInstance(maps), "History");

        contentPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(contentPager);
    }

    @Optional
    @OnClick({R.id.closedBtn, R.id.escalatedBtn, R.id.assignmentBtn, R.id.closedAssignmentBtn, R.id.assignmentToBtn, R.id.assignmentToBtn2,R.id.guidanceBtn, R.id.guidanceBtnKadepTs, R.id.replacemmentApprovedBtn})
    public void onActionPage(View view) {
        if(view.getId() == R.id.closedBtn) {
            if(selectedTicket.getTicketType() == 1 || selectedTicket.getTicketType() == 2) {
                if (preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.TECHNICIAN
                        || preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KST
                        || preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KORWIL){
                            popupCloseTicket = PopupCloseTicket.newInstance("Close Ticket","Process","Back");
                            popupCloseTicket.setBackListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupCloseTicket.dismiss();
                                }
                            });
                            popupCloseTicket.setProcessListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    additionalInfo = popupCloseTicket.getTicketInfoET().getText().toString();
                                    if(TextUtils.isEmpty(additionalInfo)) {
                                        popupCloseTicket.getTicketInfoET().requestFocus();
                                        popupCloseTicket.getTicketInfoET().setError(getResources().getString(R.string.error_close_info));
                                    } else {
                                        String title = "Submission Confirmation";
                                        String msg = "You will closed " + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType()) +" incident with detail : \nLocation : "+
                                                selectedTicket.getStationName() +
                                                "\nSuspect : " + selectedTicket.getSuspect1Name() + " - " + selectedTicket.getSuspect2Name() + " - " + selectedTicket.getSuspect3Name() +
                                                "\nSeverity : " + CommonsUtil.severityToString(selectedTicket.getTicketSeverity());
                                        confDialog = CustomPopConfirm.newInstance(title,msg,"Yes","No");
                                        confDialog.setBackListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                confDialog.dismiss();
                                            }
                                        });
                                        confDialog.setProcessListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                popupCloseTicket.dismiss();
                                                confDialog.dismiss();
                                                new ClosedTicketTask().execute();

                                            }
                                        });
                                        confDialog.show(getFragmentManager(), null);
                                    }

                                }
                            });
                            popupCloseTicket.show(getFragmentManager(), null);
                    }else if (preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_WIL){
                    //KADEP TN
                            popupCloseTicketCustom = PopupCloseTicketCustom.newInstance("Close Ticket","Process","Back");
                            popupCloseTicketCustom.setBackListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupCloseTicketCustom.dismiss();
                                }
                            });
                            popupCloseTicketCustom.setProcessListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    additionalInfo = popupCloseTicketCustom.getTicketInfoET().getText().toString();
                                    prNo = popupCloseTicketCustom.getPrNoET().getText().toString();
                                    replacmentNo = popupCloseTicketCustom.getReplacmentNo().getText().toString();
                                    partNo = popupCloseTicketCustom.getPartNo().getText().toString();
                                    serialNo = popupCloseTicketCustom.getSerialNo().getText().toString();
                                    Log.d("addt11111",String.valueOf(prNo));
                                    String regexStr = "^[0-9]*$";
                                     if (popupCloseTicketCustom.getStringSpinnerItem().getSelectedItemPosition()==2) {
                                        if (additionalInfo.equals("")) {
                                            popupCloseTicketCustom.getTicketInfoET().setError(getResources().getString(R.string.error_close_info));
                                        }else if(prNo.length()!=9){
                                            popupCloseTicketCustom.getPrNoET().requestFocus();
                                            popupCloseTicketCustom.getPrNoET().setError(getResources().getString(R.string.error_prNo_action_digit));
                                        }else if(!prNo.trim().matches(regexStr)){
                                            popupCloseTicketCustom.getPrNoET().requestFocus();
                                            popupCloseTicketCustom.getPrNoET().setError(getResources().getString(R.string.error_prNo_action_number));
                                        }else{
                                            String title = "Submission Confirmation";
                                            String msg =
                                                    "You will Close \""
                                                            + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType())
                                                            + "\" ticket with detail : \nLocation : "
                                                            + selectedTicket.getStationName()
                                                            + "\nSuspect : "
                                                            + selectedTicket.getSuspect1Name()
                                                            + " - "
                                                            + selectedTicket.getSuspect2Name()
                                                            + " - "
                                                            + selectedTicket.getSuspect3Name()
                                                            + "\nSeverity : "
                                                            + CommonsUtil.severityToString(selectedTicket.getTicketSeverity())
                                                            + "\nPR.No : "
                                                            + prNo.toString();

                                            popConfirm = CustomPopConfirm.newInstance(title, msg, "Yes", "No");
                                            popConfirm.setBackListener(
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            popConfirm.dismiss();
                                                        }
                                                    });
                                            popConfirm.setProcessListener(
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            popupCloseTicketCustom.dismiss();
                                                            popConfirm.dismiss();

                                                            new ClosedTicketTaskKERUSAKAN_WITHPR().execute();
                                                        }
                                                    });
                                            popConfirm.show(getFragmentManager(), null);

                                        }
//                                    //CLOSE BIASA
                                    }else if (popupCloseTicketCustom.getStringSpinnerItem().getSelectedItemPosition()==1) {
                                        if (additionalInfo.equals("")) {
                                            popupCloseTicketCustom.getTicketInfoET().setError(getResources().getString(R.string.error_close_info));
                                        }else {
                                            String title = "Submission Confirmation";
                                            String msg =
                                                    "You will Close \""
                                                            + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType())
                                                            + "\" ticket with detail : \nLocation : "
                                                            + selectedTicket.getStationName()
                                                            + "\nSuspect : "
                                                            + selectedTicket.getSuspect1Name()
                                                            + " - "
                                                            + selectedTicket.getSuspect2Name()
                                                            + " - "
                                                            + selectedTicket.getSuspect3Name()
                                                            + "\nSeverity : "
                                                            + CommonsUtil.severityToString(selectedTicket.getTicketSeverity());

                                            popConfirm = CustomPopConfirm.newInstance(title, msg, "Yes", "No");
                                            popConfirm.setBackListener(
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            popConfirm.dismiss();
                                                        }
                                                    });
                                            popConfirm.setProcessListener(
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            popupCloseTicketCustom.dismiss();
                                                            popConfirm.dismiss();

                                            new ClosedTicketTaskKERUSAKAN_WITHPR().execute();

                                                        }
                                                    });
                                            popConfirm.show(getFragmentManager(), null);
                                        }

                                    } else {
//                                        popupCloseTicketCustom.setTicketInfoETHint("test piter");
                                        popupCloseTicketCustom.getFixTypeSpinner().requestFocus();
                                        popupCloseTicketCustom.getFixTypeSpinner().setError(getResources().getString(R.string.error_close_type));
                                    }
                                }
                            });
                            popupCloseTicketCustom.show(getFragmentManager(), null);

                    }else if (preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_INFRA){

                            popupCloseTicketCustomReplacment = PopupCloseTicketCustomReplacment.newInstance("Close Ticket","Process","Back");
                            popupCloseTicketCustomReplacment.setBackListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupCloseTicketCustomReplacment.dismiss();
                                }
                            });
                            popupCloseTicketCustomReplacment.setProcessListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    additionalInfo = popupCloseTicketCustomReplacment.getTicketInfoET().getText().toString();
                                    prNo = popupCloseTicketCustomReplacment.getPrNoET().getText().toString();
                                    replacmentNo = popupCloseTicketCustomReplacment.getReplacmentNo().getText().toString();
                                    partNo = popupCloseTicketCustomReplacment.getPartNo().getText().toString();
                                    serialNo = popupCloseTicketCustomReplacment.getSerialNo().getText().toString();
                                    Log.d("addt11111",String.valueOf(prNo));
                                    String regexStr = "^[0-9]*$";

                                    //Close By REPLACMENT
                                    if (popupCloseTicketCustomReplacment.getStringSpinnerItem().getSelectedItemPosition()==3) {
                                        if (additionalInfo.equals("")) {
                                            popupCloseTicketCustomReplacment.getTicketInfoET().setError(getResources().getString(R.string.error_close_info));
                                        }else if(replacmentNo.length()==0){
                                            popupCloseTicketCustomReplacment.getReplacmentNo().requestFocus();
                                            popupCloseTicketCustomReplacment.getReplacmentNo().setError(getResources().getString(R.string.error_replacmentNo_action_requered));
                                        }else if(partNo.length()==0){
                                            popupCloseTicketCustomReplacment.getPartNo().requestFocus();
                                            popupCloseTicketCustomReplacment.getPartNo().setError(getResources().getString(R.string.error_partNo_action_requered));
                                        }else if(serialNo.length()==0){
                                            popupCloseTicketCustomReplacment.getSerialNo().requestFocus();
                                            popupCloseTicketCustomReplacment.getSerialNo().setError(getResources().getString(R.string.error_serialNo_action_requered));
                                        }else{
                                            String title = "Submission Confirmation";
                                            String msg =
                                                    "You will Close \""
                                                            + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType())
                                                            + "\" ticket with detail : \nLocation : "
                                                            + selectedTicket.getStationName()
                                                            + "\nSuspect : "
                                                            + selectedTicket.getSuspect1Name()
                                                            + " - "
                                                            + selectedTicket.getSuspect2Name()
                                                            + " - "
                                                            + selectedTicket.getSuspect3Name()
                                                            + "\nSeverity : "
                                                            + CommonsUtil.severityToString(selectedTicket.getTicketSeverity())
                                                            + "\nAsset.No : "
                                                            + replacmentNo.toString()
                                                            + "\nPart.No : "
                                                            + partNo.toString()
                                                            + "\nSerial.No : "
                                                            + serialNo.toString();

                                            popConfirm = CustomPopConfirm.newInstance(title, msg, "Yes", "No");
                                            popConfirm.setBackListener(
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            popConfirm.dismiss();
                                                        }
                                                    });
                                            popConfirm.setProcessListener(
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            popupCloseTicketCustomReplacment.dismiss();
                                                            popConfirm.dismiss();

                                                            new ClosedTicketByReplacment().execute();
                                                        }
                                                    });
                                            popConfirm.show(getFragmentManager(), null);
                                        }
                                    //Close By PR
                                    }else if (popupCloseTicketCustomReplacment.getStringSpinnerItem().getSelectedItemPosition()==2) {
                                        if (additionalInfo.equals("")) {
                                            popupCloseTicketCustomReplacment.getTicketInfoET().setError(getResources().getString(R.string.error_close_info));
                                        }else if(prNo.length()!=9){
                                            popupCloseTicketCustomReplacment.getPrNoET().requestFocus();
                                            popupCloseTicketCustomReplacment.getPrNoET().setError(getResources().getString(R.string.error_prNo_action_digit));
                                        }else if(!prNo.trim().matches(regexStr)){
                                            popupCloseTicketCustomReplacment.getPrNoET().requestFocus();
                                            popupCloseTicketCustomReplacment.getPrNoET().setError(getResources().getString(R.string.error_prNo_action_number));
                                        }else{
                                            String title = "Submission Confirmation";
                                            String msg =
                                                    "You will Close \""
                                                            + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType())
                                                            + "\" ticket with detail : \nLocation : "
                                                            + selectedTicket.getStationName()
                                                            + "\nSuspect : "
                                                            + selectedTicket.getSuspect1Name()
                                                            + " - "
                                                            + selectedTicket.getSuspect2Name()
                                                            + " - "
                                                            + selectedTicket.getSuspect3Name()
                                                            + "\nSeverity : "
                                                            + CommonsUtil.severityToString(selectedTicket.getTicketSeverity())
                                                            + "\nPR.No : "
                                                            + prNo.toString();

                                            popConfirm = CustomPopConfirm.newInstance(title, msg, "Yes", "No");
                                            popConfirm.setBackListener(
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            popConfirm.dismiss();
                                                        }
                                                    });
                                            popConfirm.setProcessListener(
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            popupCloseTicketCustomReplacment.dismiss();
                                                            popConfirm.dismiss();

                                                            new ClosedTicketTaskKERUSAKAN_WITHPR().execute();
                                                        }
                                                    });
                                            popConfirm.show(getFragmentManager(), null);
                                        }
                                    //CLOSE BIASA
                                    }else if (popupCloseTicketCustomReplacment.getStringSpinnerItem().getSelectedItemPosition()==1) {
                                        if (additionalInfo.equals("")) {
                                            popupCloseTicketCustomReplacment.getTicketInfoET().setError(getResources().getString(R.string.error_close_info));
                                        }else {
                                            String title = "Submission Confirmation";
                                            String msg =
                                                    "You will Close \""
                                                            + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType())
                                                            + "\" ticket with detail : \nLocation : "
                                                            + selectedTicket.getStationName()
                                                            + "\nSuspect : "
                                                            + selectedTicket.getSuspect1Name()
                                                            + " - "
                                                            + selectedTicket.getSuspect2Name()
                                                            + " - "
                                                            + selectedTicket.getSuspect3Name()
                                                            + "\nSeverity : "
                                                            + CommonsUtil.severityToString(selectedTicket.getTicketSeverity());

                                            popConfirm = CustomPopConfirm.newInstance(title, msg, "Yes", "No");
                                            popConfirm.setBackListener(
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            popConfirm.dismiss();
                                                        }
                                                    });
                                            popConfirm.setProcessListener(
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            popupCloseTicketCustomReplacment.dismiss();
                                                            popConfirm.dismiss();

                                                            new ClosedTicketTaskKERUSAKAN_WITHPR().execute();

                                                        }
                                                    });
                                            popConfirm.show(getFragmentManager(), null);
                                        }

                                    }
                                    else {
        //                                        popupCloseTicketCustom.setTicketInfoETHint("test piter");
                                        popupCloseTicketCustomReplacment.getFixTypeSpinner().requestFocus();
                                        popupCloseTicketCustomReplacment.getFixTypeSpinner().setError(getResources().getString(R.string.error_close_type));
                                    }

                                }
                            });
                            popupCloseTicketCustomReplacment.show(getFragmentManager(), null);
                    }
            }


        } else if(view.getId() == R.id.escalatedBtn) {
            popupEscalationTicket = PopupEscalationTicket.newInstance("Escalation Ticket","Process","Back");
            popupEscalationTicket.setBackListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupEscalationTicket.dismiss();
                }
            });
            popupEscalationTicket.setProcessListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    actionDescribe = popupEscalationTicket.getActionET().getText().toString();
                    requireSupport = popupEscalationTicket.getRequireET().getText().toString();
                    if(TextUtils.isEmpty(actionDescribe) || actionDescribe.length() < 60) {
                        popupEscalationTicket.getActionET().requestFocus();
                        popupEscalationTicket.getActionET().setError(getResources().getString(R.string.error_escalation_action));
                    } else if(TextUtils.isEmpty(requireSupport) || requireSupport.length() < 60) {
                        popupEscalationTicket.getRequireET().requestFocus();
//                        popupEscalationTicket.getRequireET().setError(getResources().getString(R.string.label_escalation_require));
                        popupEscalationTicket.getRequireET().setError(getResources().getString(R.string.error_escalation_require));
                    } else {
                        String title = "Submission Confirmation";
                        String msg = "You will escalation " + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType()) +" incident with detail : \nLocation : "+
                                selectedTicket.getStationName() +
                                "\nSuspect : " + selectedTicket.getSuspect1Name() + " - " + selectedTicket.getSuspect2Name() + " - " + selectedTicket.getSuspect3Name() +
                                "\nSeverity : " + CommonsUtil.severityToString(selectedTicket.getTicketSeverity());
                        confDialog = CustomPopConfirm.newInstance(title,msg,"Yes","No");
                        confDialog.setBackListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confDialog.dismiss();
                            }
                        });
                        confDialog.setProcessListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupEscalationTicket.dismiss();
                                confDialog.dismiss();
                                new EscalatedTicketTask().execute();

                            }
                        });
                        confDialog.show(getFragmentManager(), null);

                    }
                }
            });
            popupEscalationTicket.show(getFragmentManager(), null);
        } else if(view.getId() == R.id.assignmentBtn) {
            // edit by ptr.nov
            // Kst - Reason for re-assignment Engginer
            new GetEngineerTask().execute();

        } else if(view.getId() == R.id.assignmentToBtn) {
            new GetAssignToTask().execute();

        } else if(view.getId() == R.id.assignmentToBtn2) {
            new GetAssignToTask2().execute();

        } else if(view.getId() == R.id.closedAssignmentBtn) {

            // Kadep TS - ptr.nov
            popupCloseTicketCustomReplacment = PopupCloseTicketCustomReplacment.newInstance("Close Ticket" + " [" + selectedTicket.getTicketNo().toString() +"]","Process","Back");

            popupCloseTicketCustomReplacment.setBackListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupCloseTicketCustomReplacment.dismiss();
                }
            });
            popupCloseTicketCustomReplacment.setProcessListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    additionalInfo = popupCloseTicketCustomReplacment.getTicketInfoET().getText().toString();
//                    closedTypes = popupCloseTicketCustom.getStringSpinnerItem();
//                                    penyebab = String.valueOf(popupCloseTicketV2.getPenyebabET());
//                                    program = String.valueOf(popupCloseTicketV2.getProgramET());
//                                    closedTypes = String.valueOf(popupCloseTicketV2.getPrNoET());
                    prNo = popupCloseTicketCustomReplacment.getPrNoET().getText().toString();
                    replacmentNo = popupCloseTicketCustomReplacment.getReplacmentNo().getText().toString();
                    partNo = popupCloseTicketCustomReplacment.getPartNo().getText().toString();
                    serialNo = popupCloseTicketCustomReplacment.getSerialNo().getText().toString();
                    Log.d("addt11111",String.valueOf(prNo));

                    String regexStr = "^[0-9]*$";

//                    if (closedTypes.equals("Closed by PR")) {
                    if (popupCloseTicketCustomReplacment.getStringSpinnerItem().getSelectedItemPosition()==3) {
                        if (additionalInfo.equals("")) {
                            popupCloseTicketCustomReplacment.getTicketInfoET().setError(getResources().getString(R.string.error_close_info));
                        }else if(replacmentNo.length()==0){
                            popupCloseTicketCustomReplacment.getReplacmentNo().requestFocus();
                            popupCloseTicketCustomReplacment.getReplacmentNo().setError(getResources().getString(R.string.error_replacmentNo_action_requered));
                        }else if(partNo.length()==0){
                            popupCloseTicketCustomReplacment.getPartNo().requestFocus();
                            popupCloseTicketCustomReplacment.getPartNo().setError(getResources().getString(R.string.error_partNo_action_requered));
                        }else if(serialNo.length()==0){
                            popupCloseTicketCustomReplacment.getSerialNo().requestFocus();
                            popupCloseTicketCustomReplacment.getSerialNo().setError(getResources().getString(R.string.error_serialNo_action_requered));
                        }else{
                            String title = "Submission Confirmation";
                            String msg =
                                    "You will Close \""
                                            + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType())
                                            + "\" ticket with detail : \nLocation : "
                                            + selectedTicket.getStationName()
                                            + "\nSuspect : "
                                            + selectedTicket.getSuspect1Name()
                                            + " - "
                                            + selectedTicket.getSuspect2Name()
                                            + " - "
                                            + selectedTicket.getSuspect3Name()
                                            + "\nSeverity : "
                                            + CommonsUtil.severityToString(selectedTicket.getTicketSeverity())
                                            + "\nAsset.No : "
                                            + replacmentNo.toString()
                                            + "\nPart.No : "
                                            + partNo.toString()
                                            + "\nSerial.No : "
                                            + serialNo.toString();

                            popConfirm = CustomPopConfirm.newInstance(title  + " [" + selectedTicket.getTicketNo().toString() +"]", msg, "Yes", "No");
                            popConfirm.setBackListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            popConfirm.dismiss();
                                        }
                                    });
                            popConfirm.setProcessListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            popupCloseTicketCustomReplacment.dismiss();
                                            popConfirm.dismiss();

                                            new ClosedTicketByReplacment().execute();
                                        }
                                    });
                            popConfirm.show(getFragmentManager(), null);

                        }
//                                   //Close By PR
                    }else if (popupCloseTicketCustomReplacment.getStringSpinnerItem().getSelectedItemPosition()==2) {
                        if (additionalInfo.equals("")) {
                            popupCloseTicketCustomReplacment.getTicketInfoET().setError(getResources().getString(R.string.error_close_info));
                        }else if (prNo.equals("")) {
                            popupCloseTicketCustomReplacment.getPrNoET().setError(getResources().getString(R.string.error_pr_no));
                        }else if(prNo.length()!=9){
                            popupCloseTicketCustomReplacment.getPrNoET().requestFocus();
                            popupCloseTicketCustomReplacment.getPrNoET().setError(getResources().getString(R.string.error_prNo_action_digit));
                        }else if(!prNo.trim().matches(regexStr)){
                            popupCloseTicketCustomReplacment.getPrNoET().requestFocus();
                            popupCloseTicketCustomReplacment.getPrNoET().setError(getResources().getString(R.string.error_prNo_action_number));
                        }else{
                            String title = "Submission Confirmation";
                            String msg =
                                    "You will Close \""
                                            + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType())
                                            + "\" ticket with detail : \nLocation : "
                                            + selectedTicket.getStationName()
                                            + "\nSuspect : "
                                            + selectedTicket.getSuspect1Name()
                                            + " - "
                                            + selectedTicket.getSuspect2Name()
                                            + " - "
                                            + selectedTicket.getSuspect3Name()
                                            + "\nSeverity : "
                                            + CommonsUtil.severityToString(selectedTicket.getTicketSeverity())
                                            + "\nPR.No : "
                                            + prNo.toString();

                                    popConfirm = CustomPopConfirm.newInstance(title + " [" + selectedTicket.getTicketNo().toString() +"]" , msg, "Yes", "No");
                            popConfirm.setBackListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            popConfirm.dismiss();
                                        }
                                    });
                            popConfirm.setProcessListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            popupCloseTicketCustomReplacment.dismiss();
                                            popConfirm.dismiss();

                                            new ClosedTicketTaskKERUSAKAN_WITHPR().execute();
                                        }
                                    });
                            popConfirm.show(getFragmentManager(), null);

                        }
                    }else if (popupCloseTicketCustomReplacment.getStringSpinnerItem().getSelectedItemPosition()==1) {
                        if (additionalInfo.equals("")) {
                            popupCloseTicketCustomReplacment.getTicketInfoET().setError(getResources().getString(R.string.error_close_info));
                        }else {
                            String title = "Submission Confirmation";
                            String msg =
                                    "You will Close \""
                                            + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType())
                                            + "\" ticket with detail : \nLocation : "
                                            + selectedTicket.getStationName()
                                            + "\nSuspect : "
                                            + selectedTicket.getSuspect1Name()
                                            + " - "
                                            + selectedTicket.getSuspect2Name()
                                            + " - "
                                            + selectedTicket.getSuspect3Name()
                                            + "\nSeverity : "
                                            + CommonsUtil.severityToString(selectedTicket.getTicketSeverity());

                            popConfirm = CustomPopConfirm.newInstance(title + " [" + selectedTicket.getTicketNo().toString() +"]", msg, "Yes", "No");
                            popConfirm.setBackListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            popConfirm.dismiss();
                                        }
                                    });
                            popConfirm.setProcessListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            popupCloseTicketCustomReplacment.dismiss();
                                            popConfirm.dismiss();

                                            new ClosedTicketTaskKERUSAKAN_WITHPR().execute();
                                        }
                                    });
                            popConfirm.show(getFragmentManager(), null);
                        }
                    }else {
                        popupCloseTicketCustomReplacment.getFixTypeSpinner().requestFocus();
                        popupCloseTicketCustomReplacment.getFixTypeSpinner().setError(getResources().getString(R.string.error_close_type));
                    }
                }
            });
            popupCloseTicketCustomReplacment.show(getFragmentManager(), null);

//            if (selectedTicket.getTicketType() == 1 && preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_TS) {
//                Log.d(TAG, "onActionPage: " + "INI KADEP_TS 1");
//                popupCloseTicketV2 = PopupCloseTicketV2.newInstance("Close Ticket", "Process", "Back");
//                popupCloseTicketV2.setBackListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        popupCloseTicketV2.dismiss();
//                    }
//                });
//                popupCloseTicketV2.setProcessListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        additionalInfo = popupCloseTicketV2.getTicketInfoET().getText().toString();
//                        penyebab = String.valueOf(popupCloseTicketV2.getPenyebabET());
//                        program = String.valueOf(popupCloseTicketV2.getProgramET());
//                        closedTypes = String.valueOf(popupCloseTicketV2.getPrNoET());
//                        prNo = popupCloseTicketCustom.getPrNoET().getText().toString();
//
//                        if (popupCloseTicketV2.getFixTypeSpinner() == null) {
//                            popupCloseTicketV2.getTicketInfoET().setError(getResources().getString(R.string.error_close_info));
//                        }
//                        startTime = popupCloseTicketV2.getStartTimeET().getText().toString();
//                        duration = popupCloseTicketV2.getDurationET().getText().toString();
//                        if (TextUtils.isEmpty(additionalInfo)) {
//                            popupCloseTicketV2.getTicketInfoET().requestFocus();
//                            popupCloseTicketV2.getTicketInfoET().setError(getResources().getString(R.string.error_close_info));
//                        }

//                        popupCloseTicketV2.getFixTypeSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                            @Override
//                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                                Log.d(TAG, "onItemSelected: " + position);
//                                Log.d(TAG, "onItemSelected: " + popupCloseTicketV2.getFixTypeSpinner().getSelectedItem().toString());
//
//                                program = String.valueOf(popupCloseTicketV2.getFixTypeSpinner().getSelectedItemId());
//                                Log.d(TAG, "onItemSelected: " + program);
//
//                                if (popupCloseTicketV2.getFixTypeSpinner().getSelectedItem().toString().equals("Closed by PR")) {
//                                    popupCloseTicketV2.getPrNoET().setVisibility(View.VISIBLE);
//                                    closedType = 2;
//                                } else {
//                                    popupCloseTicketV2.getPrNoET().setVisibility(View.GONE);
//                                    closedType = 1;
//                                }
//
//                            }
//
//                            @Override
//                            public void onNothingSelected(AdapterView<?> parent) {
//
//                            }
//                        });

//                        if (popupCloseTicketV2.getFixTypeSpinner().getSelectedItemPosition() == 0) {
//                            popupCloseTicketV2.getFixTypeSpinner().getSelectedView().requestFocus();
//                            popupCloseTicketV2.getFixTypeSpinner().setError(getResources().getString(R.string.error_fix_type));
//
//                        }


//                        else if (TextUtils.isEmpty(startTime)) {
//                            popupCloseTicketV2.getStartTimeET().requestFocus();
//                            popupCloseTicketV2.getStartTimeET().setError(getResources().getString(R.string.error_close_info));
//                        } else if (TextUtils.isEmpty(duration)) {
//                            popupCloseTicketV2.getDurationET().requestFocus();
//                            popupCloseTicketV2.getDurationET().setError(getResources().getString(R.string.error_close_info));
//                        } else {
//                            String title = "Submission Confirmation";
//                            String msg = "You will closed " + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType()) + " incident with detail : \nLocation : " +
//                                    selectedTicket.getStationName() +
//                                    "\nSuspect : " + selectedTicket.getSuspect1Name() + " - " + selectedTicket.getSuspect2Name() + " - " + selectedTicket.getSuspect3Name() +
//                                    "\nSeverity : " + CommonsUtil.severityToString(selectedTicket.getTicketSeverity());
//                            confDialog = CustomPopConfirm.newInstance(title, msg, "Yes", "No");
//                            confDialog.setBackListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    confDialog.dismiss();
//                                }
//                            });
//                            confDialog.setProcessListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    popupCloseTicketV2.dismiss();
//                                    confDialog.dismiss();
////                                        new ClosedTicketTask().execute();
//                                    new ClosedTicketTaskDT_WITHPR().execute();
//
//                                }
//                            });
//                            confDialog.show(getFragmentManager(), null);
//                        }
//
//                    }
//                });
////                    popupCloseTicketV2.show(getFragmentManager(), null);
//                popupCloseTicketV2.show(getSupportFragmentManager(), null);
//            }
//            if (selectedTicket.getTicketType() == 2 && preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_TS) {
//                popupCloseTicketCustom = PopupCloseTicketCustom.newInstance("Close Ticket", "Process", "Back");
//                popupCloseTicketCustom.setBackListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        popupCloseTicketCustom.dismiss();
//                    }
//                });
//                popupCloseTicketCustom.setProcessListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        additionalInfo = popupCloseTicketCustom.getTicketInfoET().getText().toString();
//
//                        penyebab = String.valueOf(popupCloseTicketV2.getPenyebabET());
//                        program = String.valueOf(popupCloseTicketV2.getProgramET());
//                        closedTypes = String.valueOf(popupCloseTicketV2.getPrNoET());
//                        prNo = popupCloseTicketCustom.getPrNoET().getText().toString();
//
//                        if (popupCloseTicketCustom.getFixTypeSpinner_2() == null) {
//                            popupCloseTicketCustom.getTicketInfoET().setError(getResources().getString(R.string.error_close_info));
//
//
//                        } else if (TextUtils.isEmpty(additionalInfo)) {
//                            popupCloseTicketCustom.getTicketInfoET().requestFocus();
//                            popupCloseTicketCustom.getTicketInfoET().setError(getResources().getString(R.string.error_close_info));
//                        } else {
//
//
//                            String title = "Submission Confirmation";
//                            String msg = "You will closed " + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType()) + " incident with detail : \nLocation : " +
//                                    selectedTicket.getStationName() +
//                                    "\nSuspect : " + selectedTicket.getSuspect1Name() + " - " + selectedTicket.getSuspect2Name() + " - " + selectedTicket.getSuspect3Name() +
//                                    "\nSeverity : " + CommonsUtil.severityToString(selectedTicket.getTicketSeverity());
//                            confDialog = CustomPopConfirm.newInstance(title, msg, "Yes", "No");
//                            confDialog.setBackListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    confDialog.dismiss();
//                                }
//                            });
//                            confDialog.setProcessListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    popupCloseTicketCustom.dismiss();
//                                    confDialog.dismiss();
//                                    new ClosedTicketTaskKERUSAKAN_WITHPR().execute();
//
//                                }
//                            });
//                            confDialog.show(getFragmentManager(), null);
//                        }
//
//                    }
//                });
//                popupCloseTicketCustom.show(getFragmentManager(), null);
//            }
        }else if(view.getId() == R.id.guidanceBtn) {
            //Editing by ptr.nov guidanceBtnKadepTs
            popupGuidanceTicket = PopupGuidanceTicket.newInstance("Guidance Ticket","Process","Back");
            popupGuidanceTicket.setBackListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupGuidanceTicket.dismiss();
                }
            });
            popupGuidanceTicket.setProcessListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    actionDescribe = popupGuidanceTicket.getActionET().getText().toString();
                    if(TextUtils.isEmpty(actionDescribe) || actionDescribe.length() < 60) {
                        popupGuidanceTicket.getActionET().requestFocus();
                        popupGuidanceTicket.getActionET().setError(getResources().getString(R.string.error_guidance_action));
                    } else {
                        String title = "Submission Confirmation";
                        String msg = "You will guidance " + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType()) +" incident with detail : \nLocation : "+
                                selectedTicket.getStationName() +
                                "\nSuspect : " + selectedTicket.getSuspect1Name() + " - " + selectedTicket.getSuspect2Name() + " - " + selectedTicket.getSuspect3Name() +
                                "\nSeverity : " + CommonsUtil.severityToString(selectedTicket.getTicketSeverity());
                        confDialog = CustomPopConfirm.newInstance(title,msg,"Yes","No");
                        confDialog.setBackListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confDialog.dismiss();
                            }
                        });
                        confDialog.setProcessListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupGuidanceTicket.dismiss();
                                confDialog.dismiss();
                                new GuidanceTicketTask().execute();

                            }
                        });
                        confDialog.show(getFragmentManager(), null);

                    }
                }
            });
            popupGuidanceTicket.show(getFragmentManager(), null);
        }else if(view.getId() == R.id.guidanceBtnKadepTs) {
            //Editing by ptr.nov
            popupGuidanceTicket = PopupGuidanceTicket.newInstance("Guidance Ticket","Process","Back");
            popupGuidanceTicket.setBackListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupGuidanceTicket.dismiss();
                }
            });
            popupGuidanceTicket.setProcessListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    actionDescribe = popupGuidanceTicket.getActionET().getText().toString();
                    if(TextUtils.isEmpty(actionDescribe) || actionDescribe.length() < 60) {
                        popupGuidanceTicket.getActionET().requestFocus();
                        popupGuidanceTicket.getActionET().setError(getResources().getString(R.string.error_guidance_action));
                    } else {
                        String title = "Submission Confirmation";
                        String msg = "You will guidance " + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType()) +" incident with detail : \nLocation : "+
                                selectedTicket.getStationName() +
                                "\nSuspect : " + selectedTicket.getSuspect1Name() + " - " + selectedTicket.getSuspect2Name() + " - " + selectedTicket.getSuspect3Name() +
                                "\nSeverity : " + CommonsUtil.severityToString(selectedTicket.getTicketSeverity());
                        confDialog = CustomPopConfirm.newInstance(title,msg,"Yes","No");
                        confDialog.setBackListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confDialog.dismiss();
                            }
                        });
                        confDialog.setProcessListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupGuidanceTicket.dismiss();
                                confDialog.dismiss();
                                new GuidanceTicketTask().execute();

                            }
                        });
                        confDialog.show(getFragmentManager(), null);

                    }
                }
            });
            popupGuidanceTicket.show(getFragmentManager(), null);


        }else if(view.getId() == R.id.replacemmentApprovedBtn){
//            String itemCon=selectedTicket.getItemCondition().toString();
//            if(!String.valueOf(selectedTicket.getItemCondition()).isEmpty()){
//            if (itemCon==null) {
//                String title = "Submission Confirmation";
//                String msg = "Your data is invalid, please contact the administrator.";
//                confDialog = CustomPopConfirm.newInstance(title,msg,"Send","Close");
//                confDialog.setBackListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        confDialog.dismiss();
//                    }
//                });
//                confDialog.setProcessListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        confDialog.dismiss();
//                    }
//                });
//                confDialog.show(getFragmentManager(), null);
//            }else
                if (Integer.valueOf(selectedTicket.getItemCondition())==2){
                Log.d(TAG, "respon.001=" + Integer.valueOf(selectedTicket.getItemCondition()));
               String[] ar1={"no-data","item has been repaired","items cannot be repaired"};
               String msgLabel ="\n"
                        + "\nTicket.No : " + selectedTicket.getTicketNo()
                        + "\nAsset.No : " + selectedTicket.getAssetNno()
                        + "\nPart.No : " + selectedTicket.getPartNo()
                        + "\nSerial.No : " + selectedTicket.getSerialNo()
                        + "\nEngineer Check : " + ar1[Integer.valueOf(selectedTicket.getItemCondition())]
               ;

                popupApprovalReplacment = PopupApprovalReplacment.newInstance("Approved Assets"+ " [" + selectedTicket.getTicketNo().toString() +"]","Process","Back","Ticket Info  : " + msgLabel);

                popupApprovalReplacment.setBackListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupApprovalReplacment.dismiss();
                    }
                });
                popupApprovalReplacment.setProcessListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        additionalInfo = popupApprovalReplacment.getTicketInfoET().getText().toString();
                        prNoomor = popupApprovalReplacment.getPrNomor().getText().toString();
                        String regexStr = "^[0-9]*$";

                        if(TextUtils.isEmpty(additionalInfo)) {
                            popupApprovalReplacment.getTicketInfoET().requestFocus();
                            popupApprovalReplacment.getTicketInfoET().setError(getResources().getString(R.string.error_close_info));
                        }else if (prNoomor.equals("")) {
                            popupApprovalReplacment.getPrNomor().setError(getResources().getString(R.string.error_pr_no));
                        }else if(prNoomor.length()!=9){
                            popupApprovalReplacment.getPrNomor().requestFocus();
                            popupApprovalReplacment.getPrNomor().setError(getResources().getString(R.string.error_prNo_action_digit));
                        }else if(!prNoomor.trim().matches(regexStr)){
                            popupApprovalReplacment.getPrNomor().requestFocus();
                            popupApprovalReplacment.getPrNomor().setError(getResources().getString(R.string.error_prNo_action_number));
                        } else {
                            String title = "Submission Confirmation";
                            String msg = "You will Approved  : "
                                    + "\nLocation : "
                                    + selectedTicket.getStationName()
                                    + "\nSuspect : " + selectedTicket.getSuspect1Name()
                                    + " - " + selectedTicket.getSuspect2Name() + " - " + selectedTicket.getSuspect3Name()
                                    +"\nSeverity : " + CommonsUtil.severityToString(selectedTicket.getTicketSeverity());
//                                    +"\nItemCondition : " + selectedTicket.getItemCondition().toString();
                            confDialog = CustomPopConfirm.newInstance(title,msg,"Yes","No");
                            confDialog.setBackListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    confDialog.dismiss();
                                }
                            });
                            confDialog.setProcessListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupApprovalReplacment.dismiss();
                                    confDialog.dismiss();
                                    new ApprovedAssetReplacmentTask().execute();
                                }
                            });
                            confDialog.show(getFragmentManager(), null);
                        }

                    }
                });
                popupApprovalReplacment.show(getFragmentManager(), null);
            }else{
                popupCloseTicket = PopupCloseTicket.newInstance("Approved Assets","Process","Back");

                popupCloseTicket.setBackListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupCloseTicket.dismiss();
                    }
                });
                popupCloseTicket.setProcessListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        additionalInfo = popupCloseTicket.getTicketInfoET().getText().toString();
                        if(TextUtils.isEmpty(additionalInfo)) {
                            popupCloseTicket.getTicketInfoET().requestFocus();
                            popupCloseTicket.getTicketInfoET().setError(getResources().getString(R.string.error_close_info));
                        } else {
                            String title = "Submission Confirmation";
                            String msg = "You will Approved  : "
                                    + "\nLocation : "
                                    + selectedTicket.getStationName()
                                    + "\nSuspect : " + selectedTicket.getSuspect1Name()
                                    + " - " + selectedTicket.getSuspect2Name() + " - " + selectedTicket.getSuspect3Name()
                                    +"\nSeverity : " + CommonsUtil.severityToString(selectedTicket.getTicketSeverity());
//                                    +"\nItemCondition : " + selectedTicket.getItemCondition().toString();
                            confDialog = CustomPopConfirm.newInstance(title,msg,"Yes","No");
                            confDialog.setBackListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    confDialog.dismiss();
                                }
                            });
                            confDialog.setProcessListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupCloseTicket.dismiss();
                                    confDialog.dismiss();
                                    new ApprovedAssetReplacmentTask2().execute();
                                }
                            });
                            confDialog.show(getFragmentManager(), null);
                        }

                    }
                });
                popupCloseTicket.show(getFragmentManager(), null);

            }

        }
    }


    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(getApplicationContext(),slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    private class TicketTask extends AsyncTask<Void, Void, Void> {

        String result;

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
                Log.d(TAG, "TecketHistory: " +result);
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
                Log.d(TAG, "TecketHistoryLogs: " +ticketLogs);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            updateComponent();

            progressDialog.dismiss();
        }
    }

    class ClosedTicketTask extends AsyncTask<Void, Void, Void> {

        String result = "";
        JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(CommonsUtil.getAbsoluteUrl("close_ticket"), new FormBody.Builder()
                        .add("ticket_id", selectedTicket.getTicketId())
                        .add("ticket_closedby", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                        .add("additional_info", additionalInfo)
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
//                    returnTicket = gsona.fromJson(object.getString("data").toString(), Ticket.class);
                    returnTicket = gson.fromJson(object.getString("data"), Ticket.class);
                    String title = returnTicket.getTicketId() + " - " + CommonsUtil.severityToString(returnTicket.getTicketSeverity()) + " - " + CommonsUtil.ticketTypeToString(returnTicket.getTicketType());
                    String content = "Down Time on site " + returnTicket.getStationName() + " has been solved.";
                    NotificationManager mgr=
                            (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle(title)
                                    .setContentText(content);

                    Notification note = mBuilder.build();

                    mgr.notify(NotificationManager.IMPORTANCE_HIGH, note);


                    finish();

                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class ApprovedAssetReplacmentTask extends AsyncTask<Void, Void, Void> {

        String result = "";
        JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(CommonsUtil.getAbsoluteUrl("approve_condition_item"), new FormBody.Builder()
                        .add("ticket_id", selectedTicket.getTicketId())
                        .add("ticket_confirmby", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                        .add("pr_no", prNoomor.toString())
                        .add("approve_item_info", additionalInfo)
                        .build());
//                finish();
//                progressDialog.dismiss();
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
                Logcat.e("response: " + result);
                if (object.getString("status").equalsIgnoreCase("success")) {
                    finish();
                    progressDialog.dismiss();
                }

//

//                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
//                    returnTicket = gsona.fromJson(object.getString("data").toString(), Ticket.class);
////                    returnTicket = gson.fromJson(object.getString("data"), Ticket.class);
////                    String title = returnTicket.getTicketId() + " - " + CommonsUtil.severityToString(returnTicket.getTicketSeverity()) + " - " + CommonsUtil.ticketTypeToString(returnTicket.getTicketType());
////                    String content = "Down Time on site " + returnTicket.getStationName() + " has been solved.";
////                    NotificationManager mgr=
////                            (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
////                    NotificationCompat.Builder mBuilder =
////                            new NotificationCompat.Builder(getApplicationContext())
////                                    .setSmallIcon(R.mipmap.ic_launcher)
////                                    .setContentTitle(title)
////                                    .setContentText(content);
////
////                    Notification note = mBuilder.build();
//
//                    mgr.notify(NotificationManager.IMPORTANCE_HIGH, note);


//                    finish();

//                    if (progressDialog != null && progressDialog.isShowing())
//                        progressDialog.dismiss();

//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class ApprovedAssetReplacmentTask2 extends AsyncTask<Void, Void, Void> {

        String result = "";
        JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(CommonsUtil.getAbsoluteUrl("approve_condition_item"), new FormBody.Builder()
                        .add("ticket_id", selectedTicket.getTicketId())
                        .add("ticket_confirmby", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                        .add("approve_item_info", additionalInfo)
                        .build());
                finish();
                progressDialog.dismiss();
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
                Logcat.e("response: " + result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    class ClosedTicketTaskDD extends AsyncTask<Void, Void, Void> {

        String result = "";
        JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(CommonsUtil.getAbsoluteUrl("close_ticket_dd"), new FormBody.Builder()
                        .add("ticket_id", selectedTicket.getTicketId())
                        .add("ticket_closedby", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                        .add("additional_info", additionalInfo)
                        .add("penyebab", penyebab)
                        .add("program", program)
                        .add("start_time", startTime)
                        .add("duration", duration)
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
                Logcat.e("CloseDD::" + result);
                JSONObject object = new JSONObject(result);
                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
//                    returnTicket = gsona.fromJson(object.getString("data").toString(), Ticket.class);
                    returnTicket = gson.fromJson(object.getString("data"), Ticket.class);
                    String title = returnTicket.getTicketId() + " - " + CommonsUtil.severityToString(returnTicket.getTicketSeverity()) + " - " + CommonsUtil.ticketTypeToString(returnTicket.getTicketType());
                    String content = "Down Time on site " + returnTicket.getStationName() + " has been solved.";
                    NotificationManager mgr=
                            (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle(title)
                                    .setContentText(content);

                    Notification note = mBuilder.build();

                    mgr.notify(NotificationManager.IMPORTANCE_HIGH, note);

                    /*ComponentName componentName = getCallingActivity();

                    RecyclerView ticketListRV = (RecyclerView) findViewById(R.id.ticketListRV);
                    ((TicketAdapter) ticketListRV.getAdapter()).getmDataset().remove(ticket);
                    ((TicketAdapter) ticketListRV.getAdapter()).notifyDataSetChanged();*/




                    finish();

                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class ClosedTicketTaskDT extends AsyncTask<Void, Void, Void> {

        String result = "";
        JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(CommonsUtil.getAbsoluteUrl("close_ticket_dt"), new FormBody.Builder()
                        .add("ticket_id", selectedTicket.getTicketId())
                        .add("ticket_closedby", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                        .add("additional_info", additionalInfo)
                        .add("penyebab", penyebab)
                        .add("program", program)
                        .add("start_time", startTime)
                        .add("duration", duration)
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
                Logcat.e("RESULT_CLOSE_DT::" + result);
                JSONObject object = new JSONObject(result);
                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
//                    returnTicket = gsona.fromJson(object.getString("data").toString(), Ticket.class);
                    returnTicket = gson.fromJson(object.getString("data"), Ticket.class);
                    String title = returnTicket.getTicketId() + " - " + CommonsUtil.severityToString(returnTicket.getTicketSeverity()) + " - " + CommonsUtil.ticketTypeToString(returnTicket.getTicketType());
                    String content = "Down Time on site " + returnTicket.getStationName() + " has been solved.";
                    NotificationManager mgr=
                            (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle(title)
                                    .setContentText(content);

                    Notification note = mBuilder.build();

                    mgr.notify(NotificationManager.IMPORTANCE_HIGH, note);

                    /*ComponentName componentName = getCallingActivity();

                    RecyclerView ticketListRV = (RecyclerView) findViewById(R.id.ticketListRV);
                    ((TicketAdapter) ticketListRV.getAdapter()).getmDataset().remove(ticket);
                    ((TicketAdapter) ticketListRV.getAdapter()).notifyDataSetChanged();*/




                    finish();

                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class ClosedTicketTaskDT_WITHPR extends AsyncTask<Void, Void, Void> {

        String result = "";
        JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(CommonsUtil.getAbsoluteUrl("close_ticket_downtime_with_input_pr_no"), new FormBody.Builder()
                        .add("ticket_id", selectedTicket.getTicketId())
                        .add("ticket_closedby", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                        .add("additional_info", additionalInfo)
                        .add("penyebab", penyebab)
                        .add("program", program)
                        .add("closed_type", closedTypes)
                        .add("pr_no", prNo)
                        .add("start_time", startTime)
                        .add("duration", duration)
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
                Logcat.e("RESULT_CLOSE_DT::" + result);
                JSONObject object = new JSONObject(result);
                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
//                    returnTicket = gsona.fromJson(object.getString("data").toString(), Ticket.class);
                    returnTicket = gson.fromJson(object.getString("data"), Ticket.class);
                    String title = returnTicket.getTicketId() + " - " + CommonsUtil.severityToString(returnTicket.getTicketSeverity()) + " - " + CommonsUtil.ticketTypeToString(returnTicket.getTicketType());
                    String content = "Down Time on site " + returnTicket.getStationName() + " has been solved.";
                    NotificationManager mgr=
                            (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle(title)
                                    .setContentText(content);

                    Notification note = mBuilder.build();

                    mgr.notify(NotificationManager.IMPORTANCE_HIGH, note);

                    /*ComponentName componentName = getCallingActivity();

                    RecyclerView ticketListRV = (RecyclerView) findViewById(R.id.ticketListRV);
                    ((TicketAdapter) ticketListRV.getAdapter()).getmDataset().remove(ticket);
                    ((TicketAdapter) ticketListRV.getAdapter()).notifyDataSetChanged();*/




                    finish();

                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class ClosedTicketTaskKERUSAKAN_WITHPR extends AsyncTask<Void, Void, Void> {

        String result = "";
        JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(CommonsUtil.getAbsoluteUrl("close_ticket_kerusakan_with_input_pr_no_v2"), new FormBody.Builder()
                        .add("ticket_id", selectedTicket.getTicketId())
                        .add("ticket_closedby", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                        .add("additional_info", additionalInfo)
//                        .add("closed_type", closedTypes)
                        .add("pr_no", prNo)
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
                Logcat.e("RESULT_CLOSE_DT::" + result);
                JSONObject object = new JSONObject(result);
                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
//                    returnTicket = gsona.fromJson(object.getString("data").toString(), Ticket.class);
                    returnTicket = gson.fromJson(object.getString("data"), Ticket.class);
                    String title = returnTicket.getTicketId() + " - " + CommonsUtil.severityToString(returnTicket.getTicketSeverity()) + " - " + CommonsUtil.ticketTypeToString(returnTicket.getTicketType());
                    String content = "Down Time on site " + returnTicket.getStationName() + " has been solved.";
                    NotificationManager mgr=
                            (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle(title)
                                    .setContentText(content);

                    Notification note = mBuilder.build();

                    mgr.notify(NotificationManager.IMPORTANCE_HIGH, note);

                    /*ComponentName componentName = getCallingActivity();

                    RecyclerView ticketListRV = (RecyclerView) findViewById(R.id.ticketListRV);
                    ((TicketAdapter) ticketListRV.getAdapter()).getmDataset().remove(ticket);
                    ((TicketAdapter) ticketListRV.getAdapter()).notifyDataSetChanged();*/




                    finish();

                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }





    class EscalatedTicketTask extends AsyncTask<Void, Void, Void> {

        String result = "";
        JSONObject jsonObject;
        String url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(CommonsUtil.getAbsoluteUrl("new_escalation"), new FormBody.Builder()
                        .add("ticket_id", selectedTicket.getTicketId())
                        .add("from_user_id", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                        .add("from_position_id", String.valueOf(preferences.getPreferencesInt(Constants.POSITION_ID)))
                        .add("action", actionDescribe)
                        .add("require", requireSupport)
                        .build());

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            Logcat.i(result);
            Log.d("Log123123",String.valueOf(result));
            try {
                JSONObject object = new JSONObject(result);
                Log.d("Log123123",String.valueOf(object.get(Constants.RESPONSE_STATUS)));
                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
                    finish();
                    progressDialog.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class ClosedTicketByReplacment extends AsyncTask<Void, Void, Void> {

        String result = "";
        JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(CommonsUtil.getAbsoluteUrl("close_ticket_kerusakan_with_input_pr_no_v2"), new FormBody.Builder()
                        .add("ticket_id", selectedTicket.getTicketId())
                        .add("ticket_closedby", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                        .add("additional_info", additionalInfo)
//                        .add("closed_type", closedTypes)
                        .add("asset_no", replacmentNo)
                        .add("part_no", partNo)
                        .add("serial_no", serialNo)
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
                Logcat.e("RESULT_CLOSE_DT::" + result);
                JSONObject object = new JSONObject(result);
                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
//                    returnTicket = gsona.fromJson(object.getString("data").toString(), Ticket.class);
                    returnTicket = gson.fromJson(object.getString("data"), Ticket.class);
                    String title = returnTicket.getTicketId() + " - " + CommonsUtil.severityToString(returnTicket.getTicketSeverity()) + " - " + CommonsUtil.ticketTypeToString(returnTicket.getTicketType());
                    String content = "Down Time on site " + returnTicket.getStationName() + " has been solved.";
                    NotificationManager mgr=
                            (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle(title)
                                    .setContentText(content);

                    Notification note = mBuilder.build();

                    mgr.notify(NotificationManager.IMPORTANCE_HIGH, note);

                    /*ComponentName componentName = getCallingActivity();

                    RecyclerView ticketListRV = (RecyclerView) findViewById(R.id.ticketListRV);
                    ((TicketAdapter) ticketListRV.getAdapter()).getmDataset().remove(ticket);
                    ((TicketAdapter) ticketListRV.getAdapter()).notifyDataSetChanged();*/




                    finish();

                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class GuidanceTicketTask extends AsyncTask<Void, Void, Void> {

        String result = "";
        JSONObject jsonObject;
        String url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(CommonsUtil.getAbsoluteUrl("guidance_to"), new FormBody.Builder()
                        .add("ticket_id", selectedTicket.getTicketId())
                        .add("id_updrs", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
//                        .add("from_position_id", String.valueOf(preferences.getPreferencesInt(Constants.POSITION_ID)))
                        .add("remarks", actionDescribe)
//                        .add("require", requireSupport)
                        .build());

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            Logcat.i(result);
            Log.d("Log123123",String.valueOf(result));
            try {
                JSONObject object = new JSONObject(result);
                Log.d("Log123123",String.valueOf(object.get(Constants.RESPONSE_STATUS)));
                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
                    finish();
                    progressDialog.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private class GetEngineerTask extends AsyncTask<Void, Void, Void> {

        String result;
        JSONObject object;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }


        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(
                        CommonsUtil.getAbsoluteUrl(Constants.URL_GET_ENGINEER), new FormBody.Builder().build());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

//            try {
//                sassignSerializer = ApiClient.post(
//                        CommonsUtil.getAbsoluteUrl("cek_total_assign"),
//                        new FormBody.Builder()
//                                .add(Constants.PARAM_TICKET_ID, selectedTicket.getTicketId())
//                                .build());
//                JSONObject obj = null;
//                try {
//                    obj = new JSONObject(sassignSerializer);
//                    Log.d(TAG, "check_assign: " +obj.getString("assign"));
//                    totalSassign=Integer.parseInt(obj.getString("assign"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
////                };
//            try {
//                sassignSerializer = ApiClient.post(
//                        CommonsUtil.getAbsoluteUrl("cek_total_assign"),
//                        new FormBody.Builder()
//                                .add(Constants.PARAM_TICKET_ID, selectedTicket.getTicketId())
//                                .build());
//                JSONObject obj = null;
//                try {
//                    obj = new JSONObject(sassignSerializer);
//                    Log.d(TAG, "check_assign: " +obj.getString("assign"));
//                    totalSassign=Integer.parseInt(obj.getString("assign"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            try{
                if (totalSassign > 0) {
//                    popupReAssignmentTicket
//                    SubmitAssignmentTaskTotalassign

                    object = new JSONObject(result);
                    if (object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
                        Type type = new TypeToken<List<UserModel>>() {}.getType();
                        try {
                            engineers = gson.fromJson(object.getString("data"), type);
                            Logcat.i("engineer_size:" + engineers.size());

                            popupReAssignmentTicket =
                                    popupReAssignmentTicket.newInstance(
                                            "Re-Assignment Ticket", "Process", "Back", engineers);
                            popupReAssignmentTicket.setBackListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            popupReAssignmentTicket.dismiss();
                                        }
                                    });
                            popupReAssignmentTicket.setProcessListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(popupReAssignmentTicket.getAssignTypeSpinner().getSelectedItemPosition()==2){
                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                                    Calendar c = Calendar.getInstance();
                                                    String tglNow = sdf.format(c.getTime());

                                                    Date testDate;
                                                    Date testDate1;
                                                    Date testDate3;
                                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                                    assignType=popupReAssignmentTicket.getAssignTypeSpinner().getSelectedItem().toString();
                                                    assignDate = popupReAssignmentTicket.getAssignmentDateET().getText().toString();
                                                    assignAction =popupReAssignmentTicket.getAssignmentActionET().getText().toString();
                                                    reasonAssign = popupReAssignmentTicket.getReassignToSpinner().getSelectedItem().toString();
                                                    try{
                                                        testDate = df.parse(assignDate);
                                                        testDate1 = df.parse(tglNow);
        //                                                if (testDate1.after(testDate)) {
        //                                                    popupReAssignmentTicket.getAssignmentDateET().requestFocus();
        ////                                                        popupReAssignmentTicket.getAssignmentDateET().setError(df.format(testDate));
        //                                                    popupReAssignmentTicket.getAssignmentDateET().setError(getResources().getString(R.string.out_dated));
        //                                                }else{
        //                                                    return;
        //                                                }
                                                        assignDate = popupReAssignmentTicket.getAssignmentDateET().getText().toString();
                                                        assignAction =popupReAssignmentTicket.getAssignmentActionET().getText().toString();
                                                    if (popupReAssignmentTicket.getAssignToSpinner().getSelectedItemPosition()== 0) {
                                                        popupReAssignmentTicket.getAssignToSpinner().getSelectedView().requestFocus();
                                                        popupReAssignmentTicket.getAssignToSpinner().setError(getResources().getString(R.string.error_assignment_to));
                                                    //
                                                    }else if (popupReAssignmentTicket.getReassignToSpinner().getSelectedItemPosition()== 0) {
                                                        popupReAssignmentTicket.getReassignToSpinner().getSelectedView().requestFocus();
                                                        popupReAssignmentTicket.getReassignToSpinner().setError(getResources().getString(R.string.error_reason_to));
                                                    }else if (TextUtils.isEmpty(assignDate)) {
                                                        popupReAssignmentTicket.getAssignmentDateET().requestFocus();
                                                        popupReAssignmentTicket.getAssignmentDateET().setError(getResources().getString(R.string.error_assignment_date));
                                                    }else if (testDate1.after(testDate)){
                                                        popupReAssignmentTicket.getAssignmentDateET().requestFocus();
                                                        popupReAssignmentTicket.getAssignmentDateET().setError(getResources().getString(R.string.out_dated));
                                                    }else if (TextUtils.isEmpty(assignAction)) {
                                                        popupReAssignmentTicket.getAssignmentActionET().requestFocus();
                                                        popupReAssignmentTicket.getAssignmentActionET().setError(getResources().getString(R.string.label_assignment_action));
                                                    }else {
                                                        String title = "Submission Confirmation";
                                                        String msg =
                                                                "You will assign \""
                                                                        + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType())
                                                                        + "\" ticket with detail : \nLocation : "
                                                                        + selectedTicket.getStationName()
                                                                        + "\nSuspect : "
                                                                        + selectedTicket.getSuspect1Name()
                                                                        + " - "
                                                                        + selectedTicket.getSuspect2Name()
                                                                        + " - "
                                                                        + selectedTicket.getSuspect3Name()
                                                                        + "\nSeverity : "
                                                                        + CommonsUtil.severityToString(selectedTicket.getTicketSeverity())
                                                                        + "\nto : "
                                                                        + popupReAssignmentTicket.getSelectedEngineer().getUserName()
                                                                        + "\nfor : "
                                                                        + popupReAssignmentTicket.getSelectedAssignType()
                                                                        + "\non : "
                                                                        + assignDate
                                                                        + "\nnotes : "
                                                                        + assignAction
                                                                        + "\nreason : "
                                                                        + reasonAssign;
                                                        popConfirm = CustomPopConfirm.newInstance(title, msg, "Yes", "No");
                                                        popConfirm.setBackListener(
                                                                new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        popConfirm.dismiss();
                                                                    }
                                                                });
                                                        popConfirm.setProcessListener(
                                                                new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        popupReAssignmentTicket.dismiss();
                                                                        popConfirm.dismiss();

                                                                        new SubmitAssignmentTaskTotalassign().execute();
                                                                    }
                                                                });
                                                        popConfirm.show(getFragmentManager(), null);
                                                    }

                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    };
                                            }else if(popupReAssignmentTicket.getAssignTypeSpinner().getSelectedItemPosition()==1){
                                                assignType=popupReAssignmentTicket.getAssignTypeSpinner().getSelectedItem().toString();
                                                assignAction =popupReAssignmentTicket.getAssignmentActionET().getText().toString();
                                                if (popupReAssignmentTicket.getAssignToSpinner().getSelectedItemPosition()== 0) {
                                                    popupReAssignmentTicket.getAssignToSpinner().getSelectedView().requestFocus();
                                                    popupReAssignmentTicket.getAssignToSpinner().setError(getResources().getString(R.string.error_assignment_to));
                                                    //
                                                }else if (popupReAssignmentTicket.getReassignToSpinner().getSelectedItemPosition()== 0) {
                                                    popupReAssignmentTicket.getReassignToSpinner().getSelectedView().requestFocus();
                                                    popupReAssignmentTicket.getReassignToSpinner().setError(getResources().getString(R.string.error_reason_to));
                                                }else if (TextUtils.isEmpty(assignAction)) {
                                                    popupReAssignmentTicket.getAssignmentActionET().requestFocus();
                                                    popupReAssignmentTicket.getAssignmentActionET().setError(getResources().getString(R.string.label_assignment_action));
                                                }else {
                                                    String title = "Submission Confirmation";
                                                    String msg =
                                                            "You will assign \""
                                                                    + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType())
                                                                    + "\" ticket with detail : \nLocation : "
                                                                    + selectedTicket.getStationName()
                                                                    + "\nSuspect : "
                                                                    + selectedTicket.getSuspect1Name()
                                                                    + " - "
                                                                    + selectedTicket.getSuspect2Name()
                                                                    + " - "
                                                                    + selectedTicket.getSuspect3Name()
                                                                    + "\nSeverity : "
                                                                    + CommonsUtil.severityToString(selectedTicket.getTicketSeverity())
                                                                    + "\nto : "
                                                                    + popupReAssignmentTicket.getSelectedEngineer().getUserName()
                                                                    + "\nfor : "
                                                                    + popupReAssignmentTicket.getSelectedAssignType()
                                                                    + "\nnotes : "
                                                                    + assignAction
                                                                    + "\nreason : "
                                                                    + reasonAssign;
                                                    popConfirm = CustomPopConfirm.newInstance(title, msg, "Yes", "No");
                                                    popConfirm.setBackListener(
                                                            new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    popConfirm.dismiss();
                                                                }
                                                            });
                                                    popConfirm.setProcessListener(
                                                            new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    popupReAssignmentTicket.dismiss();
                                                                    popConfirm.dismiss();

                                                                    new SubmitAssignmentTaskTotalassign().execute();
                                                                }
                                                            });
                                                    popConfirm.show(getFragmentManager(), null);
                                                }
                                            }else{
                                                popupReAssignmentTicket.getAssignTypeSpinner().getSelectedView().requestFocus();
                                                popupReAssignmentTicket.getAssignTypeSpinner().setError(getResources().getString(R.string.error_assignment_type));
                                            }

                                        }
                                    });

                            popupReAssignmentTicket.show(getSupportFragmentManager(), null);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    progressDialog.dismiss();

                } else {
                    assignmentBtn.setText("Assignment");
                      object = new JSONObject(result);
                      if (object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
                        Type type = new TypeToken<List<UserModel>>() {}.getType();
                        try {
                          engineers = gson.fromJson(object.getString("data"), type);
                          Logcat.i("engineer_size:" + engineers.size());

                          popupAssignmentTicket =
                              PopupAssignmentTicket.newInstance(
                                  "Assignment Ticket", "Process", "Back", engineers);
                          popupAssignmentTicket.setBackListener(
                              new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                  popupAssignmentTicket.dismiss();
                                }
                              });
                          popupAssignmentTicket.setProcessListener(
                              new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if(popupAssignmentTicket.getAssignTypeSpinner().getSelectedItemPosition()==2){
                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                            Calendar c = Calendar.getInstance();
                                            String tglNow = sdf.format(c.getTime());

                                            Date testDate;
                                            Date testDate1;
                                            Date testDate3;
                                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                            assignDate = popupAssignmentTicket.getAssignmentDateET().getText().toString();
                                            assignAction =popupAssignmentTicket.getAssignmentActionET().getText().toString();
                                            assignType=popupAssignmentTicket.getAssignTypeSpinner().getSelectedItem().toString();
                                            try{
                                                testDate = df.parse(assignDate);
                                                testDate1 = df.parse(tglNow);

        //                                      assignDate = popupAssignmentTicket.getAssignmentDateET().getText().toString();
        //                                      assignAction =popupAssignmentTicket.getAssignmentActionET().getText().toString();
                                              if (popupAssignmentTicket.getAssignToSpinner().getSelectedItemPosition()== 0) {
                                                popupAssignmentTicket.getAssignToSpinner().getSelectedView().requestFocus();
                                                popupAssignmentTicket.getAssignToSpinner().setError(getResources().getString(R.string.error_assignment_to));
                                              } else if (popupAssignmentTicket
                                                      .getAssignTypeSpinner()
                                                      .getSelectedItemPosition()
                                                  == 0) {
                                                popupAssignmentTicket
                                                    .getAssignTypeSpinner()
                                                    .getSelectedView()
                                                    .requestFocus();
                                                popupAssignmentTicket
                                                    .getAssignTypeSpinner()
                                                    .setError(getResources().getString(R.string.error_assignment_type));
                                              }
                                              else if (TextUtils.isEmpty(assignDate)) {
                                                popupAssignmentTicket.getAssignmentDateET().requestFocus();
                                                popupAssignmentTicket.getAssignmentDateET().setError(getResources().getString(R.string.error_assignment_date));
                                              }
                                              else if (testDate1.after(testDate)){
                                                  popupAssignmentTicket.getAssignmentDateET().requestFocus();
                                                  popupAssignmentTicket.getAssignmentDateET().setError(getResources().getString(R.string.out_dated));
                                              }
                                              else if (TextUtils.isEmpty(assignAction)) {
                                                popupAssignmentTicket.getAssignmentActionET().requestFocus();
                                                popupAssignmentTicket.getAssignmentActionET().setError(getResources().getString(R.string.label_assignment_action));
                                              } else {
                                                    String title = "Submission Confirmation";
                                                    String msg =
                                                        "You will assign \""
                                                            + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType())
                                                            + "\" ticket with detail : \nLocation : "
                                                            + selectedTicket.getStationName()
                                                            + "\nSuspect : "
                                                            + selectedTicket.getSuspect1Name()
                                                            + " - "
                                                            + selectedTicket.getSuspect2Name()
                                                            + " - "
                                                            + selectedTicket.getSuspect3Name()
                                                            + "\nSeverity : "
                                                            + CommonsUtil.severityToString(selectedTicket.getTicketSeverity())
                                                            + "\nto : "
                                                            + popupAssignmentTicket.getSelectedEngineer().getUserName()
                                                            + "\nfor : "
                                                            + popupAssignmentTicket.getSelectedAssignType()
                                                            + "\non : "
                                                            + assignDate
                                                            + "\nnotes : "
                                                            + assignAction;
                                                    popConfirm = CustomPopConfirm.newInstance(title, msg, "Yes", "No");
                                                    popConfirm.setBackListener(
                                                        new View.OnClickListener() {
                                                          @Override
                                                          public void onClick(View v) {
                                                            popConfirm.dismiss();
                                                          }
                                                        });
                                                    popConfirm.setProcessListener(
                                                        new View.OnClickListener() {
                                                          @Override
                                                          public void onClick(View v) {
                                                            popupAssignmentTicket.dismiss();
                                                            popConfirm.dismiss();

                                                            new SubmitAssignmentTask().execute();
                                                          }
                                                        });
                                                    popConfirm.show(getFragmentManager(), null);
                                                  }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            };

                                    } else if(popupAssignmentTicket.getAssignTypeSpinner().getSelectedItemPosition()==1){
                                        assignAction =popupAssignmentTicket.getAssignmentActionET().getText().toString();
                                        assignType=popupAssignmentTicket.getAssignTypeSpinner().getSelectedItem().toString();
                                        if (popupAssignmentTicket.getAssignToSpinner().getSelectedItemPosition()== 0) {
                                            popupAssignmentTicket.getAssignToSpinner().getSelectedView().requestFocus();
                                            popupAssignmentTicket.getAssignToSpinner().setError(getResources().getString(R.string.error_assignment_to));
                                        }
//                                        else if (popupAssignmentTicket.getAssignTypeSpinner().getSelectedItemPosition()== 0) {
//                                            popupAssignmentTicket.getAssignTypeSpinner().getSelectedView().requestFocus();
//                                            popupAssignmentTicket.getAssignTypeSpinner().setError(getResources().getString(R.string.error_assignment_type));
//                                        }
                                        else if (TextUtils.isEmpty(assignAction)) {
                                            popupAssignmentTicket.getAssignmentActionET().requestFocus();
                                            popupAssignmentTicket.getAssignmentActionET().setError(getResources().getString(R.string.label_assignment_action));
                                        } else {
                                            String title = "Submission Confirmation";
                                            String msg =
                                                    "You will assign \""
                                                            + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType())
                                                            + "\" ticket with detail : \nLocation : "
                                                            + selectedTicket.getStationName()
                                                            + "\nSuspect : "
                                                            + selectedTicket.getSuspect1Name()
                                                            + " - "
                                                            + selectedTicket.getSuspect2Name()
                                                            + " - "
                                                            + selectedTicket.getSuspect3Name()
                                                            + "\nSeverity : "
                                                            + CommonsUtil.severityToString(selectedTicket.getTicketSeverity())
                                                            + "\nto : "
                                                            + popupAssignmentTicket.getSelectedEngineer().getUserName()
                                                            + "\nfor : "
                                                            + popupAssignmentTicket.getSelectedAssignType()
                                                            + "\nnotes : "
                                                            + assignAction;
                                            popConfirm = CustomPopConfirm.newInstance(title, msg, "Yes", "No");
                                            popConfirm.setBackListener(
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            popConfirm.dismiss();
                                                        }
                                                    });
                                            popConfirm.setProcessListener(
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            popupAssignmentTicket.dismiss();
                                                            popConfirm.dismiss();

                                                            new SubmitAssignmentTask().execute();
                                                        }
                                                    });
                                            popConfirm.show(getFragmentManager(), null);
                                        };

                                    } else {
                                        popupAssignmentTicket.getAssignTypeSpinner().getSelectedView().requestFocus();
                                        popupAssignmentTicket.getAssignTypeSpinner().setError(getResources().getString(R.string.error_assignment_type));
                                    }
                                }


                              });

                          popupAssignmentTicket.show(getSupportFragmentManager(), null);

                        } catch (JSONException e) {
                          e.printStackTrace();
                        }
                      }
                        progressDialog.dismiss();
                  }

            } catch (JSONException e) {
                e.printStackTrace();
            }

//            popupAssignmentTicket.show(getFragmentManager(), null);

        }
    }

    private class GetAssignToTask2 extends AsyncTask<Void, Void, Void> {

        String result;
        JSONObject object;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(
                        CommonsUtil.getAbsoluteUrl(Constants.URL_GET_ASSIGN_TO_ME),
                        new FormBody.Builder().build());
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
                    Type type = new TypeToken<List<UserModel>>(){}.getType();
                    try {
                        engineers = gson.fromJson(object.getString("data"), type);
                        Logcat.i("engineer_size:" + engineers.size());

                        popupAssignToTicket = PopupAssignToTicket.newInstance("Assignment Ticket","Process","Back", engineers);
                        popupAssignToTicket.setBackListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupAssignToTicket.dismiss();
                            }
                        });
                        popupAssignToTicket.setProcessListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                assignAction = popupAssignToTicket.getAssignmentActionET().getText().toString();
                                if(popupAssignToTicket.getAssignToSpinner().getSelectedItemPosition() == 0) {
                                    popupAssignToTicket.getAssignToSpinner().getSelectedView().requestFocus();
                                    popupAssignToTicket.getAssignToSpinner().setError(getResources().getString(R.string.error_assignment_to));
                                } else if(TextUtils.isEmpty(assignAction)) {
                                    popupAssignToTicket.getAssignmentActionET().requestFocus();
                                    popupAssignToTicket.getAssignmentActionET().setError(getResources().getString(R.string.label_assignment_action));
                                } else {
                                    String title = "Submission Confirmation";
                                    String msg = "You will assign \""+ CommonsUtil.ticketTypeToString(selectedTicket.getTicketType())+ "\" ticket with detail : \nLocation : "+
                                            selectedTicket.getStationName() +
                                            "\nSuspect : " + selectedTicket.getSuspect1Name() + " - " + selectedTicket.getSuspect2Name() + " - " + selectedTicket.getSuspect3Name() +
                                            "\nSeverity : " + CommonsUtil.severityToString(selectedTicket.getTicketSeverity()) +
                                            "\nto : " + popupAssignToTicket.getSelectedEngineer().getUserName() +
                                            "\nnotes : " + assignAction;
                                    popConfirm = CustomPopConfirm.newInstance(title,msg,"Yes","No");
                                    popConfirm.setBackListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            popConfirm.dismiss();
                                        }
                                    });
                                    popConfirm.setProcessListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            popupAssignToTicket.dismiss();
                                            popConfirm.dismiss();

                                            new SubmitAssignToTask().execute();
                                        }
                                    });
                                    popConfirm.show(getFragmentManager(), null);
                                }
                            }
                        });

                        popupAssignToTicket.show(getSupportFragmentManager(), null);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
//            popupAssignmentTicket.show(getFragmentManager(), null);

        }
    }

    private class GetAssignToTask extends AsyncTask<Void, Void, Void> {

        String result;
        JSONObject object;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(
                        CommonsUtil.getAbsoluteUrl(Constants.URL_GET_ASSIGN_TO),
                        new FormBody.Builder()
                                .add(Constants.POSITION_ID,String.valueOf(preferences.getPreferencesInt(Constants.POSITION_ID)))
                                .add("ticket_id",selectedTicket.getTicketId())
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
                    Type type = new TypeToken<List<UserModel>>(){}.getType();
                    try {
                        engineers = gson.fromJson(object.getString("data"), type);
                        Logcat.i("engineer_size:" + engineers.size());

                        popupAssignToTicket = PopupAssignToTicket.newInstance("Assignment Ticket","Process","Back", engineers);
                        popupAssignToTicket.setBackListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupAssignToTicket.dismiss();
                            }
                        });
                        popupAssignToTicket.setProcessListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                assignAction = popupAssignToTicket.getAssignmentActionET().getText().toString();
                                if(popupAssignToTicket.getAssignToSpinner().getSelectedItemPosition() == 0) {
                                    popupAssignToTicket.getAssignToSpinner().getSelectedView().requestFocus();
                                    popupAssignToTicket.getAssignToSpinner().setError(getResources().getString(R.string.error_assignment_to));
                                } else if(TextUtils.isEmpty(assignAction)) {
                                    popupAssignToTicket.getAssignmentActionET().requestFocus();
                                    popupAssignToTicket.getAssignmentActionET().setError(getResources().getString(R.string.label_assignment_action));
                                } else {
                                    String title = "Submission Confirmation";
                                    String msg = "You will assign \""+ CommonsUtil.ticketTypeToString(selectedTicket.getTicketType())+ "\" ticket with detail : \nLocation : "+
                                            selectedTicket.getStationName() +
                                            "\nSuspect : " + selectedTicket.getSuspect1Name() + " - " + selectedTicket.getSuspect2Name() + " - " + selectedTicket.getSuspect3Name() +
                                            "\nSeverity : " + CommonsUtil.severityToString(selectedTicket.getTicketSeverity()) +
                                            "\nto : " + popupAssignToTicket.getSelectedEngineer().getUserName() +
                                            "\nnotes : " + assignAction;
                                    popConfirm = CustomPopConfirm.newInstance(title,msg,"Yes","No");
                                    popConfirm.setBackListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            popConfirm.dismiss();
                                        }
                                    });
                                    popConfirm.setProcessListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            popupAssignToTicket.dismiss();
                                            popConfirm.dismiss();

                                            new SubmitAssignToTask().execute();
                                        }
                                    });
                                    popConfirm.show(getFragmentManager(), null);
                                }
                            }
                        });

                        popupAssignToTicket.show(getSupportFragmentManager(), null);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
//            popupAssignmentTicket.show(getFragmentManager(), null);

        }
    }

    private class SubmitAssignmentTask extends AsyncTask<Void, Void, Void> {

        String result;
        JSONObject object;
        String engineerID_UPDRS;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            int i = 0;
            for (UserModel model:engineers) {
                if (String.valueOf(popupAssignmentTicket.getSelectedEngineer().getUserId())
                        .equals(String.valueOf(engineers.get(i).getUserId()))){
                    engineerID_UPDRS = String.valueOf(engineers.get(i).getId_updrs());
                }
                i++;
            }


        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(CommonsUtil.getAbsoluteUrl(Constants.URL_SUBMIT_ASSIGNMENT), new FormBody.Builder()
                        .add(Constants.PARAM_TICKET_ID, selectedTicket.getTicketId())
                        .add("from_user_id", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
//                        .add("to_user_id", String.valueOf(popupAssignmentTicket.getSelectedEngineer().getUserId()))
                        .add("to_user_id", String.valueOf(engineerID_UPDRS))
                        .add("date", assignDate)
                        .add("action_id", String.valueOf(CommonsUtil.assignTypeToInt(popupAssignmentTicket.getSelectedAssignType())))
                        .add("info", assignAction)
                        .add("onsite", assignType)
                        .add("req_remarks", "")
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
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();

        }
    }

    private class SubmitAssignmentTaskTotalassign extends AsyncTask<Void, Void, Void> {

        String result;
        JSONObject object;
        String engineerID_UPDRS;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            int i = 0;
            for (UserModel model:engineers) {
                if (String.valueOf(popupReAssignmentTicket.getSelectedEngineer().getUserId())
                        .equals(String.valueOf(engineers.get(i).getUserId()))){
                    engineerID_UPDRS = String.valueOf(engineers.get(i).getId_updrs());
                }
                i++;
            }


        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(CommonsUtil.getAbsoluteUrl(Constants.URL_SUBMIT_ASSIGNMENTV2), new FormBody.Builder()
                        .add(Constants.PARAM_TICKET_ID, selectedTicket.getTicketId())
                        .add("from_user_id", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
//                        .add("to_user_id", String.valueOf(popupAssignmentTicket.getSelectedEngineer().getUserId()))
                        .add("to_user_id", String.valueOf(engineerID_UPDRS))
                        .add("date", assignDate)
                        .add("action_id", String.valueOf(CommonsUtil.assignTypeToInt(popupReAssignmentTicket.getSelectedAssignType())))
                        .add("info", assignAction)
                        .add("onsite", assignType)
//                        .add("onsite", "0")
                        .add("req_remarks", "")
//                        .add("reason_assign", "reasonAssign")
                        .add("reason_assign", reasonAssign)
                        .build());
                Log.d(TAG, "chek_api: " + reasonAssign);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "chek_api: " + result);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                object = new JSONObject(result);
                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();

        }
    }

    private class SubmitAssignToTask extends AsyncTask<Void, Void, Void> {

        String result;
        JSONObject object;
        String engineerID_UPDRS;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            int i = 0;
            for (UserModel model:engineers) {
                if (String.valueOf(popupAssignToTicket.getSelectedEngineer().getUserId())
                        .equals(String.valueOf(engineers.get(i).getUserId()))){
                    engineerID_UPDRS = String.valueOf(engineers.get(i).getId_updrs());
                }
                i++;
            }


        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(CommonsUtil.getAbsoluteUrl(Constants.URL_SUBMIT_ASSIGNMENT_TO), new FormBody.Builder()
                        .add(Constants.PARAM_TICKET_ID, selectedTicket.getTicketId())
                        .add("from_user_id", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
//                        .add("to_user_id", String.valueOf(popupAssignmentTicket.getSelectedEngineer().getUserId()))
                        .add("to_user_id", String.valueOf(engineerID_UPDRS))
                        .add("date", "")
                        .add("action_id", "")
                        .add("info", assignAction)
                        .add("onsite", "")
                        .add("req_remarks", "")
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
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();

        }
    }

}
