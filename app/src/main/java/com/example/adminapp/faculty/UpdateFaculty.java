/*package com.example.adminapp.faculty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.adminapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class UpdateFaculty extends AppCompatActivity {
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_faculty);

        fab=findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateFaculty.this, AddTeachers.class));
            }
        });
    }
}*/

package com.example.adminapp.faculty;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateFaculty extends AppCompatActivity {

    private RecyclerView courseRecyclerView;
    private LinearLayout noDataLayout;

    private TeacherAdapter teacherAdapter; // Adapter handles both course headers and teacher items
    private List<ListItem> itemList;

    private String adminUid;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_faculty);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> startActivity(new Intent(UpdateFaculty.this, AddTeachers.class)));

        // Initialize views
        courseRecyclerView = findViewById(R.id.courseRecylerView);
        noDataLayout = findViewById(R.id.csNoData);

        // Setup RecyclerView
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemList = new ArrayList<>();
        teacherAdapter = new TeacherAdapter(itemList);
        courseRecyclerView.setAdapter(teacherAdapter);

        // Get the authenticated admin UID
        adminUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch teacher data
        fetchTeacherData();
    }

    private void fetchTeacherData() {
        DatabaseReference teacherRef = FirebaseDatabase.getInstance().getReference().child("teacher");

        teacherRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                boolean hasData = false;

                // Map to group teachers by course
                Map<String, List<TeacherData>> courseMap = new HashMap<>();

                for (DataSnapshot facultySnapshot : snapshot.getChildren()) {
                    for (DataSnapshot departmentSnapshot : facultySnapshot.getChildren()) {
                        for (DataSnapshot yearSnapshot : departmentSnapshot.getChildren()) {
                            for (DataSnapshot teacherSnapshot : yearSnapshot.getChildren()) {
                                TeacherData teacherData = teacherSnapshot.getValue(TeacherData.class);

                                if (teacherData != null && adminUid.equals(teacherData.getAdminUid())) {
                                    hasData = true;
                                    String course = teacherData.getCourse();
                                    if (!courseMap.containsKey(course)) {
                                        courseMap.put(course, new ArrayList<>());
                                    }
                                    courseMap.get(course).add(teacherData);
                                }
                            }
                        }
                    }
                }

                // Prepare the item list with course headers and teacher items
                for (Map.Entry<String, List<TeacherData>> entry : courseMap.entrySet()) {
                    String courseName = entry.getKey();
                    List<TeacherData> teachers = entry.getValue();

                    // Add course header
                    itemList.add(new CourseItem(courseName));

                    // Add teacher items
                    for (TeacherData teacher : teachers) {
                        itemList.add(new TeacherItem(teacher));
                    }
                }

                if (hasData) {
                    courseRecyclerView.setVisibility(View.VISIBLE);
                    noDataLayout.setVisibility(View.GONE);
                } else {
                    courseRecyclerView.setVisibility(View.GONE);
                    noDataLayout.setVisibility(View.VISIBLE);
                }

                teacherAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateFaculty.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                Log.e("DatabaseError", error.getMessage());
            }
        });
    }
}
