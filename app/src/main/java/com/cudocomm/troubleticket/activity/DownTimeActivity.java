package com.cudocomm.troubleticket.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.TTSApplication;
import com.cudocomm.troubleticket.adapter.SeverityAdapter2;
import com.cudocomm.troubleticket.adapter.SeverityUpdateAdapter;
import com.cudocomm.troubleticket.adapter.Suspect1Adapter;
import com.cudocomm.troubleticket.adapter.Suspect2Adapter;
import com.cudocomm.troubleticket.adapter.Suspect3Adapter;
import com.cudocomm.troubleticket.adapter.holder.HSpinner;
import com.cudocomm.troubleticket.component.CustomPopConfirm;
import com.cudocomm.troubleticket.database.DatabaseHelper;
import com.cudocomm.troubleticket.database.dao.SeverityDAO;
import com.cudocomm.troubleticket.database.dao.SeverityUpdateDAO;
import com.cudocomm.troubleticket.database.dao.Suspect1DAO;
import com.cudocomm.troubleticket.database.dao.Suspect2DAO;
import com.cudocomm.troubleticket.database.dao.Suspect3DAO;
import com.cudocomm.troubleticket.database.model.SeverityModel;
import com.cudocomm.troubleticket.database.model.SeverityUpdateModel;
import com.cudocomm.troubleticket.database.model.Suspect1Model;
import com.cudocomm.troubleticket.database.model.Suspect2Model;
import com.cudocomm.troubleticket.database.model.Suspect3Model;
import com.cudocomm.troubleticket.database.model.Suspect4Model;
import com.cudocomm.troubleticket.model.SeverityUpdate;
import com.cudocomm.troubleticket.model.Ticket;
import com.cudocomm.troubleticket.model.penyebab.PenyebabNew;
import com.cudocomm.troubleticket.model.program.ProgramNew;
import com.cudocomm.troubleticket.service.trouble.PenyebabService;
import com.cudocomm.troubleticket.service.trouble.TrobleServcice;
import com.cudocomm.troubleticket.service.trouble.TroubleApplication;
import com.cudocomm.troubleticket.takephoto.TakeImage;
import com.cudocomm.troubleticket.util.ApiClient;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Logcat;
import com.ikovac.timepickerwithseconds.MyTimePickerDialog;
import com.ikovac.timepickerwithseconds.TimePicker;
import com.j256.ormlite.misc.TransactionManager;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.Optional;
import dmax.dialog.SpotsDialog;
import fr.ganfra.materialspinner.MaterialSpinner;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.HttpException;

import static com.cudocomm.troubleticket.TTSApplication.getContext;

public class DownTimeActivity extends BaseActivity {

    public static final String TAG = DownTimeActivity.class.getSimpleName();
    private CompositeDisposable subscription=new CompositeDisposable();

    private SpotsDialog progressDialog;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ticketCreatorET)
    EditText ticketCreatorET;
    @BindView(R.id.ticketTypeET)
    EditText ticketTypeET;
    @BindView(R.id.ticketStationET)
    EditText ticketStationET;
    @BindView(R.id.ticketRemarksET)
    EditText ticketRemarksET;

    @BindView(R.id.ticketPhoto1IV)
    ImageView ticketPhoto1IV;
    @BindView(R.id.ticketPhoto2IV)
    ImageView ticketPhoto2IV;
    @BindView(R.id.ticketPhoto3IV)
    ImageView ticketPhoto3IV;


    private String remarks;
    private File photo1, photo2, photo3;
    private String severityRslt;
    private String spc1,spc2,spc3,spc4;

    private TakeImage takeImage;
    private int RESIZE_PHOTO_PIXELS_PERCENTAGE = 80;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private CustomPopConfirm confDialog;


    List<Suspect1Model> suspect1Models;
    List<Suspect2Model> suspect2Models;
    List<Suspect3Model> suspect3Models;
    List<Suspect4Model> suspect4Models;
    Suspect1Model selectedSuspect1Model;
    Suspect2Model selectedSuspect2Model;
    Suspect3Model selectedSuspect3Model;
    Suspect4Model selectedSuspect4Model;

