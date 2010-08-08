package org.wololo.viper;

import java.util.ArrayList;
import java.util.List;

import orc.wololo.viper.Point;
import orc.wololo.viper.Worm;

import com.admob.android.ads.AdView;
import com.admob.android.ads.SimpleAdListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;

public class GameThread extends Thread implements SensorListener {

	public static final int STATE_UNINITIALIZED = 0;
	public static final int STATE_LOSE = 1;
	public static final int STATE_PAUSE = 2;
	public static final int STATE_READY = 3;
	public static final int STATE_RUNNING = 4;
	public static final int STATE_WIN = 5;

	private AdView adView;

	Handler handler;

	SensorManager sensorManager;

	SurfaceHolder surfaceHolder;
	int canvasWidth;
	int canvasHeight;
	int canvasBoardOffsetX;
	int canvasBoardOffsetY;
	int boardSize;

	Canvas boardCanvas;
	Bitmap boardBitmap;

	int state;
	boolean running = false;

	boolean firstRun = true;
	boolean firstTimestep = true;

	long lastTime;

	Game game;

	List<AndroidWorm> worms = new ArrayList<AndroidWorm>();

	public GameThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
		this.surfaceHolder = surfaceHolder;
		this.handler = handler;

		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		sensorManager.registerListener(this, SensorManager.SENSOR_ORIENTATION, SensorManager.SENSOR_DELAY_GAME);

	}

	public void setSurfaceSize(int width, int height) {
		synchronized (surfaceHolder) {
			canvasWidth = width;
			canvasHeight = height;

			initBitmap();
		}
	}

	public void initBitmap() {
		if (canvasHeight < canvasWidth) {
			boardSize = canvasHeight;
			canvasBoardOffsetY = 0;
			canvasBoardOffsetX = (canvasWidth - canvasHeight) / 2;
		} else {
			boardSize = canvasWidth;
			canvasBoardOffsetX = 0;
			canvasBoardOffsetY = (canvasHeight - canvasWidth) / 2;
		}

		boardBitmap = Bitmap.createBitmap(boardSize, boardSize, Config.ARGB_8888);
		boardBitmap.eraseColor(Color.BLACK);
		boardCanvas = new Canvas(boardBitmap);

		Paint border = new Paint();
		border.setColor(Color.WHITE);
		border.setStyle(Style.STROKE);
		Rect rect = new Rect(0, 0, boardSize - 1, boardSize - 1);
		boardCanvas.drawRect(rect, border);
	}

	public void newGame(AdView adView) {
		synchronized (surfaceHolder) {

			Message msg = handler.obtainMessage();
			Bundle data = new Bundle();
			data.putInt("viz", View.GONE);
			msg.setData(data);
			handler.sendMessage(msg);

			// TODO more initial game state stuff..?
			worms.clear();
			worms.add(new AndroidWorm(new Point(0.2f, 0.2f), 0.4f, Color.WHITE));
			// worms.add(new Worm(new Point(0.8f, 0.8f), -2.2f, Color.BLUE));

			// make physics calc start in about 100 ms
			lastTime = System.currentTimeMillis() + 100;

			state = STATE_RUNNING;

			initBitmap();
		}
	}

	public void pause() {
		synchronized (surfaceHolder) {

		}
	}

	@Override
	public void run() {
		while (running) {
			Canvas canvas = null;
			try {
				canvas = surfaceHolder.lockCanvas(null);
				synchronized (surfaceHolder) {
					if (state == STATE_RUNNING) {
						timestep(canvas);
					} else {
						if (firstRun) {
							Message msg = handler.obtainMessage();
							Bundle data = new Bundle();
							data.putInt("viz", View.VISIBLE);
							msg.setData(data);
							handler.sendMessage(msg);

							firstRun = false;
						}
					}
				}
			} finally {
				// do this in a finally so that if an exception is thrown
				// during the above, we don't leave the Surface in an
				// inconsistent state
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}

	void collisionTest(Worm worm) {
		for (Worm otherWorm : worms) {
			int result = otherWorm.collisionTest(worm);

			if (result == Worm.COLLISIONWORM) {
				worm.setAlive(false);
			} else if (result == Worm.COLLISIONHOLE) {
				worm.setScore(worm.getScore() + 1);
			}
		}
	}

	void timestep(Canvas canvas) {
		long now = System.currentTimeMillis();

		if (lastTime > now)
			return;

		long elapsed = now - lastTime;

		for (AndroidWorm worm : worms) {
			if (worm.isAlive()) {
				worm.move(elapsed);
				worm.draw(boardCanvas);
				collisionTest(worm);
			}
		}

		canvas.drawColor(Color.BLACK);
		Paint background = new Paint();
		background.setColor(Color.BLACK);
		background.setStyle(Style.FILL);
		canvas.drawRect(0, canvasBoardOffsetY, canvasWidth - 1, canvasHeight - 1, background);
		canvas.drawBitmap(boardBitmap, canvasBoardOffsetX, canvasBoardOffsetY, null);

		Paint text = new Paint();
		text.setColor(Color.WHITE);
		text.setStyle(Style.STROKE);
		canvas.drawText("Score: " + worms.get(0).getScore(), 50, canvasHeight - 20, text);

		lastTime = now;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public void setState(int state) {
		this.state = state;
	}

	public void setAdView(AdView ad) {
		this.adView = ad;
	}

	public AdView getAd() {
		return adView;
	}

	public void onSensorChanged(int sensor, float[] values) {
		synchronized (surfaceHolder) {
			if (sensor == SensorManager.SENSOR_ORIENTATION) {
				if (worms.size() > 0) {
					worms.get(0).setTorque(values[2] / 4000);
				}
			}
		}
	}

	public void onAccuracyChanged(int sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
}
