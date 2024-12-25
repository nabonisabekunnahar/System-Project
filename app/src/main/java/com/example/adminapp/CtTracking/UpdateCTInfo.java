package com.example.adminapp.CtTracking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.List;

public class UpdateCTInfo extends AppCompatActivity {

    private RecyclerView ctRecyclerView;
    private CTAdapter ctAdapter; // Adapter for RecyclerView
    private List<CTData> ctList; // List to hold CT information

    private String adminUid;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_ctinfo);

        // Initialize FloatingActionButton and RecyclerView
        fab = findViewById(R.id.fab_add_ct);
        ctRecyclerView = findViewById(R.id.ctRecyclerView);

        // Setup RecyclerView
        ctRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ctList = new ArrayList<>();
        ctAdapter = new CTAdapter(this, ctList); // Pass context and list
        ctRecyclerView.setAdapter(ctAdapter);

        // FloatingActionButton click listener to navigate to AddCTInfo activity
        fab.setOnClickListener(v -> startActivity(new Intent(UpdateCTInfo.this, AddCTInfo.class)));

        // Get the authenticated admin UID
        adminUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch CT data
        fetchCTData();
    }

    private void fetchCTData() {
        // Reference to CT_Info in the database
        DatabaseReference ctRef = FirebaseDatabase.getInstance().getReference().child("CT_Info");

        ctRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ctList.clear(); // Clear list to avoid duplicates

                // Iterate through faculties (e.g., "EEE")
                for (DataSnapshot facultySnapshot : snapshot.getChildren()) {
                    // Iterate through departments within each faculty (e.g., "CSE")
                    for (DataSnapshot departmentSnapshot : facultySnapshot.getChildren()) {
                        // Iterate through years (e.g., "1st Year", "2nd Year")
                        for (DataSnapshot yearSnapshot : departmentSnapshot.getChildren()) {
                            // Iterate through each CT entry within the year
                            for (DataSnapshot ctSnapshot : yearSnapshot.getChildren()) {
                                CTData ctData = ctSnapshot.getValue(CTData.class);

                                // Match adminUid with the adminUid in CT_Info
                                if (ctData != null && adminUid.equals(ctData.getAdminUid())) {
                                    ctList.add(ctData); // Add CTData to the list
                                }
                            }
                        }
                    }
                }

                // Notify adapter after data change
                ctAdapter.notifyDataSetChanged();

                // Show message if no data is found
                if (ctList.isEmpty()) {
                    Toast.makeText(UpdateCTInfo.this, "No CT data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateCTInfo.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                Log.e("DatabaseError", error.getMessage());
            }
        });
    }
}
