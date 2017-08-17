package com.android.system.core.smart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.guang.gadlib.GAdController;

/**
 * Created by guang on 2017/8/15.
 */

public class CoreReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        GAdController.getInstance().init(context);
    }
}
