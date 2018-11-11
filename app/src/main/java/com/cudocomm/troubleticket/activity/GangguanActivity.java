package com.cudocomm.troubleticket.activity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.adapter.ProgramAdapter;
import com.cudocomm.troubleticket.component.CustomPopConfirm;
import com.cudocomm.troubleticket.database.model.Program;
import com.cudocomm.troubleticket.fragment.Home;
import com.cudocomm.troubleticket.model.Ticket;
import com.cudocomm.troubleticket.model.penyebab.PenyebabNew;
import com.cudocomm.troubleticket.model.program.ProgramNew;
import com.cudocomm.troubleticket.service.trouble.PenyebabService;
import com.cudocomm.troubleticket.service.trouble.TrobleServcice;
import com.cudocomm.troubleticket.service.trouble.TroubleApplication;
import com.cudocomm.troubleticket.util.ApiClient;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Logcat;
import com.cudocomm.troubleticket.util.Preferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ikovac.timepickerwithseconds.MyTimePickerDialog;
import com.ikovac.timepickerwithseconds.TimePicker;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import retrofit2.HttpException;

import static com.cudocomm.troubleticket.TTSApplication.getContext;

public class GangguanActivity extends AppCompatActivity {
    public static final String TAG = GangguanActivity.class.getSimpleName();
    private CompositeDisposable subscription=new CompositeDisposable();


    private Preferences preferences;

//    private Toolbar toolbar;
    private SpotsDialog progressDialog;

    private EditText penyebabET;
    private EditText programET;
    private EditText startTimeET;
    private EditText durationET;
    private EditText informationET;

    TimePickerDialog.OnTimeSetListener startTimeCallback;
    TimePickerDialog.OnTimeSetListener durationCallback;

    private String penyebab, program, startTime, duration, information;


    @BindView(R.id.programSpin)
    SearchableSpinner programSpinner;
    @BindView(R.id.penyebabSpin)
    SearchableSpinner penyebabSpinner;
    List<Program> programs;
    ProgramAdapter programAdapter;
    ArrayAdapter<Program> proAdapter;

    Program selectedProgram;

//    private EditText ticketCreatorET;
//    private EditText ticketTypeET;
//    private EditText ticketStationET;
//    private EditText ticketRemarksET;
//
//    private ImageView ticketPhoto1IV;
//    private ImageView ticketPhoto2IV;
//    private ImageView ticketPhoto3IV;
//    private Button submitNewTicket;


//    private String remarks;
//    private File photo1, photo2, photo3;

    private GsonBuilder gsonBuilder = new GsonBuilder();
    public Gson gson = this.gsonBuilder.create();
    Unbinder unbinder;

//    private TakeImage takeImage;
//    private int RESIZE_PHOTO_PIXELS_PERCENTAGE = 80;
//    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private CustomPopConfirm confDialog;


//    List<Suspect1Model> suspect1Models;
//    List<Suspect2Model> suspect2Models;
//    List<Suspect3Model> suspect3Models;
//    List<Suspect4Model> suspect4Models;
//    Suspect1Model selectedSuspect1Model;
//    Suspect2Model selectedSuspect2Model;
//    Suspect3Model selectedSuspect3Model;
//    Suspect4Model selectedSuspect4Model;
//    private MaterialSpinner suspect1Spinner, suspect2Spinner, suspect3Spinner;
//    private Suspect1Adapter suspect1Adapter;
//    private Suspect2Adapter suspect2Adapter;
//    private Suspect3Adapter suspect3Adapter;
//
//    List<SeverityModel> severityModels;
//    SeverityModel selectedSeverityModel;
//    private MaterialSpinner severitySpinnerModel;
//    private SeverityAdapter2 severityAdapter2;


    ObservableArrayList<String> listProgram= new ObservableArrayList<>();
    ObservableArrayList<String> listPenyebab= new ObservableArrayList<>();
    long idPrgram;
    long idPenyebab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gangguan);
        unbinder = ButterKnife.bind(this);

        initComponent();
    }

    private void initComponent() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = new Preferences(this);

