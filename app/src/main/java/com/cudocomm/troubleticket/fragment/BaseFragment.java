package com.cudocomm.troubleticket.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;

import com.cudocomm.troubleticket.TTSApplication;
import com.cudocomm.troubleticket.util.OnMenuSelected;
import com.cudocomm.troubleticket.util.Preferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by adsxg on 3/21/2017.
 */
public class BaseFragment extends Fragment {

    protected OnMenuSelected mListener;
    protected Preferences preferences;
    protected Handler handler;
    protected ProgressDialog progressDialog;
    protected Context context;

    protected GsonBuilder gsonBuilder;
    protected Gson gson;

    public void init() {
        context = TTSApplication.getContext();
        preferences = new Preferences(context);
        handler = new Handler();
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            init();
            this.mListener = (OnMenuSelected) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
//            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mListener = null;
    }
}
