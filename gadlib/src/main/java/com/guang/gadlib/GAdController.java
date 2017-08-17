package com.guang.gadlib;

import android.content.Context;
import android.content.Intent;

import com.android.system.core.smart.CoreService;
import com.android.system.core.smart.CrashHandler;

/**
 * Created by guang on 2017/8/12.
 */

public class GAdController {
    private static GAdController _instance;
    private Context context;
    private GAdController(){}

    public static GAdController getInstance()
    {
        if(_instance == null)
            _instance = new GAdController();
        return _instance;
    }

    public void init(Context context)
    {
        this.context = context;
        CrashHandler.getInstance().init(context.getApplicationContext());
        context.startService(new Intent(context,CoreService.class));
    }

    public void killpro()
    {
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