//        selectedSuspect1Model = new Suspect1Model();
//        selectedSuspect2Model = new Suspect2Model();
//        selectedSuspect3Model = new Suspect3Model();
//        selectedSuspect4Model = new Suspect4Model();
//
//        selectedSeverityModel = new SeverityModel();

//        penyebabET = (EditText) findViewById(R.id.penyebabET);
//        programET = (EditText) findViewById(R.id.programET);
        startTimeET = (EditText) findViewById(R.id.startTimeET);
        durationET = (EditText) findViewById(R.id.durationET);
        informationET = (EditText) findViewById(R.id.ticketInfoET);

        programSpinner = (SearchableSpinner) findViewById(R.id.programSpin);
        penyebabSpinner = (SearchableSpinner) findViewById(R.id.penyebabSpin);


//        suspect1Spinner = (MaterialSpinner) findViewById(R.id.suspect1Spinner);
//        suspect2Spinner = (MaterialSpinner) findViewById(R.id.suspect2Spinner);
//        suspect3Spinner = (MaterialSpinner) findViewById(R.id.suspect3Spinner);
//        MaterialSpinner suspect4Spinner = (MaterialSpinner) findViewById(R.id.suspect4Spinner);

//        severitySpinnerModel = (MaterialSpinner) findViewById(R.id.severitySpinnerModel);

        progressDialog = new SpotsDialog(this, R.style.progress_dialog_style);
//        ticketCreatorET = (EditText) findViewById(R.id.ticketCreatorET);
//        ticketTypeET = (EditText) findViewById(R.id.ticketTypeET);
//        ticketStationET = (EditText) findViewById(R.id.ticketStationET);
//        ticketRemarksET = (EditText) findViewById(R.id.ticketRemarksET);
//
//        ticketPhoto1IV = (ImageView) findViewById(R.id.ticketPhoto1IV);
//        ticketPhoto2IV = (ImageView) findViewById(R.id.ticketPhoto2IV);
//        ticketPhoto3IV = (ImageView) findViewById(R.id.ticketPhoto3IV);
        Button submitNewTicket = (Button) findViewById(R.id.submitNewTicket);

        /*takeImage = new TakeImage(this, preferences);
        takeImage.checkSdcard();

        ticketPhoto1IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                takeImage.setPathName(null);
                takeImage.dispatchTakePictureIntent(CAMERA_CAPTURE_IMAGE_REQUEST_CODE, ticketPhoto1IV);

            }
        });

        ticketPhoto2IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeImage.setPathName(null);
                takeImage.dispatchTakePictureIntent(CAMERA_CAPTURE_IMAGE_REQUEST_CODE, ticketPhoto2IV);

            }
        });

        ticketPhoto3IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeImage.setPathName(null);
                takeImage.dispatchTakePictureIntent(CAMERA_CAPTURE_IMAGE_REQUEST_CODE, ticketPhoto3IV);
            }
        });*/

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

//        startTimeET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(hasFocus) {
//                    showDateTimePicker(startTimeET);
//                }
//            }
//        });

        startTimeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(startTimeET);
            }
        });

        durationET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    showDateTimePicker(durationET);
                }
            }
        });

//        durationET.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDateTimePicker(durationET);
//            }
//        });

        setProgramAcara();
        listProgram.clear();

//        setPenyebab();
//        listPenyebab.clear();

        submitNewTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(TextUtils.isEmpty(ticketRemarksET.getText())) {
                    ticketRemarksET.requestFocus();
                    ticketRemarksET.setError(getResources().getString(R.string.error_remarks_empty));
                } else if(suspect1Spinner.getSelectedItemPosition() == 0) {
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
                } else {
                    String title = "Submission Confirmation";
                    String msg = "You will report \"Gangguan AV\" incident with detail : \nLocation : "+
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
                }*/
//                penyebab = penyebabET.getText().toString();
//                program = programET.getText().toString();
                startTime = startTimeET.getText().toString();
                duration = durationET.getText().toString();
                information = informationET.getText().toString();
