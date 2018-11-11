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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.adapter.ViewPagerAdapter;
import com.cudocomm.troubleticket.component.CustomPopConfirm;
import com.cudocomm.troubleticket.component.PopupAssignToTicket;
import com.cudocomm.troubleticket.component.PopupAssignmentTicket;
import com.cudocomm.troubleticket.component.PopupCloseTicket;
import com.cudocomm.troubleticket.component.PopupCloseTicketCustom;
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

public class TicketActivity extends BaseActivity implements
        BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
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

    private CustomPopConfirm popConfirm;
    private PopupCloseTicket popupCloseTicket;
    private PopupCloseTicketV2 popupCloseTicketV2;
    private PopupCloseTicketCustom popupCloseTicketCustom;
    private PopupEscalationTicket popupEscalationTicket;
    private PopupAssignmentTicket popupAssignmentTicket;
    private PopupAssignToTicket popupAssignToTicket;
    private PopupGuidanceTicket popupGuidanceTicket;

    public int closedType = 1;

    private SpotsDialog progressDialog;

    private String additionalInfo;
    String prNo;
    private String actionDescribe;
    private String requireSupport;
    private String assignDate, assignAction;
    private String penyebab, program;
    private String startTime, duration;
    private String closedTypes;

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

    }

    private void updateComponent() {
        if(selectedTicket.getTicketStatus() == 1) {
            if(preferences.getPreferencesInt(Constants.ID_UPDRS) == selectedTicket.getTicketPosition()) {
//                if(preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_TS || preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_INFRA) {
                if (selectedTicket.getHasAssign().equals("1")){
                    actionLayout.setVisibility(View.VISIBLE);
                    actionLL.setVisibility(View.GONE);
                    actionKadepTSLL.setVisibility(View.GONE);
                    actionAssign.setVisibility(View.VISIBLE);
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
                    } else {
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
        } else {
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
    @OnClick({R.id.closedBtn, R.id.escalatedBtn, R.id.assignmentBtn, R.id.closedAssignmentBtn, R.id.assignmentToBtn, R.id.assignmentToBtn2,R.id.guidanceBtn})
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
                    }else if (preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_WIL
                                || preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_INFRA){
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
                                    closedTypes = popupCloseTicketCustom.getStringSpinnerItem();
//                                    penyebab = String.valueOf(popupCloseTicketV2.getPenyebabET());
//                                    program = String.valueOf(popupCloseTicketV2.getProgramET());
//                                    closedTypes = String.valueOf(popupCloseTicketV2.getPrNoET());
                                    prNo = popupCloseTicketCustom.getPrNoET().getText().toString();
//
                                    Log.d("addt11111",String.valueOf(prNo));
                                    if (closedTypes.equals("Close Type")) {
                                        popupCloseTicketCustom.getFixTypeSpinner().requestFocus();
                                        popupCloseTicketCustom.getFixTypeSpinner().setError(getResources().getString(R.string.error_close_type));
                                    }else if (closedTypes.equals("Closed by PR")) {
                                        if (additionalInfo.equals("")) {
                                            popupCloseTicketCustom.getTicketInfoET().setError(getResources().getString(R.string.error_close_info));
                                        }
                                        if (prNo.equals("")) {
                                            popupCloseTicketCustom.getPrNoET().setError(getResources().getString(R.string.error_pr_no));
                                        }

                                        if (!prNo.equals("") && !additionalInfo.equals("")) {
                                            popupCloseTicketCustom.dismiss();
                                            Log.d("berhasil", "close");
                                            Log.d("param111",selectedTicket.getTicketId()+","
                                                     +String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS))+","
                                                     +additionalInfo+","+closedTypes+","+prNo);
                                            new ClosedTicketTaskKERUSAKAN_WITHPR().execute();
                                        }
                                    }else if (closedTypes.equals("Fix Closed")) {
                                        if (additionalInfo.equals("")) {
                                            popupCloseTicketCustom.getTicketInfoET().setError(getResources().getString(R.string.error_close_info));
                                        }else {
                                            popupCloseTicketCustom.dismiss();
                                            Log.d("berhasil", "close");
                                            Log.d("param111",selectedTicket.getTicketId()+","
                                                    +String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS))+","
                                                    +additionalInfo+","+closedTypes+","+prNo);
                                            new ClosedTicketTaskKERUSAKAN_WITHPR().execute();
                                        }

                                    }
