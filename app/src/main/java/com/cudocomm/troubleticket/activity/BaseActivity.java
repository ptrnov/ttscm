package com.cudocomm.troubleticket.activity;

import android.support.v7.app.AppCompatActivity;

import com.cudocomm.troubleticket.TTSApplication;
import com.cudocomm.troubleticket.util.Preferences;
import com.cudocomm.troubleticket.util.SessionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by adsxg on 11/22/2017.
 */

public class BaseActivity extends AppCompatActivity {

    protected SessionManager sessionManager;
    protected Preferences preferences;
    protected GsonBuilder gsonBuilder;
    protected Gson gson;

    public BaseActivity() {
        sessionManager = new SessionManager(TTSApplication.getContext());
        preferences = new Preferences(this);
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

}
