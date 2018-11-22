package com.cudocomm.troubleticket.adapter;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.TTSApplication;
import com.cudocomm.troubleticket.model.Assignment;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {

    private List<Assignment> mDataset = new ArrayList<>();

    private OnItemClickListener listener;

    private int selectedPosition = 0;

    public interface OnItemClickListener {
        void onItemClick(Assignment assignment);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

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

        }

        public void bind(final Assignment assignment, final OnItemClickListener listener) {
            ticketNoTV.setText("Ticket No. " + assignment.getTicket().getTicketNo());
            ticketRemarksTV.setText(assignment.getTicket().getTicketRemarks());
            ticketDateTV.setText(assignment.getTicket().getTicketDate());
//            ticketStatusTV.setText(CommonsUtil.statusToString(assignment.getTicket().getTicketStatus()));

            String status = CommonsUtil.statusToString(assignment.getTicket().getTicketStatus());
            if(assignment.getTicket().getTicketStatus() == 1) {
                if(assignment.getTicket().getTicketPosition() == 2) {
                    status = status + " - ESC 1";
                } else if(assignment.getTicket().getTicketPosition() == 3) {
                    status = status + " - ESC 2";
                } else if(assignment.getTicket().getTicketPosition() == 4) {
                    status = status + " - ESC 3";
                } else if(assignment.getTicket().getTicketPosition() == 5) {
                    status = status + " - ESC 4";
                } else if(assignment.getTicket().getTicketPosition() == 7) {
                    status = status + " - ASG";
                }
            }

//            if (assignment.getTicket().getHasAssign().equals(0)){
//                ticketAgingTV.setEnabled(true);
//            }else{
//                ticketAgingTV.setEnabled(false);
//            }


            ticketStatusTV.setText(status);
            ticketAgingTV.setText(CommonsUtil.countAging(assignment.getTicket().getTicketDate()));

            suspect = new StringBuilder();
            if(assignment.getTicket().getSuspect1Name() != null)
                suspect.append(assignment.getTicket().getSuspect1Name());
            if(assignment.getTicket().getSuspect2Name() != null)
                suspect.append(" - ").append(assignment.getTicket().getSuspect2Name());
            if(assignment.getTicket().getSuspect3Name() != null)
                suspect.append(" - ").append(assignment.getTicket().getSuspect3Name());

            ticketSuspectTV.setText(suspect);
            ticketStationTV.setText(assignment.getTicket().getStationName());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(assignment);
                }
            });


        }
    }

    public AssignmentAdapter(List<Assignment> dataset, OnItemClickListener listener) {
        this.listener = listener;
        mDataset.clear();
        mDataset.addAll(dataset);
        notifyDataSetChanged();
    }

    public void swap(List<Assignment> list){
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.assignment_row_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Assignment assignment = mDataset.get(position);
        int color = Color.RED;
        int statusColor = Color.RED;
        switch (assignment.getTicket().getTicketSeverity()) {
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

        if(assignment.getTicket().getTicketStatus() == Constants.STATUS_OPEN)
//            statusColor = Color.RED;
            statusColor = ContextCompat.getColor(TTSApplication.getContext(), R.color.red_900);
        if(assignment.getTicket().getTicketStatus() == Constants.STATUS_CONF)
//            statusColor = Color.YELLOW;
            statusColor = ContextCompat.getColor(TTSApplication.getContext(), R.color.yellow_600);

        holder.ticketStatusTV.setTextColor(statusColor);

        holder.indicator.setBackgroundColor(color);

        holder.bind(assignment, listener);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public List<Assignment> getmDataset() {
        return mDataset;
    }

    public void setmDataset(List<Assignment> mDataset) {
        this.mDataset = mDataset;
    }
}