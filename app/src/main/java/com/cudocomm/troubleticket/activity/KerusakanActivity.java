package com.cudocomm.troubleticket.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.TTSApplication;
import com.cudocomm.troubleticket.adapter.SeverityUpdateAdapter;
import com.cudocomm.troubleticket.adapter.SeverityUpdateAdapter2;
import com.cudocomm.troubleticket.database.DatabaseHelper;
import com.cudocomm.troubleticket.adapter.SeverityAdapter2;
import com.cudocomm.troubleticket.adapter.Suspect1Adapter;
import com.cudocomm.troubleticket.adapter.Suspect2Adapter;
import com.cudocomm.troubleticket.adapter.Suspect3Adapter;
import com.cudocomm.troubleticket.adapter.Suspect4Adapter;
import com.cudocomm.troubleticket.adapter.holder.HSpinner;
import com.cudocomm.troubleticket.adapter.holder.HSpinnerUpdate;
import com.cudocomm.troubleticket.component.CustomPopConfirm;
import com.cudocomm.troubleticket.database.dao.SeverityDAO;
import com.cudocomm.troubleticket.database.dao.SeverityUpdateDAO;
import com.cudocomm.troubleticket.database.dao.Suspect1DAO;
import com.cudocomm.troubleticket.database.dao.Suspect2DAO;
import com.cudocomm.troubleticket.database.dao.Suspect3DAO;
import com.cudocomm.troubleticket.database.dao.Suspect4DAO;
import com.cudocomm.troubleticket.database.model.SeverityModel;
import com.cudocomm.troubleticket.database.model.SeverityUpdateModel;
import com.cudocomm.troubleticket.database.model.Suspect1Model;
import com.cudocomm.troubleticket.database.model.Suspect2Model;
import com.cudocomm.troubleticket.database.model.Suspect3Model;
import com.cudocomm.troubleticket.database.model.Suspect4Model;
import com.j256.ormlite.misc.TransactionManager;
import com.cudocomm.troubleticket.model.ImportModel;
import com.cudocomm.troubleticket.model.MasterModel;
import com.cudocomm.troubleticket.model.SeverityUpdate;
import com.cudocomm.troubleticket.model.Ticket;
import com.cudocomm.troubleticket.takephoto.TakeImage;
import com.cudocomm.troubleticket.util.ApiClient;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Logcat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import dmax.dialog.SpotsDialog;
import fr.ganfra.materialspinner.MaterialSpinner;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class KerusakanActivity extends BaseActivity {

    private static final String TAG = "Kerusakan";
    private Toolbar toolbar;
    private SpotsDialog progressDialog;

    private EditText ticketRemarksET;

    private ImageView ticketPhoto1IV;
    private ImageView ticketPhoto2IV;
    private ImageView ticketPhoto3IV;
    private Button submitNewTicket;

    private String remarks;
    private String severityRslt;
    private String spc1,spc2,spc3,spc4;
    private File photo1, photo2, photo3;

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
    private MaterialSpinner suspect1Spinner, suspect2Spinner, suspect3Spinner, suspect4Spinner;
    private Suspect1Adapter suspect1Adapter;
    private Suspect2Adapter suspect2Adapter;
    private Suspect3Adapter suspect3Adapter;
    private Suspect4Adapter suspect4Adapter;
    private EditText ambilSeverity;

    List<SeverityModel> severityModels;
    SeverityModel selectedSeverityModel;

    //Load Data from API checkV2, just for View Spinner
    List<SeverityUpdateModel> severityUpdateModels;
//    SeverityUpdateModel selectedSeverityModel;

    private int severitySize = 0;

    //Load data severity [Critical,major,Minor] from database from login


    private MaterialSpinner severitySpinnerModel;
    private SeverityAdapter2 severityAdapter2;
    private SeverityUpdateAdapter2 severityUpdateAdapter2;
    private SeverityUpdateAdapter severityUpdateAdapter;
    private int severityMenu;
    private int severityValue;
    String[] severity_ar1={"Severity","Critical","Major","Minor"};
    String[] severity_ar2={"0","1","2","3"};
    String SeverityId="0";
    String SeverityNm="Severity";
//    https://stackoverflow.com/questions/11343570/android-create-a-spinner-with-items-that-have-a-hidden-value-and-display-some-te

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kerusakan);

        initComponent();
        spc1="";
        spc2="";
        spc3="";
        spc4="";
    }

    private void showConfirmSubmit() {
        String title = "Submission Confirmation";
        StringBuilder builder = new StringBuilder();
        builder.append("You will report \"KERUSAKAN\" incident with detail : \nLocation : ")
                .append(preferences.getPreferencesString(Constants.STATION_NAME))
                .append("\nSuspect : ");
        if(selectedSuspect1Model.getSuspectName() != null)
            builder.append(selectedSuspect1Model.getSuspectName());

        if(selectedSuspect2Model.getSuspectName() != null)
            builder.append(" - ").append(selectedSuspect2Model.getSuspectName());

        if(selectedSuspect3Model.getSuspectName() != null)
            builder.append(" - ").append(selectedSuspect3Model.getSuspectName());

//        builder.append("\nSeverity : ").append(selectedSeverityModel.getSeverityName());
        builder.append("\nSeverity : ").append(SeverityNm);

        String msg = builder.toString();
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
                new SubmitKerusakanTask().execute();
                confDialog.dismiss();
            }
        });
        confDialog.show(getFragmentManager(), null);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(KerusakanActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    private void checkSuspect() {
        if(!suspect2Models.isEmpty()) {
            Logcat.i("SUSPECT 2 SIZE ::" + suspect2Models.size());
            if(suspect2Spinner.getSelectedItemPosition() == 0) {
                suspect2Spinner.getSelectedView().requestFocus();
                suspect2Spinner.setError(getResources().getString(R.string.error_suspect2_empty));
            } else {
                if(!suspect3Models.isEmpty()) {
                    if(suspect3Spinner.getSelectedItemPosition() == 0) {
                        suspect3Spinner.getSelectedView().requestFocus();
                        suspect3Spinner.setError(getResources().getString(R.string.error_suspect3_empty));
                    } else {
                        if(!suspect4Models.isEmpty()) {
                            if(suspect4Spinner.getSelectedItemPosition() == 0) {
                                suspect4Spinner.getSelectedView().requestFocus();
                                suspect4Spinner.setError(getResources().getString(R.string.error_suspect4_empty));
                            } else {
                                showConfirmSubmit();
                            }
                        } else {
                            showConfirmSubmit();
                        }
                    }
                } else {
                    showConfirmSubmit();
                }
            }
        }  else {
            showConfirmSubmit();
        }
    }


    private void initComponent() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selectedSuspect1Model = new Suspect1Model();
        selectedSuspect2Model = new Suspect2Model();
        selectedSuspect3Model = new Suspect3Model();
        selectedSuspect4Model = new Suspect4Model();

        selectedSeverityModel = new SeverityModel();

        suspect1Spinner = (MaterialSpinner) findViewById(R.id.suspect1Spinner);
        suspect2Spinner = (MaterialSpinner) findViewById(R.id.suspect2Spinner);
        suspect3Spinner = (MaterialSpinner) findViewById(R.id.suspect3Spinner);
        suspect4Spinner = (MaterialSpinner) findViewById(R.id.suspect4Spinner);
        severitySpinnerModel = (MaterialSpinner) findViewById(R.id.severitySpinnerModel);
        severitySpinnerModel.setEnabled(false);
        progressDialog = new SpotsDialog(this, R.style.progress_dialog_style);
        ticketRemarksET = (EditText) findViewById(R.id.ticketRemarksET);
        ticketPhoto1IV = (ImageView) findViewById(R.id.ticketPhoto1IV);
        ticketPhoto2IV = (ImageView) findViewById(R.id.ticketPhoto2IV);
        ticketPhoto3IV = (ImageView) findViewById(R.id.ticketPhoto3IV);
        submitNewTicket = (Button) findViewById(R.id.submitNewTicket);

        takeImage = new TakeImage(this, preferences);
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
        });

        submitNewTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(ticketRemarksET.getText())) {
                    ticketRemarksET.requestFocus();
                    ticketRemarksET.setError(getResources().getString(R.string.error_remarks_empty));
                } else if(ticketRemarksET.getText().length() < 60) {
                    ticketRemarksET.requestFocus();
                    ticketRemarksET.setError(getResources().getString(R.string.error_remarks_length));
                }

                else if(severitySpinnerModel.getSelectedItemPosition() == 0) {
                    severitySpinnerModel.getSelectedView().requestFocus();
                    severitySpinnerModel.setError(getResources().getString(R.string.error_severity_empty));
                }

                else if(suspect1Spinner.getSelectedItemPosition() == 0) {
                    suspect1Spinner.getSelectedView().requestFocus();
                    suspect1Spinner.setError(getResources().getString(R.string.error_suspect1_empty));
                } else {
                    checkSuspect();
                }

            }
        });

        new KerusakanTask().execute();
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

    class KerusakanTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {

                suspect1Models = Suspect1DAO.readAllByModule(-11, -11, 2);
                severityModels = SeverityDAO.readAll(-11, -11);
                severityUpdateModels = SeverityUpdateDAO.readAll(-11, -11);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(suspect1Models.size()>0) {
                suspect1Adapter = new Suspect1Adapter(getApplicationContext(), suspect1Models);
                suspect1Spinner.setAdapter(suspect1Adapter);
                suspect1Spinner.setVisibility(View.VISIBLE);
            }


            suspect1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position > -1) {
                        HSpinner hSpinner = new HSpinner(view);
                        Log.d(TAG, "listSpiner " + "modul="+hSpinner.spinnerKeyTV);
                        selectedSuspect1Model.setSuspectId(new Integer(hSpinner.spinnerKeyTV.getText().toString()));
                        selectedSuspect1Model.setSuspectName(hSpinner.spinnerValueTV.getText().toString());

                        spc1=selectedSuspect1Model.getSuspectId().toString();




                        Log.d(TAG, "kerusakan " + "modul="+ selectedSuspect1Model.getModuleId() + ";sp1="+selectedSuspect1Model.getSuspectId());

                        StringRequest request = new StringRequest(Request.Method.POST, CommonsUtil.getAbsoluteUrl("cek_severityv2"),
                                new Response.Listener<String>() {
                                    public void onResponse(String response) {
                                        try{
                                            JSONObject Obj = new JSONObject(response);
//                                          Object objSeveritas = Obj.getJSONObject("severities").get("severities");
//                                            Object objSeveritas = Obj.getJSONObject("severities");
//                                          Logcat.e("ptr.severity: " + severitas);
                                            Log.d(TAG, "severity_check:" + "sp1=" + spc1 + ";Data=" + Obj.toString());
//                                            SeverityUpdate severityUpdate = gson.fromJson(objSeveritas.toString(), SeverityUpdate.class);

                                            SeverityUpdate severityUpdate = gson.fromJson(Obj.toString(), SeverityUpdate.class);
//                                          Log.d(TAG, "ptr.severity" + severityUpdate.getSeverities().get(0).getSeverityName().toString());
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
                                                    SeverityNm=severity_ar1[Integer.valueOf(Integer.parseInt(Obj.getString("severity")))].toString();
                                                    SeverityId=severity_ar2[Integer.valueOf(Integer.parseInt(Obj.getString("severity")))].toString();
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
//                                                Log.d(TAG, "severity_check212:" + severitySpinnerModel.getSelectedItemPosition());
//                                                Log.d(TAG, "severity_check212:" + severitySpinnerModel.getSelectedItem().toString());
//                                                Log.d(TAG, "severity_check212:" + Integer.parseInt(Obj.getString("severity")));
                                                Log.d(TAG, "severity_check212:" + severity_ar1[Integer.valueOf(Integer.parseInt(Obj.getString("severity")))].toString());
                                                Log.d(TAG, "severity_check212:" + severity_ar2[Integer.valueOf(Integer.parseInt(Obj.getString("severity")))].toString());
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                        Logcat.e("svr: " + severityAdapter2);
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
                    } else {
                        resetSuspect4Model();
                        resetSuspect3Model();
                        resetSuspect2Model();
                        resetSuspect1Model();
                        severitySpinnerModel.setVisibility(View.GONE);
                        suspect2Spinner.setVisibility(View.GONE);
                        suspect3Spinner.setVisibility(View.GONE);
                        suspect4Spinner.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            suspect2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position > -1) {
                        HSpinner hSpinner= new HSpinner(view);
                        selectedSuspect2Model.setSuspectId(new Integer(hSpinner.spinnerKeyTV.getText().toString()));
                        selectedSuspect2Model.setSuspectName(hSpinner.spinnerValueTV.getText().toString());


//                        suspect3Spinner.setVisibility(View.VISIBLE);

                        spc2=selectedSuspect2Model.getSuspectId().toString();
                        Log.e(TAG, "kerusakan " + "modul="+ selectedSuspect1Model.getModuleId() +";sp1="+selectedSuspect1Model.getSuspectId()+"; sp2="+selectedSuspect2Model.getSuspectId());

                        StringRequest request = new StringRequest(Request.Method.POST, CommonsUtil.getAbsoluteUrl("cek_severityv2"),
                                new Response.Listener<String>() {
                                    public void onResponse(String response) {
                                        try{
                                            JSONObject Obj = new JSONObject(response);
//                                          Object objSeveritas = Obj.getJSONObject("severities").get("severities");
//                                            Object objSeveritas = Obj.getJSONObject("severities");
//                                          Logcat.e("ptr.severity: " + severitas);
                                            Log.d(TAG, "severity_check:" + "sp2=" + spc2 + ";Data=" + Obj.toString());
//                                            SeverityUpdate severityUpdate = gson.fromJson(objSeveritas.toString(), SeverityUpdate.class);

                                            SeverityUpdate severityUpdate = gson.fromJson(Obj.toString(), SeverityUpdate.class);
//                                          Log.d(TAG, "ptr.severity" + severityUpdate.getSeverities().get(0).getSeverityName().toString());
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
                                                    SeverityNm=severity_ar1[Integer.valueOf(Integer.parseInt(Obj.getString("severity")))].toString();
                                                    SeverityId=severity_ar2[Integer.valueOf(Integer.parseInt(Obj.getString("severity")))].toString();
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
                                                Log.d(TAG, "severity_check212:" + severity_ar1[Integer.valueOf(Integer.parseInt(Obj.getString("severity")))].toString());
                                                Log.d(TAG, "severity_check212:" + severity_ar2[Integer.valueOf(Integer.parseInt(Obj.getString("severity")))].toString());
//                                                Log.d(TAG, "severity_check212:" + severitySpinnerModel.getSelectedItemPosition());
//                                                Log.d(TAG, "severity_check212:" + severitySpinnerModel.getSelectedItem().toString());
//                                                Log.d(TAG, "severity_check212:" + Integer.parseInt(Obj.getString("severity")));
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
//                                items.put("spc1", spc1.toString());
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

//                        try {
//                            severityRslt = ApiClient.post(
//                                    CommonsUtil.getAbsoluteUrl("cek_severityv2"),
//                                    new FormBody.Builder()
//                                            .add("spc1",spc1)
//                                            .add("spc2",selectedSuspect2Model.getSuspectId().toString())
//                                            .build());
//                            JSONObject Obj = null;
//                            try {
//                                 Obj = new JSONObject(severityRslt);
////                                Log.d(TAG, "severity_check: " +Obj.getString("severity"));
////                              severitySpinnerModel.setSelection(Integer.parseInt(obj.getString("severity")));
////                                Object objSeveritas = Obj.getJSONObject("severities").get("severities");
//                                Log.d(TAG, "severity_check:" + "sp2=" + spc2 + ";Data=" + Obj.toString());
//
//                                SeverityUpdate severityUpdate = gson.fromJson(Obj.toString(), SeverityUpdate.class);
//                                updateSeverity(severityUpdate);
//
//                                severityValue=Integer.parseInt(Obj.getString("severity"));
//                                severityMenu=Integer.parseInt(Obj.getString("severity_menu"));
//                                try {
//                                    loadSuspect3Models(selectedSuspect2Model.getSuspectId());
//                                    //sleep 5 seconds
//                                    Thread.sleep(1000);
//
//                                    if(severityMenu==0){
//                                        severitySpinnerModel.setSelection(Integer.parseInt(Obj.getString("severity")));
////                                    selectedSeverityModel.setSeverityId(Integer.parseInt(Obj.getString("severity")));
//                                        severitySpinnerModel.setFloatingLabelText("Auto Severity");
//                                        severitySpinnerModel.setEnabled(false);
//                                    }else if (severityMenu==1){
//                                        severitySpinnerModel.setFloatingLabelText("Select Severity");
//                                        severitySpinnerModel.setEnabled(true);
//                                    }else if (severityMenu==2){
//                                        severitySpinnerModel.setFloatingLabelText("Select Severity");
//                                        severitySpinnerModel.setEnabled(true);
//                                    }else if (severityMenu==3){
//                                        severitySpinnerModel.setFloatingLabelText("Select Severity");
//                                        severitySpinnerModel.setEnabled(true);
//                                    }
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }



                    } else {
                        resetSuspect4Model();
                        resetSuspect3Model();
                        resetSuspect2Model();
                        suspect3Spinner.setVisibility(View.GONE);
                        suspect4Spinner.setVisibility(View.GONE);
//                        severitySpinnerModel.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            suspect3Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position > -1) {
                        HSpinner hSpinner = new HSpinner(view);
                        selectedSuspect3Model.setSuspectId(new Integer(hSpinner.spinnerKeyTV.getText().toString()));
                        selectedSuspect3Model.setSuspectName(hSpinner.spinnerValueTV.getText().toString());

//                        severitySpinnerModel.setSelection(3);
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
                                                loadSuspect4Models(selectedSuspect3Model.getSuspectId());
                                                //sleep 5 seconds
                                                Thread.sleep(1000);


                                                if(severityMenu==0){
                                                    severitySpinnerModel.setSelection(Integer.parseInt(Obj.getString("severity")));
                                                    SeverityNm=severity_ar1[Integer.valueOf(Integer.parseInt(Obj.getString("severity")))].toString();
                                                    SeverityId=severity_ar2[Integer.valueOf(Integer.parseInt(Obj.getString("severity")))].toString();
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
//                                                Log.d(TAG, "severity_check212:" + severitySpinnerModel.getSelectedItemPosition());
//                                                Log.d(TAG, "severity_check212:" + severitySpinnerModel.getSelectedItem().toString());
//                                                Log.d(TAG, "severity_check212:" + Integer.parseInt(Obj.getString("severity")));
                                                Log.d(TAG, "severity_check212:" + severity_ar1[Integer.valueOf(Integer.parseInt(Obj.getString("severity")))].toString());
                                                Log.d(TAG, "severity_check212:" + severity_ar2[Integer.valueOf(Integer.parseInt(Obj.getString("severity")))].toString());
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
//                                items.put("spc1", spc1.toString());
//                                items.put("spc2", spc2.toString());
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
                    } else {
                        resetSuspect4Model();
                        resetSuspect3Model();
                        suspect4Spinner.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            suspect4Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position > -1) {
                        HSpinner hSpinner = new HSpinner(view);
                        selectedSuspect4Model.setSuspectId(new Integer(hSpinner.spinnerKeyTV.getText().toString()));
                        selectedSuspect4Model.setSuspectName(hSpinner.spinnerValueTV.getText().toString());
//
                        spc4=selectedSuspect4Model.getSuspectId().toString();

                        StringRequest request = new StringRequest(Request.Method.POST, CommonsUtil.getAbsoluteUrl("cek_severityv2"),
                                new Response.Listener<String>() {
                                    public void onResponse(String response) {
                                        try{
                                            JSONObject Obj = new JSONObject(response);
//                                          Object objSeveritas = Obj.getJSONObject("severities").get("severities");
//                                            Object objSeveritas = Obj.getJSONObject("severities");
//                                          Logcat.e("ptr.severity: " + severitas);
                                            Log.d(TAG, "severity_check:" + "sp4=" + spc4 + ";Data=" + Obj.toString());
//                                            SeverityUpdate severityUpdate = gson.fromJson(objSeveritas.toString(), SeverityUpdate.class);

                                            SeverityUpdate severityUpdate = gson.fromJson(Obj.toString(), SeverityUpdate.class);
//                                          Log.d(TAG, "ptr.severity" + severityUpdate.getSeverities().get(0).getSeverityName().toString());
                                            updateSeverity(severityUpdate);
                                            severitySpinnerModel.setVisibility(View.VISIBLE);
                                            severityValue=Integer.parseInt(Obj.getString("severity"));
                                            severityMenu=Integer.parseInt(Obj.getString("severity_menu"));
                                            try {
//                                              //sleep 5 seconds
                                                Thread.sleep(1000);
                                                if(severityMenu==0){
                                                    severitySpinnerModel.setSelection(Integer.parseInt(Obj.getString("severity")));
                                                    SeverityNm=severity_ar1[Integer.valueOf(Integer.parseInt(Obj.getString("severity")))].toString();
                                                    SeverityId=severity_ar2[Integer.valueOf(Integer.parseInt(Obj.getString("severity")))].toString();
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
//                                                Log.d(TAG, "severity_check212:" + severitySpinnerModel.getSelectedItemPosition());
//                                                Log.d(TAG, "severity_check212:" + severitySpinnerModel.getSelectedItem().toString());
//                                                Log.d(TAG, "severity_check212:" + Integer.parseInt(Obj.getString("severity")));
                                                Log.d(TAG, "severity_check212:" + severity_ar1[Integer.valueOf(Integer.parseInt(Obj.getString("severity")))].toString());
                                                Log.d(TAG, "severity_check212:" + severity_ar2[Integer.valueOf(Integer.parseInt(Obj.getString("severity")))].toString());
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
//                                items.put("spc1", spc1.toString());
//                                items.put("spc2", spc2.toString());
//                                items.put("spc3", spc3.toString());
                                items.put("spc4", selectedSuspect4Model.getSuspectId().toString());
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
                        resetSuspect4Model();
//                        severitySpinnerModel.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

      //            if(severityUpdateModels.size()>0) {
      //                severityUpdateAdapter2 = new SeverityUpdateAdapter2(getApplicationContext(),
      // severityUpdateModels);
      //                severitySpinnerModel.setAdapter(severityUpdateAdapter2);
      //                severitySpinnerModel.setVisibility(View.VISIBLE);
      //
      //            }

      severitySpinnerModel.setOnItemSelectedListener(
          new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
              if (position > -1) {
                  SeverityNm=severity_ar1[parent.getSelectedItemPosition()];
                  SeverityId=severity_ar2[parent.getSelectedItemPosition()];

//                  Object item = parent.getItemAtPosition(position);
//                  HSpinnerUpdate hSpinnerUpdate = new HSpinnerUpdate(view);

//                  SeverityId=severity_ar2[selectedSeverityModel.getSeverityId()].toString();
//                  Log.d(
//                    TAG,
//                    "caritau:" + ";Data=" + severitySpinnerModel.getAdapter().getItemViewType(position) );
////                                       try {
//
//                                severityModels = SeverityDAO.readAll(-11, -11);
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                HSpinnerUpdate hSpinnerUpdate = new HSpinnerUpdate(view);
//                  selectedSeverityModel.setSeverityId(1);
//                  selectedSeverityModel.setSeverityName("piter streess");
//                selectedSeverityModel.setSeverityId(
//                    Integer.valueOf(hSpinnerUpdate.spinnerKeyTV.getText().toString()));
//                selectedSeverityModel.setSeverityName(
//                    hSpinnerUpdate.spinnerValueTV.getText().toString());
//                Log.d(
//                    TAG,
//                    "caritau:" + ";Data=" + hSpinnerUpdate.spinnerValueTV.getText().toString());

              } else {
                                        resetSeverity();
              }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
          });

            progressDialog.dismiss();
        }

    }

    private void resetSeverity() {
        selectedSeverityModel.setSeverityId(null);
        selectedSeverityModel.setSeverityName(null);
        selectedSeverityModel.setSeverityTime(null);
    }












    class SubmitKerusakanTask extends AsyncTask<Void, Void, Void> {

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
                builder.addFormDataPart("ticket_type", "2")
                        .addFormDataPart("ticket_station_id", String.valueOf(preferences.getPreferencesString(Constants.STATION_ID)))
                        .addFormDataPart("ticket_suspect_id", String.valueOf("0"))
                        .addFormDataPart("ticket_creator_id", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                        .addFormDataPart("ticket_remarks", remarks)
                        .addFormDataPart("suspect1", String.valueOf(selectedSuspect1Model.getSuspectId()));

                if(selectedSuspect2Model.getSuspectId() != null)
                    builder.addFormDataPart("suspect2", String.valueOf(selectedSuspect2Model.getSuspectId()));
                if(selectedSuspect3Model.getSuspectId() != null)
                    builder.addFormDataPart("suspect3", String.valueOf(selectedSuspect3Model.getSuspectId()));

                if(photo1 != null)
                    builder.addFormDataPart("ticket_photo_1", photo1.getName(), RequestBody.create(MEDIA_TYPE_PNG, photo1));
                if(photo2 != null)
                    builder.addFormDataPart("ticket_photo_2", photo2.getName(), RequestBody.create(MEDIA_TYPE_PNG, photo2));
                if(photo3 != null)
                    builder.addFormDataPart("ticket_photo_3", photo3.getName(), RequestBody.create(MEDIA_TYPE_PNG, photo3));

//                builder.addFormDataPart("ticket_severity", String.valueOf(selectedSeverityModel.getSeverityId()))
                builder.addFormDataPart("ticket_severity", String.valueOf(SeverityId))
                        .addFormDataPart("ticket_status", "1");

                result = ApiClient.post2(CommonsUtil.getAbsoluteUrl("new_ticket2"), builder);
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
                }
            } catch (JSONException e) {
                e.printStackTrace();
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
                } else {
                    resetSuspect4Model();
                    resetSuspect3Model();
                    resetSuspect2Model();
                    suspect3Spinner.setVisibility(View.GONE);
                    suspect4Spinner.setVisibility(View.GONE);
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
                    suspect3Adapter = new Suspect3Adapter(getApplicationContext(), suspect3Models);
                    suspect3Spinner.setAdapter(suspect3Adapter);
                    suspect3Spinner.setVisibility(View.VISIBLE);
                } else {
                    resetSuspect4Model();
                    resetSuspect3Model();
                    suspect3Spinner.setVisibility(View.GONE);
                    suspect4Spinner.setVisibility(View.GONE);
                }

                progressDialog.dismiss();
            }
        });
    }

    private void loadSuspect4Models(final Integer parentId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.show();
                try {
                    suspect4Models = Suspect4DAO.readAllByParent(-11, -11, parentId);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if(suspect4Models.size()>0) {
                    suspect4Adapter = new Suspect4Adapter(getApplicationContext(), suspect4Models);
                    suspect4Spinner.setAdapter(suspect4Adapter);
                    suspect4Spinner.setVisibility(View.VISIBLE);
                } else {
                    resetSuspect4Model();
                    suspect4Spinner.setVisibility(View.GONE);
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

    private void resetSuspect4Model() {
        selectedSuspect4Model.setSuspectId(null);
        selectedSuspect4Model.setSuspectName(null);
        selectedSuspect4Model.setParentId(null);
    }


    /*
    * Creted By Ptr.nov
    * Update Severities
    *
    * */
    private void updateSeverity(final SeverityUpdate severityUpdate) {
//        Log.d(TAG, "ptr.severity" + importModel.getSeverities().get(0).getSeverityName().toString());
//        Log.d(TAG, "ptr.severity" + importModel.getSeverities().toArray().toString());//
//        final List<Severity> severity = importModel.getSeverities();
        final List<SeverityUpdateModel> severityUpdateModels = severityUpdate.getSeverityUpdateModels();
        Log.d(TAG, "ptr.severity" + severityUpdateModels.get(0).getSeverityName().toString());





//        try {
//      TransactionManager.callInTransaction(
//          DatabaseHelper.getInstance().getConnectionSource(),
//          new Callable<Object>() {
//            public Void call() throws Exception {
//              for (SeverityUpdateModel severityUpdateModel : severityUpdateModels) {
//                Log.d(TAG, "ptr.severity: " + severityUpdateModel.getSeverityName().toString());
//
//                try {
//                  Log.d(TAG,"ptr.severity: " + severityUpdateModel.getSeverityName().toString()+ "masuk");
//                  severitySize = SeverityUpdateDAO.readAll(-11, -11).size();
//                  if (severitySize == 0) {
//                    TransactionManager.callInTransaction(
//                        DatabaseHelper.getInstance().getConnectionSource(),
//                        new Callable<Object>() {
//                          public Void call() throws Exception {
//                            for (SeverityUpdateModel severityUpdateModel : severityUpdateModels) {
//                              SeverityUpdateDAO.create(severityUpdateModel);
//                            }
//                            return null;
//                          }
//                        });
//
//                  } else {
//                    TransactionManager.callInTransaction(
//                        DatabaseHelper.getInstance().getConnectionSource(),
//                        new Callable<Object>() {
//                          public Void call() throws Exception {
//                            SeverityUpdateDAO.deleteAll();
//                            for (SeverityUpdateModel severityUpdateModel : severityUpdateModels) {
//                              SeverityUpdateDAO.update(severityUpdateModel);
//                            }
//                            return null;
//                          }
//                        });
//                  }
//
//
//
//                } catch (SQLException e) {
//                  e.printStackTrace();
//                }
//              }
//
//              return null;
//            }
//          });
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }


        severityUpdateAdapter =new SeverityUpdateAdapter(getApplicationContext(), severityUpdateModels);
        severitySpinnerModel.setAdapter(severityUpdateAdapter);
        severitySpinnerModel.setVisibility(View.VISIBLE);
    }
}
