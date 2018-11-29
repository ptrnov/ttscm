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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.TTSApplication;
import com.cudocomm.troubleticket.util.Constants;
import com.rengwuxian.materialedittext.MaterialEditText;

import fr.ganfra.materialspinner.MaterialSpinner;

public class PopupCloseTicketCustomReplacment extends DialogFragment {
  public static final String TAG = PopupCloseTicketCustomReplacment.class.getSimpleName();

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

//  private Preferences preferences;

  private View.OnClickListener backListener;
  private View.OnClickListener processListener;

  public MaterialSpinner fixTypeSpinner;
  public EditText ticketInfoET;
  public EditText replacmentNo;
  public EditText serialNo;
  public EditText partNo;
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
    replacmentNo = (MaterialEditText) rootView.findViewById(R.id.replacmentNo);
    partNo = (MaterialEditText) rootView.findViewById(R.id.partNo);
    serialNo = (MaterialEditText) rootView.findViewById(R.id.serialNo);
    prNoET.setVisibility(View.GONE);
    replacmentNo.setVisibility(View.GONE);
    partNo.setVisibility(View.GONE);
    serialNo.setVisibility(View.GONE);

//    Log.d(TAG, "check_id: " +  preferences.getPreferencesInt(Constants.POSITION_ID));
//    if (preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_TS ||
//            preferences.getPreferencesInt(Constants.POSITION_ID) == Constants.KADEP_INFRA){
        closeTypes = getResources().getStringArray(R.array.close_type_array_fic_pr_replacment);
//    }else{
//        closeTypes = getResources().getStringArray(R.array.close_type_array_fix_pr);
//    }

//    closeTypes = getResources().getStringArray(R.array.close_type_array_empty);

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
          replacmentNo.setVisibility(View.GONE);
          partNo.setVisibility(View.GONE);
          serialNo.setVisibility(View.GONE);
          ticketInfoET.setHint(getResources().getString(R.string.label_close_info));
        }else if (closedTypes.equals("Closed by PR")){
          prNoET.setVisibility(View.VISIBLE);
          replacmentNo.setVisibility(View.GONE);
          partNo.setVisibility(View.GONE);
          serialNo.setVisibility(View.GONE);
          ticketInfoET.setHint(getResources().getString(R.string.label_close_info_pr));
        }else if (closedTypes.equals("Closed by Replacment")){
          prNoET.setVisibility(View.GONE);
          replacmentNo.setVisibility(View.VISIBLE);
          partNo.setVisibility(View.VISIBLE);
          serialNo.setVisibility(View.VISIBLE);
          ticketInfoET.setHint(getResources().getString(R.string.label_close_info_replacment));
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        resetEdT();
      }
    });

      prNoET.addTextChangedListener(new TextWatcher(){

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
//              if (prNoET.length()!=9) {
//                  prNoET.setError(getResources().getString(R.string.error_prNo_action_digit));
//                  prNoET.requestFocus();
//              } else {
//                  prNoET.setEnabled(true);
//              }
//              if (prNoET.equals("")) {
//                  prNoET.setError(getResources().getString(R.string.error_prNo_action_requered));
//                  prNoET.setEnabled(true);
//              }
          }
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//              if (prNoET.equals("")) {
////                  prNoET.setError(getResources().getString(R.string.error_prNo_action_number));
//                  prNoET.setEnabled(true);
//              }
          }

          @Override
          public void afterTextChanged(Editable s) {
              String regexStr = "^[0-9]*$";
                if (prNoET.length()==0) {
                    prNoET.setError(getResources().getString(R.string.error_prNo_action_requered));
                }else if(!prNoET.getText().toString().matches(regexStr) && prNoET.length()>9) {
                  prNoET.setError(getResources().getString(R.string.error_prNo_action_digit));
                  prNoET.requestFocus();
                }else if(!prNoET.getText().toString().trim().matches(regexStr)) {
                    prNoET.setError(getResources().getString(R.string.error_prNo_action_number));
                    prNoET.requestFocus();
                }else if(prNoET.length()>9){
                    prNoET.setError(getResources().getString(R.string.error_prNo_action_digit_max));
                    prNoET.requestFocus();
                }else if(prNoET.length()<9){
                    prNoET.setError(getResources().getString(R.string.error_prNo_action_digit_min));
                    prNoET.requestFocus();
                }else{
                    prNoET.setError(getResources().getString(R.string.error_prNo_action_empty));
                    prNoET.setEnabled(true);
                    prNoET.requestFocus();
                }
          }
      }
     );

    prNoET.setOnKeyListener(
            new EditText.OnKeyListener() {

              @Override
              public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (prNoET.length() == 9) {
                  AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                  alertDialogBuilder.setTitle("WARNINNG");
                  alertDialogBuilder.setMessage("Max PR.No is  9 digit");
                  alertDialogBuilder.show();

//                prNoET.getText().setFilters(new InputFilter.LengthFilter(maxLength));
//                prNoET.setLeft(9);
//                EditText editText = new EditText(v.getContext());
//                int maxLength = 9;
//                prNoET.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
                }
                return false;
              }
            });

    replacmentNo.addTextChangedListener(new TextWatcher(){

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void afterTextChanged(Editable s) {
        String regexStr = "^[0-9]*$";
        if (replacmentNo.length()==0) {
          replacmentNo.setError(getResources().getString(R.string.error_replacmentNo_action_requered));
        }
//        else if(!prNoET.getText().toString().matches(regexStr) && replacmentNo.length()>9) {
//          replacmentNo.setError(getResources().getString(R.string.error_replacmentNo_action_digit));
//          replacmentNo.requestFocus();
//        }else if(!replacmentNo.getText().toString().trim().matches(regexStr)) {
//          replacmentNo.setError(getResources().getString(R.string.error_prNo_action_number));
//          replacmentNo.requestFocus();
//        }else if(prNoET.length()>9){
//          replacmentNo.setError(getResources().getString(R.string.error_replacmentNo_action_digit_max));
//          replacmentNo.requestFocus();
//        }else if(replacmentNo.length()<9){
//          replacmentNo.setError(getResources().getString(R.string.error_replacmentNo_action_digit_min));
//          replacmentNo.requestFocus();
//        }else{
//          replacmentNo.setError(getResources().getString(R.string.error_prNo_action_empty));
//          replacmentNo.setEnabled(true);
//          replacmentNo.requestFocus();
//        }
      }
    });

    partNo.addTextChangedListener(new TextWatcher(){

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void afterTextChanged(Editable s) {
        String regexStr = "^[0-9]*$";
        if (partNo.length()==0) {
          partNo.setError(getResources().getString(R.string.error_partNo_action_requered));
        }
      }
    });

    serialNo.addTextChangedListener(new TextWatcher(){

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void afterTextChanged(Editable s) {
        String regexStr = "^[0-9]*$";
        if (serialNo.length()==0) {
          serialNo.setError(getResources().getString(R.string.error_serialNo_action_requered));
        }
      }
    });

