package com.cudocomm.troubleticket.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.adapter.UserAdapter;
import com.cudocomm.troubleticket.adapter.holder.HSpinner;
import com.cudocomm.troubleticket.component.CustomPopConfirm;
import com.cudocomm.troubleticket.model.Ticket;
import com.cudocomm.troubleticket.model.UserModel;
import com.cudocomm.troubleticket.util.ApiClient;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dmax.dialog.SpotsDialog;
import fr.ganfra.materialspinner.MaterialSpinner;
import okhttp3.FormBody;

public class AssignmentFragment extends BaseFragment implements DatePickerDialog.OnDateSetListener {

    private View rootView;

    private MaterialSpinner assignToSpinner;
    private MaterialSpinner assignTypeSpinner;

    private EditText assignmentDateET;
    private EditText assignmentActionET;

//    private ArrayAdapter assignTypeAdapter;

    private String selectedAssignType;
    private UserModel selectedEngineer = new UserModel();

    private List<UserModel> engineers = new ArrayList<>();

    private SpotsDialog progress;
    private Ticket selectedTicket;

    private Button btnClose;
    private Button btnDone;

    private CustomPopConfirm popConfirm;

    private String assignDate, assignAction;

    public AssignmentFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedTicket = (Ticket) getArguments().getSerializable(Constants.SELECTED_TICKET);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_assignment, container, false);

        initComponent();
        new GetEngineerTask().execute();

        return rootView;
    }

    private void initComponent() {
        progress = new SpotsDialog(getContext(), R.style.progress_dialog_style);
        assignToSpinner = (MaterialSpinner) rootView.findViewById(R.id.assignToSpinner);
        assignTypeSpinner = (MaterialSpinner) rootView.findViewById(R.id.assignTypeSpinner);
        assignmentDateET = (EditText) rootView.findViewById(R.id.assignmentDateET);
        assignmentActionET = (EditText) rootView.findViewById(R.id.actionET);

        btnClose = (Button) rootView.findViewById(R.id.btnClose);
        btnDone = (Button) rootView.findViewById(R.id.btnDone);
    }

    private void updateComponent() {
        ArrayAdapter assignTypeAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.assign_type));
        assignTypeSpinner.setAdapter(assignTypeAdapter);
        assignTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAssignType = (String) parent.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setSelection(0);
            }
        });

        assignmentDateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        AssignmentFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
              dpd.show(getActivity().getSupportFragmentManager(), null);
            }
        });


        UserAdapter userAdapter = new UserAdapter(getActivity(), engineers);
        assignToSpinner.setAdapter(userAdapter);

        assignToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position > -1) {
                    HSpinner hSpinner = new HSpinner(view);
                    selectedEngineer.setUserId(new Integer(hSpinner.spinnerKeyTV.getText().toString()));
                    selectedEngineer.setUserName(hSpinner.spinnerValueTV.getText().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assignDate = assignmentDateET.getText().toString();
                assignAction = assignmentActionET.getText().toString();
                if(assignToSpinner.getSelectedItemPosition() == 0) {
                    assignToSpinner.getSelectedView().requestFocus();
                    assignToSpinner.setError(getResources().getString(R.string.error_assignment_to));
                } else if(assignTypeSpinner.getSelectedItemPosition() == 0) {
                    assignTypeSpinner.getSelectedView().requestFocus();
                    assignTypeSpinner.setError(getResources().getString(R.string.error_assignment_type));
                } else if(TextUtils.isEmpty(assignDate)) {
                    assignmentDateET.requestFocus();
                    assignmentDateET.setError(getResources().getString(R.string.error_assignment_date));
                } else if(TextUtils.isEmpty(assignAction)) {
                    assignmentActionET.requestFocus();
                    assignmentActionET.setError(getResources().getString(R.string.label_assignment_action));
                } else {
                    String title = "Submission Confirmation";
                    String msg = "You will assign \""+ CommonsUtil.ticketTypeToString(selectedTicket.getTicketType())+ "\" ticket with detail : \nLocation : "+
                            selectedTicket.getStationName() +
                            "\nSuspect : " + selectedTicket.getSuspect1Name() + " - " + selectedTicket.getSuspect2Name() + " - " + selectedTicket.getSuspect3Name() +
                            "\nSeverity : " + CommonsUtil.severityToString(selectedTicket.getTicketSeverity()) +
                            "\nto : " + selectedEngineer.getUserName() +
                            "\nfor : " + selectedAssignType +
                            "\non : " + assignDate +
                            "\nnotes : " + assignAction;
                    popConfirm = CustomPopConfirm.newInstance(title,msg,"Process","Back");
                    popConfirm.setBackListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popConfirm.dismiss();
                        }
                    });
                    popConfirm.setProcessListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popConfirm.dismiss();
                            new SubmitAssignmentTask().execute();
                        }
                    });
                    popConfirm.show(getActivity().getFragmentManager(), null);
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = String.format("%02d", year) + "-" + String.format("%02d", monthOfYear + 1) + "-" + String.format("%02d", dayOfMonth);

        assignmentDateET.setText(date);
    }

    private class GetEngineerTask extends AsyncTask<Void, Void, Void> {

        String result;
        JSONObject object;
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gsona = gsonBuilder.create();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(
                        CommonsUtil.getAbsoluteUrl(Constants.URL_GET_ENGINEER), new FormBody.Builder().build());
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
                    Type type = new TypeToken<List<UserModel>>(){}.getType();
                    try {
                        engineers = gsona.fromJson(object.getString("data"), type);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            updateComponent();
            progress.dismiss();

        }
    }

    private class SubmitAssignmentTask extends AsyncTask<Void, Void, Void> {

        String result;
        JSONObject object;
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gsona = gsonBuilder.create();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(CommonsUtil.getAbsoluteUrl(Constants.URL_SUBMIT_ASSIGNMENT), new FormBody.Builder()
                        .add(Constants.PARAM_TICKET_ID, selectedTicket.getTicketId())
                        .add("from_user_id", String.valueOf(preferences.getPreferencesInt(Constants.ID_UPDRS)))
                        .add("to_user_id", String.valueOf(selectedEngineer.getUserId()))
                        .add("date", assignDate)
                        .add("action_id", String.valueOf(CommonsUtil.assignTypeToInt(selectedAssignType)))
                        .add("info", assignAction)
                        .add("onsite", "0")
                        .add("req_remarks", "")
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
                    String page = Constants.MY_TASK_PAGE;
                    Boolean flag = Boolean.FALSE;
                    Fragment f = new MyTicket();
                    Bundle args = new Bundle();
                    f.setArguments(args);
                    preferences.savePreferences(Constants.ACTIVE_PAGE, page);
                    mListener.onMenuSelected(page, f, flag);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progress.dismiss();

        }
    }

}
