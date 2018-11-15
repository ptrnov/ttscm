package com.cudocomm.troubleticket.component;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.support.v4.app.DialogFragment;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;


public class PopupConfirmTicketvisit extends DialogFragment implements DatePickerDialog.OnDateSetListener{

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

  private EditText ticketInfoET;
  private EditText tickeTglDepature;
  private EditText ticketVasselNo;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    argTitle = getArguments().getString(Constants.ARGS_TITLE);
//    argMessage = getArguments().getString(Constants.ARGS_DESC);
    argDone = getArguments().getString(Constants.ARGS_PROCESS);
    argClose = getArguments().getString(Constants.ARGS_BACK);
  }

  public Dialog onCreateDialog(Bundle savedInstanceState) {
    this.dialog = super.onCreateDialog(savedInstanceState);
    Window window = this.dialog.getWindow();
    this.dialog.requestWindowFeature(1);
    window.setGravity(17);
    this.dialog.setCancelable(false);
    this.dialog.setCanceledOnTouchOutside(false);
    window.getAttributes().windowAnimations = R.style.dialog_animation;
    window.setBackgroundDrawable(new ColorDrawable(0));
    window.setSoftInputMode(3);
    window.setLayout(-2, -2);
    return this.dialog;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.popup_close_ticketvisit, container, false);
    initUI();
    initData();
    return rootView;
  }

  private void initUI() {
    btnDone = (Button) rootView.findViewById(R.id.btnDone);
    btnClose = (Button) rootView.findViewById(R.id.btnClose);
    title = (TextView) rootView.findViewById(R.id.titledata);
    ticketInfoET = (EditText) rootView.findViewById(R.id.ticketInfoET);
    tickeTglDepature = (EditText) rootView.findViewById(R.id.tgldepature);
    ticketVasselNo = (EditText) rootView.findViewById(R.id.vesselno);
//    descAlert = (TextView) rootView.findViewById(R.id.descAlert);
  }

  private void initData() {
    title.setText(argTitle);
//    descAlert.setText(argMessage);
    btnDone.setText(argDone);
    if(argClose != null)
      btnClose.setText(argClose);

    preferences = new Preferences(getActivity());

    btnClose.setOnClickListener(getBackListener());
    btnDone.setOnClickListener(getProcessListener());

    tickeTglDepature.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, -1);
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                PopupConfirmTicketvisit.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
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

  public static PopupConfirmTicketvisit newInstance(String argTitle, String argDone, String argClose) {
    PopupConfirmTicketvisit f = new PopupConfirmTicketvisit();

    // Supply num input as an argument.
    Bundle args = new Bundle();
    args.putString(Constants.ARGS_TITLE, argTitle);
//    args.putString(Constants.ARGS_DESC, argMessage);
    args.putString(Constants.ARGS_PROCESS, argDone);
    args.putString(Constants.ARGS_BACK, argClose);
    f.setArguments(args);

    return f;
  }

  public EditText getTicketInfoET() {
    return ticketInfoET;
  }

  public EditText getTicketTglDepature() {
    return tickeTglDepature;
  }
  public EditText getTicketVasselNo() {
    return ticketVasselNo;
  }

  public void setTicketInfoET(EditText ticketInfoET) {
    this.ticketInfoET = ticketInfoET;
  }

  public void setTicketTglDepature(EditText ticketTglDepature) {
    this.tickeTglDepature = ticketTglDepature;
  }

  public void setTicketVasselNo(EditText ticketVasselNo) {
    this.ticketVasselNo = ticketVasselNo;
  }

  @Override
  public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
    String date = String.format("%02d", year) + "-" + String.format("%02d", monthOfYear + 1) + "-" + String.format("%02d", dayOfMonth);

    tickeTglDepature.setText(date);
  }
}
