package org.wololo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
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
}
