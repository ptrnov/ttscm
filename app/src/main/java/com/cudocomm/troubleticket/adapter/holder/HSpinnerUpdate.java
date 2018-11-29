package com.cudocomm.troubleticket.adapter.holder;

import android.view.View;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;

public class HSpinnerUpdate {

  public TextView spinnerKeyTV;
  public TextView spinnerValueTV;

  public HSpinnerUpdate(View paramView)  {
    this.spinnerKeyTV = ((TextView)paramView.findViewById(R.id.spinnerKeyTV));
    this.spinnerValueTV = ((TextView)paramView.findViewById(R.id.spinnerValueTV));
  }

}