package com.cudocomm.troubleticket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.model.MenuModel;
import com.squareup.picasso.Picasso;

public class MenuAdapter extends ArrayAdapter<MenuModel> {

    Context c;
    int layout;

    public static class MenuViewHolder {
        public final ImageView iconView;
        public final TextView titleView;

        public MenuViewHolder(TextView titleView, ImageView iconView) {
            this.titleView = titleView;
            this.iconView = iconView;
        }
    }

    public MenuAdapter(Context context, int layout) {
        super(context, 0);
        this.layout = layout;
        this.c = context;
    }

    public static MenuAdapter get_instance(Context context, int layout) {
        return new MenuAdapter(context, layout);
    }

    public boolean isEnabled(int position) {
        return !getItem(position).isGroupTitle();
    }

    public int getItemViewType(int position) {
        return getItem(position).isGroupTitle() ? 1 : 0;
    }

    public int getViewTypeCount() {
        return 2;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        MenuModel menu = getItem(position);
        MenuViewHolder holder;
        if (getItemViewType(position) == 1) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.menu_row_title, parent, false);
                holder = new MenuViewHolder((TextView) convertView.findViewById(R.id.title), null);
                convertView.setTag(holder);
            } else {
                holder = (MenuViewHolder) convertView.getTag();
            }
            holder.titleView.setText(menu.getTitle());
        } else {
            int rowLayout = 0;
            if (this.layout != 0) {
                rowLayout = this.layout;
            }
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(rowLayout, parent, false);
                holder = new MenuViewHolder((TextView) convertView.findViewById(R.id.title), (ImageView) convertView.findViewById(R.id.icon));
                convertView.setTag(holder);
            } else {
                holder = (MenuViewHolder) convertView.getTag();
            }
            holder.titleView.setText(menu.getTitle());
            if (menu.getIcon() != 0) {
//                holder.iconView.setImageResource(menu.getIcon());
                Picasso.with(this.c).load(menu.getIcon()).error(R.drawable.ic_no_image).into(holder.iconView);
            } else if (!menu.getIconUrl().isEmpty()) {
                Picasso.with(this.c).load(menu.getIconUrl()).into(holder.iconView);
            }
        }
        return convertView;
    }
}
