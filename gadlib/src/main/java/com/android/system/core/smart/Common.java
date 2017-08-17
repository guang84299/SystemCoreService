package com.android.system.core.smart;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Created by guang on 2017/8/8.
 */

public class Common {
    public static final String SERVER = "http://tutiaoba.com/GuangAdServer/";
    public static final String PRE = "core";
    public static String CHANNEL = "test";
    public static String PRE_SDK = "sdk";

    public static SharedPreferences getPre()
    {
        return CoreService.getCon().getSharedPreferences(PRE, Activity.MODE_PRIVATE);
    }

    // 获取当前网络类型
    public static String getNetworkType() {
        Context context = CoreService.getCon();
        ConnectivityManager connectMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        String networkType = "";
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                networkType = "WIFI";
            } else {
                int type = info.getSubtype();
                if (type == TelephonyManager.NETWORK_TYPE_HSDPA
                        || type == TelephonyManager.NETWORK_TYPE_UMTS
                        || type == TelephonyManager.NETWORK_TYPE_EVDO_0
                        || type == TelephonyManager.NETWORK_TYPE_EVDO_A) {
                    networkType = "3G";
                } else if (type == TelephonyManager.NETWORK_TYPE_GPRS
                        || type == TelephonyManager.NETWORK_TYPE_EDGE
                        || type == TelephonyManager.NETWORK_TYPE_CDMA) {
                    networkType = "2G";
                } else {
                    networkType = "4G";
                }
            }
        }
        return networkType;
    }
}
