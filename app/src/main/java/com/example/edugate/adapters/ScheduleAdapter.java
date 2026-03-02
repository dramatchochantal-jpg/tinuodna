package com.example.edugate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.edugate.R;
import com.example.edugate.models.ScheduleItem;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    private List<ScheduleItem> scheduleList;

    public ScheduleAdapter(List<ScheduleItem> scheduleList) {
        this.scheduleList = scheduleList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScheduleItem item = scheduleList.get(position);
        holder.tvSubject.setText(item.getSubject());
        holder.tvDayTime.setText(item.getDay() + " | " + item.getTime());
        holder.tvRoom.setText(item.getRoom());
    }

    @Override
    public int getItemCount() { return scheduleList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubject, tvDayTime, tvRoom;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvDayTime = itemView.findViewById(R.id.tvDayTime);
            tvRoom = itemView.findViewById(R.id.tvRoom);
        }
    }
}