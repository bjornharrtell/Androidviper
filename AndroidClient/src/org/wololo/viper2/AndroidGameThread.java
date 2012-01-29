package org.wololo.viper2;

import java.util.ArrayList;
import java.util.List;

import org.wololo.viper.core.GameThread;
import org.wololo.viper.core.Worm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class AndroidGameThread extends GameThread implements SurfaceHolder.Callback, Handler.Callback {

	private SurfaceHolder surfaceHolder;
	Handler handler;
	Handler handlerGUI;

	private final PowerManager.WakeLock wakeLock;

	int canvasWidth;
	int canvasHeight;
	int canvasBoardOffsetX;
	int canvasBoardOffsetY;

	Canvas boardCanvas;
	Bitmap boardBitmap;

	boolean running = false;

	Context context;

	public AndroidGameThread(Context context, Handler handlerGUI) {
		this.handlerGUI = handlerGUI;
		this.handler = new Handler(this);
		this.context = context;

		PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "Viper");
	}

	public boolean handleMessage(Message m) {
		Bundle data = m.getData();

		int action = data.getInt("action");

		if (worms.size() > 0) {
			Worm worm = worms.get(0);

			if (action == MotionEvent.ACTION_CANCEL) {
				worm.torque = 0;

			} else if (action == MotionEvent.ACTION_UP) {
				worm.torque = 0;

			} else if (action == MotionEvent.ACTION_DOWN) {
				float x = data.getFloat("x");

				if (x < (canvasWidth / 2)) {
					worm.torque = -0.0015 - worm.velocity;
				} else {
					worm.torque = 0.0015 - worm.velocity;
				}
			}
		}

		return true;
	}

	public void setSurfaceSize(int width, int height) {
		canvasWidth = width;
		canvasHeight = height;

		heightFactor = (float) canvasHeight / canvasWidth;
	}

	public void initBitmap() {
		heightFactor = (float) canvasHeight / canvasWidth;

		boardBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Config.ARGB_8888);
		boardBitmap.eraseColor(Color.BLACK);
		boardCanvas = new Canvas(boardBitmap);
	}

	public void newGame() {

		ViperActivity.playSound(ViperActivity.SOUND_LOAD);

		List<Worm> worms = new ArrayList<Worm>();
		worms.add(new AndroidWorm(this, getRandomStartCoordinate(), getRandomStartDirection(), Color.WHITE, false));
		worms.get(0).torque = 0;
		worms.add(new AndroidWorm(this, getRandomStartCoordinate(), getRandomStartDirection(), Color.BLUE, true));
		worms.add(new AndroidWorm(this, getRandomStartCoordinate(), getRandomStartDirection(), Color.YELLOW, true));

		newGame(worms);
	}

	public void pause() {
	}

	@Override
	public void run() {
		wakeLock.acquire();

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

		wakeLock.release();
	}

	@Override
	protected void onScore(int score, boolean sound) {
		Message msg = handlerGUI.obtainMessage();
		Bundle data = new Bundle();
		data.putInt("score", score);
		msg.setData(data);
		handlerGUI.sendMessage(msg);

		if (sound)
			ViperActivity.playSound(ViperActivity.SOUND_THREAD);
	}

	@Override
	protected void onBounce() {
		ViperActivity.playSound(ViperActivity.SOUND_BOUNCE);
	}

	@Override
	protected void onDeath() {
		int nr = (int) (Math.random() * 5);
		int id = ViperActivity.SOUND_DOH1;

		switch (nr) {
		case 0:
			id = ViperActivity.SOUND_DOH1;
			break;
		case 1:
			id = ViperActivity.SOUND_DOH2;
			break;
		case 2:
			id = ViperActivity.SOUND_DOH3;
			break;
		case 3:
			id = ViperActivity.SOUND_DOH4;
			break;
		case 4:
			id = ViperActivity.SOUND_DOH5;
			break;
		case 5:
			id = ViperActivity.SOUND_DOH6;
			break;
		}

		ViperActivity.playSound(id);
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

	@Override
	public void setState(int state) {
		super.setState(state);

		Message msg = handlerGUI.obtainMessage();
		Bundle data = new Bundle();
		data.putInt("state", state);
		msg.setData(data);
		handlerGUI.sendMessage(msg);

		if (state == STATE_RUNNING) {
			// MediaPlayer mediaPlayer = MediaPlayer.create(context,
			// R.raw.wohoo);
			// mediaPlayer.start();
		} else if (state == STATE_LOSE) {
			ViperActivity.playSound(ViperActivity.SOUND_GAMEOVER);
		} else if (state == STATE_WIN) {
			ViperActivity.playSound(ViperActivity.SOUND_GAMEOVER);
		}
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
