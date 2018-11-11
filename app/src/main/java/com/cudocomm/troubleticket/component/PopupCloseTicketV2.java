package com.cudocomm.troubleticket.component;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.databinding.ObservableArrayList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
import android.widget.Toast;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.TTSApplication;
import com.cudocomm.troubleticket.model.penyebab.PenyebabNew;
import com.cudocomm.troubleticket.model.program.ProgramNew;
import com.cudocomm.troubleticket.service.trouble.PenyebabService;
import com.cudocomm.troubleticket.service.trouble.TrobleServcice;
import com.cudocomm.troubleticket.service.trouble.TroubleApplication;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Preferences;
import com.ikovac.timepickerwithseconds.MyTimePickerDialog;
import com.ikovac.timepickerwithseconds.TimePicker;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

import fr.ganfra.materialspinner.MaterialSpinner;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import retrofit2.HttpException;

//import android.app.DialogFragment;

public class PopupCloseTicketV2 extends DialogFragment {

  public static final String TAG = PopupCloseTicketV2.class.getSimpleName();
  private CompositeDisposable subscription=new CompositeDisposable();


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
//  private SearchableSpinner penyebabET;
//  private SearchableSpinner programET;
  private EditText startTimeET;
  private EditText durationET;
  private MaterialSpinner fixTypeSpinner;
  private EditText prNoET;

  String[] closeTypes;

  SearchableSpinner programSpinner;
  SearchableSpinner penyebabSpinner;

  String program, penyebab;

  ObservableArrayList<String> listProgram= new ObservableArrayList<>();
  ObservableArrayList<String> listPenyebab= new ObservableArrayList<>();
  long idPrgram;
  long idPenyebab;

  String closedTypes;



  TimePickerDialog.OnTimeSetListener startTimeCallback;
  TimePickerDialog.OnTimeSetListener durationCallback;

//  Context context;

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
    rootView = inflater.inflate(R.layout.popup_close_ticket_v2, container, false);
    initUI();
    initData();


