package org.wololo.viper2;

import android.view.MotionEvent;
import android.view.View;

public class OnTouchListener implements android.view.View.OnTouchListener {
	AndroidGameThread thread;

	OnTouchListener(AndroidGameThread thread) {
		this.thread = thread;
	}

	public boolean onTouch(View view, MotionEvent motionEvent) {
		int action = motionEvent.getAction();

		if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
			thread.handleMotion(action, 0, 0);
			return true;
		}
		int p = 0;

		float x = motionEvent.getX(p);
		float y = motionEvent.getY(p);

		thread.handleMotion(action, x, y);

		try {
			Thread.sleep(16L);
		} catch (InterruptedException e) {
		}

		return true;
	}

}
