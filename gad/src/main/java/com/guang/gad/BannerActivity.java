package com.guang.gad;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.game.UMGameAgent;


@SuppressWarnings("deprecation")
public class BannerActivity extends Activity {
	private BannerActivity context;
	private RelativeLayout view;
	private int l_height;

	private String appName;
	private final String adId = "ca-app-pub-3264772490175149/2069181951";

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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
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
		
		l_height = Common.dip2px(50);
		
		final LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
//		p.width = width*2;  
		p.height = l_height;    
        p.x = 0;
        p.y = -height/2 + l_height/2 + title_h;
        getWindow().setAttributes(p); 
        
        AbsoluteLayout root = new AbsoluteLayout(this);
        AbsoluteLayout.LayoutParams rootlayoutParams = new AbsoluteLayout.LayoutParams(p.width,p.height,0,0);
// 		rootlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        view = new RelativeLayout(this);
 		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, l_height);
 		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
 		view.setLayoutParams(layoutParams);
 		
 		root.addView(view);

		this.appName = getIntent().getStringExtra("appName");


		AdView mAdView = new AdView(this);
		RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
		view.addView(mAdView,layoutParams2);

		mAdView.setAdSize(AdSize.BANNER);
		mAdView.setAdUnitId(this.adId);
		mAdView.setAdListener(new AdListener() {
			@Override
			public void onAdClosed() {
				super.onAdClosed();
				MobclickAgent.onEvent(context, Common.EVENT_BANNER_CLOSE);
				Log.e("-------------","onAdClosed");
			}

			@Override
			public void onAdFailedToLoad(int i) {
				super.onAdFailedToLoad(i);
				hide(false);
				MobclickAgent.onEvent(context, Common.EVENT_BANNER_FAIL);
				Log.e("-------------","onAdFailedToLoad code="+i + "  adid="+adId);
			}

			@Override
			public void onAdLeftApplication() {
				super.onAdLeftApplication();
				MobclickAgent.onEvent(context, Common.EVENT_BANNER_CLICK);
				hide(false);
			}

			@Override
			public void onAdOpened() {
				super.onAdOpened();
				MobclickAgent.onEvent(context, Common.EVENT_BANNER_SHOW);
			}

			@Override
			public void onAdLoaded() {
				super.onAdLoaded();
				if(!Common.isAppInBackground(appName))
				{
					show();
					Log.e("--------------", "banner success");
				}
				else
					hide(false);
			}
		});
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

 		this.setContentView(root,rootlayoutParams);

		int num = Common.getPre().getInt("banner_shownum",0);
		Common.getPre().edit().putInt("banner_shownum",num+1).commit();

		MobclickAgent.onEvent(context, Common.EVENT_BANNER_REQ);

		view.setOnTouchListener(new OnTouchListener() {
			private float lastX = 0;
			private float lastY = 0;
			private float lastX2 = 0;
			private boolean move;
			private int initX = 0;
			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				if(action == MotionEvent.ACTION_DOWN)
				{
					move = false;
					lastX = lastX2 = event.getRawX();
					lastY = event.getRawY();
					
					AbsoluteLayout.LayoutParams par = (AbsoluteLayout.LayoutParams) view.getLayoutParams();
					initX = par.x;
				}
				else if(action == MotionEvent.ACTION_MOVE)
				{
					float disX = Math.abs(event.getRawX() - lastX);
					float disY = Math.abs(event.getRawY() - lastY);
					if(disX >= Common.dip2px(3) || disY >= Common.dip2px(3))
					{
						move = true;
					}

					int mx = (int)(event.getRawX() - lastX2);
					if(Math.abs(mx) >= Common.dip2px(2))
					{
						AbsoluteLayout.LayoutParams par = (AbsoluteLayout.LayoutParams) view.getLayoutParams();
						par.x += mx;
						view.setLayoutParams(par);
						
						float dis = Math.abs(par.x - initX);
						float alpha = 1-(dis/800.f);
						
						try{
							view.setAlpha(alpha);
						}catch(NoSuchMethodError e)
						{
							
						}
						
						lastX2 = event.getRawX();
					}
				}
				else if(action == MotionEvent.ACTION_UP)
				{
					if(!move)
					{
						hide(true);
					}
					else
					{
						AbsoluteLayout.LayoutParams par = (AbsoluteLayout.LayoutParams) view.getLayoutParams();
						try{
							if(view.getAlpha() <= 0.8f)
							{
								float tx = 800;
								if(par.x<0)
									tx = -tx;
								remove(0,tx);
							}
							else
							{
								par.x = initX;
								view.setLayoutParams(par);
								view.setAlpha(1);
							}
						}catch(NoSuchMethodError e)
						{
							if(par.x > 0.2f*Common.getScreenW())
							{
								float tx = 800;
								if(par.x<0)
									tx = -tx;
								remove(0,tx);
							}
							else
							{
								par.x = initX;
								view.setLayoutParams(par);
							}
						}
						
					}
				}
				return true;
			}
		});

	}
	
	private void show()
	{
		AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation =
           new TranslateAnimation(
           		Animation.ABSOLUTE,0.0f,
           		Animation.ABSOLUTE,0f,
	                Animation.ABSOLUTE,-l_height,
	                Animation.ABSOLUTE,0f);
        translateAnimation.setDuration(1000);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setInterpolator(new AccelerateInterpolator());
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation animation) {
				}
				@Override
				public void onAnimationStart(Animation animation) {}
				@Override
				public void onAnimationRepeat(Animation animation) {}
			});
        view.startAnimation(animationSet);


		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.what == 0x01)
				{
					hide(false);
				}
			}
		};

		new Thread(){
			public void run() {
				try {
					Thread.sleep(1000*13);
					handler.sendEmptyMessage(0x01);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();

	}
	
	private void hide(final boolean isClick)
	{
		AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation =
           new TranslateAnimation(
           		Animation.ABSOLUTE,0.0f,
           		Animation.ABSOLUTE,0f,
	                Animation.ABSOLUTE,0f,
	                Animation.ABSOLUTE,-l_height);
        translateAnimation.setDuration(500);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(500);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(translateAnimation);
        animationSet.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				view.setVisibility(View.GONE);
				context.finish();
			}
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
		});
       view.startAnimation(animationSet);
	}
	
	private void remove(float fx,float tx)
	{
		AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation =
           new TranslateAnimation(
           		Animation.ABSOLUTE,fx,
           		Animation.ABSOLUTE,tx,
	                Animation.RELATIVE_TO_SELF,0f,
	                Animation.RELATIVE_TO_SELF,0);
        translateAnimation.setDuration(500);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(500);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(translateAnimation);
        animationSet.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				context.finish();
			}
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
		});
       view.startAnimation(animationSet);
	}

}
