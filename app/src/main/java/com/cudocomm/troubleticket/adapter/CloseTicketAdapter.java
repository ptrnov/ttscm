package com.cudocomm.troubleticket.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.TTSApplication;
import com.cudocomm.troubleticket.model.CloseTicket;
import com.cudocomm.troubleticket.util.CommonsUtil;

import java.util.ArrayList;
import java.util.List;

public class CloseTicketAdapter extends RecyclerView.Adapter<CloseTicketAdapter.ViewHolder> {

    private List<CloseTicket> mDataset = new ArrayList<>();

    private OnItemClickListener listener;

    private int selectedPosition = 0;

    public interface OnItemClickListener {
        void onItemClick(CloseTicket closeTicket);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View indicator;
        public TextView ticketNoTV;
        public TextView ticketRemarksTV;
//        public TextView ticketSuspectTV;
        public TextView ticketSeverityTV;
        public TextView ticketCloseDateTV;
        public TextView ticketCloseInfoTV;
        public TextView ticketAgingTV;

        String closeDate;
        StringBuilder suspect;

        public ViewHolder(View v) {
            super(v);
            indicator = v.findViewById(R.id.indicator);
            ticketNoTV = (TextView) v.findViewById(R.id.ticketNoTV);
            ticketRemarksTV = (TextView) v.findViewById(R.id.ticketRemarksTV);
            ticketAgingTV = (TextView) v.findViewById(R.id.ticketAgingTV);
            ticketSeverityTV = (TextView) v.findViewById(R.id.ticketSeverityTV);
            ticketCloseDateTV = (TextView) v.findViewById(R.id.ticketCloseDateTV);
            ticketCloseInfoTV = (TextView) v.findViewById(R.id.ticketCloseInfoTV);
        }

        public void bind(final CloseTicket closeTicket, final OnItemClickListener listener) {
            ticketNoTV.setText(
                    CommonsUtil.ticketTypeToString(closeTicket.getTicket().getTicketType()) + " No. " + closeTicket.getTicket().getTicketNo());
            ticketRemarksTV.setText(closeTicket.getTicket().getTicketRemarks());
            closeDate = TTSApplication.getContext().getResources().getString(R.string.row_ticket_close_date, CommonsUtil.datetimeToString(closeTicket.getTicketDate()), closeTicket.getCloseByName());
            ticketCloseDateTV.setText(closeDate);

            ticketAgingTV.setText(CommonsUtil.countAging(closeTicket.getTicket().getTicketDate()));
            ticketCloseInfoTV.setText(closeTicket.getTicketInfo());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(closeTicket);
                }
            });
        }
    }

    /*public CloseTicketAdapter(List<CloseTicket> dataset, OnItemClickListener listener) {
        this.listener = listener;
        mDataset.clear();
        mDataset.addAll(dataset);
        notifyDataSetChanged();
    }*/
    public CloseTicketAdapter(List<CloseTicket> dataset, OnItemClickListener listener) {
        this.listener = listener;
        mDataset = dataset;
        notifyDataSetChanged();
    }

    public void swap(List<CloseTicket> list){
        if (mDataset != null) {
            mDataset.clear();
            mDataset.addAll(list);
        } else {
            mDataset = list;
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.close_ticket_row_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CloseTicket closeTicket = mDataset.get(position);
        int color = Color.RED;
        switch (closeTicket.getTicket().getTicketSeverity()) {
            case 1:
                color = Color.RED;
                break;
            case 2:
                color = Color.YELLOW;
                break;
            case 3:
                color = Color.GREEN;
                break;
            default:
                break;
        }
        holder.indicator.setBackgroundColor(color);

        holder.bind(closeTicket, listener);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public List<CloseTicket> getmDataset() {
        return mDataset;
    }

    public void setmDataset(List<CloseTicket> mDataset) {
        this.mDataset = mDataset;
    }
}