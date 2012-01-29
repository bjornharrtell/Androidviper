package org.wololo.viper2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;

public class OnTouchListener implements android.view.View.OnTouchListener {
	Handler handler;

	OnTouchListener(Handler handler) {
		this.handler = handler;
	}

	public boolean onTouch(View view, MotionEvent motionEvent) {

		Message message = handler.obtainMessage();
		Bundle data = new Bundle();
		message.setData(data);

		int action = motionEvent.getAction();

		data.putInt("action", action);

		if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
			handler.sendMessage(message);
			return true;
		}

		final int historySize = motionEvent.getHistorySize();

		int p = 0;

		for (int h = 0; h < historySize; h++) {
			float x = motionEvent.getHistoricalX(p, h);
			float y = motionEvent.getHistoricalY(p, h);

			data.putFloat("x", x);
			data.putFloat("y", y);
			message = new Message();
			message.setData(data);
			handler.sendMessage(message);
		}

		float x = motionEvent.getX(p);
		float y = motionEvent.getY(p);

		data.putFloat("x", x);
		data.putFloat("y", y);

		message = new Message();
		message.setData(data);
		handler.sendMessage(message);

		try {
			Thread.sleep(16L);
			// this.wait(16L);
		} catch (InterruptedException e) {
			// ignore interruptions
		}

		return true;
	}

}
