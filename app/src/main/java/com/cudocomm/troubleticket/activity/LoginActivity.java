package com.cudocomm.troubleticket.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.TTSApplication;
import com.cudocomm.troubleticket.component.CustomPopConfirm;
import com.cudocomm.troubleticket.database.DatabaseHelper;
import com.cudocomm.troubleticket.database.dao.SeverityDAO;
import com.cudocomm.troubleticket.database.dao.StationDAO;
import com.cudocomm.troubleticket.database.dao.Suspect1DAO;
import com.cudocomm.troubleticket.database.dao.Suspect2DAO;
import com.cudocomm.troubleticket.database.dao.Suspect3DAO;
import com.cudocomm.troubleticket.database.dao.Suspect4DAO;
import com.cudocomm.troubleticket.database.dao.UserDAO;
import com.cudocomm.troubleticket.database.model.SeverityModel;
import com.cudocomm.troubleticket.database.model.StationModel;
import com.cudocomm.troubleticket.database.model.Suspect1Model;
import com.cudocomm.troubleticket.database.model.Suspect2Model;
import com.cudocomm.troubleticket.database.model.Suspect3Model;
import com.cudocomm.troubleticket.database.model.Suspect4Model;
import com.cudocomm.troubleticket.database.model.UserLoginModel;
import com.cudocomm.troubleticket.model.CounterModel;
import com.cudocomm.troubleticket.model.ImportModel;
import com.cudocomm.troubleticket.model.MasterModel;
import com.cudocomm.troubleticket.model.SeverityTime;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Logcat;
import com.google.firebase.iid.FirebaseInstanceId;
import com.j256.ormlite.misc.TransactionManager;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import dmax.dialog.SpotsDialog;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "Login";

    @BindView(R.id.signInRL)
    RelativeLayout signInRL;
    @BindView(R.id.usernameET)
    EditText usernameET;
    @BindView(R.id.passwordET)
    EditText passwordET;
    @BindView(R.id.loginBtn)
    Button loginBtn;

    private SpotsDialog progressDialog;
    private String username;
    private String password;
    private String token;

    private int stationSize = 0;
    private int severitySize = 0;
    private int suspect1Size = 0;
    private int suspect2Size = 0;
    private int suspect3Size = 0;
    private int suspect4Size = 0;

    CustomPopConfirm customPopConfirm;
    UserLoginModel currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        dialogUpdate();
