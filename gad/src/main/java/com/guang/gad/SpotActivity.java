package com.guang.gad;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sixth.adwoad.ErrorCode;
import com.sixth.adwoad.InterstitialAd;
import com.sixth.adwoad.InterstitialAdListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.game.UMGameAgent;

import java.util.ArrayList;
import java.util.List;


public class SpotActivity extends Activity {
	private static SpotActivity activity;
	private RelativeLayout layout;

	private String appName;
	private final String mSlotId = "d6f51193ca014fc5a4d21947373417bb";

	private InterstitialAd ad;
	private AVLoadingIndicatorView vl;

	private List<String> bgColors = new ArrayList<String>();
	private List<String> loadColors = new ArrayList<String>();

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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(ad != null)
		{
			ad.dismiss();
			ad= null;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                 WindowManager.LayoutParams.FLAG_FULLSCREEN );


		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		layout = new RelativeLayout(this);
		layout.setLayoutParams(layoutParams);
		this.setContentView(layout);

		this.appName = getIntent().getStringExtra("appName");

//		initLoads();
//
//		int loadNum = Common.getPre().getInt("spotLoadNum",0);
//		if(loadNum >= bgColors.size())
//			loadNum = 0;
//
//		layout.setBackgroundColor(Color.parseColor(bgColors.get(loadNum)));
//
//		vl = new AVLoadingIndicatorView(this);
//		RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(
//				LinearLayout.LayoutParams.WRAP_CONTENT,
//				LinearLayout.LayoutParams.WRAP_CONTENT);
//		layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
//		vl.setIndicatorColor(Color.parseColor(loadColors.get(loadNum)));
//		layout.addView(vl,layoutParams2);
//		vl.setIndicator("BallPulseIndicator");
//
//		Common.getPre().edit().putInt("spotLoadNum",loadNum+1).commit();

		showAppSpot();
	}

	private void initLoads()
	{
		bgColors.add("#25aafa");
		bgColors.add("#fd9500");
		bgColors.add("#ffc20c");
		bgColors.add("#7bb811");
		bgColors.add("#4e4e4e");
		bgColors.add("#02b8cd");
		bgColors.add("#e85170");
		bgColors.add("#8a229c");
		bgColors.add("#ffffff");
		bgColors.add("#d3f675");

		loadColors.add("#c7eaff");
		loadColors.add("#ffffff");
		loadColors.add("#ff7e04");
		loadColors.add("#ecff54");
		loadColors.add("#25aafa");
		loadColors.add("#abfeff");
		loadColors.add("#ffbbc9");
		loadColors.add("#ffadf4");
		loadColors.add("#d6d6d6");
		loadColors.add("#8bc34a");
	}

	public void showAppSpot()
	{
		ad = new InterstitialAd(this, mSlotId, false, new InterstitialAdListener() {
			@Override
			public void onReceiveAd() {

			}

			@Override
			public void onLoadAdComplete() {
				ad.displayAd();
			}

			@Override
			public void onFailedToReceiveAd(ErrorCode errorCode) {
				MobclickAgent.onEvent(activity, Common.EVENT_SPOT_FAIL);
				hide();
				Log.e("-------------","onAdFailedToLoad code="+errorCode.getErrorString());
			}

			@Override
			public void onAdDismiss() {
				MobclickAgent.onEvent(activity, Common.EVENT_SPOT_CLOSE);
				hide();
				Log.e("--------------", "onAdClosed");
			}

			@Override
			public void OnShow() {
				MobclickAgent.onEvent(activity, Common.EVENT_SPOT_SHOW);
			}
		});
		ad.prepareAd();

		int num = Common.getPre().getInt("spot_shownum",0);
		Common.getPre().edit().putInt("spot_shownum",num+1).commit();

		MobclickAgent.onEvent(activity, Common.EVENT_SPOT_REQ);

//		final Handler handler = new Handler(){
//			@Override
//			public void handleMessage(Message msg) {
//				super.handleMessage(msg);
//				if(msg.what == 0x01)
//				{
//					hide();
//				}
//			}
//		};
//
//		new Thread(){
//			public void run() {
//				try {
//					Thread.sleep(1000*40);
//					handler.sendEmptyMessage(0x01);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			};
//		}.start();
	}
	
	public static void hide()
	{
		if(activity!=null)
		{
			activity.finish();
			activity = null;
		}
	}
	


}