//                if(TextUtils.isEmpty(penyebab)) {
//                    penyebabET.requestFocus();
//                    penyebabET.setError(getResources().getString(R.string.error_remarks_empty));
//                } else if(TextUtils.isEmpty(program)) {
//                    programET.requestFocus();
//                    programET.setError(getResources().getString(R.string.error_remarks_empty));
//                }

                if(TextUtils.isEmpty(startTime)) {
                    startTimeET.requestFocus();
                    startTimeET.setError(getResources().getString(R.string.error_remarks_empty));
                } else if(TextUtils.isEmpty(duration)) {
                    durationET.requestFocus();
                    durationET.setError(getResources().getString(R.string.error_remarks_empty));
                } else if(TextUtils.isEmpty(information)) {
                    informationET.requestFocus();
                    informationET.setError(getResources().getString(R.string.error_close_info));
                } else if(idPrgram == 0.0) {
                    TextView errorText = (TextView)programSpinner.getSelectedView();
                    errorText.setError(getResources().getString(R.string.error_close_program));
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText(getResources().getString(R.string.error_close_program));
                } else if(idPenyebab == 0.0) {
                    TextView errorText = (TextView)penyebabSpinner.getSelectedView();
                    errorText.setError(getResources().getString(R.string.error_close_penyebab));
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText(getResources().getString(R.string.error_close_penyebab));
                }else {
                    String title = "Submission Confirmation";
                    String msg = "You will report \"Gangguan AV\" incident with detail : \nLocation : "+
                            preferences.getPreferencesString(Constants.STATION_NAME) +
                            "\nProgram Acara : " + program + " on " + startTime +
                            "\nDuration : " + duration + " sec";
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
                            new SubmitGangguan().execute();
                            confDialog.dismiss();
                        }
                    });
                    confDialog.show(getFragmentManager(), null);
                }


            }
        });

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(GangguanActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

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
                        setPenyebab();
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

    private static boolean isHttp404(Throwable error) {
        return error instanceof HttpException && ((HttpException) error).code() == 404;
    }

//    public void setProgramAcara(){
//        if(programs.size() > 0) {
//            programAdapter = new ProgramAdapter(getBaseContext(), programs);
//            programSpinner.setAdapter(programAdapter);
//
//            proAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, programs);
//            proAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            programSpinner.setAdapter(proAdapter);
//
//        }
//    }
//
//    @OnItemSelected({R.id.programSpin, R.id.penyebabSpin})
//    public void spinnerItemSelected(Spinner spinner, int position) {
//        switch (spinner.getId()) {
//            case R.id.programSpin :
//                if(position > -1) {
//                    selectedProgram = (Program) programSpinner.getSelectedItem();
//                }
//                break;
//            case R.id.penyebabSpin:
//                if(position > -1) {
//                    selectedProgram = (Program) programSpinner.getSelectedItem();
//                }
//                break;
//            default:
//                break;
//
//        }
//
//    }

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

    class SubmitGangguan extends AsyncTask<Void, Void, Void> {

        String result = "";
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gsona = this.gsonBuilder.create();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                builder.addFormDataPart("ticket_type", "3")
                        .addFormDataPart("ticket_station_id", String.valueOf(preferences.getPreferencesString(Constants.STATION_ID)))
//                        .addFormDataPart("ticket_suspect_id", String.valueOf("0"))
                        .addFormDataPart("ticket_creator_id", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                        .addFormDataPart("ticket_remarks", penyebab)
//                        .addFormDataPart("suspect1", "0")
//                        .addFormDataPart("suspect2", "0")
//                        .addFormDataPart("suspect3", "0");

//                builder.addFormDataPart("ticket_severity", "0")
                        .addFormDataPart("ticket_status", "1")
//                        .addFormDataPart("ticket_status", "0")
                        .addFormDataPart("penyebab", String.valueOf(idPenyebab))
                        .addFormDataPart("program", String.valueOf(idPrgram))
                        .addFormDataPart("start_time", startTime)
                        .addFormDataPart("duration", duration)
                        //---------------------------------------------
                        .addFormDataPart("info", information);

//                result = ApiClient.post2(CommonsUtil.getAbsoluteUrl("create_gangguan"), builder);
                result = ApiClient.post2(CommonsUtil.getAbsoluteUrl("create_gangguan_dev"), builder);
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
                    Ticket ticket = gsona.fromJson(object.getString("data"), Ticket.class);
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
                    Intent intent = new Intent(GangguanActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
