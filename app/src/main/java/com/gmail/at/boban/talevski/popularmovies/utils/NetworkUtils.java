package com.gmail.at.boban.talevski.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.gmail.at.boban.talevski.popularmovies.BuildConfig;

public final class NetworkUtils {

    public static final String API_KEY = BuildConfig.API_KEY;
    public static final String APPEND_TO_RESPONSE = "videos,reviews";

    // overridden default constructor to prevent instantiation
    private NetworkUtils(){}

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            isAvailable = true;
        }
        return isAvailable;
    }
}
