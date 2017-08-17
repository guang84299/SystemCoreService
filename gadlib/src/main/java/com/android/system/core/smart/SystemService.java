package com.android.system.core.smart;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.android.system.core.ginter.ProcessService;

/**
 * Created by guang on 2017/8/8.
 */

public class SystemService extends Service{
    private MyBinder binder;
    private MyConn conn;
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        binder = new MyBinder();
        if(conn == null)
        {
            conn = new MyConn();
        }

        Intent intentTo = new Intent(SystemService.this,CoreService.class);
        SystemService.this.startService(intentTo);
        this.bindService(intentTo, conn, Context.BIND_IMPORTANT);
    }


    class MyBinder extends ProcessService.Stub
    {
        @Override
        public String getServiceName() throws RemoteException {
            return "SystemService";
        }

    }

    class MyConn implements ServiceConnection
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
//            Toast.makeText(SystemService.this, "CoreService die!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SystemService.this,CoreService.class);
            SystemService.this.startService(intent);
            SystemService.this.bindService(intent, conn, Context.BIND_IMPORTANT);
        }
    }
}
