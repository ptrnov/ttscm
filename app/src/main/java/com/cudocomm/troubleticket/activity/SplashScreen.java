package com.cudocomm.troubleticket.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.cudocomm.troubleticket.database.dao.ProgramDAO;
import com.cudocomm.troubleticket.database.dao.SeverityDAO;
import com.cudocomm.troubleticket.database.dao.StationDAO;
import com.cudocomm.troubleticket.database.dao.Suspect1DAO;
import com.cudocomm.troubleticket.database.dao.Suspect2DAO;
import com.cudocomm.troubleticket.database.dao.Suspect3DAO;
import com.cudocomm.troubleticket.database.dao.Suspect4DAO;
import com.cudocomm.troubleticket.database.dao.UserDAO;
import com.cudocomm.troubleticket.database.model.Program;
import com.cudocomm.troubleticket.database.model.SeverityModel;
import com.cudocomm.troubleticket.database.model.StationModel;
import com.cudocomm.troubleticket.database.model.Suspect1Model;
import com.cudocomm.troubleticket.database.model.Suspect2Model;
import com.cudocomm.troubleticket.database.model.Suspect3Model;
import com.cudocomm.troubleticket.database.model.Suspect4Model;
import com.cudocomm.troubleticket.database.model.UserLoginModel;
import com.cudocomm.troubleticket.model.MasterModel;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Logcat;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.misc.TransactionManager;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class SplashScreen extends BaseActivity {

    private ImageView imSplashScreen;
    CustomPopConfirm customPopConfirm;
    UserLoginModel currentUser;
    private String token;

    private int stationSize = 0;
    private int severitySize = 0;
    private int suspect1Size = 0;
    private int suspect2Size = 0;
    private int suspect3Size = 0;
    private int suspect4Size = 0;

    List<Program> programs;
    int programSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        imSplashScreen = (ImageView) findViewById(R.id.splashScreen);
        cekVersionApp();
        //ptr.nov --
//        FileUtils.deleteQuietly(getApplicationContext().getCacheDir());
//        Intent intent = new Intent(SplashScreen.this,LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
    }

    public void cekVersionApp() {
        Picasso.with(SplashScreen.this).load(R.mipmap.ic_splash_tt).into(imSplashScreen);
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
                                if(sessionManager.isLoggedIn()) {
                                    postUpdateMaster();
                                } else {
//                                    loadProgramAcara();
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            FileUtils.deleteQuietly(getApplicationContext().getCacheDir());
                                            Intent intent = new Intent(SplashScreen.this,LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                    }, 3000);


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
                        Toast.makeText(TTSApplication.getContext(), "Terjadi Kesalahan version. Umumnya karena masalah jaringan.", Toast.LENGTH_SHORT).show();

                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(SplashScreen.this,"Error Koneksi",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(SplashScreen.this,"Error Auth",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(SplashScreen.this,"Error Server",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            //TODO
                            Toast.makeText(SplashScreen.this,"Error Network",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(SplashScreen.this,"Error Parse",
                                    Toast.LENGTH_LONG).show();
                        }
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

    private void postUpdateMaster() {
        token = FirebaseInstanceId.getInstance().getToken();
        currentUser = sessionManager.getUserLoginModel();
        final JSONObject items = new JSONObject();
        try {
            items.put("token_from", "mobile");
            items.put("user_id", String.valueOf(currentUser.getUserId()));
            items.put("token_data", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringRequest request = new StringRequest(Request.Method.POST, CommonsUtil.getAbsoluteUrl(Constants.REFRESH_TOKEN),

                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Logcat.e("response: " + response);
                        MasterModel masterModel = gson.fromJson(response, MasterModel.class);
                        if(masterModel.getStatus().equals("success")) {
                            currentUser = sessionManager.getUserLoginModel();
                            currentUser.setFcmRegisteredId(token);
                            sessionManager.createLoginSession(currentUser);
                            //Editing By: ptr.nov
//                            updateMaster(masterModel);

                            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                        }
                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Logcat.e("Volley error: " + error.getMessage() + ", code: " + error.networkResponse);
                        Toast.makeText(getApplicationContext(), "Terjadi Kesalahan. Umumnya karena masalah jaringan.", Toast.LENGTH_SHORT).show();

                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(SplashScreen.this,"Error Koneksi",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(SplashScreen.this,"Error Auth",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(SplashScreen.this,"Error Server",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            //TODO
                            Toast.makeText(SplashScreen.this,"Error Network",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(SplashScreen.this,"Error Parse",
                                    Toast.LENGTH_LONG).show();
                        }
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

//        loadProgramAcara();
    }

    public void loadProgramAcara(){
        StringRequest request = new StringRequest(Request.Method.GET, CommonsUtil.getAbsoluteUrl(Constants.URL_PROGRAM_ACARA),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        Logcat.e("response: " + response);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject jObj = new JSONObject(response);
                                    if (jObj.getString("status").equalsIgnoreCase("success")) {
                                        Type programType = new TypeToken<List<Program>>() {
                                        }.getType();

                                        programs = gson.fromJson(jObj.getString("data"), programType);

                                        loadMaster();
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        finish();
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 4000);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Logcat.e("Volley error: " + error.getMessage() + ", code: " + error.networkResponse);
                        Toast.makeText(getApplicationContext(), "Terjadi Kesalahan. Umumnya karena masalah jaringan.", Toast.LENGTH_SHORT).show();

                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(SplashScreen.this,"Error Koneksi",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(SplashScreen.this,"Error Auth",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(SplashScreen.this,"Error Server",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            //TODO
                            Toast.makeText(SplashScreen.this,"Error Network",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(SplashScreen.this,"Error Parse",
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                }
        );
        TTSApplication.getInstance().addToRequestQueue(request);
    }

    private void loadMaster() {
        try {

            programSize = ProgramDAO.readAll(-11, -11).size();
            if(programSize == 0) {
                TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                    public Void call() throws Exception {
                        for(Program program : programs) {
                            ProgramDAO.create(program);
                        }
                        return null;
                    }
                });
            } else {
                TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), new Callable<Object>() {
                    public Void call() throws Exception {
                        for(Program program : programs) {
                            try {
                                if (ProgramDAO.readByName(program.getProgramName()) != null) {
                                    ProgramDAO.update(program);
                                } else {
                                    ProgramDAO.create(program);
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
    }

//    public void synchMaster() {
//        final ProgressDialog pDialog = new ProgressDialog(this);
//        pDialog.setMessage("Loading...");
//        pDialog.show();
//
//        StringRequest request = new StringRequest(Request.Method.POST, TTPMConfig.URL_SYNCH_MASTER,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Logcat.e("response: " + response);
//                        try {
//                            JSONObject jObj = new JSONObject(response);
//                            if (jObj.getString("status").equalsIgnoreCase("success")) {
//                                Type typeType = new TypeToken<List<TaskType>>() {
//                                }.getType();
//                                Type programType = new TypeToken<List<Program>>() {
//                                }.getType();
//                                Type siaranType = new TypeToken<List<Siaran>>() {
//                                }.getType();
//                                Type severityType = new TypeToken<List<SeverityModel>>() {
//                                }.getType();
//                                Type suspectType1 = new TypeToken<List<Suspect1Model>>() {
//                                }.getType();
//                                Type suspectType2 = new TypeToken<List<Suspect2Model>>() {
//                                }.getType();
//                                Type suspectType3 = new TypeToken<List<Suspect3Model>>() {
//                                }.getType();
//                                Type suspectType4 = new TypeToken<List<Suspect4Model>>() {
//                                }.getType();
//                                Type channelType = new TypeToken<List<ChannelModel>>() {
//                                }.getType();
//                                Type vqType = new TypeToken<List<VideoQualityModel>>() {
//                                }.getType();
//                                Type aqType = new TypeToken<List<AudioQualityModel>>() {
//                                }.getType();
//                                Type txType = new TypeToken<List<TX>>() {
//                                }.getType();
//
//                                programs = gson.fromJson(jObj.getString("programs"), programType);
//                                taskTypes = gson.fromJson(jObj.getString("types"), typeType);
//
//                                severityModels = gson.fromJson(jObj.getString("severities"), severityType);
//                                suspect1Models = gson.fromJson(jObj.getString("suspect1"), suspectType1);
//                                suspect2Models = gson.fromJson(jObj.getString("suspect2"), suspectType2);
//                                suspect3Models = gson.fromJson(jObj.getString("suspect3"), suspectType3);
//                                suspect4Models = gson.fromJson(jObj.getString("suspect4"), suspectType4);
//                                channelModels = gson.fromJson(jObj.getString("channels"), channelType);
//                                videoQualityModels = gson.fromJson(jObj.getString("video_qualities"), vqType);
//                                audioQualityModels = gson.fromJson(jObj.getString("audio_qualities"), aqType);
//                                txs = gson.fromJson(jObj.getString("txs"), txType);
//                                loadMaster();
//                                pDialog.dismiss();
//                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                                finish();
//                            }
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Logcat.e("Volley error: " + error.getMessage() + ", code: " + error.networkResponse);
//                        pDialog.dismiss();
//                        Toast.makeText(getApplicationContext(), "Terjadi Kesalahan. Umumnya karena masalah jaringan.", Toast.LENGTH_SHORT).show();
//
//                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                            Toast.makeText(LoginActivity.this,"Error Koneksi",
//                                    Toast.LENGTH_LONG).show();
//                        } else if (error instanceof AuthFailureError) {
//                            //TODO
//                            Toast.makeText(LoginActivity.this,"Error Auth",
//                                    Toast.LENGTH_LONG).show();
//                        } else if (error instanceof ServerError) {
//                            //TODO
//                            Toast.makeText(LoginActivity.this,"Error Server",
//                                    Toast.LENGTH_LONG).show();
//                        } else if (error instanceof NetworkError) {
//                            //TODO
//                            Toast.makeText(LoginActivity.this,"Error Network",
//                                    Toast.LENGTH_LONG).show();
//                        } else if (error instanceof ParseError) {
//                            //TODO
//                            Toast.makeText(LoginActivity.this,"Error Parse",
//                                    Toast.LENGTH_LONG).show();
//                        }
//
//                    }
//                }
//        );
//        TTPMApplication.getInstance().addToRequestQueue(request);
//    }
}
