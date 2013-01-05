package org.wololo.viper2;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ViperView extends SurfaceView implements SurfaceHolder.Callback {

	AndroidGameThread thread;
	
	ViperActivity activity;
	
	public ViperView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		Log.v(toString(), "Creating ViperView");
		
		getHolder().addCallback(this);
		
		thread = new AndroidGameThread(context, new Handler() {
			@Override
			public void handleMessage(Message m) {
				Bundle data = m.getData();

				if (data.containsKey("state")) {
					int state = data.getInt("state");
					activity.handleStateChange(state);
				} else if (data.containsKey("score")) {
					int score = data.getInt("score");
					activity.handleScoreChange(score);
				}
			}
		});
		
        setFocusable(true); // make sure we get key events
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.v(toString(), "surfaceChanged called");
		thread.setSurfaceSize(width, height);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.v(toString(), "surfaceCreated called");
		thread.surfaceHolder = holder;
		if (thread.running == false) {
			thread.start();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.v(toString(), "surfaceDestroyed called");
		/*boolean retry = true;
		thread.running = false;
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}*/
	}

}
