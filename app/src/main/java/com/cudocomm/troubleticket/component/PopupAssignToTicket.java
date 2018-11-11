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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.adapter.UserAdapter;
import com.cudocomm.troubleticket.adapter.holder.HSpinner;
import com.cudocomm.troubleticket.model.UserModel;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Preferences;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

public class PopupAssignToTicket extends DialogFragment {

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

    private MaterialSpinner assignToSpinner;

    private EditText assignmentActionET;

    private ArrayAdapter assignTypeAdapter;

    private String selectedAssignType;
    private UserModel selectedEngineer = new UserModel();

    private List<UserModel> engineers = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        argTitle = getArguments().getString(Constants.ARGS_TITLE);
        argDone = getArguments().getString(Constants.ARGS_PROCESS);
        argClose = getArguments().getString(Constants.ARGS_BACK);

        engineers = (List<UserModel>) getArguments().getSerializable(Constants.PARAM_ENGINEERS);

    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = super.onCreateDialog(savedInstanceState);
        Window window = this.dialog.getWindow();
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
        rootView = inflater.inflate(R.layout.popup_assignto_ticket, container, false);
        initUI();
        initData();
        return rootView;
    }

    private void initUI() {
        btnDone = (Button) rootView.findViewById(R.id.btnDone);
        btnClose = (Button) rootView.findViewById(R.id.btnClose);
        title = (TextView) rootView.findViewById(R.id.titledata);


        assignToSpinner = (MaterialSpinner) rootView.findViewById(R.id.assignToSpinner);
        assignmentActionET = (EditText) rootView.findViewById(R.id.actionET);

//    actionET = (EditText) rootView.findViewById(R.id.actionET);
//    requireET = (EditText) rootView.findViewById(R.id.requireET);
    }

    private void initData() {
        title.setText(argTitle);
//    descAlert.setText(argMessage);
        btnDone.setText(argDone);
        if (argClose != null)
            btnClose.setText(argClose);

        preferences = new Preferences(getActivity());

        btnClose.setOnClickListener(getBackListener());
        btnDone.setOnClickListener(getProcessListener());

        UserAdapter userAdapter = new UserAdapter(getActivity(), engineers);
        assignToSpinner.setAdapter(userAdapter);

        assignToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > -1) {
                    HSpinner hSpinner = new HSpinner(view);
                    selectedEngineer.setUserId(new Integer(hSpinner.spinnerKeyTV.getText().toString()));
                    selectedEngineer.setUserName(hSpinner.spinnerValueTV.getText().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                        selectedEngineer.setUserId(null);
//                        selectedEngineer.setUserName(hSpinner.spinnerValueTV.getText().toString());
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

    public static PopupAssignToTicket newInstance(String argTitle, String argDone, String argClose, List<UserModel> engineers) {
        PopupAssignToTicket f = new PopupAssignToTicket();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString(Constants.ARGS_TITLE, argTitle);
//    args.putString(Constants.ARGS_DESC, argMessage);
        args.putString(Constants.ARGS_PROCESS, argDone);
        args.putString(Constants.ARGS_BACK, argClose);
        args.putSerializable(Constants.PARAM_ENGINEERS, (Serializable) engineers);
        f.setArguments(args);

        return f;
    }

    public MaterialSpinner getAssignToSpinner() {
        return assignToSpinner;
    }

    public void setAssignToSpinner(MaterialSpinner assignToSpinner) {
        this.assignToSpinner = assignToSpinner;
    }


    public EditText getAssignmentActionET() {
        return assignmentActionET;
    }

    public void setAssignmentActionET(EditText assignmentActionET) {
        this.assignmentActionET = assignmentActionET;
    }

    public String getSelectedAssignType() {
        return selectedAssignType;
    }

    public void setSelectedAssignType(String selectedAssignType) {
        this.selectedAssignType = selectedAssignType;
    }

    public UserModel getSelectedEngineer() {
        return selectedEngineer;
    }

    public void setSelectedEngineer(UserModel selectedEngineer) {
        this.selectedEngineer = selectedEngineer;
    }
}
