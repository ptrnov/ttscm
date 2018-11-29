package com.cudocomm.troubleticket.component;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.model.Ticket;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Preferences;

public class PopupApprovalReplacment extends DialogFragment {

  private static final String TAG = "Popup_Approval";
  private Dialog dialog;

  private View rootView;

  private String argTitle;
  private String argLabelInfo;
//  private String argMessage;
  private String argDone;
  private String argClose;

  private Button btnClose;
  private Button btnDone;

  private TextView title;
  private TextView labelInfo;
  private TextView descAlert;

  private Preferences preferences;

  private View.OnClickListener backListener;
  private View.OnClickListener processListener;

  private EditText ticketInfoET;
  private EditText prNomor;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    argTitle = getArguments().getString(Constants.ARGS_TITLE);
    argLabelInfo = getArguments().getString(Constants.ARGS_INFO_LABEL);
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
    rootView = inflater.inflate(R.layout.popup_approval_replacment, container, false);
    initUI();
    initData();
    return rootView;
  }

  private void initUI() {
    btnDone = (Button) rootView.findViewById(R.id.btnDone);
    btnClose = (Button) rootView.findViewById(R.id.btnClose);
    title = (TextView) rootView.findViewById(R.id.titledata);
    labelInfo = (TextView) rootView.findViewById(R.id.textInfo);
    prNomor = (EditText) rootView.findViewById(R.id.prNo);
//    prNomor.setVisibility(View.GONE);
    ticketInfoET = (EditText) rootView.findViewById(R.id.ticketInfoET);
//    descAlert = (TextView) rootView.findViewById(R.id.descAlert);

    prNomor.addTextChangedListener(new TextWatcher(){
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
          String regexStr = "^[0-9]*$";
          if (prNomor.length()==0) {
            prNomor.setError(getResources().getString(R.string.error_prNo_action_requered));
          }else if(!prNomor.getText().toString().matches(regexStr) && prNomor.length()>9) {
            prNomor.setError(getResources().getString(R.string.error_prNo_action_digit));
            prNomor.requestFocus();
          }else if(!prNomor.getText().toString().trim().matches(regexStr)) {
            prNomor.setError(getResources().getString(R.string.error_prNo_action_number));
            prNomor.requestFocus();
          }else if(prNomor.length()>9){
            prNomor.setError(getResources().getString(R.string.error_prNo_action_digit_max));
            prNomor.requestFocus();
          }else if(prNomor.length()<9){
            prNomor.setError(getResources().getString(R.string.error_prNo_action_digit_min));
            prNomor.requestFocus();
          }else{
            prNomor.setError(getResources().getString(R.string.error_prNo_action_empty));
            prNomor.setEnabled(true);
            prNomor.requestFocus();
          }
        }
      }
    );

    prNomor.setOnKeyListener(
        new EditText.OnKeyListener() {

          @Override
          public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (prNomor.length() == 9) {
              AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
              alertDialogBuilder.setTitle("WARNINNG");
              alertDialogBuilder.setMessage("Max PR.No is  9 digit");
              alertDialogBuilder.show();
            }
            return false;
          }
    });

  }

  private void initData() {
    title.setText(argTitle);
    labelInfo.setText(argLabelInfo);
//    descAlert.setText(argMessage);
    btnDone.setText(argDone);
    if(argClose != null)
      btnClose.setText(argClose);

    preferences = new Preferences(getActivity());

    btnClose.setOnClickListener(getBackListener());
    btnDone.setOnClickListener(getProcessListener());


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

  public static PopupApprovalReplacment newInstance(String argTitle, String argDone, String argClose,String argLabelInfo) {
    PopupApprovalReplacment f = new PopupApprovalReplacment();

    // Supply num input as an argument.
    Bundle args = new Bundle();
    args.putString(Constants.ARGS_TITLE, argTitle);
//    args.putString(Constants.ARGS_DESC, argMessage);
    args.putString(Constants.ARGS_PROCESS, argDone);
    args.putString(Constants.ARGS_BACK, argClose);
    args.putString(Constants.ARGS_INFO_LABEL, argLabelInfo);
    f.setArguments(args);

    return f;
  }

  public EditText getTicketInfoET() {
    return ticketInfoET;
  }

  public void setTicketInfoET(EditText ticketInfoET) {
    this.ticketInfoET = ticketInfoET;
  }

  public EditText getPrNomor() {
    return prNomor;
  }

  public void setPrNomor(EditText prNomor) {
    this.prNomor = prNomor;
  }
}