//        cekVersionApp();
        ButterKnife.bind(this);

        progressDialog = new SpotsDialog(this, R.style.progress_dialog_style);
        if(sessionManager.isLoggedIn()) {
            postUpdateMaster();
        } else {
            FileUtils.deleteQuietly(getApplicationContext().getCacheDir());
            signInRL.setVisibility(View.VISIBLE);
        }

    }

    @Optional
    @OnClick(R.id.loginBtn)
    public void onClickLoginBtn(View view) {
        if(view.getId() == R.id.loginBtn) {
            username = usernameET.getText().toString().trim();
            password = passwordET.getText().toString().trim();
            token = FirebaseInstanceId.getInstance().getToken();
            if(TextUtils.isEmpty(username)) {
                usernameET.requestFocus();
                usernameET.setError(getResources().getString(R.string.error_email_empty));
            } else if(!CommonsUtil.emailValidator(username)) {
                usernameET.requestFocus();
                usernameET.setError(getResources().getString(R.string.error_email_not_valid));
            } else if(TextUtils.isEmpty(password)) {
                passwordET.requestFocus();
                passwordET.setError(getResources().getString(R.string.error_password_empty));
            } else {
                postLogin();
            }
        }
    }

    public void cekVersionApp() {
        final String version = String.valueOf(Constants._VERSION);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.URL_CEK_UPDATE_APK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logcat.e("response: " + response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            String status = jObj.getString("status");
                            String link = jObj.getString("link");

                            if (status.equals("true")){
                                dialogUpdate(link);
                            }else {
                                ButterKnife.bind(LoginActivity.this);

                                progressDialog = new SpotsDialog(LoginActivity.this, R.style.progress_dialog_style);
                                if(sessionManager.isLoggedIn()) {
                                    postUpdateMaster();
                                } else {
                                    FileUtils.deleteQuietly(getApplicationContext().getCacheDir());
                                    signInRL.setVisibility(View.VISIBLE);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Logcat.e("Volley error: " + error.getMessage() + ", code: " + error.networkResponse);
                        Toast.makeText(TTSApplication.getContext(), "Terjadi Kesalahan. Umumnya karena masalah jaringan.", Toast.LENGTH_SHORT).show();

                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("version", version);
                Logcat.e("params: " + params.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        TTSApplication.getInstance().addToRequestQueue(request);
    }

    public void dialogUpdate(final String link){
        customPopConfirm = CustomPopConfirm.newInstance(
                "Update Confirmation",
                "Update your application version!",
                "Yes", "No");
        customPopConfirm.setProcessListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://www.google.com";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(link));
                startActivity(i);
                finish();

            }
        });
        customPopConfirm.setBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        customPopConfirm.show(getFragmentManager(), null);

    }

    private void postLogin() {
        progressDialog.show();
        final JSONObject items = new JSONObject();
        try {
            items.put(Constants.PARAM_USERNAME, username);
            items.put(Constants.PARAM_PASSWORD, password);
            items.put(Constants.PARAM_TOKEN, token);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        StringRequest request = new StringRequest(Request.Method.POST, CommonsUtil.getAbsoluteUrl(Constants.URL_LOGIN),
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Logcat.e("response: " + response);
                        ImportModel importModel = gson.fromJson(response, ImportModel.class);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            if (jObj.getString("status").equalsIgnoreCase("failed")) {
                                progressDialog.dismiss();
                                passwordET.setError(getResources().getString(R.string.login_failed));
                                passwordET.requestFocus();
                                return;
                            }

                            currentUser = importModel.getUserModel().get(0);
                            sessionManager.createLoginSession(currentUser);

                            if(currentUser.getPositionId() == Constants.KST) {
                                CounterModel needApproval = importModel.getNeedApproval();
                                if(needApproval != null)
                                    preferences.savePreferences(Constants.COUNTER_NEED_APPROVAL, getResources().getString(R.string.widget_need_approval_counter, needApproval.getCritical(), needApproval.getMajor(), needApproval.getMinor()));
                                else
                                    preferences.savePreferences(Constants.COUNTER_NEED_APPROVAL, getResources().getString(R.string.widget_need_approval_counter_null));

                                CounterModel myTaskCounter = importModel.getMyTaskCounter();
                                if(myTaskCounter != null)
                                    preferences.savePreferences(Constants.COUNTER_MY_TASK, getResources().getString(R.string.widget_need_approval_counter, myTaskCounter.getCritical(), myTaskCounter.getMajor(), myTaskCounter.getMinor()));
                                else
                                    preferences.savePreferences(Constants.COUNTER_MY_TASK, getResources().getString(R.string.widget_need_approval_counter_null));

                                CounterModel totalTicketCounter = importModel.getTotalTicket();
                                if(totalTicketCounter != null)
                                    preferences.savePreferences(Constants.COUNTER_TOTAL, getResources().getString(R.string.widget_need_approval_counter, totalTicketCounter.getCritical(), totalTicketCounter.getMajor(), totalTicketCounter.getMinor()));
                                else
                                    preferences.savePreferences(Constants.COUNTER_TOTAL, getResources().getString(R.string.widget_need_approval_counter_null));

                            } else if(currentUser.getPositionId() == Constants.KORWIL) {
                                CounterModel myTaskCounter = importModel.getMyTaskCounter();
                                if(myTaskCounter != null)
                                    preferences.savePreferences(Constants.COUNTER_MY_TASK, getResources().getString(R.string.widget_need_approval_counter, myTaskCounter.getCritical(), myTaskCounter.getMajor(), myTaskCounter.getMinor()));
                                else
                                    preferences.savePreferences(Constants.COUNTER_MY_TASK, getResources().getString(R.string.widget_need_approval_counter_null));

                                CounterModel totalTicketCounter = importModel.getTotalTicket();
                                if(totalTicketCounter != null)
                                    preferences.savePreferences(Constants.COUNTER_TOTAL, getResources().getString(R.string.widget_need_approval_counter, totalTicketCounter.getCritical(), totalTicketCounter.getMajor(), totalTicketCounter.getMinor()));
                                else
                                    preferences.savePreferences(Constants.COUNTER_TOTAL, getResources().getString(R.string.widget_need_approval_counter_null));

                            } else if(currentUser.getPositionId() == Constants.KADEP_WIL) {
                                CounterModel myTaskCounter = importModel.getMyTaskCounter();
                                if(myTaskCounter != null)
                                    preferences.savePreferences(Constants.COUNTER_MY_TASK, getResources().getString(R.string.widget_need_approval_counter, myTaskCounter.getCritical(), myTaskCounter.getMajor(), myTaskCounter.getMinor()));
                                else
                                    preferences.savePreferences(Constants.COUNTER_MY_TASK, getResources().getString(R.string.widget_need_approval_counter_null));

                                CounterModel totalTicketCounter = importModel.getTotalTicket();
                                if(totalTicketCounter != null)
                                    preferences.savePreferences(Constants.COUNTER_TOTAL, getResources().getString(R.string.widget_need_approval_counter, totalTicketCounter.getCritical(), totalTicketCounter.getMajor(), totalTicketCounter.getMinor()));
                                else
                                    preferences.savePreferences(Constants.COUNTER_TOTAL, getResources().getString(R.string.widget_need_approval_counter_null));

                            } else if(currentUser.getPositionId() == Constants.KADEP_TS) {
                                CounterModel myTaskCounter = importModel.getMyTaskCounter();
                                if(myTaskCounter != null)
                                    preferences.savePreferences(Constants.COUNTER_MY_TASK, getResources().getString(R.string.widget_need_approval_counter, myTaskCounter.getCritical(), myTaskCounter.getMajor(), myTaskCounter.getMinor()));
                                else
                                    preferences.savePreferences(Constants.COUNTER_MY_TASK, getResources().getString(R.string.widget_need_approval_counter_null));

                                CounterModel totalTicketCounter = importModel.getTotalTicket();
                                if(totalTicketCounter != null)
                                    preferences.savePreferences(Constants.COUNTER_TOTAL, getResources().getString(R.string.widget_need_approval_counter, totalTicketCounter.getCritical(), totalTicketCounter.getMajor(), totalTicketCounter.getMinor()));
                                else
                                    preferences.savePreferences(Constants.COUNTER_TOTAL, getResources().getString(R.string.widget_need_approval_counter_null));

                            }

                            List<SeverityTime> severityTimes = importModel.getSeverityTimes();
                            for(SeverityTime time : severityTimes) {
                                preferences.savePreferences(time.getSeverityName().toLowerCase(), time.getSeverityTime());
                            }

                            importMaster(importModel);

                            progressDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
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
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(LoginActivity.this,"Error Koneksi",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(LoginActivity.this,"Error Auth",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(LoginActivity.this,"Error Server",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            //TODO
                            Toast.makeText(LoginActivity.this,"Error Network",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(LoginActivity.this,"Error Parse",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> items = new HashMap<>();
                items.put(Constants.PARAM_USERNAME, username);
                items.put(Constants.PARAM_PASSWORD, password);
                items.put(Constants.PARAM_TOKEN, token);
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
        Log.d(TAG, "loginFaram" + items);
        TTSApplication.getInstance().addToRequestQueue(request);
    }

    private void postUpdateMaster() {
        token = FirebaseInstanceId.getInstance().getToken();
        currentUser = sessionManager.getUserLoginModel();
        progressDialog.show();
        final JSONObject items = new JSONObject();
        try {
            items.put("token_from", "mobile");
            items.put("user_id", String.valueOf(currentUser.getUserId()));
            items.put("token_data", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringRequest request = new StringRequest(Request.Method.POST, CommonsUtil.getAbsoluteUrl(Constants.REFRESH_TOKEN),
//        StringRequest request = new StringRequest(Request.Method.POST, CommonsUtil.getAbsoluteUrl(Constants.URL_LOGIN),
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Logcat.e("response: " + response);
                        MasterModel masterModel = gson.fromJson(response, MasterModel.class);
                        if(masterModel.getStatus().equals("success")) {
                            currentUser = sessionManager.getUserLoginModel();
                            currentUser.setFcmRegisteredId(token);
                            sessionManager.createLoginSession(currentUser);

                            //--update by ptr.nov
//                            if(masterModel.getUserId()==String.valueOf(currentUser.getUserId())){
//                            if(currentUser.getUserId()!=639){
//                                updateMaster(masterModel);
//                            };

                            Log.d(TAG,"check_id="+currentUser.getUserId());

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            progressDialog.dismiss();
                            finish();
                        } else {
                            signInRL.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();
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
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> items = new HashMap<>();
                items.put("token_from", "mobile");
                items.put("user_id", String.valueOf(currentUser.getUserId()));
                items.put("token_data", token);
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
        Log.d(TAG, "loginFaram" + items);
    }

    private void importMaster(final ImportModel importModel) {
        final List<SeverityModel> severityModels = importModel.getSeverityModels();
        try {
            TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                public Void call() throws Exception {
                    UserDAO.deleteAll();
                    for(UserLoginModel userLoginModel : importModel.getUserModel()) {
                        UserDAO.create(userLoginModel);
                    }
                    return null;
                }
            });

            severitySize = SeverityDAO.readAll(-11, -11).size();
            if(severitySize == 0) {
                TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                    public Void call() throws Exception {
                        for(SeverityModel severityModel : severityModels) {
                            SeverityDAO.create(severityModel);
                        }
                        return null;
                    }
                });

            } else {
                TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                    public Void call() throws Exception {
                        for(SeverityModel severityModel : severityModels) {
                            try {
                                Logcat.d("SEVERITY::" + severityModel.getSeverityName());
                                if (SeverityDAO.readByName(severityModel.getSeverityName()) != null) {
                                    SeverityDAO.update(severityModel);
                                } else {
                                    SeverityDAO.create(severityModel);
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        final List<StationModel> stationModels = importModel.getStations();
        try {
            stationSize = StationDAO.readAll(-11, -11).size();
            if(stationSize == 0) {
                TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                    public Void call() throws Exception {
                        for(StationModel stationModel : stationModels) {
                            StationDAO.create(stationModel);
                        }
                        return null;
                    }
                });

            } else {
                TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                    public Void call() throws Exception {
                        for(StationModel stationModel : stationModels) {
                            try {
                                if (StationDAO.readByName(stationModel.getStationName()) != null) {
                                    StationDAO.update(stationModel);
                                } else {
                                    StationDAO.create(stationModel);
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        final List<Suspect1Model> suspect1Models = importModel.getSuspect1Models();
        final List<Suspect2Model> suspect2Models = importModel.getSuspect2Models();
        final List<Suspect3Model> suspect3Models = importModel.getSuspect3Models();
        final List<Suspect4Model> suspect4Models = importModel.getSuspect4Models();
        try {
            suspect1Size = Suspect1DAO.readAll(-11, -11).size();
            if(suspect1Size == 0) {
                TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                    public Void call() throws Exception {
                        for (Suspect1Model suspect1Model : suspect1Models) {
                            Suspect1DAO.create(suspect1Model);
                        }
                        return null;
                    }
                });

            } else {
                TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                    public Void call() throws Exception {
                        for (Suspect1Model suspect1Model : suspect1Models) {
                            if (Suspect1DAO.readByName(suspect1Model.getSuspectName()) != null) {
                                Logcat.i("SUSPECT_1 UPDATE::"+suspect1Model.getSuspectName());
                                Suspect1DAO.update(suspect1Model);
                            } else {
                                Logcat.i("SUSPECT_1 CREATE2::"+suspect1Model.getSuspectName());
                                Suspect1DAO.create(suspect1Model);
                            }
                        }
                        return null;
                    }
                });

            }

            suspect2Size = Suspect2DAO.readAll(-11, -11).size();
            if(suspect2Size == 0) {
                TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                    public Void call() throws Exception {
                        for(Suspect2Model suspect2Model : suspect2Models) {
                            Suspect2DAO.create(suspect2Model);
                        }
                        return null;
                    }
                });
            } else {
                TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                    public Void call() throws Exception {
                        for(Suspect2Model suspect2Model : suspect2Models) {
                            if (Suspect2DAO.readByName(suspect2Model.getSuspectName()) != null) {
                                Suspect2DAO.update(suspect2Model);
                            } else {
                                Suspect2DAO.create(suspect2Model);
                            }
                        }
                        return null;
                    }
                });
            }

            suspect3Size = Suspect3DAO.readAll(-11, -11).size();
            if(suspect3Size == 0) {
                TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                    public Void call() throws Exception {
                        for(Suspect3Model suspect3Model : suspect3Models) {
                            Suspect3DAO.create(suspect3Model);
                        }
                        return null;
                    }
                });
            } else {
                TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                    public Void call() throws Exception {
                        for(Suspect3Model suspect3Model : suspect3Models) {
                            try {
                                if (Suspect3DAO.readByName(suspect3Model.getSuspectName()) != null) {
                                    Suspect3DAO.update(suspect3Model);
                                } else {
                                    Suspect3DAO.create(suspect3Model);
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }
                });
            }

            suspect4Size = Suspect4DAO.readAll(-11, -11).size();
            if(suspect4Size == 0) {
                TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                    public Void call() throws Exception {
                        for(Suspect4Model suspect4Model : suspect4Models) {
                            Suspect4DAO.create(suspect4Model);
                        }
                        return null;
                    }
                });
            } else {
                TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                    public Void call() throws Exception {
                        for(Suspect4Model suspect4Model : suspect4Models) {
                            if (Suspect4DAO.readByName(suspect4Model.getSuspectName()) != null) {
                                Suspect4DAO.update(suspect4Model);
                            } else {
                                Suspect4DAO.create(suspect4Model);
                            }
                        }
                        return null;
                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateMaster(final MasterModel masterModel) {
        final List<SeverityModel> severityModels = masterModel.getSeverityModels();
        try {
            TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                public Void call() throws Exception {
                    UserDAO.deleteAll();
                    UserDAO.create(masterModel.getUserLoginModel());
                    return null;
                }
            });

            severitySize = SeverityDAO.readAll(-11, -11).size();
            if(severitySize == 0) {
                TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                    public Void call() throws Exception {
                        for(SeverityModel severityModel : severityModels) {
                            SeverityDAO.create(severityModel);
                        }
                        return null;
                    }
                });
            } else {
                TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                    public Void call() throws Exception {
                        for(SeverityModel severityModel : severityModels) {
                            try {
                                Logcat.d("SEVERITY::" + severityModel.getSeverityName());
                                if (SeverityDAO.readByName(severityModel.getSeverityName()) != null) {
                                    SeverityDAO.update(severityModel);
                                } else {
                                    SeverityDAO.create(severityModel);
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        final List<StationModel> stationModels = masterModel.getStations();
        try {
            stationSize = StationDAO.readAll(-11, -11).size();
            if(stationSize == 0) {
                TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                    public Void call() throws Exception {
                        for(StationModel stationModel : stationModels) {
                            StationDAO.create(stationModel);
                        }
                        return null;
                    }
                });

            } else {
                TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                    public Void call() throws Exception {
                        for(StationModel stationModel : stationModels) {
                            try {
                                if (StationDAO.readByName(stationModel.getStationName()) != null) {
                                    StationDAO.update(stationModel);
                                } else {
                                    StationDAO.create(stationModel);
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        final List<Suspect1Model> suspect1Models = masterModel.getSuspect1Models();
        final List<Suspect2Model> suspect2Models = masterModel.getSuspect2Models();
        final List<Suspect3Model> suspect3Models = masterModel.getSuspect3Models();
        final List<Suspect4Model> suspect4Models = masterModel.getSuspect4Models();
        try {
            suspect1Size = Suspect1DAO.readAll(-11, -11).size();
            if(suspect1Size == 0) {
                TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                    public Void call() throws Exception {
                        for (Suspect1Model suspect1Model : suspect1Models) {
                            Suspect1DAO.create(suspect1Model);
                        }
                        return null;
                    }
                });
            } else {
                TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                    public Void call() throws Exception {
                        for (Suspect1Model suspect1Model : suspect1Models) {
                            if (Suspect1DAO.readByName(suspect1Model.getSuspectName()) != null) {
                                Logcat.i("SUSPECT_1 UPDATE::"+suspect1Model.getSuspectName());
                                Suspect1DAO.update(suspect1Model);
                            } else {
                                Logcat.i("SUSPECT_1 CREATE2::"+suspect1Model.getSuspectName());
                                Suspect1DAO.create(suspect1Model);
                            }
                        }
                        return null;
                    }
                });
            }

            suspect2Size = Suspect2DAO.readAll(-11, -11).size();
            if(suspect2Size == 0) {
                TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                    public Void call() throws Exception {
                        for(Suspect2Model suspect2Model : suspect2Models) {
                            Suspect2DAO.create(suspect2Model);
                        }
                        return null;
                    }
                });
            } else {
                TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                    public Void call() throws Exception {
                        for(Suspect2Model suspect2Model : suspect2Models) {
                            if (Suspect2DAO.readByName(suspect2Model.getSuspectName()) != null) {
                                Suspect2DAO.update(suspect2Model);
                            } else {
                                Suspect2DAO.create(suspect2Model);
                            }
                        }
                        return null;
                    }
                });
            }

            suspect3Size = Suspect3DAO.readAll(-11, -11).size();
            if(suspect3Size == 0) {
                TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                    public Void call() throws Exception {
                        for(Suspect3Model suspect3Model : suspect3Models) {
                            Suspect3DAO.create(suspect3Model);
                        }
                        return null;
                    }
                });
            } else {
                TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                    public Void call() throws Exception {
                        for(Suspect3Model suspect3Model : suspect3Models) {
                            try {
                                if (Suspect3DAO.readByName(suspect3Model.getSuspectName()) != null) {
                                    Suspect3DAO.update(suspect3Model);
                                } else {
                                    Suspect3DAO.create(suspect3Model);
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }
                });
            }

            suspect4Size = Suspect4DAO.readAll(-11, -11).size();
            if(suspect4Size == 0) {
                TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                    public Void call() throws Exception {
                        for(Suspect4Model suspect4Model : suspect4Models) {
                            Suspect4DAO.create(suspect4Model);
                        }
                        return null;
                    }
                });
            } else {
                TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                    public Void call() throws Exception {
                        for(Suspect4Model suspect4Model : suspect4Models) {
                            if (Suspect4DAO.readByName(suspect4Model.getSuspectName()) != null) {
                                Suspect4DAO.update(suspect4Model);
                            } else {
                                Suspect4DAO.create(suspect4Model);
                            }
                        }
                        return null;
                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
