package org.wololo.viper;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.admob.android.ads.AdView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	PowerManager.WakeLock wl;
	
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "Viper");
		

		// register our interest in hearing about changes to our surface
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		// create thread only; it's started in surfaceCreated()
		gameThread = new GameThread(holder, context, new Handler() {
			private boolean firstTime = true;

			@Override
			public void handleMessage(Message m) {
				Bundle data = m.getData();

				int visibility = data.getInt("viz");

				if (visibility == VISIBLE) {

					adView.setVisibility(VISIBLE);
					
					// The ad will fade in over 0.4 seconds.
					AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
					animation.setDuration(400);
					animation.setFillAfter(true);
					animation.setInterpolator(new AccelerateInterpolator());
					adView.startAnimation(animation);
				} else {
					adView.setVisibility(GONE);
					textView.setVisibility(GONE);
				}
			}
		});

		setFocusable(true); // make sure we get key events
	}

	GameThread gameThread;

	private AdView adView;
	private TextView textView;

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

		gameThread.setSurfaceSize(width, height);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// start the thread here so that we don't busy-wait in run()
		// waiting for the surface to be created
		gameThread.setRunning(true);
		gameThread.start();
		
		wl.acquire();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// we have to tell thread to shut down & wait for it to finish, or else
		// it might touch the Surface after we return and explode
		boolean retry = true;
		gameThread.setRunning(false);
		while (retry) {
			try {
				gameThread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
		
		wl.release();
	}
	
	public void setAdView(AdView adView) {
		this.adView = adView;
		this.gameThread.setAdView(adView);
	}
	
	public void setTextView(TextView textView) {
		this.textView = textView;
	}
}
