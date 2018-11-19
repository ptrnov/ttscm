package com.cudocomm.troubleticket.component;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Preferences;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

public class PopupApprovalRequestVisitTicket extends DialogFragment implements DatePickerDialog.OnDateSetListener {

  private Dialog dialog;

  private View rootView;

  private String argTitle;
//  private String argMessage;
  private String argDone;
  private String argClose;

  private Button btnClose;
  private Button btnDone;

  private TextView title;
  private TextView descAlert;

  private Preferences preferences;

  private View.OnClickListener backListener;
  private View.OnClickListener processListener;

  private EditText infoET;
  private EditText dateET;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    argTitle = getArguments().getString(Constants.ARGS_TITLE);
//    argMessage = getArguments().getString(Constants.ARGS_DESC);
    argDone = getArguments().getString(Constants.ARGS_PROCESS);
    argClose = getArguments().getString(Constants.ARGS_BACK);
  }

  public Dialog onCreateDialog(Bundle savedInstanceState) {
    dialog = super.onCreateDialog(savedInstanceState);
    Window window = dialog.getWindow();
    dialog.requestWindowFeature(1);
    window.setGravity(17);
    dialog.setCancelable(false);
    dialog.setCanceledOnTouchOutside(false);
    window.getAttributes().windowAnimations = R.style.dialog_animation;
    window.setBackgroundDrawable(new ColorDrawable(0));
    window.setSoftInputMode(3);
    window.setLayout(-2, -2);
    return dialog;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.popup_approval_request_visit_ticket, container, false);
    initUI();
    initData();
    return rootView;
  }

  private void initUI() {
    btnDone = (Button) rootView.findViewById(R.id.btnDone);
    btnClose = (Button) rootView.findViewById(R.id.btnClose);
    title = (TextView) rootView.findViewById(R.id.titledata);
    infoET = (EditText) rootView.findViewById(R.id.infoET);
    dateET = (EditText) rootView.findViewById(R.id.dateET);
  }

  private void initData() {
    title.setText(argTitle);
    btnDone.setText(argDone);
    if(argClose != null)
      btnClose.setText(argClose);

    preferences = new Preferences(getActivity());

    btnClose.setOnClickListener(getBackListener());
    btnDone.setOnClickListener(getProcessListener());

    dateET.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                PopupApprovalRequestVisitTicket.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setMinDate(Calendar.getInstance()); //validasi sidable tangal kemarin.
        dpd.show(getActivity().getSupportFragmentManager(), null);
      }
    });
  }

  public View.OnClickListener getBackListener() {
    return backListener;
  }

  public void setBackListener(View.OnClickListener backListener) {
    this.backListener = backListener;
  }

  public View.OnClickListener getProcessListener() {
    return processListener;
  }

  public void setProcessListener(View.OnClickListener processListener) {
    this.processListener = processListener;
  }

  public static PopupApprovalRequestVisitTicket newInstance(String argTitle, String argDone, String argClose) {
    PopupApprovalRequestVisitTicket f = new PopupApprovalRequestVisitTicket();
    Bundle args = new Bundle();
    args.putString(Constants.ARGS_TITLE, argTitle);
    args.putString(Constants.ARGS_PROCESS, argDone);
    args.putString(Constants.ARGS_BACK, argClose);
    f.setArguments(args);

    return f;
  }

  @Override
  public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
    String date = String.format("%02d", year) + "-" + String.format("%02d", monthOfYear + 1) + "-" + String.format("%02d", dayOfMonth);

    dateET.setText(date);
  }

  public EditText getInfoET() {
    return infoET;
  }

  public void setInfoET(EditText infoET) {
    this.infoET = infoET;
  }

  public EditText getDateET() {
    return dateET;
  }

  public void setDateET(EditText dateET) {
    this.dateET = dateET;
  }
}
