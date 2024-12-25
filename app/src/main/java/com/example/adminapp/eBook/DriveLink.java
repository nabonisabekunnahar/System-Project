package com.example.adminapp.eBook;

public class DriveLink {
    private String driveName;
    private String driveLink;
    private String adminUid;

    // Default constructor required for calls to DataSnapshot.getValue(DriveLink.class)
    public DriveLink() {}

    public DriveLink(String driveName, String driveLink, String adminUid) {
        this.driveName = driveName;
        this.driveLink = driveLink;
        this.adminUid = adminUid;
    }

    // Getters and Setters
    public String getDriveName() {
        return driveName;
    }

    public void setDriveName(String driveName) {
        this.driveName = driveName;
    }

    public String getDriveLink() {
        return driveLink;
    }

    public void setDriveLink(String driveLink) {
        this.driveLink = driveLink;
    }

    public String getAdminUid() {
        return adminUid;
    }

    public void setAdminUid(String adminUid) {
        this.adminUid = adminUid;
    }

    // Check if this is a folder
    public boolean isFolder() {
        return driveLink == null || driveLink.isEmpty();
    }
}
