package com.cudocomm.troubleticket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.adapter.holder.HSpinner;
import com.cudocomm.troubleticket.database.model.StationModel;

import java.util.List;

/**
 * Created by adsxg on 4/18/2017.
 */

public class StationAdapter extends BaseAdapter {

    private Context context;
    private List<StationModel> stations;
    private StationModel selectedStation;

    public StationAdapter(Context context, List<StationModel> stations) {
        this.context = context;
        this.stations = stations;
    }

    public int getCount() {
        return stations != null?stations.size():0;
    }

    public Object getItem(int position) {
        return stations.get(position);
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
        if (stations != null) {
            selectedStation = stations.get(position);
            holder.spinnerKeyTV.setText(String.valueOf(selectedStation.getStationId()));
            holder.spinnerValueTV.setText(selectedStation.getStationName());
        }
        return view;
    }

}
