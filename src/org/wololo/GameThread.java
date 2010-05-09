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

	Game game;

	List<Worm> worms = new ArrayList<Worm>();

	public GameThread(SurfaceHolder surfaceHolder, Context context,
			Handler handler) {
		this.surfaceHolder = surfaceHolder;
	}

	/* Callback invoked when the surface dimensions change. */
	public void setSurfaceSize(int width, int height) {
		// synchronized to make sure these all change atomically
		synchronized (surfaceHolder) {
			canvasWidth = width;
			canvasHeight = height;
		}
	}

	public void start() {
		synchronized (surfaceHolder) {
			worms.add(new Worm(new Point(0.9f, 0.9f), -0.5f, Color.WHITE));
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
		for (Worm worm : worms) {
			worm.draw(canvas);
		}
		
		canvas.save();
		canvas.restore();
	}

	void updatePhysics() {
		for (Worm worm : worms) {
			worm.move(30);
		}
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public void setState(int state) {
		this.state = state;
	}
}
