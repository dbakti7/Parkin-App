package com.example.android.cz3002project;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Dian Bakti on 3/16/2016.
 * This class is used to check whether the device has internet connection.
 */
public class CheckNetworkConnection {

    public static Boolean checknetwork(Context mContext) {
        /**
         * Return internet connection status
         */
        NetworkInfo info = ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if (info == null || !info.isConnected())
        {
            // return false if no internet connection
            return false;
        }
        if (info.isRoaming()) {
            // return false if we want to disable internet connection while roaming
            // for default, return true
            return true;
        }
        // return true if internet connection is on
        return true;

    }
}
