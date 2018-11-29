package com.cudocomm.troubleticket.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.adapter.AssignmentAdapter;
import com.cudocomm.troubleticket.model.Assignment;
import com.cudocomm.troubleticket.util.ApiClient;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.component.CustomPopConfirm;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import dmax.dialog.SpotsDialog;
import okhttp3.FormBody;

public class SendReminder extends BaseFragment {

    private View rootView;

    private EditText messageInfo;
    private Button btnDone;
    private SpotsDialog progressDialog;
    private CustomPopConfirm popConfirm;
    private String rsltMsg;

    private View.OnClickListener processListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.send_message_kadiv, container, false);

        initUI();
        initData();

        return rootView;
    }

    private void initUI() {
        messageInfo = (EditText) rootView.findViewById(R.id.tilisMessage);
        btnDone = (Button) rootView.findViewById(R.id.btnDone);
        progressDialog = new SpotsDialog(getContext(), R.style.progress_dialog_style);

    }

    private void initData() {
//        btnDone.setOnClickListener(getProcessListener());

        btnDone.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rsltMsg=getTilisMessage().getText().toString();

                    if (rsltMsg.length()==0){
                        getTilisMessage().requestFocus();
                        getTilisMessage().setError(getResources().getString(R.string.label_send_required));
                    }else{
                        String title = "Submission Confirmation";
                        String msg =
                                "You will send message : "
                                        + "\n"
                                        + rsltMsg;
                        popConfirm = CustomPopConfirm.newInstance(title, msg, "Send", "No");
                        popConfirm.setBackListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        popConfirm.dismiss();
                                    }
                                });
                        popConfirm.setProcessListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        popConfirm.dismiss();
                                        new SendMessageTask().execute();
                                    }
                                });
                        popConfirm.show(getActivity().getFragmentManager(),null);
                    }
                }
            }
        );
    }
//
//    public View.OnClickListener getProcessListener() {
//        return processListener;
//    }
//
//    public void setProcessListener(View.OnClickListener processListener) {
//        this.processListener = processListener;
//    }



    private class SendMessageTask extends AsyncTask<Void, Void, Void> {
        String result;
        JSONObject object;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(
                        CommonsUtil.getAbsoluteUrl(Constants.URL_GET_SEND_REMINDER_KADIV),
                        new FormBody.Builder()
                                .add("desc", rsltMsg.toString())
                                .build());

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                object = new JSONObject(result);
                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public EditText getTilisMessage() {
        return messageInfo;
    }

    public void setTilisMessage(EditText messageInfo) {
        this.messageInfo = messageInfo;
    }

}
