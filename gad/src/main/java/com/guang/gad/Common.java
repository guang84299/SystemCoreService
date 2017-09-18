package com.guang.gad;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.storage.StorageManager;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guang on 2017/8/8.
 */

public class Common {
    public static final String SERVER = "http://tutiaoba.com/GuangAdServer/";
    public static final String PRE = "core";
    public static String CHANNEL = "test";
    public static String PRE_SDK = "sdk";
    public static final String EVENT_BANNER_REQ = "banner_request";
    public static final String EVENT_BANNER_SHOW = "banner_show";
    public static final String EVENT_BANNER_CLICK = "banner_click";
    public static final String EVENT_BANNER_CLOSE = "banner_close";
    public static final String EVENT_BANNER_FAIL = "banner_fail";

    public static final String EVENT_SPOT_REQ = "spot_request";
    public static final String EVENT_SPOT_SHOW = "spot_show";
    public static final String EVENT_SPOT_CLICK = "spot_click";
    public static final String EVENT_SPOT_CLOSE = "spot_close";
    public static final String EVENT_SPOT_FAIL = "spot_fail";

    public static final String EVENT_GP_SHOW = "gp_show";

    public static final String ACTION_BEHIND_SHOW = "action.behind.show";
    public static final String ACTION_BEHIND_HIDE = "action.behind.hide";


    public static SharedPreferences getPre()
    {
        return GController.getInstance().getContext().getSharedPreferences(PRE, Activity.MODE_PRIVATE);
    }

    // 获取当前网络类型
    public static String getNetworkType() {
        Context context = GController.getInstance().getContext();
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

    public static int getCallLogNum() {
        Context context = GController.getInstance().getContext();
        // 1.获得ContentResolver
        ContentResolver resolver = context.getContentResolver();
        int num = 0;
        // 2.利用ContentResolver的query方法查询通话记录数据库
        Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, // 查询通话记录的URI
                new String[] { CallLog.Calls.CACHED_NAME// 通话记录的联系人
                        , CallLog.Calls.NUMBER// 通话记录的电话号码
                        , CallLog.Calls.DATE// 通话记录的日期
                        , CallLog.Calls.DURATION// 通话时长
                        , CallLog.Calls.TYPE }// 通话类型
                , null, null, CallLog.Calls.DEFAULT_SORT_ORDER// 按照时间逆序排列，最近打的最先显示
        );
        // 3.通过Cursor获得数据
        while (cursor.moveToNext()) {
            num++;
        }

        return num;
    }


    //获取外置应用
    public static List<PackageInfo> getApps() {
        // 桌面应用的启动在INTENT中需要包含ACTION_MAIN 和CATEGORY_HOME.
        Context context = GController.getInstance().getContext();
        List<PackageInfo> names = new ArrayList<PackageInfo>();
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> apps = packageManager.getInstalledPackages(0);
        for (PackageInfo app : apps) {
            if ((app.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                names.add(app);
            }
        }
        return names;
    }

    //获取外置应用
    public static List<String> getExtApps() {
        // 桌面应用的启动在INTENT中需要包含ACTION_MAIN 和CATEGORY_HOME.
        Context context = GController.getInstance().getContext();
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> apps = packageManager.getInstalledPackages(0);
        for (PackageInfo app : apps) {
            if ((app.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                names.add(app.packageName);
            }
        }
        return names;
    }

    public static List<String> getLauncherApps()
    {
        // 桌面应用的启动在INTENT中需要包含ACTION_MAIN 和CATEGORY_HOME.
        Context context = GController.getInstance().getContext();
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = context.getPackageManager();
        //属性
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for(ResolveInfo ri : resolveInfo){
            names.add(ri.activityInfo.packageName);
        }

        return names;
    }

    public static int dip2px(float dipValue) {
        Context context = GController.getInstance().getContext();
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int getScreenW() {
        Context context = GController.getInstance().getContext();
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }

    public static int getScreenH() {
        Context context = GController.getInstance().getContext();
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }

    public static boolean isAppInBackground(String packageName)
    {
        String launcherApps = getLauncherApps().toString();
        return launcherApps.contains(packageName);
    }


    public static String readFile(File file){
        BufferedReader in = null;
        String lines = "";
        try {
            in = new BufferedReader(new FileReader(file));
            String currentLine;
            while ((currentLine = in.readLine()) != null) {
                lines = currentLine;
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return lines;
        }
    }

    public static File getStorageFile(String dir){
        File f = getStoragePath(false);
        if(f == null){
            f = getStoragePath(true);
        }
        if(f == null){
            return null;
        }
        return new File(f,"Android/data/com.qianqi.mylook/files/"+dir);
    }

    private static File getStoragePath(boolean removable) {
        Context context = GController.getInstance().getContext();
        StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean bool = (Boolean) isRemovable.invoke(storageVolumeElement);
//                L.d(path+","+removable);
                if (removable == bool) {
                    File f = new File(path);
                    if(f.exists()) {
                        return f;
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
