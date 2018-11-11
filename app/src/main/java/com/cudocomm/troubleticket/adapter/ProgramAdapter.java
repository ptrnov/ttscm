package com.cudocomm.troubleticket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.adapter.holder.HSpinner;
import com.cudocomm.troubleticket.database.model.Program;

import java.util.List;

/**
 * Created by adsxg on 7/10/2017.
 */

public class ProgramAdapter extends BaseAdapter {

    private Context context;
    private List<Program> programs;

    public ProgramAdapter(Context context, List<Program> programs) {
        this.context = context;
        this.programs = programs;
    }

    public int getCount() {
        return programs != null?programs.size():0;
    }

    public Object getItem(int position) {
        return programs.get(position);
    }

    public long getItemId(int position) {
        return (long)position;
    }

    public int getItemPosition(Program obj) {
        for(Program model : programs) {
            if(obj.getProgramId().equals(model.getProgramId()))
                return programs.indexOf(model) + 1;
        }
        return -1;
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
        if (programs != null) {
            Program selectedProgram = programs.get(position);
            holder.spinnerKeyTV.setText(String.valueOf(selectedProgram.getProgramId()));
            holder.spinnerValueTV.setText(selectedProgram.getProgramName());
        }
        return view;
    }

}
