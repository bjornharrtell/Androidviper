package org.wololo.viper;

import com.admob.android.ads.AdView;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// register our interest in hearing about changes to our surface
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		// create thread only; it's started in surfaceCreated()
		gameThread = new GameThread(holder, context, new Handler() {
		});

		setFocusable(true); // make sure we get key events
	}

	GameThread gameThread;
	
	private AdView mAd;

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		gameThread.setSurfaceSize(width, height);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// start the thread here so that we don't busy-wait in run()
		// waiting for the surface to be created
		gameThread.setRunning(true);
		gameThread.start();
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
	}

	/**
     * Standard override to get key-press events.
     */
    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
        return gameThread.doKeyDown(keyCode, msg);
    }*/

    /**
     * Standard override for key-up. We actually care about these, so we can
     * stop rotating.
     */
    /*Override
    public boolean onKeyUp(int keyCode, KeyEvent msg) {
        return gameThread.doKeyUp(keyCode, msg);
    }*/
    
    /*
    @Override
    public boolean onTrackballEvent(MotionEvent motionEvent) {
    	return gameThread.onTrackballEvent(motionEvent);
    }*/
    

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
		return gameThread.onTouchEvent(motionEvent);
    }

	public void setmAd(AdView mAd) {
		this.mAd = mAd;
	}

	public AdView getmAd() {
		return mAd;
	}
}
