package com.android.system.core.sometools;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.guang.gadlib.GAdController;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.game.UMGameAgent;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        GAdController.getInstance().init(this);

        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
//        UMGameAgent.setDebugMode(true);//设置输出运行时日志
        UMGameAgent.init( this );
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UMGameAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UMGameAgent.onPause(this);
    }
}