//    private MaterialSpinner suspectQtySpinner;

    @BindView(R.id.suspect1Spinner)
    MaterialSpinner suspect1Spinner;
    @BindView(R.id.suspect2Spinner)
    MaterialSpinner suspect2Spinner;
    @BindView(R.id.suspect3Spinner)
    MaterialSpinner suspect3Spinner;
    @BindView(R.id.severitySpinnerModel)
    MaterialSpinner severitySpinnerModel;

    @BindView(R.id.programSpin)
    SearchableSpinner programSpinner;
    @BindView(R.id.penyebabSpin)
    SearchableSpinner penyebabSpinner;

    private EditText startTimeET;
    private EditText durationET;

    TimePickerDialog.OnTimeSetListener startTimeCallback;
    TimePickerDialog.OnTimeSetListener durationCallback;

    @BindView(R.id.submitNewTicket)
    Button submitNewTicket;

    private Suspect2Adapter suspect2Adapter;
    private Suspect3Adapter suspect3Adapter;

    List<SeverityModel> severityModels;
    SeverityModel selectedSeverityModel;

    List<SeverityUpdateModel> severityUpdateModels;

    private int severityMenu;
    private int severityValue;
    ObservableArrayList<String> listProgram= new ObservableArrayList<>();
    ObservableArrayList<String> listPenyebab= new ObservableArrayList<>();

    long idPrgram;
    long idPenyebab;

    private String penyebab, program, startTime, duration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_time);

        startTimeET = (EditText) findViewById(R.id.startTimeET);
        durationET = (EditText) findViewById(R.id.durationET);
        spc1="";
        spc2="";
        spc3="";
        spc4="";

        ButterKnife.bind(this);
        initComponent();
    }

    private void initComponent() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selectedSuspect1Model = new Suspect1Model();
        selectedSuspect2Model = new Suspect2Model();
        selectedSuspect3Model = new Suspect3Model();
        selectedSuspect4Model = new Suspect4Model();

        selectedSeverityModel = new SeverityModel();
        severitySpinnerModel = (MaterialSpinner) findViewById(R.id.severitySpinnerModel);
        severitySpinnerModel.setEnabled(false);
//        suspectQtySpinner = (MaterialSpinner) findViewById(R.id.suspectQty);


        progressDialog = new SpotsDialog(this, R.style.progress_dialog_style);

        takeImage = new TakeImage(this, preferences);
        takeImage.checkSdcard();

//        ticketRemarksET.setFilters(new InputFilter[]{remarksSpaceValidator(ticketRemarksET)});

//
//        String[] qty={"1", "2", "3", "4","5","6","7","8","9","10"};
//        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this,
//                R.layout.support_simple_spinner_dropdown_item, qty);
//        suspectQtySpinner.setAdapter(adapterSpinner);

        new DownTimeTask().execute();
        SetTimeAndDuration();
        setProgramAcara();
        listProgram.clear();


    }

    public void SetTimeAndDuration(){
        startTimeCallback = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                String date = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second);
                startTimeET.setText(date);
            }
        };

        durationCallback = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                String date = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second);
                durationET.setText(date);
            }
        };

        startTimeET.setFocusable(true);
        durationET.setFocusable(true);

        startTimeET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    showDateTimePicker(startTimeET);
                }
            }
        });

//        startTimeET.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDateTimePicker(startTimeET);
//            }
//        });

