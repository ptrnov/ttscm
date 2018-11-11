package com.cudocomm.troubleticket.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.model.TopTenSuspect;

import java.util.ArrayList;
import java.util.List;

public class TopTenSuspectAdapter extends RecyclerView.Adapter<TopTenSuspectAdapter.ViewHolder> {

    private List<TopTenSuspect> mDataset = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView suspectNameTV;
        public TextView totalTV;

        public ViewHolder(View v) {
            super(v);
            suspectNameTV = (TextView) v.findViewById(R.id.stationNameTV);
            totalTV = (TextView) v.findViewById(R.id.totalTV);
        }

        public void bind(final TopTenSuspect topTenSuspect) {
            suspectNameTV.setText(topTenSuspect.getSuspectName());
            totalTV.setText(topTenSuspect.getTotal());
        }
    }

    public TopTenSuspectAdapter(List<TopTenSuspect> dataset) {
        mDataset.clear();
        mDataset.addAll(dataset);
        notifyDataSetChanged();
    }

    public void swap(List<TopTenSuspect> list){
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
        TopTenSuspect topTenActive = mDataset.get(position);
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

    public List<TopTenSuspect> getmDataset() {
        return mDataset;
    }

    public void setmDataset(List<TopTenSuspect> mDataset) {
        this.mDataset = mDataset;
    }
}