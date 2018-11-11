package com.cudocomm.troubleticket.component;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.activity.MainActivity;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Preferences;

public class PopupNotification extends Activity {

  private Preferences preferences;

  private Dialog dialog;
  private View rootView;
  private Button closeBtn, doneBtn;
  private TextView titleMsg, contentMsg;

  private String argTitle, argMsg;

  private View.OnClickListener backListener;
  private View.OnClickListener processListener;

  private NotificationManager mNotificationManager;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(1);
    setContentView(R.layout.activity_popup_notif);
    setFinishOnTouchOutside(false);
    titleMsg = (TextView) findViewById(R.id.titleMsg);
    contentMsg = (TextView) findViewById(R.id.contentMsg);
    mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    argTitle = getIntent().getExtras().getString(Constants.PARAM_TITLE);
    argMsg = getIntent().getExtras().getString(Constants.PARAM_MSG);
    titleMsg.setText(argTitle);
    contentMsg.setText(argMsg);
  }

  public void positiveButton(View v) {
    mNotificationManager.cancel(NotificationManager.IMPORTANCE_MAX); //575857
    launch_activity();
  }

  public void negativeButton(View v) {
    mNotificationManager.cancel(NotificationManager.IMPORTANCE_NONE); //575857
    finish();
  }

  private void launch_activity() {
    Intent i = new Intent();
    i.setAction("android.intent.action.MAIN");
    i.setClass(this, MainActivity.class);
    i.setFlags(67108864);
    startActivity(i);
    finish();
  }

}