//        durationET.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDateTimePicker(durationET);
//            }
//        });

        durationET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    showDateTimePicker(durationET);
                }
            }
        });
    }

    private void showDateTimePicker(final EditText et) {
        Calendar now = Calendar.getInstance();
        String nameResource = getResources().getResourceEntryName(et.getId());
        int hour = 0,
                min = 0,
                sec = 0;
        if (nameResource.equals("startTimeET")) {
            hour = now.get(Calendar.HOUR_OF_DAY);
            min = now.get(Calendar.MINUTE);
            sec = now.get(Calendar.SECOND);
        }
        MyTimePickerDialog mTimePicker = new MyTimePickerDialog(this, new MyTimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute, int seconds) {
                // TODO Auto-generated method stub
                et.setText(String.format("%02d", hourOfDay)+
                        ":" + String.format("%02d", minute) +
                        ":" + String.format("%02d", seconds));
            }
        }, hour, min, sec, true);
        mTimePicker.show();
    }

    private void setProgramAcara() {
        if (!subscription.isDisposed()) subscription.clear();
        TroubleApplication troubleApplication = TroubleApplication.get(getApplicationContext());
        troubleApplication.setTrobleServcice(TrobleServcice.Factory.createTroubleService(TrobleServcice.TYPE_GSON));
        TrobleServcice trobleServcice = troubleApplication.getTrobleServcice();
        subscription.add(trobleServcice.getProgram()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(troubleApplication.defaultSubscribeScheduler())
                .subscribeWith(new DisposableObserver<ProgramNew>() {

                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onNext(final ProgramNew programNew) {
                        Log.d(TAG, "onNext programNew: " + programNew);
                        for (int i = 0; i < programNew.getData().size(); i++) {
                            Log.d(TAG, "onNext PROGRAM: " + i);

                            listProgram.add(programNew.getData().get(i).getProgramName());
                            programSpinner.setEnabled(true);
                            ArrayAdapter adapterSubLocation = new ArrayAdapter<>(getContext(), R.layout.row_spinner2,
                                    R.id.spinnerValueTV, listProgram);
                            adapterSubLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            programSpinner.setAdapter(adapterSubLocation);
                            programSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    idPrgram = id;

                                    idPrgram = Long.parseLong(programNew.getData().get(position).getProgramId());
                                    program = String.valueOf(programSpinner.getSelectedItemId());





                                    Log.d(TAG, "onItemSelected Program: " + program);




                                    Log.d(TAG, "onItemSelected Program: " + (int) idPrgram);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
//                                }


//                            Log.d(TAG, "onNextManufacture: " + manufacture.getData().get(i).getNameManufacture());
//                                ArrayAdapter adapterSubLocation = new ArrayAdapter<>(getContext(), R.layout.spinners, listManufacure);


                        }
//                        setPenyebab();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.e(TAG, "onError: ",e );

                        if (isHttp404(e)) {
                            Toast.makeText(getApplicationContext(), "Failed Connected", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed Connected", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                }));

    }

    private static boolean isHttp404(Throwable error) {
        return error instanceof HttpException && ((HttpException) error).code() == 404;
    }

    private void setPenyebab() {
        if (!subscription.isDisposed()) subscription.clear();
        TroubleApplication troubleApplication = TroubleApplication.get(getApplicationContext());
        troubleApplication.setPenyebab(PenyebabService.Factory.createPenyebabService(TrobleServcice.TYPE_GSON));
        PenyebabService penyebabService = troubleApplication.getPenyebabService();
        subscription.add(penyebabService.getPenyebab()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(troubleApplication.defaultSubscribeScheduler())
                .subscribeWith(new DisposableObserver<PenyebabNew>() {

                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onNext(final PenyebabNew programNew) {
                        Log.d(TAG, "onNext programNew: " + programNew);
                        for (int i = 0; i < programNew.getData().size(); i++) {
                            Log.d(TAG, "onNext PROGRAM: " + i);

                            listPenyebab.add(programNew.getData().get(i).getPenyebab());
                            penyebabSpinner.setEnabled(true);
                            ArrayAdapter adapterSubLocation = new ArrayAdapter<>(getContext(), R.layout.row_spinner2,
                                    R.id.spinnerValueTV, listPenyebab);
                            adapterSubLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            penyebabSpinner.setAdapter(adapterSubLocation);
                            penyebabSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    idPenyebab = id;

                                    idPenyebab = Long.parseLong(programNew.getData().get(position).getId());
                                    penyebab = String.valueOf(penyebabSpinner.getSelectedItemId());





                                    Log.d(TAG, "onItemSelected Program: " + penyebab);


                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
//                                }


//                            Log.d(TAG, "onNextManufacture: " + manufacture.getData().get(i).getNameManufacture());
//                                ArrayAdapter adapterSubLocation = new ArrayAdapter<>(getContext(), R.layout.spinners, listManufacure);


                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.e(TAG, "onError: ",e );

                        if (isHttp404(e)) {
                            Toast.makeText(getApplicationContext(), "Failed Connected", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed Connected", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                }));
    }

    private InputFilter remarksSpaceValidator(final EditText editText) {
        InputFilter filter = new InputFilter() {
            boolean canEnterSpace = false;

            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {

                if(editText.getText().toString().equals(""))
                {
                    canEnterSpace = false;
                }

                StringBuilder builder = new StringBuilder();

                for (int i = start; i < end; i++) {
                    char currentChar = source.charAt(i);

                    if (Character.isLetterOrDigit(currentChar) || currentChar == '_') {
                        builder.append(currentChar);
                        canEnterSpace = true;
                    }

                    if(Character.isWhitespace(currentChar) && canEnterSpace) {
                        builder.append(currentChar);
                    }


                }
                return builder.toString();
            }

        };

        return filter;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(DownTimeActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    @Optional
    @OnClick({R.id.submitNewTicket, R.id.ticketPhoto1IV, R.id.ticketPhoto2IV, R.id.ticketPhoto3IV})
    public void actionPage(View view) {
        if(view.getId() == R.id.submitNewTicket) {
            startTime = startTimeET.getText().toString();
            duration = durationET.getText().toString();
            if(TextUtils.isEmpty(ticketRemarksET.getText())) {
                ticketRemarksET.requestFocus();
                ticketRemarksET.setError(getResources().getString(R.string.error_remarks_empty));
            } else if(ticketRemarksET.getText().length() < 60) {
                ticketRemarksET.requestFocus();
                ticketRemarksET.setError(getResources().getString(R.string.error_remarks_length));
            }
//            else if(ticketRemarksET.getText().toString().contains("\\s")) {
//                ticketRemarksET.requestFocus();
//                ticketRemarksET.setError(getResources().getString(R.string.error_remarks_length));
//            }
//            else if(ticketRemarksET.getText().toString().contains("\\s\\s")) {
//                ticketRemarksET.requestFocus();
//                ticketRemarksET.setError(getResources().getString(R.string.error_remarks_space));
//            }
            else if(suspect1Spinner.getSelectedItemPosition() == 0) {
                suspect1Spinner.getSelectedView().requestFocus();
                suspect1Spinner.setError(getResources().getString(R.string.error_suspect1_empty));
            } else if(suspect2Spinner.getSelectedItemPosition() == 0) {
                suspect2Spinner.getSelectedView().requestFocus();
                suspect2Spinner.setError(getResources().getString(R.string.error_suspect2_empty));
            } else if(suspect3Spinner.getSelectedItemPosition() == 0) {
                suspect3Spinner.getSelectedView().requestFocus();
                suspect3Spinner.setError(getResources().getString(R.string.error_suspect3_empty));
            } else if(severitySpinnerModel.getSelectedItemPosition() == 0) {
                severitySpinnerModel.getSelectedView().requestFocus();
                severitySpinnerModel.setError(getResources().getString(R.string.error_severity_empty));
            } else if(idPrgram == 0.0) {
                TextView errorText = (TextView)programSpinner.getSelectedView();
                errorText.setError(getResources().getString(R.string.error_close_program));
                errorText.setTextColor(Color.RED);//just to highlight that this is an error
                errorText.setText(getResources().getString(R.string.error_close_program));
            }  else if(TextUtils.isEmpty(startTime)) {
//                startTimeET.requestFocus();
                startTimeET.setError(getResources().getString(R.string.error_remarks_empty));
            } else if(TextUtils.isEmpty(duration)) {
//                durationET.requestFocus();
                durationET.setError(getResources().getString(R.string.error_remarks_empty));
            }else {
                Log.d("test222222","111111111111111");
                String title = "Submission Confirmation";
                String msg = "You will report \"DOWN TIME\" incident with detail : \nLocation : "+
                        preferences.getPreferencesString(Constants.STATION_NAME) +
                        "\nSuspect : " + selectedSuspect1Model.getSuspectName() + " - " + selectedSuspect2Model.getSuspectName() + " - " + selectedSuspect3Model.getSuspectName() +
                        "\nSeverity : " + selectedSeverityModel.getSeverityName();
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
                        remarks = ticketRemarksET.getText().toString();
                        new SubmitDownTimeTask().execute();
                        confDialog.dismiss();
                    }
                });
                confDialog.show(getFragmentManager(), null);
            }
        } else if(view.getId() == R.id.ticketPhoto1IV) {
            takeImage.setPathName(null);
            takeImage.dispatchTakePictureIntent(CAMERA_CAPTURE_IMAGE_REQUEST_CODE, ticketPhoto1IV);
        } else if(view.getId() == R.id.ticketPhoto2IV) {
            takeImage.setPathName(null);
            takeImage.dispatchTakePictureIntent(CAMERA_CAPTURE_IMAGE_REQUEST_CODE, ticketPhoto2IV);
        } else if(view.getId() == R.id.ticketPhoto3IV) {
            takeImage.setPathName(null);
            takeImage.dispatchTakePictureIntent(CAMERA_CAPTURE_IMAGE_REQUEST_CODE, ticketPhoto3IV);
        }
    }

    private void resetSeverity() {
        selectedSeverityModel.setSeverityId(null);
        selectedSeverityModel.setSeverityName(null);
        selectedSeverityModel.setSeverityTime(null);
    }

    @Optional
    @OnItemSelected({R.id.suspect1Spinner, R.id.suspect2Spinner, R.id.suspect3Spinner, R.id.severitySpinnerModel})
    public void actionSpinner(Spinner spinner, int position) {
//        if(suspectQtySpinner.getSelectedItemPosition()==3) {
////            if(position > -1) {
//                HSpinner hSpinner = new HSpinner(spinner);
//
//                selectedSuspect1Model.setSuspectId(new Integer(hSpinner.spinnerKeyTV.getText().toString()));
//                selectedSuspect1Model.setSuspectName(hSpinner.spinnerValueTV.getText().toString());
//
//                loadSuspect2Models(selectedSuspect1Model.getSuspectId());
//
//
//                spc1=selectedSuspect1Model.getSuspectId().toString();
//                Log.e(TAG, "kerusakan " + "modul="+ selectedSuspect1Model.getModuleId() + ";sp1="+selectedSuspect1Model.getSuspectId());
//                try {
////                      if (spc1.equals("57") || spc1.equals("78")) {
//                        severityRslt =
//                            ApiClient.post(
//                                CommonsUtil.getAbsoluteUrl("cek_severity"),
//                                new FormBody.Builder()
//                                    .add("spc1", selectedSuspect1Model.getSuspectId().toString())
////                                    .add("qty", suspectQtySpinner.getSelectedItem().toString())
//                                    .build());
////                      }else{
////                          severityRslt =
////                                ApiClient.post(
////                                  CommonsUtil.getAbsoluteUrl("cek_severity"),
////                                  new FormBody.Builder()
////                                          .add("spc1", selectedSuspect1Model.getSuspectId().toString())
////                                          .build());
////                      }
//                    JSONObject obj = null;
//                    try {
//                        obj = new JSONObject(severityRslt);
//                        Log.d(TAG, "severity_check: " +obj.getString("severity"));//                                totalSassign=Integer.parseInt(obj.getString("severity"));
//                        severitySpinnerModel.setSelection(Integer.parseInt(obj.getString("severity")));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                suspect2Spinner.setVisibility(View.VISIBLE);
//                if(spc1.equals("57") || spc1.equals("78")){
//                    suspectQtySpinner.setVisibility(View.VISIBLE);
//                }else{
//                    suspectQtySpinner.setVisibility(View.GONE);
//                }
//
////            }else {
////                resetSuspect3Model();
////                resetSuspect2Model();
////                resetSuspect1Model();
////                suspect2Spinner.setVisibility(View.GONE);
////                suspect3Spinner.setVisibility(View.GONE);
//////                suspectQtySpinner.setVisibility(View.GONE);
////            }
//        }
        if(spinner.getId() == R.id.suspect1Spinner ) {
            if(position > -1) {
                HSpinner hSpinner = new HSpinner(spinner);

                selectedSuspect1Model.setSuspectId(new Integer(hSpinner.spinnerKeyTV.getText().toString()));
                selectedSuspect1Model.setSuspectName(hSpinner.spinnerValueTV.getText().toString());

                spc1=selectedSuspect1Model.getSuspectId().toString();

                Log.d(TAG, "kerusakan " + "modul="+ selectedSuspect1Model.getModuleId() + ";sp1="+selectedSuspect1Model.getSuspectId());

                StringRequest request = new StringRequest(Request.Method.POST, CommonsUtil.getAbsoluteUrl("cek_severityv2"),
                        new Response.Listener<String>() {
                            public void onResponse(String response) {
                                try{
                                    JSONObject Obj = new JSONObject(response);
//                                  Object objSeveritas = Obj.getJSONObject("severities").get("severities");
//                                  Object objSeveritas = Obj.getJSONObject("severities");
                                    Log.d(TAG, "severity_check:" + "sp1=" + spc1 + ";Data=" + Obj.toString());
//                                  SeverityUpdate severityUpdate = gson.fromJson(objSeveritas.toString(), SeverityUpdate.class);

                                    SeverityUpdate severityUpdate = gson.fromJson(Obj.toString(), SeverityUpdate.class);
//                                  Log.d(TAG, "ptr.severity" + severityUpdate.getSeverities().get(0).getSeverityName().toString());
                                    updateSeverity(severityUpdate);
                                    severitySpinnerModel.setVisibility(View.VISIBLE);
                                    severityValue=Integer.parseInt(Obj.getString("severity"));
                                    severityMenu=Integer.parseInt(Obj.getString("severity_menu"));

                                    try {
                                        loadSuspect2Models(selectedSuspect1Model.getSuspectId());
                                        //sleep 5 seconds
                                        Thread.sleep(1000);

                                        if(severityMenu==0){
                                            severitySpinnerModel.setSelection(Integer.parseInt(Obj.getString("severity")));
                                            severitySpinnerModel.setFloatingLabelText("Auto Severity");
                                            severitySpinnerModel.setEnabled(false);
                                        }else if (severityMenu==1){
                                            severitySpinnerModel.setFloatingLabelText("Select Severity");
                                            severitySpinnerModel.setEnabled(true);
                                        }else if (severityMenu==2){
                                            severitySpinnerModel.setFloatingLabelText("Select Severity");
                                            severitySpinnerModel.setEnabled(true);
                                        }else if (severityMenu==3){
                                            severitySpinnerModel.setFloatingLabelText("Select Severity");
                                            severitySpinnerModel.setEnabled(true);
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError error) {
                                Logcat.e("Volley error: " + error.getMessage() + ", code: " + error.networkResponse);
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Terjadi Kesalahan. Umumnya karena masalah jaringan.", Toast.LENGTH_SHORT).show();
                            }
                        }
                )
                {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> items = new HashMap<>();
                        items.put("spc1", selectedSuspect1Model.getSuspectId().toString());
                        Logcat.e("params: " + items.toString());
                        return items;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("Content-Type", "application/x-www-form-urlencoded");
                        return params;
                    }
                };
                TTSApplication.getInstance().addToRequestQueue(request);
            }else {
                resetSuspect3Model();
                resetSuspect2Model();
                resetSuspect1Model();
                suspect2Spinner.setVisibility(View.GONE);
                suspect3Spinner.setVisibility(View.GONE);
           }
        }
        else if(spinner.getId() == R.id.suspect2Spinner) {
            if(position > -1) {
                HSpinner hSpinner = new HSpinner(spinner);
                selectedSuspect2Model.setSuspectId(new Integer(hSpinner.spinnerKeyTV.getText().toString()));
                selectedSuspect2Model.setSuspectName(hSpinner.spinnerValueTV.getText().toString());

                spc2=selectedSuspect2Model.getSuspectId().toString();
                Log.e(TAG, "kerusakan " + "modul="+ selectedSuspect1Model.getModuleId() +";sp1="+selectedSuspect1Model.getSuspectId()+"; sp2="+selectedSuspect2Model.getSuspectId());

                StringRequest request = new StringRequest(Request.Method.POST, CommonsUtil.getAbsoluteUrl("cek_severityv2"),
                        new Response.Listener<String>() {
                            public void onResponse(String response) {
                                try{
                                    JSONObject Obj = new JSONObject(response);
//                                  Object objSeveritas = Obj.getJSONObject("severities").get("severities");
//                                  Object objSeveritas = Obj.getJSONObject("severities");
//                                  Logcat.e("ptr.severity: " + severitas);
                                    Log.d(TAG, "severity_check:" + "sp2=" + spc2 + ";Data=" + Obj.toString());
//                                  SeverityUpdate severityUpdate = gson.fromJson(objSeveritas.toString(), SeverityUpdate.class);

                                    SeverityUpdate severityUpdate = gson.fromJson(Obj.toString(), SeverityUpdate.class);
//                                  Log.d(TAG, "ptr.severity" + severityUpdate.getSeverities().get(0).getSeverityName().toString());
                                    updateSeverity(severityUpdate);
                                    severitySpinnerModel.setVisibility(View.VISIBLE);
                                    severityValue=Integer.parseInt(Obj.getString("severity"));
                                    severityMenu=Integer.parseInt(Obj.getString("severity_menu"));
                                    try {
                                        loadSuspect3Models(selectedSuspect2Model.getSuspectId());
                                        //sleep 5 seconds
                                        Thread.sleep(1000);

                                        if(severityMenu==0){
                                            severitySpinnerModel.setSelection(Integer.parseInt(Obj.getString("severity")));
                                            severitySpinnerModel.setFloatingLabelText("Auto Severity");
                                            severitySpinnerModel.setEnabled(false);
                                        }else if (severityMenu==1){
                                            severitySpinnerModel.setFloatingLabelText("Select Severity");
                                            severitySpinnerModel.setEnabled(true);
                                        }else if (severityMenu==2){
                                            severitySpinnerModel.setFloatingLabelText("Select Severity");
                                            severitySpinnerModel.setEnabled(true);
                                        }else if (severityMenu==3){
                                            severitySpinnerModel.setFloatingLabelText("Select Severity");
                                            severitySpinnerModel.setEnabled(true);
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError error) {
                                Logcat.e("Volley error: " + error.getMessage() + ", code: " + error.networkResponse);
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Terjadi Kesalahan. Umumnya karena masalah jaringan.", Toast.LENGTH_SHORT).show();
                            }
                        }
                )
                {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> items = new HashMap<>();
                        items.put("spc1", spc1.toString());
                        items.put("spc2", selectedSuspect2Model.getSuspectId().toString());
                        Logcat.e("params: " + items.toString());
                        return items;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("Content-Type", "application/x-www-form-urlencoded");
                        return params;
                    }
                };
                TTSApplication.getInstance().addToRequestQueue(request);
            } else {
                resetSuspect3Model();
                resetSuspect2Model();
                suspect3Spinner.setVisibility(View.GONE);
            }
        } else if(spinner.getId() == R.id.suspect3Spinner) {
            if(position > -1) {
                HSpinner hSpinner = new HSpinner(spinner);
                selectedSuspect3Model.setSuspectId(new Integer(hSpinner.spinnerKeyTV.getText().toString()));
                selectedSuspect3Model.setSuspectName(hSpinner.spinnerValueTV.getText().toString());
                spc3=selectedSuspect3Model.getSuspectId().toString();

                spc3=selectedSuspect3Model.getSuspectId().toString();

                StringRequest request = new StringRequest(Request.Method.POST, CommonsUtil.getAbsoluteUrl("cek_severityv2"),
                        new Response.Listener<String>() {
                            public void onResponse(String response) {
                                try{
                                    JSONObject Obj = new JSONObject(response);
//                                          Object objSeveritas = Obj.getJSONObject("severities").get("severities");
//                                            Object objSeveritas = Obj.getJSONObject("severities");
//                                          Logcat.e("ptr.severity: " + severitas);
                                    Log.d(TAG, "severity_check:" + "sp3=" + spc3 + ";Data=" + Obj.toString());
//                                            SeverityUpdate severityUpdate = gson.fromJson(objSeveritas.toString(), SeverityUpdate.class);

                                    SeverityUpdate severityUpdate = gson.fromJson(Obj.toString(), SeverityUpdate.class);
//                                          Log.d(TAG, "ptr.severity" + severityUpdate.getSeverities().get(0).getSeverityName().toString());
                                    updateSeverity(severityUpdate);
                                    severitySpinnerModel.setVisibility(View.VISIBLE);
                                    severityValue=Integer.parseInt(Obj.getString("severity"));
                                    severityMenu=Integer.parseInt(Obj.getString("severity_menu"));
                                    try {
                                        //sleep 5 seconds
                                        Thread.sleep(1000);

                                        if(severityMenu==0){
                                            severitySpinnerModel.setSelection(Integer.parseInt(Obj.getString("severity")));
                                            severitySpinnerModel.setFloatingLabelText("Auto Severity");
                                            severitySpinnerModel.setEnabled(false);
                                        }else if (severityMenu==1){
                                            severitySpinnerModel.setFloatingLabelText("Select Severity");
                                            severitySpinnerModel.setEnabled(true);
                                        }else if (severityMenu==2){
                                            severitySpinnerModel.setFloatingLabelText("Select Severity");
                                            severitySpinnerModel.setEnabled(true);
                                        }else if (severityMenu==3){
                                            severitySpinnerModel.setFloatingLabelText("Select Severity");
                                            severitySpinnerModel.setEnabled(true);
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError error) {
                                Logcat.e("Volley error: " + error.getMessage() + ", code: " + error.networkResponse);
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Terjadi Kesalahan. Umumnya karena masalah jaringan.", Toast.LENGTH_SHORT).show();
                            }
                        }
                )
                {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> items = new HashMap<>();
                        items.put("spc1", spc1.toString());
                        items.put("spc2", spc2.toString());
                        items.put("spc3", selectedSuspect3Model.getSuspectId().toString());
                        Logcat.e("params: " + items.toString());
                        return items;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("Content-Type", "application/x-www-form-urlencoded");
                        return params;
                    }
                };
                TTSApplication.getInstance().addToRequestQueue(request);

            }else {
                resetSuspect3Model();
            }
        } else if(spinner.getId() == R.id.severitySpinnerModel) {
            if(position > -1) {
                HSpinner hSpinner = new HSpinner(spinner);
                selectedSeverityModel.setSeverityId(new Integer(hSpinner.spinnerKeyTV.getText().toString()));
                selectedSeverityModel.setSeverityName(hSpinner.spinnerValueTV.getText().toString());
            } else {
                resetSeverity();
            }
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Logcat.i("CAMERA GET::" + takeImage.getPathName());
                if(preferences.getPreferencesInt(Constants.IV_TARGET_DT) == ticketPhoto2IV.getId()) {
                    takeImage.handleCameraPhoto(ticketPhoto2IV, TakeImage.PhotoFrom.CAMERA, data);
                    photo2 = new File(takeImage.getPathThumbName());
                } else if(preferences.getPreferencesInt(Constants.IV_TARGET_DT) == ticketPhoto3IV.getId()) {
                    takeImage.handleCameraPhoto(ticketPhoto3IV, TakeImage.PhotoFrom.CAMERA, data);
                    photo3 = new File(takeImage.getPathThumbName());
                } else {
                    takeImage.handleCameraPhoto(ticketPhoto1IV, TakeImage.PhotoFrom.CAMERA, data);
                    photo1 = new File(takeImage.getPathThumbName());
                }
            }

        }

    }

    class DownTimeTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                suspect1Models = Suspect1DAO.readAllByModule(-11, -11, 1);
                severityModels = SeverityDAO.readAll(-11, -11);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            ticketCreatorET.setText(preferences.getPreferencesString(Constants.USER_NAME));
            ticketTypeET.setText(Constants.DOWN_TIME);
            ticketStationET.setText(preferences.getPreferencesString(Constants.STATION_NAME));

            ticketStationET.setEnabled(false);
            ticketCreatorET.setEnabled(false);
            ticketTypeET.setEnabled(false);

            if(suspect1Models.size()>0) {
                Suspect1Adapter suspect1Adapter = new Suspect1Adapter(getApplicationContext(), suspect1Models);
                suspect1Spinner.setAdapter(suspect1Adapter);
                suspect1Spinner.setVisibility(View.VISIBLE);
            }

            if(severityModels.size()>0) {
                SeverityAdapter2 severityAdapter2 = new SeverityAdapter2(getApplicationContext(), severityModels);
                severitySpinnerModel.setAdapter(severityAdapter2);
                severitySpinnerModel.setVisibility(View.VISIBLE);
            }

            progressDialog.dismiss();
        }

    }

    class SubmitDownTimeTask extends AsyncTask<Void, Void, Void> {

        String result = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                builder.addFormDataPart("ticket_type", "1")
                        .addFormDataPart("ticket_station_id", String.valueOf(preferences.getPreferencesString(Constants.STATION_ID)))
                        .addFormDataPart("ticket_suspect_id", String.valueOf("0"))
                        .addFormDataPart("ticket_creator_id", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                        .addFormDataPart("ticket_remarks", remarks)
                        .addFormDataPart("suspect1", String.valueOf(selectedSuspect1Model.getSuspectId()))
                        .addFormDataPart("suspect2", String.valueOf(selectedSuspect2Model.getSuspectId()))
                        .addFormDataPart("suspect3", String.valueOf(selectedSuspect3Model.getSuspectId()))
                        //----------------------------------------
                        .addFormDataPart("program", String.valueOf(idPrgram))
                        .addFormDataPart("start_time", startTime)
                        .addFormDataPart("duration", duration);

                if(photo1 != null)
                    builder.addFormDataPart("ticket_photo_1", photo1.getName(), RequestBody.create(MEDIA_TYPE_PNG, photo1));
                if(photo2 != null)
                    builder.addFormDataPart("ticket_photo_2", photo2.getName(), RequestBody.create(MEDIA_TYPE_PNG, photo2));
                if(photo3 != null)
                    builder.addFormDataPart("ticket_photo_3", photo3.getName(), RequestBody.create(MEDIA_TYPE_PNG, photo3));

                builder.addFormDataPart("ticket_severity", String.valueOf(selectedSeverityModel.getSeverityId()))
                        .addFormDataPart("ticket_status", "1");

//                result = ApiClient.post2(CommonsUtil.getAbsoluteUrl("new_ticket2"), builder);
                result = ApiClient.post2(CommonsUtil.getAbsoluteUrl("new_ticket2_dev"), builder);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Logcat.i("RESULT_POST_DOWN_TIME::" + result);

            try {
                JSONObject object = new JSONObject(result);
                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
//                    Ticket ticket = gsona.fromJson(object.getString("data").toString(), Ticket.class);
                    Ticket ticket = gson.fromJson(object.getString("data"), Ticket.class);
                    String title = ticket.getTicketId() + " - " + CommonsUtil.severityToString(ticket.getTicketSeverity()) + " - " + CommonsUtil.ticketTypeToString(ticket.getTicketType());
                    String content = "Site " + ticket.getStationName() + " is down since " + ticket.getTicketDate();
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
                    progressDialog.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                if (progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        }
    }

    private void loadSuspect2Models(final Integer parentId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.show();
                try {
                    suspect2Models = Suspect2DAO.readAllByParent(-11, -11, parentId);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if(suspect2Models.size()>0) {
                    suspect2Adapter = new Suspect2Adapter(getApplicationContext(), suspect2Models);
                    suspect2Spinner.setAdapter(suspect2Adapter);
                    suspect2Spinner.setVisibility(View.VISIBLE);

                }else {
                    resetSuspect3Model();
                    resetSuspect2Model();
                    suspect3Spinner.setVisibility(View.GONE);
                    suspect2Spinner.setVisibility(View.GONE);
                }

                progressDialog.dismiss();
            }
        });
    }

    private void loadSuspect3Models(final Integer parentId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.show();
                try {
                    suspect3Models = Suspect3DAO.readAllByParent(-11, -11, parentId);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if(suspect3Models.size()>0) {
                    suspect3Spinner.setVisibility(View.VISIBLE);
                    suspect3Adapter = new Suspect3Adapter(getApplicationContext(), suspect3Models);
                    suspect3Spinner.setAdapter(suspect3Adapter);

                }else {
                    resetSuspect3Model();
                    suspect3Spinner.setVisibility(View.GONE);
                }

                progressDialog.dismiss();
            }
        });
    }

    private void resetSuspect1Model() {
        selectedSuspect1Model.setSuspectId(null);
        selectedSuspect1Model.setSuspectName(null);
        selectedSuspect1Model.setModuleId(null);
    }

    private void resetSuspect2Model() {
        selectedSuspect2Model.setSuspectId(null);
        selectedSuspect2Model.setSuspectName(null);
        selectedSuspect2Model.setParentId(null);
    }

    private void resetSuspect3Model() {
        selectedSuspect3Model.setSuspectId(null);
        selectedSuspect3Model.setSuspectName(null);
        selectedSuspect3Model.setParentId(null);
    }

    /*
     * Creted By Ptr.nov
     *  Spinner For View Manipulation
     * */
    private void updateSeverity(final SeverityUpdate severityUpdate) {
//        Log.d(TAG, "ptr.severity" + importModel.getSeverities().get(0).getSeverityName().toString());
//        Log.d(TAG, "ptr.severity" + importModel.getSeverities().toArray().toString());//
//        final List<Severity> severity = importModel.getSeverities();
        final List<SeverityUpdateModel> severityUpdateModels = severityUpdate.getSeverityUpdateModels();
        Log.d(TAG, "ptr.severity" + severityUpdateModels.get(0).getSeverityName().toString());
    }

}
