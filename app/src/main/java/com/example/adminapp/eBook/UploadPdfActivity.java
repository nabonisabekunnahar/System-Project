package com.example.adminapp.eBook;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UploadPdfActivity extends AppCompatActivity {

    private RecyclerView driveRecyclerView;
    private FloatingActionButton fab;
    private FolderAdapter driveAdapter;
    private List<DriveLink> driveList; // Use DriveLink instead of ParentFolderData

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf);

        // Initialize UI elements
        driveRecyclerView = findViewById(R.id.folderRecyclerView); // Reuse the RecyclerView ID
        fab = findViewById(R.id.fabCreateFolder);

        // Set up RecyclerView
        driveRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        driveList = new ArrayList<>();

        // Initialize the adapter with DriveLink
        driveAdapter = new FolderAdapter(this, driveList, driveLink -> {
            // Handle item click: Open the drive link in a browser
            String driveLinkUrl = driveLink.getDriveLink(); // Get the drive link
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(driveLinkUrl));
            startActivity(intent);
        });

        // Set adapter to RecyclerView
        driveRecyclerView.setAdapter(driveAdapter);

        // FloatingActionButton click listener to navigate to create ParentFolder
        fab.setOnClickListener(v -> startActivity(new Intent(UploadPdfActivity.this, UploadDriveActivity.class)));

        // Fetch drive data from Firebase
        fetchDriveLinks();
    }

    private void fetchDriveLinks() {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("drive")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Clear previous data
                        driveList.clear();
                        // Parse new data from Firebase
                        for (DataSnapshot facultySnapshot : snapshot.getChildren()) {
                            for (DataSnapshot departmentSnapshot : facultySnapshot.getChildren()) {
                                for (DataSnapshot batchSnapshot : departmentSnapshot.getChildren()) {
                                    for (DataSnapshot driveSnapshot : batchSnapshot.getChildren()) {
                                        DriveLink driveLink = driveSnapshot.getValue(DriveLink.class); // Fetch DriveLink data
                                        if (driveLink != null && driveLink.getAdminUid().equals(currentUid)) {
                                            driveList.add(driveLink); // Add to the list if admin UID matches
                                        }
                                    }
                                }
                            }
                        }
                        // Notify the adapter that data has changed
                        if (driveAdapter != null) {
                            driveAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseError", error.getMessage());
                    }
                });
    }
}
