package com.cudocomm.troubleticket.fragment;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.adapter.ISuspectAdapter;
import com.cudocomm.troubleticket.adapter.SeverityAdapter;
import com.cudocomm.troubleticket.adapter.holder.HSpinner;
import com.cudocomm.troubleticket.component.CustomPopConfirm;
import com.cudocomm.troubleticket.model.ISuspect;
import com.cudocomm.troubleticket.model.ImportModel;
import com.cudocomm.troubleticket.model.Severity;
import com.cudocomm.troubleticket.model.Ticket;
import com.cudocomm.troubleticket.takephoto.TakeImage;
import com.cudocomm.troubleticket.util.ApiClient;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Logcat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;
import fr.ganfra.materialspinner.MaterialSpinner;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class DownTimeFragment extends BaseFragment {

    private View rootView;

    private SpotsDialog progressDialog;

    private EditText ticketCreatorET;
    private EditText ticketTypeET;
    private EditText ticketStationET;
    private EditText ticketRemarksET;

    private MaterialSpinner suspectSpinner1;
    private MaterialSpinner suspectSpinner2;
    private MaterialSpinner suspectSpinner3;
    private MaterialSpinner severitySpinner;

    private ImageView ticketPhoto1IV;
    private ImageView ticketPhoto2IV;
    private ImageView ticketPhoto3IV;
    private Button submitNewTicket;

    private ISuspectAdapter iSuspectAdapter;
//    private SeverityAdapter severityAdapter;
//    private ArrayAdapter<String> severityAdapter;

    private List<ISuspect> downTimeSuspects1;
    private List<ISuspect> downTimeSuspects2;
    private List<ISuspect> downTimeSuspects3;
    private ISuspect selectedSuspect1;
    private ISuspect selectedSuspect2;
    private ISuspect selectedSuspect3;
//    private String selectedSeverity;
    private Severity selectedSeverity;
    private String remarks;
    private File photo1, photo2, photo3;

    private GsonBuilder gsonBuilder = new GsonBuilder();
    public Gson gson = this.gsonBuilder.create();

    private TakeImage takeImage;
    private int RESIZE_PHOTO_PIXELS_PERCENTAGE = 80;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private CustomPopConfirm confDialog;

    public DownTimeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_down_time, container, false);

        initComponent();
        updateComponent();

        return rootView;
    }

    private void initComponent() {
        selectedSuspect1 = new ISuspect();
        selectedSuspect2 = new ISuspect();
        selectedSuspect3 = new ISuspect();
        selectedSeverity = new Severity();
        progressDialog = new SpotsDialog(getActivity(), R.style.progress_dialog_style);
        ticketCreatorET = (EditText) rootView.findViewById(R.id.ticketCreatorET);
        ticketTypeET = (EditText) rootView.findViewById(R.id.ticketTypeET);
        ticketStationET = (EditText) rootView.findViewById(R.id.ticketStationET);
        ticketRemarksET = (EditText) rootView.findViewById(R.id.ticketRemarksET);
        suspectSpinner1 = (MaterialSpinner) rootView.findViewById(R.id.suspectSpinner1);
        suspectSpinner2 = (MaterialSpinner) rootView.findViewById(R.id.suspectSpinner2);
        suspectSpinner3 = (MaterialSpinner) rootView.findViewById(R.id.suspectSpinner3);
        severitySpinner = (MaterialSpinner) rootView.findViewById(R.id.severitySpinner);
        ticketPhoto1IV = (ImageView) rootView.findViewById(R.id.ticketPhoto1IV);
        ticketPhoto2IV = (ImageView) rootView.findViewById(R.id.ticketPhoto2IV);
        ticketPhoto3IV = (ImageView) rootView.findViewById(R.id.ticketPhoto3IV);
        submitNewTicket = (Button) rootView.findViewById(R.id.submitNewTicket);

    }

    private void updateComponent() {
        takeImage = new TakeImage(getActivity(), this, preferences);
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
                } else if(suspectSpinner1.getSelectedItemPosition() == 0) {
                    suspectSpinner1.getSelectedView().requestFocus();
                    suspectSpinner1.setError(getResources().getString(R.string.error_suspect1_empty));
                } else if(suspectSpinner2.getSelectedItemPosition() == 0) {
                    suspectSpinner2.getSelectedView().requestFocus();
                    suspectSpinner2.setError(getResources().getString(R.string.error_suspect2_empty));
                } else if(suspectSpinner3.getSelectedItemPosition() == 0) {
                    suspectSpinner3.getSelectedView().requestFocus();
                    suspectSpinner3.setError(getResources().getString(R.string.error_suspect3_empty));
                } else if(severitySpinner.getSelectedItemPosition() == 0) {
                    severitySpinner.getSelectedView().requestFocus();
                    severitySpinner.setError(getResources().getString(R.string.error_severity_empty));
                } else {
                    String title = "Submission Confirmation";
                    String msg = "You will report \"DOWN TIME\" incident with detail : \nLocation : "+
                            preferences.getPreferencesString(Constants.STATION_NAME) +
                            "\nSuspect : " + selectedSuspect1.getSuspectName() + " - " + selectedSuspect2.getSuspectName() + " - " + selectedSuspect3.getSuspectName() +
                            "\nSeverity : " + selectedSeverity.getSeverityName();
                    confDialog = CustomPopConfirm.newInstance(title,msg,"Process","Back");
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
                    confDialog.show(getActivity().getFragmentManager(), null);
                }


            }
        });

        new DownTimeTask().execute();
    }

    private void resetSuspect1() {
        selectedSuspect1.setSuspectId(null);
        selectedSuspect1.setSuspectName(null);
        selectedSuspect1.setParentId(null);
    }

    private void resetSuspect2() {
        selectedSuspect2.setSuspectId(null);
        selectedSuspect2.setSuspectName(null);
        selectedSuspect2.setParentId(null);
    }

    private void resetSuspect3() {
        selectedSuspect3.setSuspectId(null);
        selectedSuspect3.setSuspectName(null);
        selectedSuspect3.setParentId(null);
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

        ImportModel importModel;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                String json = CommonsUtil.readJSONFromAsset(Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name) + File.separator + Constants.DEFAULT_FILE_NAME);
                importModel = gson.fromJson(json, ImportModel.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            /*
            List<Station> stationList = importModel.getStations();
            for(Station station : stationList) {
                Logcat.i("STATION_::"+station.getStationName());
                stations.add(station.getStationName());
            }
*/
            ticketCreatorET.setText(preferences.getPreferencesString(Constants.USER_NAME));
            ticketTypeET.setText(Constants.DOWN_TIME);
            ticketStationET.setText(preferences.getPreferencesString(Constants.STATION_NAME));

            ticketStationET.setEnabled(false);
            ticketCreatorET.setEnabled(false);
            ticketTypeET.setEnabled(false);

            List<ISuspect> downTimeSuspects1 = importModel.getDownTimeSuspects1();
            if(downTimeSuspects1.size()>0) {
                iSuspectAdapter = new ISuspectAdapter(context, downTimeSuspects1);
                suspectSpinner1.setAdapter(iSuspectAdapter);

            }

            suspectSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position > -1) {
                        HSpinner hSpinner = new HSpinner(view);
                        loadSuspect2(hSpinner.spinnerKeyTV.getText().toString());
                        selectedSuspect1.setSuspectId(hSpinner.spinnerKeyTV.getText().toString());
                        selectedSuspect1.setSuspectName(hSpinner.spinnerValueTV.getText().toString());
                        suspectSpinner2.setVisibility(View.VISIBLE);
                    } else {
                        resetSuspect3();
                        resetSuspect2();
                        resetSuspect1();
                        suspectSpinner2.setVisibility(View.GONE);
                        suspectSpinner3.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            suspectSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position > -1) {
                        HSpinner hSpinner = new HSpinner(view);
                        loadSuspect3(hSpinner.spinnerKeyTV.getText().toString());
                        selectedSuspect2.setSuspectId(hSpinner.spinnerKeyTV.getText().toString());
                        selectedSuspect2.setSuspectName(hSpinner.spinnerValueTV.getText().toString());
                        suspectSpinner3.setVisibility(View.VISIBLE);
                    } else {
                        resetSuspect3();
                        resetSuspect2();
                        suspectSpinner3.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            suspectSpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position > -1) {
                        HSpinner hSpinner = new HSpinner(view);
                        selectedSuspect3.setSuspectId(hSpinner.spinnerKeyTV.getText().toString());
                        selectedSuspect3.setSuspectName(hSpinner.spinnerValueTV.getText().toString());
                    } else {
                        resetSuspect3();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            List<Severity> severities = importModel.getSeverities();
            if(severities.size()>0) {
                SeverityAdapter severityAdapter = new SeverityAdapter(context, severities);
                severitySpinner.setAdapter(severityAdapter);

            }

            severitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position > -1) {
                        HSpinner hSpinner = new HSpinner(view);
                        selectedSeverity.setSeverityId(hSpinner.spinnerKeyTV.getText().toString());
                        selectedSeverity.setSeverityName(hSpinner.spinnerValueTV.getText().toString());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            /*severityAdapter = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.severity));
            severityAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            severitySpinner.setAdapter(severityAdapter);
            severitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedSeverity = (String) parent.getSelectedItem();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    parent.setSelection(0);
                }
            });*/

//            locationAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, stations);
//            locationSpinner.setAdapter(locationAdapter);

            progressDialog.dismiss();
        }

    }

    private void loadSuspect2(String parentId) {
        RequestParams params = new RequestParams();
        params.put(Constants.PARAM_ID, parentId);
        ApiClient.setApplicationContext(context);
        ApiClient.post("downtime_suspect_2", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progressDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Type type = new TypeToken<List<ISuspect>>(){}.getType();
                try {
                    downTimeSuspects2 = gson.fromJson(response.getString("data"), type);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(downTimeSuspects2.size()>0) {
                    iSuspectAdapter = new ISuspectAdapter(context, downTimeSuspects2);
                    suspectSpinner2.setAdapter(iSuspectAdapter);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progressDialog.dismiss();
            }
        });
    }

    private void loadSuspect3(String parentId) {
        RequestParams params = new RequestParams();
        params.put(Constants.PARAM_ID, parentId);
        ApiClient.setApplicationContext(context);
        ApiClient.post("downtime_suspect_3", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progressDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Type type = new TypeToken<List<ISuspect>>(){}.getType();
                try {
                    downTimeSuspects3 = gson.fromJson(response.getString("data"), type);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(downTimeSuspects3.size()>0) {
                    iSuspectAdapter = new ISuspectAdapter(context, downTimeSuspects3);
                    suspectSpinner3.setAdapter(iSuspectAdapter);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progressDialog.dismiss();
            }
        });
    }


    class SubmitDownTimeTask extends AsyncTask<Void, Void, Void> {

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

                final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                builder.addFormDataPart("ticket_type", "1")
                        .addFormDataPart("ticket_station_id", String.valueOf(preferences.getPreferencesString(Constants.STATION_ID)))
                        .addFormDataPart("ticket_suspect_id", String.valueOf("0"))
                        .addFormDataPart("ticket_creator_id", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                        .addFormDataPart("ticket_remarks", remarks)
                        .addFormDataPart("suspect1", selectedSuspect1.getSuspectId())
                        .addFormDataPart("suspect2", selectedSuspect2.getSuspectId())
                        .addFormDataPart("suspect3", selectedSuspect3.getSuspectId());

                if(photo1 != null)
                    builder.addFormDataPart("ticket_photo_1", photo1.getName(), RequestBody.create(MEDIA_TYPE_PNG, photo1));
                if(photo2 != null)
                    builder.addFormDataPart("ticket_photo_2", photo2.getName(), RequestBody.create(MEDIA_TYPE_PNG, photo2));
                if(photo3 != null)
                    builder.addFormDataPart("ticket_photo_3", photo3.getName(), RequestBody.create(MEDIA_TYPE_PNG, photo3));

//                builder.addFormDataPart("ticket_severity", String.valueOf(CommonsUtil.severityToInt(selectedSeverity)))
                builder.addFormDataPart("ticket_severity", selectedSeverity.getSeverityId())
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
            Logcat.i("RESULT_POST_DOWN_TIME::" + result);

            try {
                JSONObject object = new JSONObject(result);
                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
//                    Ticket ticket = gsona.fromJson(object.getString("data").toString(), Ticket.class);
                    Ticket ticket = gsona.fromJson(object.getString("data"), Ticket.class);
                    String title = ticket.getTicketId() + " - " + CommonsUtil.severityToString(ticket.getTicketSeverity()) + " - " + CommonsUtil.ticketTypeToString(ticket.getTicketType());
                    String content = "Site " + ticket.getStationName() + " is down since " + ticket.getTicketDate();
                    NotificationManager mgr=
                            (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle(title)
                                    .setContentText(content);

                    Notification note = mBuilder.build();
                    mgr.notify(NotificationManager.IMPORTANCE_HIGH, note);

//                    finish();
                    getActivity().onBackPressed();
                    progressDialog.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
