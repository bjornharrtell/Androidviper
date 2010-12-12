package org.wololo.viper;

import java.util.ArrayList;
import java.util.List;

import org.wololo.viper.GameThread;
import org.wololo.viper.Worm;
import org.wololo.viper2.ViperActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.SurfaceHolder;

public class AndroidGameThread extends GameThread implements SensorListener, SurfaceHolder.Callback {

	private SurfaceHolder surfaceHolder;
	Handler handler;

	PowerManager.WakeLock wakeLock;
	SensorManager sensorManager;

	int canvasWidth;
	int canvasHeight;
	int canvasBoardOffsetX;
	int canvasBoardOffsetY;

	Canvas boardCanvas;
	Bitmap boardBitmap;

	boolean running = false;

	public AndroidGameThread(ViperActivity game, Handler handler) {
		this.handler = handler;

		PowerManager powerManager = (PowerManager) game.getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "Viper");

		sensorManager = (SensorManager) game.getSystemService(Context.SENSOR_SERVICE);
	}

	public void setSurfaceSize(int width, int height) {
		canvasWidth = width;
		canvasHeight = height;

		GameThread.heightFactor = canvasHeight / canvasWidth;
	}

	public void initBitmap() {
		boardBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Config.ARGB_8888);
		boardBitmap.eraseColor(Color.BLACK);
		boardCanvas = new Canvas(boardBitmap);
	}

	public void newGame() {
		List<Worm> worms = new ArrayList<Worm>();
		worms.add(new AndroidWorm(getRandomStartCoordinate(), getRandomStartDirection(), Color.WHITE, false));
		worms.add(new AndroidWorm(getRandomStartCoordinate(), getRandomStartDirection(), Color.BLUE, true));
		worms.add(new AndroidWorm(getRandomStartCoordinate(), getRandomStartDirection(), Color.YELLOW, true));
		
		newGame(worms);
	}

	public void pause() {
	}

	@Override
	public void run() {
		wakeLock.acquire();

		sensorManager.registerListener(this, SensorManager.SENSOR_ORIENTATION, SensorManager.SENSOR_DELAY_FASTEST);

		while (running) {
			Canvas canvas = null;
			try {
				canvas = surfaceHolder.lockCanvas(null);
				synchronized (surfaceHolder) {
					if (state == STATE_READY) {
						initBitmap();
						onScore(0);
						setState(STATE_RUNNING);
					}
					if (state == STATE_RUNNING) {
						timestep(canvas);
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

		sensorManager.unregisterListener(this);

		wakeLock.release();
	}

	@Override
	protected void onScore(int score) {
		Message msg = handler.obtainMessage();
		Bundle data = new Bundle();
		data.putInt("score", score);
		msg.setData(data);
		handler.sendMessage(msg);
	}

	void timestep(Canvas canvas) {
		timestep();

		for (Worm worm : worms) {
			if (worm.alive) {
				AndroidWorm androidWorm = (AndroidWorm) worm;
				androidWorm.draw(boardCanvas);
			}
		}

		canvas.drawBitmap(boardBitmap, 0, 0, null);
	}

	public void setState(int state) {
		super.setState(state);

		Message msg = handler.obtainMessage();
		Bundle data = new Bundle();
		data.putInt("state", state);
		msg.setData(data);
		handler.sendMessage(msg);
	}

	public void onSensorChanged(int sensor, float[] values) {
		if (sensor == SensorManager.SENSOR_ORIENTATION) {
			if (worms.size() > 0) {
				worms.get(0).torque = ((Math.PI / 180) * values[2]) / 100;
			}
		}
	}

	public void onAccuracyChanged(int sensor, int accuracy) {
		
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		this.setSurfaceSize(width, height);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// start the thread here so that we don't busy-wait in run()
		// waiting for the surface to be created
		this.surfaceHolder = holder;
		running = true;
		this.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// we have to tell thread to shut down & wait for it to finish, or else
		// it might touch the Surface after we return and explode
		boolean retry = true;
		running = false;
		while (retry) {
			try {
				this.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}
}
