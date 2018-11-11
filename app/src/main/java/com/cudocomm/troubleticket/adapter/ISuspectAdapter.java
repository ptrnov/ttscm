package com.cudocomm.troubleticket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.adapter.holder.HSpinner;
import com.cudocomm.troubleticket.model.ISuspect;

import java.util.List;

/**
 * Created by adsxg on 4/18/2017.
 */

public class ISuspectAdapter extends BaseAdapter {

    private Context context;
    private List<ISuspect> iSuspects;
    private ISuspect selectedISuspect;

    public ISuspectAdapter(Context context, List<ISuspect> iSuspects) {
        this.context = context;
        this.iSuspects = iSuspects;
    }

    public int getCount() {
        return iSuspects != null?iSuspects.size():0;
    }

    public Object getItem(int position) {
        return iSuspects.get(position);
    }

    public long getItemId(int position) {
        return (long)position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        HSpinner holder;
        View view = convertView;
        if (view == null) {
            view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.row_spinner, null);
            holder = new HSpinner(view);
            view.setTag(holder);
        } else {
            holder = (HSpinner) view.getTag();
        }
        if (iSuspects != null) {
            selectedISuspect = iSuspects.get(position);
            holder.spinnerKeyTV.setText(selectedISuspect.getSuspectId());
            holder.spinnerValueTV.setText(selectedISuspect.getSuspectName());
        }
        return view;
    }

}
