package com.video.download.vidlink.retrofit;

import com.google.gson.annotations.SerializedName;

public class ReferrerData {
    private static ReferrerData instance;

    @SerializedName("country")
    private String country;

    @SerializedName("packageName")
    private String packageName;

    @SerializedName("deviceId")
    private String deviceId;

    @SerializedName("medium")
    private String medium;
    private ReferrerData() {}

    public static ReferrerData getInstance() {
        if (instance == null) {
            instance = new ReferrerData();
        }
        return instance;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }
}
