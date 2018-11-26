package com.cudocomm.troubleticket.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.cudocomm.troubleticket.activity.LoginActivity;
import com.cudocomm.troubleticket.database.model.UserLoginModel;
import com.cudocomm.troubleticket.database.model.JabatanModel;

import java.util.HashMap;

public class SessionManager {

    Context _context;
    Editor editor;
    SharedPreferences pref;

    public SessionManager(Context context) {
        this._context = context;
        this.pref = this._context.getSharedPreferences(Preferences.PREFS_NAME, 0);
        this.editor = this.pref.edit();
    }

    public void createLoginSession(UserLoginModel currentUser) {
        editor.putBoolean(Constants.IS_LOGIN, true);
        editor.putInt(Constants.USER_ID, currentUser.getUserId());
        editor.putString(Constants.USER_NAME, currentUser.getUserName());
        editor.putString(Constants.USER_EMAIL, currentUser.getUserEmail());
        editor.putInt(Constants.POSITION_ID, currentUser.getPositionId());
        editor.putString(Constants.POSITION_NAME, currentUser.getPositionName());
        editor.putString(Constants.STATION_ID, currentUser.getStationId());
        editor.putString(Constants.STATION_NAME, currentUser.getStationName());
        editor.putString(Constants.REGION_ID, currentUser.getRegionId());
        editor.putString(Constants.REGION_NAME, currentUser.getRegionName());
        editor.putString(Constants.USER_PICTURE, currentUser.getUserPicture());
        editor.putString(Constants.DEPARTMENT_ID, currentUser.getDepartmentId());
        editor.putString(Constants.DEPARTMENT_NAME, currentUser.getDepartmentName());
        editor.putInt(Constants.ID_UPDRS, currentUser.getIdUpdrs());

        this.editor.commit();
    }

    public void createLoginSession1(JabatanModel currentUser) {
        editor.putBoolean(Constants.IS_LOGIN, true);
        editor.putInt(Constants.USER_ID, currentUser.getUserId());
        editor.putString(Constants.USER_NAME, currentUser.getUserName());
        editor.putString(Constants.USER_EMAIL, currentUser.getUserEmail());
        editor.putInt(Constants.POSITION_ID, currentUser.getPositionId());
        editor.putString(Constants.POSITION_NAME, currentUser.getPositionName());
        editor.putString(Constants.STATION_ID, currentUser.getStationId());
        editor.putString(Constants.STATION_NAME, currentUser.getStationName());
        editor.putString(Constants.REGION_ID, currentUser.getRegionId());
        editor.putString(Constants.REGION_NAME, currentUser.getRegionName());
        editor.putString(Constants.USER_PICTURE, currentUser.getUserPicture());
        editor.putString(Constants.DEPARTMENT_ID, currentUser.getDepartmentId());
        editor.putString(Constants.DEPARTMENT_NAME, currentUser.getDepartmentName());
        editor.putInt(Constants.ID_UPDRS, currentUser.getIdUpdrs());

        this.editor.commit();
    }

    public void checkLogin() {
        if (!isLoggedIn()) {
            Intent i = new Intent(this._context, LoginActivity.class);
            i.addFlags(67108864);
            i.setFlags(268435456);
            this._context.startActivity(i);
        }
    }

    public HashMap<String, Object> getUserDetails() {
        HashMap<String, Object> user = new HashMap();
        user.put(Constants.USER_ID, this.pref.getInt(Constants.USER_ID, 0));
        user.put(Constants.USER_NAME, this.pref.getString(Constants.USER_NAME, null));
        user.put(Constants.USER_EMAIL, this.pref.getString(Constants.USER_EMAIL, null));
        user.put(Constants.POSITION_ID, this.pref.getInt(Constants.POSITION_ID, 0));
        user.put(Constants.POSITION_NAME, this.pref.getString(Constants.POSITION_NAME, null));
        user.put(Constants.STATION_ID, this.pref.getString(Constants.STATION_ID, null));
        user.put(Constants.STATION_NAME, this.pref.getString(Constants.STATION_NAME, null));
        user.put(Constants.REGION_ID, this.pref.getString(Constants.REGION_ID, null));
        user.put(Constants.REGION_NAME, this.pref.getString(Constants.REGION_NAME, null));
        user.put(Constants.DEPARTMENT_ID, this.pref.getString(Constants.DEPARTMENT_ID, null));
        user.put(Constants.DEPARTMENT_NAME, this.pref.getString(Constants.DEPARTMENT_NAME, null));
        user.put(Constants.USER_PICTURE, this.pref.getString(Constants.USER_PICTURE, null));
        user.put(Constants.ID_UPDRS, this.pref.getInt(Constants.ID_UPDRS, 0));
        return user;
    }

    public UserLoginModel getUserLoginModel() {
        UserLoginModel model = new UserLoginModel();
        model.setUserId(this.pref.getInt(Constants.USER_ID, 0));
        model.setUserName(this.pref.getString(Constants.USER_NAME, null));
        model.setUserEmail(this.pref.getString(Constants.USER_EMAIL, null));
        model.setPositionId(this.pref.getInt(Constants.POSITION_ID, 0));
        model.setPositionName(this.pref.getString(Constants.POSITION_NAME, null));
        model.setStationId(this.pref.getString(Constants.STATION_ID, null));
        model.setStationName(this.pref.getString(Constants.STATION_NAME, null));
        model.setRegionId(this.pref.getString(Constants.REGION_ID, null));
        model.setRegionName(this.pref.getString(Constants.REGION_NAME, null));
        model.setDepartmentId(this.pref.getString(Constants.DEPARTMENT_ID, null));
        model.setDepartmentName(this.pref.getString(Constants.DEPARTMENT_NAME, null));
        model.setUserPicture(this.pref.getString(Constants.USER_PICTURE, null));
        model.setIdUpdrs(this.pref.getInt(Constants.ID_UPDRS, 0));
        return model;
    }

    public void logoutUser() {
        this.editor.clear();
        this.editor.commit();
        Intent i = new Intent(this._context, LoginActivity.class);
        i.addFlags(67108864);
        i.setFlags(268435456);
        this._context.startActivity(i);
    }

    public boolean isLoggedIn() {
        return this.pref.getBoolean(Constants.IS_LOGIN, false);
    }
}
