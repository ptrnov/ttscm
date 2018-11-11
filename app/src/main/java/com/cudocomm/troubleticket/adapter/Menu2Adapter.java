package com.cudocomm.troubleticket.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.model.MenuModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuru on 27/11/2016.
 */

public class Menu2Adapter extends RecyclerView.Adapter<Menu2Adapter.ViewHolder> {

    private List<MenuModel> mDataset = new ArrayList<>();
    private OnItemClickListener listener;
    Context context;

    public interface OnItemClickListener {
        void onItemClick(MenuModel menuModel);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView menuIconIV;
        public TextView menuNameTV;
        Context context;

        public ViewHolder(Context context, View v) {
            super(v);
            menuNameTV = (TextView) v.findViewById(R.id.menuNameTV);
            menuIconIV = (ImageView) v.findViewById(R.id.menuIconIV);
            this.context = context;
        }

        public void bind(final MenuModel menuModel, final OnItemClickListener listener) {
            menuNameTV.setText(menuModel.getTitle());
            Picasso.with(context).load(menuModel.getIcon()).error(R.drawable.ic_no_image).into(menuIconIV);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(menuModel);
                }
            });
        }
    }

    public Menu2Adapter(Context context, List<MenuModel> dataset, OnItemClickListener listener) {
        this.listener = listener;
        this.context = context;
        mDataset.clear();
        mDataset.addAll(dataset);
    }

    public void swap(List<MenuModel> list){
        if (mDataset != null) {
            mDataset.clear();
            mDataset.addAll(list);
        }
        else {
            mDataset = list;
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_view, parent, false);
        ViewHolder vh = new ViewHolder(parent.getContext(), v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MenuModel menuModel = mDataset.get(position);

        holder.bind(menuModel, listener);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
