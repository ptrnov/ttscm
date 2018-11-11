package com.cudocomm.troubleticket.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.model.TopTenActive;

import java.util.ArrayList;
import java.util.List;

public class TopTenActiveAdapter extends RecyclerView.Adapter<TopTenActiveAdapter.ViewHolder> {

    private List<TopTenActive> mDataset = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView stationNameTV;
        public TextView totalTV;

        public ViewHolder(View v) {
            super(v);
            stationNameTV = (TextView) v.findViewById(R.id.stationNameTV);
            totalTV = (TextView) v.findViewById(R.id.totalTV);
        }

        public void bind(final TopTenActive topTenActive) {
            stationNameTV.setText(topTenActive.getStationName());
            totalTV.setText(topTenActive.getTotal());
        }
    }

    public TopTenActiveAdapter(List<TopTenActive> dataset) {
        mDataset.clear();
        mDataset.addAll(dataset);
        notifyDataSetChanged();
    }

    public void swap(List<TopTenActive> list){
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_ten_active_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TopTenActive topTenActive = mDataset.get(position);
        holder.bind(topTenActive);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void removeItem(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataset.size());
        notifyDataSetChanged();
    }

    public void clear(){
        mDataset.clear();
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<TopTenActive> getmDataset() {
        return mDataset;
    }

    public void setmDataset(List<TopTenActive> mDataset) {
        this.mDataset = mDataset;
    }
}