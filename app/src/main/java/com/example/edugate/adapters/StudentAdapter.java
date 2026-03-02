package com.example.edugate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.edugate.R;
import com.example.edugate.models.Student;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {
    private List<Student> studentList;
    private boolean isAttendanceMode;

    public StudentAdapter(List<Student> studentList, boolean isAttendanceMode) {
        this.studentList = studentList;
        this.isAttendanceMode = isAttendanceMode;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.tvName.setText(student.getName());
        holder.tvStatus.setText(student.getStatus());

        if (isAttendanceMode) {
            holder.cbPresent.setVisibility(View.VISIBLE);
            holder.etGrade.setVisibility(View.GONE);
            holder.cbPresent.setChecked(student.isPresent());
            holder.cbPresent.setOnCheckedChangeListener((buttonView, isChecked) -> student.setPresent(isChecked));
        } else {
            holder.cbPresent.setVisibility(View.GONE);
            holder.etGrade.setVisibility(View.VISIBLE);
            holder.etGrade.setText(student.getGrade());
        }
    }

    @Override
    public int getItemCount() { return studentList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvStatus;
        CheckBox cbPresent;
        EditText etGrade;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvStudentName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            cbPresent = itemView.findViewById(R.id.cbPresent);
            etGrade = itemView.findViewById(R.id.etGrade);
        }
    }
}