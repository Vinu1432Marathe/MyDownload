package com.video.download.vidlink.retrofit;

import com.google.gson.annotations.SerializedName;

public class UserData {
    @SerializedName("newUser")
    private int newUser;

    @SerializedName("oldUser")
    private int oldUser;

    @SerializedName("packageName")
    private String packageName;

    @SerializedName("isActive")
    private boolean isActive;

    @SerializedName("_id")
    private String id;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("__v")
    private int version;

    // Getters and setters
    public int getNewUser() {
        return newUser;
    }

    public void setNewUser(int newUser) {
        this.newUser = newUser;
    }

    public int getOldUser() {
        return oldUser;
    }

    public void setOldUser(int oldUser) {
        this.oldUser = oldUser;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
