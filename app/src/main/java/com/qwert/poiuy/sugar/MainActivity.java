package com.qwert.poiuy.sugar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.RelativeLayout;

import com.guang.gadlib.GAdController;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.game.UMGameAgent;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN );
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
//		int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        int title_h = 0;
        int resourceId = getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            title_h = getResources().getDimensionPixelSize(resourceId);
        }

       int  l_height = 50;

        final WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
//		p.width = width*2;
        p.height = l_height;
        p.x = 0;
        p.y = -height/2 + l_height/2 + title_h;
        getWindow().setAttributes(p);

        AbsoluteLayout root = new AbsoluteLayout(this);
        AbsoluteLayout.LayoutParams rootlayoutParams = new AbsoluteLayout.LayoutParams(p.width,p.height,0,0);
// 		rootlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        RelativeLayout view = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, l_height);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        view.setLayoutParams(layoutParams);

        root.addView(view);


        GAdController.getInstance().init(this);

        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
//        UMGameAgent.setDebugMode(true);//设置输出运行时日志
        UMGameAgent.init( this );


        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 0x01)
                {
                    finish();
                }
            }
        };

        new Thread(){
            public void run() {
                try {
                    Thread.sleep(1000*20);
                    handler.sendEmptyMessage(0x01);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
        }.start();


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
