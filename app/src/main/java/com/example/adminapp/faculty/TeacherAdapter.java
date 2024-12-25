package com.example.adminapp.faculty;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.adminapp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ListItem> itemList;
    private final String DEFAULT_IMAGE_URL = "https://via.placeholder.com/150"; // Replace with your default image URL

    public TeacherAdapter(List<ListItem> itemList) {
        this.itemList = itemList;
    }

    // Define view types
    private static final int TYPE_COURSE = ListItem.TYPE_COURSE;
    private static final int TYPE_TEACHER = ListItem.TYPE_TEACHER;

    @Override
    public int getItemViewType(int position) {
        return itemList.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == TYPE_COURSE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item_layout, parent, false);
            return new CourseViewHolder(view);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faculty_item_layout, parent, false);
            return new TeacherViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListItem listItem = itemList.get(position);

        if(listItem.getType() == TYPE_COURSE){
            CourseItem courseItem = (CourseItem) listItem;
            CourseViewHolder courseViewHolder = (CourseViewHolder) holder;
            courseViewHolder.courseTitle.setText(courseItem.getCourseName());
        }
        else if(listItem.getType() == TYPE_TEACHER){
            TeacherItem teacherItem = (TeacherItem) listItem;
            TeacherViewHolder teacherViewHolder = (TeacherViewHolder) holder;
            TeacherData teacher = teacherItem.getTeacherData();

            teacherViewHolder.teacherName.setText(teacher.getName());
            teacherViewHolder.teacherEmail.setText(teacher.getEmail());
            teacherViewHolder.teacherPhone.setText(teacher.getPhone());

            // Load image or default placeholder
            if (teacher.getImageUrl() != null && !teacher.getImageUrl().isEmpty()) {
                Glide.with(teacherViewHolder.itemView.getContext())
                        .load(teacher.getImageUrl())
                        .placeholder(R.drawable.circle_img) // Replace with your default image drawable
                        .into(teacherViewHolder.teacherImage);
            } else {
                Glide.with(teacherViewHolder.itemView.getContext())
                        .load(DEFAULT_IMAGE_URL)
                        .into(teacherViewHolder.teacherImage);
            }

            // Set onClickListener for update button if needed
            teacherViewHolder.teacherUpdate.setOnClickListener(v -> {
                // Implement update functionality
                // For example, launch an activity to update teacher info
                // Intent intent = new Intent(v.getContext(), UpdateTeacherActivity.class);
                // intent.putExtra("teacherKey", teacher.getKey());
                // v.getContext().startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // ViewHolder for Course
    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseTitle;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTitle = itemView.findViewById(R.id.courseTitle);
        }
    }

    // ViewHolder for Teacher
    public static class TeacherViewHolder extends RecyclerView.ViewHolder {

        TextView teacherName, teacherEmail, teacherPhone;
        CircleImageView teacherImage;
        Button teacherUpdate;

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            teacherName = itemView.findViewById(R.id.teacherName);
            teacherEmail = itemView.findViewById(R.id.teacherEmail);
            teacherPhone = itemView.findViewById(R.id.teacherPhone);
            teacherImage = itemView.findViewById(R.id.teacherImage);
            teacherUpdate = itemView.findViewById(R.id.teacherUpdate);
        }
    }
}
