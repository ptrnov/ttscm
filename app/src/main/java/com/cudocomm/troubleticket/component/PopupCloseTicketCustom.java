package com.cudocomm.troubleticket.component;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.TTSApplication;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Preferences;
import com.rengwuxian.materialedittext.MaterialEditText;

import fr.ganfra.materialspinner.MaterialSpinner;

public class PopupCloseTicketCustom extends DialogFragment {
  public static final String TAG = PopupCloseTicketCustom.class.getSimpleName();

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

  private MaterialSpinner fixTypeSpinner;
  private EditText ticketInfoET;
  private EditText prNoET;

  String[] closeTypes;
  String closedTypes;
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
    rootView = inflater.inflate(R.layout.popup_close_ticket_custom, container, false);
    initUI();
    initData();
    return rootView;
  }

  private void initUI() {
    btnDone = (Button) rootView.findViewById(R.id.btnDone);
    btnClose = (Button) rootView.findViewById(R.id.btnClose);
    title = (TextView) rootView.findViewById(R.id.titledata);
    ticketInfoET = (EditText) rootView.findViewById(R.id.ticketInfoET);
    fixTypeSpinner = (MaterialSpinner) rootView.findViewById(R.id.fixTypeSpinner);
    prNoET = (MaterialEditText) rootView.findViewById(R.id.prNoET);

    closeTypes = getResources().getStringArray(R.array.close_type_array);
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(TTSApplication.getContext(), R.layout.spiner_dropdown_item, closeTypes);
    fixTypeSpinner.setAdapter(adapter);
    fixTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemSelected: " + fixTypeSpinner.getSelectedItem());
        Log.d(TAG, "onItemSelected: " + fixTypeSpinner);
        resetEdT();
        closedTypes = String.valueOf(fixTypeSpinner.getSelectedItem());
        if (closedTypes.equals("Fix Closed")) {
          prNoET.setVisibility(View.GONE);
        } else {
          prNoET.setVisibility(View.VISIBLE);
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        resetEdT();
      }
    });

  }

  public void resetEdT(){
    ticketInfoET.setText("");
    prNoET.setText("");
    ticketInfoET.setError(null);
    prNoET.setError(null);
    ticketInfoET.clearFocus();
    prNoET.clearFocus();
  }

  private void initData() {
    title.setText(argTitle);
//    descAlert.setText(argMessage);
    btnDone.setText(argDone);
    if(argClose != null)
      btnClose.setText(argClose);

//    fixTypeSpinner.setAdapter(new );

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

  public String getStringSpinnerItem(){
    return fixTypeSpinner.getSelectedItem().toString();
  }

  public static PopupCloseTicketCustom newInstance(String argTitle, String argDone, String argClose) {
    PopupCloseTicketCustom f = new PopupCloseTicketCustom();

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

  public void setTicketInfoET(EditText ticketInfoET) {
    this.ticketInfoET = ticketInfoET;
  }

  public MaterialSpinner getFixTypeSpinner() {
    return fixTypeSpinner;
  }

  public String getFixTypeSpinner_2() {
    return closedTypes;
  }

  public void setFixTypeSpinner(MaterialSpinner fixTypeSpinner) {
    this.fixTypeSpinner = fixTypeSpinner;
  }

  public EditText getPrNoET() {
    return prNoET;
  }

  public void setPrNoET(EditText prNoET) {
    this.prNoET = prNoET;
  }
}