//    replacmentNo.setOnKeyListener(
//        new EditText.OnKeyListener() {
//
//          @Override
//          public boolean onKey(View v, int keyCode, KeyEvent event) {
//            if (replacmentNo.length() == 9) {
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
//                alertDialogBuilder.setTitle("WARNINNG");
//                alertDialogBuilder.setMessage("Max Replacment.No is  9 digit");
//                alertDialogBuilder.show();
//            }
//            return false;
//          }
//        }
//    );
  }




  public void resetEdT(){
    ticketInfoET.setText("");
    prNoET.setText("");
    replacmentNo.setText("");
    partNo.setText("");
    serialNo.setText("");
    ticketInfoET.setError(null);
    prNoET.setError(null);
    replacmentNo.setError(null);
    partNo.setError(null);
    serialNo.setError(null);
    ticketInfoET.clearFocus();
    prNoET.clearFocus();
    replacmentNo.clearFocus();
    partNo.clearFocus();
    serialNo.clearFocus();
  }

  private void initData() {
    title.setText(argTitle);
//    descAlert.setText(argMessage);
    btnDone.setText(argDone);
    if(argClose != null)
      btnClose.setText(argClose);

//    fixTypeSpinner.setAdapter(new );

//    preferences = new Preferences(getActivity());

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

//  public String getStringSpinnerItem(){
//    return fixTypeSpinner.getSelectedItem().toString();
//  }
  public MaterialSpinner getStringSpinnerItem(){
    return fixTypeSpinner;
  }

  public static PopupCloseTicketCustomReplacment newInstance(String argTitle, String argDone, String argClose) {
    PopupCloseTicketCustomReplacment f = new PopupCloseTicketCustomReplacment();

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

  public EditText getReplacmentNo() {
    return replacmentNo;
  }

  public void setReplacmentNo(EditText replacmentNo) {
    this.replacmentNo = replacmentNo;
  }
  public EditText getPartNo() {
    return partNo;
  }

  public void setPartlNo(EditText partNo) {
    this.partNo = partNo;
  }

  public EditText getSerialNo() {
    return serialNo;
  }

  public void setSerialNo(EditText serialNo) {
    this.serialNo = serialNo;
  }


}