//
//                                    else if(TextUtils.isEmpty(additionalInfo)) {
//                                        popupCloseTicketCustom.getTicketInfoET().requestFocus();
//                                        popupCloseTicketCustom.getTicketInfoET().setError(getResources().getString(R.string.error_close_info));
//                                    } else {
//
//
//                                        String title = "Submission Confirmation";
//                                        String msg = "You will closed " + CommonsUtil.ticketTypeToString(selectedTicket.getTicketType()) +" incident with detail : \nLocation : "+
//                                                selectedTicket.getStationName() +
//                                                "\nSuspect : " + selectedTicket.getSuspect1Name() + " - " + selectedTicket.getSuspect2Name() + " - " + selectedTicket.getSuspect3Name() +
//                                                "\nSeverity : " + CommonsUtil.severityToString(selectedTicket.getTicketSeverity());
//                                        confDialog = CustomPopConfirm.newInstance(title,msg,"Yes","No");
//                                        confDialog.setBackListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                confDialog.dismiss();
//                                            }
//                                        });
//                                        confDialog.setProcessListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                popupCloseTicketCustom.dismiss();
//                                                confDialog.dismiss();
//                                                new ClosedTicketTaskKERUSAKAN_WITHPR().execute();
//
//                                            }
//                                        });
//                                        confDialog.show(getFragmentManager(), null);
//                                    }

                                }
                            });
                            popupCloseTicketCustom.show(getFragmentManager(), null);
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
            new GetEngineerTask().execute();

        } else if(view.getId() == R.id.assignmentToBtn) {
            new GetAssignToTask().execute();

        } else if(view.getId() == R.id.assignmentToBtn2) {
            new GetAssignToTask2().execute();

        } else if(view.getId() == R.id.closedAssignmentBtn) {
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
                    closedTypes = popupCloseTicketCustom.getStringSpinnerItem();
//                                    penyebab = String.valueOf(popupCloseTicketV2.getPenyebabET());
//                                    program = String.valueOf(popupCloseTicketV2.getProgramET());
//                                    closedTypes = String.valueOf(popupCloseTicketV2.getPrNoET());
                    prNo = popupCloseTicketCustom.getPrNoET().getText().toString();
//
                    Log.d("addt11111",String.valueOf(prNo));
                    if (closedTypes.equals("Close Type")) {
                        popupCloseTicketCustom.getFixTypeSpinner().requestFocus();
                        popupCloseTicketCustom.getFixTypeSpinner().setError(getResources().getString(R.string.error_close_type));
                    }else if (closedTypes.equals("Closed by PR")) {
                        if (additionalInfo.equals("")) {
                            popupCloseTicketCustom.getTicketInfoET().setError(getResources().getString(R.string.error_close_info));
                        }
                        if (prNo.equals("")) {
                            popupCloseTicketCustom.getPrNoET().setError(getResources().getString(R.string.error_pr_no));
                        }

                        if (!prNo.equals("") && !additionalInfo.equals("")) {
                            popupCloseTicketCustom.dismiss();
                            Log.d("berhasil", "close");
                            Log.d("param111",selectedTicket.getTicketId()+","
                                    +String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS))+","
                                    +additionalInfo+","+closedTypes+","+prNo);
                            new ClosedTicketTaskKERUSAKAN_WITHPR().execute();
                        }
                    }else if (closedTypes.equals("Fix Closed")) {
                        if (additionalInfo.equals("")) {
                            popupCloseTicketCustom.getTicketInfoET().setError(getResources().getString(R.string.error_close_info));
                        }else {
                            popupCloseTicketCustom.dismiss();
                            Log.d("berhasil", "close");
                            Log.d("param111",selectedTicket.getTicketId()+","
                                    +String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS))+","
                                    +additionalInfo+","+closedTypes+","+prNo);
                            new ClosedTicketTaskKERUSAKAN_WITHPR().execute();
                        }

                    }

                }
            });
            popupCloseTicketCustom.show(getFragmentManager(), null);

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
                result = ApiClient.post(CommonsUtil.getAbsoluteUrl("close_ticket_kerusakan_with_input_pr_no"), new FormBody.Builder()
                        .add("ticket_id", selectedTicket.getTicketId())
                        .add("ticket_closedby", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                        .add("additional_info", additionalInfo)
                        .add("closed_type", closedTypes)
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

            try {
                object = new JSONObject(result);
                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
                    Type type = new TypeToken<List<UserModel>>(){}.getType();
                    try {
                        engineers = gson.fromJson(object.getString("data"), type);
                        Logcat.i("engineer_size:" + engineers.size());

                        popupAssignmentTicket = PopupAssignmentTicket.newInstance("Assignment Ticket","Process","Back", engineers);
                        popupAssignmentTicket.setBackListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupAssignmentTicket.dismiss();
                            }
                        });
                        popupAssignmentTicket.setProcessListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                assignDate = popupAssignmentTicket.getAssignmentDateET().getText().toString();
                                assignAction = popupAssignmentTicket.getAssignmentActionET().getText().toString();
                                if(popupAssignmentTicket.getAssignToSpinner().getSelectedItemPosition() == 0) {
                                    popupAssignmentTicket.getAssignToSpinner().getSelectedView().requestFocus();
                                    popupAssignmentTicket.getAssignToSpinner().setError(getResources().getString(R.string.error_assignment_to));
                                } else if(popupAssignmentTicket.getAssignTypeSpinner().getSelectedItemPosition() == 0) {
                                    popupAssignmentTicket.getAssignTypeSpinner().getSelectedView().requestFocus();
                                    popupAssignmentTicket.getAssignTypeSpinner().setError(getResources().getString(R.string.error_assignment_type));
                                } else if(TextUtils.isEmpty(assignDate)) {
                                    popupAssignmentTicket.getAssignmentDateET().requestFocus();
                                    popupAssignmentTicket.getAssignmentDateET().setError(getResources().getString(R.string.error_assignment_date));
                                } else if(TextUtils.isEmpty(assignAction)) {
                                    popupAssignmentTicket.getAssignmentActionET().requestFocus();
                                    popupAssignmentTicket.getAssignmentActionET().setError(getResources().getString(R.string.label_assignment_action));
                                } else {
                                    String title = "Submission Confirmation";
                                    String msg = "You will assign \""+ CommonsUtil.ticketTypeToString(selectedTicket.getTicketType())+ "\" ticket with detail : \nLocation : "+
                                            selectedTicket.getStationName() +
                                            "\nSuspect : " + selectedTicket.getSuspect1Name() + " - " + selectedTicket.getSuspect2Name() + " - " + selectedTicket.getSuspect3Name() +
                                            "\nSeverity : " + CommonsUtil.severityToString(selectedTicket.getTicketSeverity()) +
                                            "\nto : " + popupAssignmentTicket.getSelectedEngineer().getUserName() +
                                            "\nfor : " + popupAssignmentTicket.getSelectedAssignType() +
                                            "\non : " + assignDate +
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
                                            popupAssignmentTicket.dismiss();
                                            popConfirm.dismiss();

                                            new SubmitAssignmentTask().execute();
                                        }
                                    });
                                    popConfirm.show(getFragmentManager(), null);
                                }
                            }
                        });

                        popupAssignmentTicket.show(getSupportFragmentManager(), null);

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
                        .add("onsite", "0")
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
