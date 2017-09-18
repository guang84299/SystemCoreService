package com.guang.gad;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by guang on 2017/8/15.
 */

public class GReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //锁屏
        if(Intent.ACTION_SCREEN_OFF.equals(action))
        {
            GController.getInstance().setPresent(false);
        }
        //开屏
        else if(Intent.ACTION_USER_PRESENT.equals(action))
        {
            GController.getInstance().setPresent(true);
        }
        //亮屏
        else if(Intent.ACTION_SCREEN_ON.equals(action))
        {
            GController.getInstance().setPresent(true);
        }
        else if(Common.ACTION_BEHIND_SHOW.equals(action))
        {
            GProBehind.getInstance().show((Service) GController.getInstance().getContext());
        }
        else if(Common.ACTION_BEHIND_HIDE.equals(action))
        {
            GProBehind.getInstance().hide();
        }
    }
}
