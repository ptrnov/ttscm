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
import com.cudocomm.troubleticket.util.Preferences;
import com.rengwuxian.materialedittext.MaterialEditText;

import fr.ganfra.materialspinner.MaterialSpinner;

public class PopupReplacment extends DialogFragment {
  public static final String TAG = PopupReplacment.class.getSimpleName();

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

  private MaterialSpinner itemConditionSpinner;
  public EditText itemInfo;

  String[] replacmentCheck;
  String replacmentChecked;
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
    rootView = inflater.inflate(R.layout.popup_replacment, container, false);
    initUI();
    initData();
    return rootView;
  }

  public class Item
  {
    public long id;
    public String name;
  }

  private void initUI() {
    btnDone = (Button) rootView.findViewById(R.id.btnDone);
    btnClose = (Button) rootView.findViewById(R.id.btnClose);
    title = (TextView) rootView.findViewById(R.id.titledata);
    itemConditionSpinner = (MaterialSpinner) rootView.findViewById(R.id.itemConditionSpinner);
    itemInfo = (MaterialEditText) rootView.findViewById(R.id.itemInfo);
    replacmentCheck = getResources().getStringArray(R.array.replacment_check);

//    ArrayAdapter<String> adapter =
//        new ArrayAdapter<String>(
//            TTSApplication.getContext(), R.layout.spiner_dropdown_item, replacmentCheck);
//    itemConditionSpinner.setAdapter(adapter);
    String[] ar1={"item has been repaired","items cannot be repaired"};
    String[] ar2={"1","2"};
    ArrayAdapter<String> adapter =
        new ArrayAdapter<String>(
            TTSApplication.getContext(), R.layout.spiner_dropdown_item, ar1);
    itemConditionSpinner.setAdapter(adapter);

    itemConditionSpinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "onItemSelected: " + itemConditionSpinner.getSelectedItem());
            Log.d(TAG, "onItemSelected: " + itemConditionSpinner);
            resetItems();
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {
            resetItems();
          }
        });

    itemInfo.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {}

          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          @Override
          public void afterTextChanged(Editable s) {
            String regexStr = "^[0-9]*$";
            if (itemInfo.length() == 0) {
              itemInfo.setError(
                  getResources().getString(R.string.error_replacmentNo_action_requered));
            }
          }
        });
   }


  public void resetItems(){
      itemInfo.setText("");
      itemInfo.setError(null);
      itemInfo.clearFocus();
  }

  private void initData() {
    title.setText(argTitle);
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

  public static PopupReplacment newInstance(String argTitle, String argDone, String argClose) {
    PopupReplacment f = new PopupReplacment();

    // Supply num input as an argument.
    Bundle args = new Bundle();
    args.putString(Constants.ARGS_TITLE, argTitle);
//    args.putString(Constants.ARGS_DESC, argMessage);
    args.putString(Constants.ARGS_PROCESS, argDone);
    args.putString(Constants.ARGS_BACK, argClose);
    f.setArguments(args);

    return f;
  }

  public EditText getItemInfo() {
    return itemInfo;
  }

  public void setItemInfo(EditText itemInfo) {
    this.itemInfo = itemInfo;
  }

  public MaterialSpinner getItemConditionSpinner(){
    return itemConditionSpinner;
  }

  public void setItemConditionSpinner(MaterialSpinner itemConditionSpinner){
    this.itemConditionSpinner = itemConditionSpinner;
  }
}
