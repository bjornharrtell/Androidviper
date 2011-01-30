package org.wololo.viper2;

import java.util.ArrayList;
import java.util.List;

import org.wololo.viper.core.GameThread;
import org.wololo.viper.core.Worm;
import org.wololo.viper2.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
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

	Context context;

	public AndroidGameThread(ViperActivity game, Handler handler) {
		this.handler = handler;
		this.context = game;

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
		MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.load);
		mediaPlayer.start();

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

		MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.thread);
		mediaPlayer.start();
	}

	@Override
	protected void onBounce() {
		MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.bounce);
		mediaPlayer.start();
	}

	@Override
	protected void onDeath() {
		int nr = (int) (Math.random() * 5);
		int resource = R.raw.doh1;

		switch (nr) {
		case 0:
			resource = R.raw.doh1;
			break;
		case 1:
			resource = R.raw.doh2;
			break;
		case 2:
			resource = R.raw.doh3;
			break;
		case 3:
			resource = R.raw.doh4;
			break;
		case 4:
			resource = R.raw.doh5;
			break;
		case 5:
			resource = R.raw.doh6;
			break;
		}

		MediaPlayer mediaPlayer = MediaPlayer.create(context, resource);
		mediaPlayer.start();
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

		if (state == STATE_RUNNING) {
			// MediaPlayer mediaPlayer = MediaPlayer.create(context,
			// R.raw.wohoo);
			// mediaPlayer.start();
		} else if (state == STATE_LOSE) {
			MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.gameover);
			mediaPlayer.start();
		} else if (state == STATE_WIN) {
			MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.gameover);
			mediaPlayer.start();
		}
	}

	public void onSensorChanged(int sensor, float[] values) {
		if (sensor == SensorManager.SENSOR_ORIENTATION) {
			if (worms.size() > 0) {
				double degrees = values[2];
				degrees = Math.pow(Math.abs(degrees), 0.75);
				double radians = (Math.PI / 180) * degrees;
				double torque = radians / 100;
				if (values[2] < 0)
					torque = -torque;
				worms.get(0).torque = torque;
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
