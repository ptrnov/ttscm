package com.cudocomm.troubleticket.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.util.Constants;

public class PopupNotif extends Activity {

    private NotificationManager mNotificationManager;
    String message;
    ImageView imageIcon;
    TextView textContent;
    TextView textTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.activity_popup_notif);
        setFinishOnTouchOutside(false);
        this.mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        this.message = getIntent().getExtras().getString(Constants.EXTRA_MESSAGE);
        this.textContent = (TextView) findViewById(R.id.contentMsg);
        this.imageIcon = (ImageView) findViewById(R.id.notifIcon);
        this.textContent.setText(this.message);
    }

    public void positiveButton(View v) {
        this.mNotificationManager.cancel(575857);
        launch_activity();
    }

    public void negativeButton(View v) {
        this.mNotificationManager.cancel(575857);
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