    return rootView;
  }

  private void setProgramAcara() {
    if (!subscription.isDisposed()) subscription.clear();
    TroubleApplication troubleApplication = TroubleApplication.get(getContext());
    troubleApplication.setTrobleServcice(TrobleServcice.Factory.createTroubleService(TrobleServcice.TYPE_GSON));
    TrobleServcice trobleServcice = troubleApplication.getTrobleServcice();
    subscription.add(trobleServcice.getProgram()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(troubleApplication.defaultSubscribeScheduler())
            .subscribeWith(new DisposableObserver<ProgramNew>() {

              @SuppressLint("ResourceAsColor")
              @Override
              public void onNext(final ProgramNew programNew) {
                Log.d(TAG, "onNext programNew: " + programNew);
                for (int i = 0; i < programNew.getData().size(); i++) {
                  Log.d(TAG, "onNext PROGRAM: " + i);

                  listProgram.add(programNew.getData().get(i).getProgramName());
//                  programSpinner.setEnabled(true);
                  new ArrayAdapter<>(getContext(), R.layout.row_spinner2,
                          R.id.spinnerValueTV, listProgram);
                  ArrayAdapter adapterSubLocation;
                  if (getActivity() != null) {
                    adapterSubLocation = new ArrayAdapter<>(getContext(), R.layout.row_spinner2,
                            R.id.spinnerValueTV, listProgram);

                    adapterSubLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    programSpinner.setAdapter(adapterSubLocation);
                  }
                    programSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                      @Override
                      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        idPrgram = id;

                        idPrgram = Long.parseLong(programNew.getData().get(position).getProgramId());
                        program = String.valueOf(programSpinner.getSelectedItemId());


                        Log.d(TAG, "onItemSelected Program: " + program);


                        Log.d(TAG, "onItemSelected Program: " + (int) idPrgram);
                      }

                      @Override
                      public void onNothingSelected(AdapterView<?> parent) {

                      }
                    });
//                                }


//                            Log.d(TAG, "onNextManufacture: " + manufacture.getData().get(i).getNameManufacture());
//                                ArrayAdapter adapterSubLocation = new ArrayAdapter<>(getContext(), R.layout.spinners, listManufacure);


                  }
                  setPenyebab();

              }

              @Override
              public void onError(Throwable e) {
                e.printStackTrace();
                Log.e(TAG, "onError: ",e );

                if (isHttp404(e)) {
                  Toast.makeText(getContext(), "Failed Connected", Toast.LENGTH_SHORT).show();
                } else {
                  Toast.makeText(getContext(), "Failed Connected", Toast.LENGTH_SHORT).show();
                }
              }

              @Override
              public void onComplete() {

              }
            }));

  }



  private void setPenyebab() {
    if (!subscription.isDisposed()) subscription.clear();
    TroubleApplication troubleApplication = TroubleApplication.get(getContext());
    troubleApplication.setPenyebab(PenyebabService.Factory.createPenyebabService(TrobleServcice.TYPE_GSON));
    PenyebabService penyebabService = troubleApplication.getPenyebabService();
    subscription.add(penyebabService.getPenyebab()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(troubleApplication.defaultSubscribeScheduler())
            .subscribeWith(new DisposableObserver<PenyebabNew>() {

              @SuppressLint("ResourceAsColor")
              @Override
              public void onNext(final PenyebabNew programNew) {
                Log.d(TAG, "onNext penyebab: " + programNew);
                for (int i = 0; i < programNew.getData().size(); i++) {
                  Log.d(TAG, "onNext PENYEBAB: " + i);
                  listPenyebab.add(programNew.getData().get(i).getPenyebab());
                  if (getActivity() != null) {
                    ArrayAdapter adapterSubLocation = new ArrayAdapter<>(getContext(), R.layout.row_spinner2,
                            R.id.spinnerValueTV, listPenyebab);

                    adapterSubLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    penyebabSpinner.setAdapter(adapterSubLocation);
                  }
//                  penyebabSpinner.setEnabled(true);
                  penyebabSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                      idPenyebab = id;

                      idPenyebab = Long.parseLong(programNew.getData().get(position).getId());
                      penyebab = String.valueOf(penyebabSpinner.getSelectedItemId());





                      Log.d(TAG, "onItemSelected Penyebab: " + penyebab);


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                  });
//                                }


//                            Log.d(TAG, "onNextManufacture: " + manufacture.getData().get(i).getNameManufacture());
//                                ArrayAdapter adapterSubLocation = new ArrayAdapter<>(getContext(), R.layout.spinners, listManufacure);


                }
              }

              @Override
              public void onError(Throwable e) {
                e.printStackTrace();
                Log.e(TAG, "onError: ",e );

                if (isHttp404(e)) {
                  Toast.makeText(getContext(), "Failed Connected", Toast.LENGTH_SHORT).show();
                } else {
                  Toast.makeText(getContext(), "Failed Connected", Toast.LENGTH_SHORT).show();
                }
              }

              @Override
              public void onComplete() {

              }
            }));

  }


  private static boolean isHttp404(Throwable error) {
    return error instanceof HttpException && ((HttpException) error).code() == 404;
  }

  private void initUI() {
    btnDone = (Button) rootView.findViewById(R.id.btnDone);
    btnClose = (Button) rootView.findViewById(R.id.btnClose);
    title = (TextView) rootView.findViewById(R.id.titledata);
    ticketInfoET = (EditText) rootView.findViewById(R.id.ticketInfoET);
    penyebabSpinner = (SearchableSpinner) rootView.findViewById(R.id.penyebabSpin);
    programSpinner = (SearchableSpinner) rootView.findViewById(R.id.programSpin);
    startTimeET = (EditText) rootView.findViewById(R.id.startTimeET);
    durationET = (EditText) rootView.findViewById(R.id.durationET);
    fixTypeSpinner = (MaterialSpinner) rootView.findViewById(R.id.fixTypeSpinner);
    prNoET = (MaterialEditText) rootView.findViewById(R.id.prNoET);



//    descAlert = (TextView) rootView.findViewById(R.id.descAlert);
    closeTypes = getResources().getStringArray(R.array.close_type_array);
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(TTSApplication.getContext(), android.R.layout.simple_spinner_item, closeTypes);
    fixTypeSpinner.setAdapter(adapter);
    fixTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "onItemSelected: " + fixTypeSpinner.getSelectedItem());
            Log.d(TAG, "onItemSelected: " + fixTypeSpinner);
          closedTypes = String.valueOf(fixTypeSpinner.getSelectedItem());
            if (closedTypes.equals("Fix Closed")) {
              prNoET.setVisibility(View.GONE);
            } else {
              prNoET.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    });
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

    startTimeCallback = new TimePickerDialog.OnTimeSetListener() {
      @Override
      public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String date = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second);
        startTimeET.setText(date);
      }
    };

    durationCallback = new TimePickerDialog.OnTimeSetListener() {
      @Override
      public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String date = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second);
        durationET.setText(date);
      }
    };

    startTimeET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus) {
          showDateTimePicker(startTimeET);
        }
      }
    });

    startTimeET.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showDateTimePicker(startTimeET);
      }
    });

    durationET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus) {
          showDateTimePicker(durationET);
        }
      }
    });

    durationET.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showDateTimePicker(durationET);
      }
    });

    setProgramAcara();
    listProgram.clear();
  }

  private void showDateTimePicker(final EditText et) {
    Calendar now = Calendar.getInstance();
    String nameResource = getResources().getResourceEntryName(et.getId());
    int hour = 0,
            min = 0,
            sec = 0;
    if (nameResource.equals("startTimeET")) {
      hour = now.get(Calendar.HOUR_OF_DAY);
      min = now.get(Calendar.MINUTE);
      sec = now.get(Calendar.SECOND);
    }
    MyTimePickerDialog mTimePicker = new MyTimePickerDialog(getActivity(), new MyTimePickerDialog.OnTimeSetListener() {
      @Override
      public void onTimeSet(TimePicker view, int hourOfDay, int minute, int seconds) {
        // TODO Auto-generated method stub
        et.setText(String.format("%02d", hourOfDay)+
                ":" + String.format("%02d", minute) +
                ":" + String.format("%02d", seconds));
      }
    }, hour, min, sec, true);
    mTimePicker.show();
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

  public static PopupCloseTicketV2 newInstance(String argTitle, String argDone, String argClose) {
    PopupCloseTicketV2 f = new PopupCloseTicketV2();

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

  public long getPenyebabET() {
    return idPenyebab;
  }

  public void setPenyebabET(SearchableSpinner penyebabET) {
    this.penyebabSpinner = penyebabET;
  }

  public long getProgramET() {
    return idPrgram;
  }

  public void setProgramET(SearchableSpinner programET) {
    this.programSpinner = programET;
  }

  public EditText getStartTimeET() {
    return startTimeET;
  }

  public void setStartTimeET(EditText startTimeET) {
    this.startTimeET = startTimeET;
  }

  public EditText getDurationET() {
    return durationET;
  }

  public void setDurationET(EditText durationET) {
    this.durationET = durationET;
  }

  public String getFixTypeSpinner() {

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

  /*@Override
  public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
    String date = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second);

    if(view.getId() == startTimeET.getId())
      startTimeET.setText(date);
    else if(view.getId() == durationET.getId())
      durationET.setText(date);
  }*/
}
