package com.cudocomm.troubleticket.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.model.TicketLog;
import com.cudocomm.troubleticket.util.CommonsUtil;

import java.util.ArrayList;
import java.util.List;

public class TicketLogAdapter extends RecyclerView.Adapter<TicketLogAdapter.ViewHolder> {

    private List<TicketLog> mDataset = new ArrayList<>();

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(TicketLog ticket);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView timelineTimeTV;
        public TextView timelineDateTV;
        public TextView timelineTitleTV;
        public TextView timelineDescTV;
        public TextView timelineUserTV;

        public ViewHolder(View v) {
            super(v);
            timelineTimeTV = (TextView) v.findViewById(R.id.timelineTimeTV);
            timelineDateTV = (TextView) v.findViewById(R.id.timelineDateTV);
            timelineTitleTV= (TextView) v.findViewById(R.id.timelineTitleTV);
            timelineDescTV= (TextView) v.findViewById(R.id.timelineDescTV);
            timelineUserTV= (TextView) v.findViewById(R.id.timelineUserTV);
        }

        public void bind(final TicketLog ticket, final OnItemClickListener listener) {

            timelineTimeTV.setText(CommonsUtil.timeToString(ticket.getDate()));
            timelineDateTV.setText(CommonsUtil.dateToString(ticket.getDate()));
            timelineTitleTV.setText(ticket.getTitle());
            timelineDescTV.setText(ticket.getDesc().replace("\\n", "\n"));
            timelineUserTV.setText(ticket.getUser());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(ticket);
                }
            });
        }
    }

    public TicketLogAdapter(List<TicketLog> dataset, OnItemClickListener listener) {
        this.listener = listener;
        mDataset.clear();
        mDataset.addAll(dataset);
    }

    public void swap(List<TicketLog> list){
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_create_ticket, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TicketLog ticket = mDataset.get(position);
        /*if(position % 2 == 1) {
            holder.mainRow.setBackgroundColor(ContextCompat.getColor(TTSApplication.getContext(), R.color.background_container));
        }*/

        holder.bind(ticket, listener);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}