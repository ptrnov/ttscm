package com.cudocomm.troubleticket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.adapter.holder.HSpinnerUpdate;
import com.cudocomm.troubleticket.database.model.SeverityUpdateModel;

import java.util.List;

/**
 * Created by adsxg on 4/18/2017.
 */

public class SeverityUpdateAdapter2 extends BaseAdapter {

    private Context context;
    private List<SeverityUpdateModel> severities;
    private SeverityUpdateModel selectedSeverity;

    public SeverityUpdateAdapter2(Context context, List<SeverityUpdateModel> severities) {
        this.context = context;
        this.severities = severities;
    }

    public int getCount() {
        return severities != null?severities.size():0;
    }

    public Object getItem(int position) {
        return severities.get(position);
    }

    public long getItemId(int position) {
        return (long)position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        HSpinnerUpdate holder;
        View view = convertView;
        if (view == null) {
            view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.row_spinner_update, null);
            holder = new HSpinnerUpdate(view);
            view.setTag(holder);
        } else {
            holder = (HSpinnerUpdate) view.getTag();
        }
        if (severities != null) {
            selectedSeverity = severities.get(position);
            holder.spinnerKeyTV.setText(String.valueOf(selectedSeverity.getSeverityId()));
            holder.spinnerValueTV.setText(selectedSeverity.getSeverityName());
        }
        return view;
    }

}
