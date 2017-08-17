package com.android.system.core.smart;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.android.system.core.ginter.ProcessService;
import com.duowan.mobile.netroid.Network;
import com.duowan.mobile.netroid.RequestQueue;
import com.duowan.mobile.netroid.stack.HurlStack;
import com.duowan.mobile.netroid.toolbox.BasicNetwork;
import com.duowan.mobile.netroid.toolbox.FileDownloader;

/**
 * Created by guang on 2017/8/8.
 */

public class CoreService extends Service{
    private MyBinder binder;
    private MyConn conn;
    private static CoreService con;
    private RequestQueue requestQueue;
    private FileDownloader downloader;
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        con = this;
        binder = new MyBinder();
		if(conn == null)
		{
			conn = new MyConn();
		}

        Intent intentTo = new Intent(CoreService.this,SystemService.class);
        CoreService.this.startService(intentTo);
        this.bindService(intentTo, conn, Context.BIND_IMPORTANT);

        Network network = new BasicNetwork(new HurlStack("TB", null), "utf-8");
        requestQueue = new RequestQueue(network, 2, null);
        requestQueue.start();
        downloader = new FileDownloader(requestQueue, 1);

        new UpdateHelper().run();
    }


    public static CoreService getCon()
    {
        return con;
    }

    public RequestQueue getRequestQueue()
    {
        return this.requestQueue;
    }

    public FileDownloader getDownloader()
    {
        return this.downloader;
    }

    class MyBinder extends ProcessService.Stub
    {
        @Override
        public String getServiceName() throws RemoteException {
            return "CoreService";
        }

    }

    class MyConn implements ServiceConnection
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
//			Toast.makeText(CoreService.this, "SystemService die!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CoreService.this,SystemService.class);
            CoreService.this.startService(intent);
            CoreService.this.bindService(intent, conn, Context.BIND_IMPORTANT);
        }

    }
}
