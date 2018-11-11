package com.cudocomm.troubleticket.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.model.Ticket;
import com.cudocomm.troubleticket.util.CommonsUtil;

import java.util.ArrayList;
import java.util.List;

public class TicketAdapter2 extends RecyclerView.Adapter<TicketAdapter2.ViewHolder> {

    private List<Ticket> mDataset = new ArrayList<>();

    private OnItemClickListener listener;

    private int selectedPosition = 0;

    public interface OnItemClickListener {
        void onItemClick(Ticket ticket);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View indicator;
        public TextView ticketNoTV;
        public TextView ticketSuspectTV;
        public TextView ticketSeverityTV;
        public TextView ticketDateTV;
        public TextView ticketCreatorTV;

        public ViewHolder(View v) {
            super(v);
            indicator = v.findViewById(R.id.indicator);
            ticketNoTV = (TextView) v.findViewById(R.id.ticketNoTV);
            ticketSuspectTV = (TextView) v.findViewById(R.id.ticketSuspectTV);
            ticketSeverityTV = (TextView) v.findViewById(R.id.ticketSeverityTV);
            ticketDateTV = (TextView) v.findViewById(R.id.ticketDateTV);
            ticketCreatorTV = (TextView) v.findViewById(R.id.ticketCreatorTV);
        }

        public void bind(final Ticket ticket, final OnItemClickListener listener) {
//            ticketNoTV.setText(ticket.getTicketNo());
            ticketNoTV.setText(ticket.getTicketRemarks());
            ticketDateTV.setText(ticket.getTicketDate());
            ticketSeverityTV.setText(CommonsUtil.severityToString(ticket.getTicketSeverity()));
            ticketSuspectTV.setText(ticket.getSuspectName());
            ticketCreatorTV.setText(ticket.getUserName());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(ticket);
                }
            });
        }
    }

    public TicketAdapter2(List<Ticket> dataset, OnItemClickListener listener) {
        this.listener = listener;
        mDataset.clear();
        mDataset.addAll(dataset);
        notifyDataSetChanged();
    }

    public void swap(List<Ticket> list){
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_row_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Ticket ticket = mDataset.get(position);
        int color = Color.RED;
        switch (ticket.getTicketSeverity()) {
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

        holder.bind(ticket, listener);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}