package com.example.adminapp.eBook;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminapp.R;

import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    private Context context;
    private List<DriveLink> folderList; // Use DriveLink instead of ParentFolderData
    private OnItemClickListener onItemClickListener;

    // Interface for item click listener
    public interface OnItemClickListener {
        void onItemClick(DriveLink driveLink); // Accept DriveLink instead of ParentFolderData
    }

    // Constructor with OnItemClickListener
    public FolderAdapter(Context context, List<DriveLink> folderList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.folderList = folderList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.folder_item_layout, parent, false);
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        DriveLink driveLink = folderList.get(position);
        holder.folderName.setText(driveLink.getDriveName()); // Display drive name
        holder.folderIcon.setImageResource(R.drawable.book); // Assuming you have a `book` icon

        // Set item click listener to open the drive link
        holder.itemView.setOnClickListener(v -> {
            // Open the drive link in a browser when the item is clicked
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(driveLink.getDriveLink()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return folderList.size();
    }

    public static class FolderViewHolder extends RecyclerView.ViewHolder {
        TextView folderName;
        ImageView folderIcon;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            folderName = itemView.findViewById(R.id.folderName);
            folderIcon = itemView.findViewById(R.id.folderIcon);
        }
    }
}
