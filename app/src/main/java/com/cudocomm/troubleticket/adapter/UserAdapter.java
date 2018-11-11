package com.cudocomm.troubleticket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.adapter.holder.HSpinner;
import com.cudocomm.troubleticket.model.UserModel;

import java.util.List;

/**
 * Created by adsxg on 4/18/2017.
 */

public class UserAdapter extends BaseAdapter {

    private Context context;
    private List<UserModel> users;
    private UserModel selectedUser;

    public UserAdapter(Context context, List<UserModel> users) {
        this.context = context;
        this.users = users;
    }

    public int getCount() {
        return users != null?users.size():0;
    }

    public Object getItem(int position) {
        return users.get(position);
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
        if (users != null) {
            selectedUser = users.get(position);
            holder.spinnerKeyTV.setText(String.valueOf(selectedUser.getUserId()));
            holder.spinnerValueTV.setText(selectedUser.getUserName());
        }
        return view;
    }

}
