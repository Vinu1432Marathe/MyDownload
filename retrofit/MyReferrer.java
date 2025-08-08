package com.video.download.vidlink.retrofit;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.addsdemo.mysdk.ADPrefrences.MyApp;
import com.addsdemo.mysdk.model.RemoteConfigModel;
import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;


public class MyReferrer {
    InstallReferrerClient referrerClient;

    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public String getUserCountry(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) {
                return simCountry.toLowerCase();
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) {
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) {
                    return networkCountry.toLowerCase();
                }
            }
        } catch (Exception e) {
            Log.e("TAG_API", "Error fetching country: " + e.getMessage());
        }
        return "";
    }


    public void callRefrrerApi(Activity context) {
        RemoteConfigModel remoteConfigModel = MyApp.ad_preferences.getRemoteConfig();

        referrerClient = InstallReferrerClient.newBuilder(context).build();

        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        try {
                            ReferrerDetails response = referrerClient.getInstallReferrer();
                            String referrerUrl = response.getInstallReferrer();

                            if (remoteConfigModel.getMedium().equalsIgnoreCase("organic") && referrerUrl.contains("organic")) {
                                remoteConfigModel.setAdsClick("3");
                                remoteConfigModel.setBackClick("0");
                                remoteConfigModel.setOnboardingShow(remoteConfigModel.isOrganicOnboarding());
                                MyApp.ad_preferences.saveRemoteConfig(remoteConfigModel);
                            }


                            ReferrerData referrerData = ReferrerData.getInstance();
                            referrerData.setMedium(referrerUrl);
                            referrerData.setPackageName(context.getPackageName());
                            referrerData.setDeviceId(getAndroidId(context));
                            referrerData.setCountry(getUserCountry(context));
                            makeApiCall(context);

                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        Log.d("TAG_REF", "Feature not supported.");
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        Log.d("TAG_REF", "Service unavailable.");
                        break;
                }
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                Log.d("TAG_REF", "Service disconnected.");
            }
        });
    }




    private void makeApiCall(Activity context) {
        ReferrerData referrerData = ReferrerData.getInstance();

        String version = "1.0";
        try {
            PackageInfo pInfo =context. getPackageManager().getPackageInfo(context.getPackageName(), 0);
             version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Create a HashMap for the request body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("country", referrerData.getCountry());
        requestBody.put("packageName", referrerData.getPackageName());
        requestBody.put("deviceId", referrerData.getDeviceId());
        requestBody.put("medium", referrerData.getMedium());
        requestBody.put("versionName", version);

        ApiService apiService = ApiClient.getInstance(MyApp.ad_preferences.getRemoteConfig().getInstallApiCount()).create(ApiService.class);
        Call<ApiResponse> call = apiService.addUser(requestBody);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    Log.d("TAG_API", "Sucess: " + response);

                } else {
                    Log.d("TAG_API", "Failed: " + response);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("TAG_API", "Error: " + t.getMessage());
            }
        });
    }


    public interface ApiIpCallback {
        void onSuccess(RemoteConfigModel response); // Called when the response is successful
        void onFailure(Throwable t); // Called when the API call fails
    }

    public static ApiIpCallback apiIpCallback;
    public static void GetCountryDetails(RemoteConfigModel remoteConfigModel, ApiIpCallback callback) {
        apiIpCallback = callback;
        ApiService apiService = ApiClient.getInstance("http://ip-api.com/json/").create(ApiService.class);
        Call<Pro_IPModel_Class> call = apiService.getUserDetails("?fields=61439");

        call.enqueue(new Callback<Pro_IPModel_Class>() {
            @Override
            public void onResponse(Call<Pro_IPModel_Class> call, Response<Pro_IPModel_Class> response) {
                Log.d("TAGCHECKDATA", "onResponse: " +response.body().getCountry());
                if (response.isSuccessful() && response.body() != null) {

                    for (int i = 0; i < remoteConfigModel.getCountryList().size(); i++) {
                        if (remoteConfigModel.getCountryList().get(i).getName().equalsIgnoreCase(response.body().getCountry())) {
                            remoteConfigModel.getCustomLinks().setOpenRedirectLink(remoteConfigModel.getCountryList().get(i).getAppOpenLink());
                            remoteConfigModel.getCustomLinks().setInterRedirectLink(remoteConfigModel.getCountryList().get(i).getInterLink());
                            remoteConfigModel.getCustomLinks().setNativeRedirectLink(remoteConfigModel.getCountryList().get(i).getNativeLink());
                            remoteConfigModel.getCustomLinks().setBannerRedirectLink(remoteConfigModel.getCountryList().get(i).getBannerLink());
                        }
                    }

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            apiIpCallback.onSuccess(remoteConfigModel);
                        }
                    }, 2000);

                } else {
                    apiIpCallback.onFailure(new Exception("Response unsuccessful: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Pro_IPModel_Class> call, Throwable t) {
                apiIpCallback.onFailure(t); // Notify failure

            }
        });
    }

    public interface ApiService {
        @POST("add")
        Call<ApiResponse> addUser(@Body Map<String, String> body);

        @GET()
        Call<Pro_IPModel_Class> getUserDetails(@Url String url);
    }


}