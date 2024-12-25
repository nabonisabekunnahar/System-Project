package com.example.adminapp.CtTracking;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminapp.R;

import java.util.List;

public class CTAdapter extends RecyclerView.Adapter<CTAdapter.CTViewHolder> {

    private List<CTData> ctList;
    private Context context; // Declare context here

    public CTAdapter(Context context, List<CTData> ctList) {
        this.context = context; // Assign context from constructor
        this.ctList = ctList;
    }

    @NonNull
    @Override
    public CTViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ct_item_layout, parent, false);
        return new CTViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CTViewHolder holder, int position) {
        CTData ctData = ctList.get(position);

        // Populate the item view with CTData details
        holder.ctNumber.setText("CT #" + ctData.getCtNumber());
        holder.ctDate.setText(ctData.getCtDate());
        holder.ctCourse.setText("Course: " + ctData.getCourse());
        holder.teacherName.setText("Teacher: " + ctData.getTeacherName());
        holder.ctSyllabus.setText("Syllabus: " + ctData.getSyllabus());
        holder.ctStatus.setText("Status: " + ctData.getStatus());
        holder.ctStatus.setTextColor(ctData.getStatus().equals("Done") ? 0xFF4CAF50 : 0xFFFF9800); // Green for "Done", Orange for "Pending"

        // Handle update button click
        holder.updateInfoButton.setOnClickListener(v -> {
            // Navigate to CTInfoChange activity with ctKey and additional data
            Intent intent = new Intent(context, CTInfoChange.class);
            intent.putExtra("ctKey", ctData.getCtKey()); // Pass ctKey
            intent.putExtra("faculty", ctData.getFaculty()); // Pass faculty
            intent.putExtra("department", ctData.getDepartment()); // Pass department
            intent.putExtra("year", ctData.getYear()); // Pass year
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return ctList.size();
    }

    public static class CTViewHolder extends RecyclerView.ViewHolder {

        TextView ctNumber, ctDate, ctCourse, teacherName, ctSyllabus, ctStatus;
        Button updateInfoButton;

        public CTViewHolder(@NonNull View itemView) {
            super(itemView);
            ctNumber = itemView.findViewById(R.id.ctNumber);
            ctDate = itemView.findViewById(R.id.ctDate);
            ctCourse = itemView.findViewById(R.id.ctCourse);
            teacherName = itemView.findViewById(R.id.teacherName);
            ctSyllabus = itemView.findViewById(R.id.ctSyllabus);
            ctStatus = itemView.findViewById(R.id.ctStatus);
            updateInfoButton = itemView.findViewById(R.id.updateInfoButton);
        }
    }
}
