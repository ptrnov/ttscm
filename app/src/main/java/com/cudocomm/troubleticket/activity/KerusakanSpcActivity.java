package com.cudocomm.troubleticket.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.adapter.SeverityAdapter2;
import com.cudocomm.troubleticket.adapter.StationAdapter;
import com.cudocomm.troubleticket.adapter.Suspect1Adapter;
import com.cudocomm.troubleticket.adapter.Suspect2Adapter;
import com.cudocomm.troubleticket.adapter.Suspect3Adapter;
import com.cudocomm.troubleticket.adapter.Suspect4Adapter;
import com.cudocomm.troubleticket.adapter.holder.HSpinner;
import com.cudocomm.troubleticket.component.CustomPopConfirm;
import com.cudocomm.troubleticket.database.dao.SeverityDAO;
import com.cudocomm.troubleticket.database.dao.StationDAO;
import com.cudocomm.troubleticket.database.dao.Suspect1DAO;
import com.cudocomm.troubleticket.database.dao.Suspect2DAO;
import com.cudocomm.troubleticket.database.dao.Suspect3DAO;
import com.cudocomm.troubleticket.database.dao.Suspect4DAO;
import com.cudocomm.troubleticket.database.model.SeverityModel;
import com.cudocomm.troubleticket.database.model.StationModel;
import com.cudocomm.troubleticket.database.model.Suspect1Model;
import com.cudocomm.troubleticket.database.model.Suspect2Model;
import com.cudocomm.troubleticket.database.model.Suspect3Model;
import com.cudocomm.troubleticket.database.model.Suspect4Model;
import com.cudocomm.troubleticket.model.Ticket;
import com.cudocomm.troubleticket.takephoto.TakeImage;
import com.cudocomm.troubleticket.util.ApiClient;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Logcat;
import com.cudocomm.troubleticket.util.Preferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import dmax.dialog.SpotsDialog;
import fr.ganfra.materialspinner.MaterialSpinner;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class KerusakanSpcActivity extends AppCompatActivity {

    private Preferences preferences;

    private Toolbar toolbar;
    private SpotsDialog progressDialog;

    private EditText ticketRemarksET;

    private ImageView ticketPhoto1IV;
    private ImageView ticketPhoto2IV;
    private ImageView ticketPhoto3IV;
    private Button submitNewTicket;

    private String remarks;
    private File photo1, photo2, photo3;

    private GsonBuilder gsonBuilder = new GsonBuilder();
    public Gson gson = this.gsonBuilder.create();

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

    List<SeverityModel> severityModels;
    SeverityModel selectedSeverityModel;
    private MaterialSpinner severitySpinnerModel;
    private SeverityAdapter2 severityAdapter2;

    List<StationModel> stationModels;
    StationModel selectedStationModel;
    MaterialSpinner stationSpinner;
    StationAdapter stationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kerusakan_spc);

        initComponent();
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

        builder.append("\nSeverity : ").append(selectedSeverityModel.getSeverityName());

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
        preferences = new Preferences(this);

        selectedSuspect1Model = new Suspect1Model();
        selectedSuspect2Model = new Suspect2Model();
        selectedSuspect3Model = new Suspect3Model();
        selectedSuspect4Model = new Suspect4Model();

        selectedSeverityModel = new SeverityModel();

        selectedStationModel = new StationModel();
        stationSpinner = (MaterialSpinner) findViewById(R.id.stationSpinner);

        suspect1Spinner = (MaterialSpinner) findViewById(R.id.suspect1Spinner);
        suspect2Spinner = (MaterialSpinner) findViewById(R.id.suspect2Spinner);
        suspect3Spinner = (MaterialSpinner) findViewById(R.id.suspect3Spinner);
        suspect4Spinner = (MaterialSpinner) findViewById(R.id.suspect4Spinner);

        severitySpinnerModel = (MaterialSpinner) findViewById(R.id.severitySpinnerModel);

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
                stationModels = StationDAO.readAll(-11, -11);
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

                        selectedSuspect1Model.setSuspectId(new Integer(hSpinner.spinnerKeyTV.getText().toString()));
                        selectedSuspect1Model.setSuspectName(hSpinner.spinnerValueTV.getText().toString());

                        loadSuspect2Models(selectedSuspect1Model.getSuspectId());
//                        suspect2Spinner.setVisibility(View.VISIBLE);
                    } else {
                        resetSuspect4Model();
                        resetSuspect3Model();
                        resetSuspect2Model();
                        resetSuspect1Model();
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
                        HSpinner hSpinner = new HSpinner(view);
                        selectedSuspect2Model.setSuspectId(new Integer(hSpinner.spinnerKeyTV.getText().toString()));
                        selectedSuspect2Model.setSuspectName(hSpinner.spinnerValueTV.getText().toString());
                        loadSuspect3Models(selectedSuspect2Model.getSuspectId());
//                        suspect3Spinner.setVisibility(View.VISIBLE);
                    } else {
                        resetSuspect4Model();
                        resetSuspect3Model();
                        resetSuspect2Model();
                        suspect3Spinner.setVisibility(View.GONE);
                        suspect4Spinner.setVisibility(View.GONE);
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
                        loadSuspect4Models(selectedSuspect3Model.getSuspectId());
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
                    } else {
                        resetSuspect4Model();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            if(severityModels.size()>0) {
                severityAdapter2 = new SeverityAdapter2(getApplicationContext(), severityModels);
                severitySpinnerModel.setAdapter(severityAdapter2);
                severitySpinnerModel.setVisibility(View.VISIBLE);
            }

            severitySpinnerModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position > -1) {
                        HSpinner hSpinner = new HSpinner(view);

                        selectedSeverityModel.setSeverityId(new Integer(hSpinner.spinnerKeyTV.getText().toString()));
                        selectedSeverityModel.setSeverityName(hSpinner.spinnerValueTV.getText().toString());
                    } else {
                        resetSeverity();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            if(stationModels.size()>0) {
                stationAdapter = new StationAdapter(getApplicationContext(), stationModels);
                stationSpinner.setAdapter(stationAdapter);
                stationSpinner.setVisibility(View.VISIBLE);
            }

            stationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position > -1) {
                        HSpinner hSpinner = new HSpinner(view);

                        selectedStationModel.setStationId(new Integer(hSpinner.spinnerKeyTV.getText().toString()));
                        selectedStationModel.setStationName(hSpinner.spinnerValueTV.getText().toString());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
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
                        .addFormDataPart("ticket_position_id", String.valueOf(preferences.getPreferencesInt(Constants.POSITION_ID)))
                        .addFormDataPart("ticket_station_id", String.valueOf(selectedStationModel.getStationId()))
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

                builder.addFormDataPart("ticket_severity", String.valueOf(selectedSeverityModel.getSeverityId()))
                        .addFormDataPart("ticket_status", "1");

                result = ApiClient.post2(CommonsUtil.getAbsoluteUrl("kadep_create_ticket"), builder);
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


}
