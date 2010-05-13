package org.wololo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.SurfaceHolder;

public class GameThread extends Thread {

	public static final int STATE_LOSE = 1;
	public static final int STATE_PAUSE = 2;
	public static final int STATE_READY = 3;
	public static final int STATE_RUNNING = 4;
	public static final int STATE_WIN = 5;

	SurfaceHolder surfaceHolder;
	int canvasWidth;
	int canvasHeight;

	int state;
	boolean running = false;

	long lastTime;

	Game game;

	List<Worm> worms = new ArrayList<Worm>();

	public GameThread(SurfaceHolder surfaceHolder, Context context,
			Handler handler) {
		this.surfaceHolder = surfaceHolder;
	}

	public void setSurfaceSize(int width, int height) {
		synchronized (surfaceHolder) {
			canvasWidth = width;
			canvasHeight = height;
		}
	}

	public void newGame() {
		synchronized (surfaceHolder) {
			// TODO more initial game state stuff..?
			worms.add(new Worm(new Point(0.2f, 0.2f), 0.4f, Color.WHITE));
			// worms.add(new Worm(new Point(0.8f, 0.8f), -2.2f, Color.BLUE));

			// make physics calc start in about 100 ms
			lastTime = System.currentTimeMillis() + 100;

			state = STATE_RUNNING;
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
						updatePhysics();
					}
					draw(canvas);
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

	void draw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);

		for (Worm worm : worms) {
			worm.draw(canvas);
		}

		canvas.save();
		canvas.restore();
	}

	void updatePhysics() {

		long now = System.currentTimeMillis();

		if (lastTime > now)
			return;

		long elapsed = now - lastTime;

		for (Worm worm : worms) {
			worm.move(elapsed);
		}

		lastTime = now;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public void setState(int state) {
		this.state = state;
	}

	/**
	 * Handles a key-down event.
	 * 
	 * @param keyCode
	 *            the key that was pressed
	 * @param msg
	 *            the original event object
	 * @return true
	 */
	boolean doKeyDown(int keyCode, KeyEvent msg) {
		synchronized (surfaceHolder) {
			if (state != STATE_RUNNING)
				return false;

			if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
				worms.get(0).setTorque(-0.001);
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				worms.get(0).setTorque(0.001);
				return true;
			}

			return false;
		}
	}

	/**
	 * Handles a key-up event.
	 * 
	 * @param keyCode
	 *            the key that was pressed
	 * @param msg
	 *            the original event object
	 * @return true if the key was handled and consumed, or else false
	 */
	boolean doKeyUp(int keyCode, KeyEvent msg) {
		boolean handled = false;

		synchronized (surfaceHolder) {
			if (state != STATE_RUNNING)
				return handled;

			if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
					|| keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {

				worms.get(0).setTorque(0);

				handled = true;
			}
		}

		return handled;
	}
}
