package com.cudocomm.troubleticket.adapter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.TTSApplication;
import com.cudocomm.troubleticket.model.Ticket;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.getIntent;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.ViewHolder> {
// Job SIGNMENT TICKETS -ptr.nov

    private List<Ticket> mDataset = new ArrayList<>();

    private OnItemClickListener listener;

    private Ticket selectedTicket;
    private int selectedPosition = 0;

    public interface OnItemClickListener {
        void onItemClick(Ticket ticket);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View indicator;
        public TextView ticketNoTV;
        public TextView ticketSuspectTV;
        public TextView ticketStatusTV;
        public TextView ticketDateTV;
        public TextView ticketStationTV;
        public TextView ticketRemarksTV;
        public TextView ticketAgingTV;

        private StringBuilder suspect;



        public ViewHolder(View v) {
            super(v);
            indicator = v.findViewById(R.id.indicator);
            ticketNoTV = (TextView) v.findViewById(R.id.ticketNoTV);
            ticketSuspectTV = (TextView) v.findViewById(R.id.ticketSuspectTV);
            ticketStatusTV = (TextView) v.findViewById(R.id.ticketStatusTV);
            ticketDateTV = (TextView) v.findViewById(R.id.ticketDateTV);
            ticketStationTV = (TextView) v.findViewById(R.id.ticketStationTV);
            ticketRemarksTV = (TextView) v.findViewById(R.id.ticketRemarksTV);
            ticketAgingTV = (TextView) v.findViewById(R.id.ticketAgingTV);
//            CommonsUtil.get


        }

        public void bind(final Ticket ticket, final OnItemClickListener listener) {
            ticketNoTV.setText(CommonsUtil.ticketTypeToString(ticket.getTicketType()) + " No. " + ticket.getTicketNo());
            ticketRemarksTV.setText(ticket.getTicketRemarks());
            ticketDateTV.setText(ticket.getTicketDate());
            String status = CommonsUtil.statusToString(ticket.getTicketStatus());
            if(ticket.getTicketStatus() == 1) {
                if(ticket.getTicketPosition() == 2) {
                    status = status + " - ESC 1";
                } else if(ticket.getTicketPosition() == 3) {
                    status = status + " - ESC 2";
                } else if(ticket.getTicketPosition() == 4) {
                    status = status + " - ESC 3";
                } else if(ticket.getTicketPosition() == 5) {
                    status = status + " - ESC 4";
                } else if(ticket.getTicketPosition() == 7) {
                    status = status + " - ASG";
                }
            }

            /*
            * Agging Hidden
            * 1.counter aging hidden tapi tetap conter
            * 2.setelah close by PR, agging show dengan new counter sampai close ticket
            * */
            if(ticket.getHasAssign()=="0"){
                ticketAgingTV.setVisibility(View.VISIBLE);
            }else{
                ticketAgingTV.setVisibility(View.GONE);
            }

            ticketStatusTV.setText(status);
            ticketAgingTV.setText(CommonsUtil.countAging(ticket.getTicketDate()));

            suspect = new StringBuilder();
            if(ticket.getSuspect1Name() != null)
                suspect.append(ticket.getSuspect1Name());
            if(ticket.getSuspect2Name() != null)
                suspect.append(" - ").append(ticket.getSuspect2Name());
            if(ticket.getSuspect3Name() != null)
                suspect.append(" - ").append(ticket.getSuspect3Name());

            ticketSuspectTV.setText(suspect);
            ticketStationTV.setText(ticket.getStationName());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(ticket);
                }
            });
        }
    }

    public TicketAdapter(List<Ticket> dataset, OnItemClickListener listener) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_row_item_2, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (payloads.isEmpty()) {
            // Perform a full update
            onBindViewHolder(holder, position);
        } else {
            // Perform a partial update
            for (Object payload : payloads) {
                if (payload instanceof Ticket) {
                    holder.bind((Ticket) payload, listener);
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Ticket ticket = mDataset.get(position);
        int color = Color.RED;
        int statusColor = Color.RED;
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

        if(ticket.getTicketStatus() == Constants.STATUS_OPEN)
//            statusColor = Color.RED;
            statusColor = ContextCompat.getColor(TTSApplication.getContext(), R.color.red_900);
        if(ticket.getTicketStatus() == Constants.STATUS_CONF)
//            statusColor = Color.YELLOW;
            statusColor = ContextCompat.getColor(TTSApplication.getContext(), R.color.yellow_600);

        holder.ticketStatusTV.setTextColor(statusColor);

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

    public void removeItem(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataset.size());
        notifyDataSetChanged();
//        mDataset.remove(position);
//        notifyDataSetChanged();
    }

    public void addItem(int position, Ticket ticket) {
        mDataset.add(position, ticket);
        notifyItemChanged(position);
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

    public List<Ticket> getmDataset() {
        return mDataset;
    }

    public void setmDataset(List<Ticket> mDataset) {
        this.mDataset = mDataset;
    }
}