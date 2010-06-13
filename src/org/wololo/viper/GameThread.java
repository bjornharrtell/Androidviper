package org.wololo.viper;

import java.util.ArrayList;
import java.util.List;

import com.admob.android.ads.AdView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;

public class GameThread extends Thread {

	public static final int STATE_UNINITIALIZED = 0;
	public static final int STATE_LOSE = 1;
	public static final int STATE_PAUSE = 2;
	public static final int STATE_READY = 3;
	public static final int STATE_RUNNING = 4;
	public static final int STATE_WIN = 5;

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

	boolean firstTimestep = true;

	long lastTime;

	Game game;

	List<Worm> worms = new ArrayList<Worm>();

	public GameThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
		this.surfaceHolder = surfaceHolder;
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
			

			adView.setVisibility( View.VISIBLE );

			// The ad will fade in over 0.4 seconds.
			AlphaAnimation animation = new AlphaAnimation( 0.0f, 1.0f );
			animation.setDuration( 400 );
			animation.setFillAfter( true );
			animation.setInterpolator( new AccelerateInterpolator() );
			adView.startAnimation( animation );
			
			// TODO more initial game state stuff..?
			worms.clear();
			worms.add(new Worm(new Point(0.2f, 0.2f), 0.4f, Color.WHITE));
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
					}
					else {
						Paint text = new Paint();
						text.setColor(Color.WHITE);
						text.setStyle(Style.STROKE);
						canvas.drawText("Welcome to Viper 1.1 (c) 2010 Björn Harrtell", 10, 20, text);
						canvas.drawText("Press MENU", 10, 35, text);
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
			int result = otherWorm.collisionTest(worm.segments.get(worm.segments.size() - 1));

			if (result == Worm.COLLISIONWORM) {
				worm.alive = false;
			} else if (result == Worm.COLLISIONHOLE) {
				worm.score += 1;
			}
		}
	}

	void timestep(Canvas canvas) {
		long now = System.currentTimeMillis();

		if (lastTime > now)
			return;

		long elapsed = now - lastTime;

		for (Worm worm : worms) {
			if (worm.alive) {
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

		//if (firstTimestep) {

			Paint touchZone = new Paint();
			touchZone.setColor(Color.RED);
			touchZone.setStyle(Style.FILL);
			Rect rect = new Rect(0, canvasHeight - 40, 40, canvasHeight - 1);
			canvas.drawRect(rect, touchZone);
			rect = new Rect(canvasWidth - 40, canvasHeight - 40, canvasWidth - 1, canvasHeight - 1);
			canvas.drawRect(rect, touchZone);

		//	firstTimestep = false;
		//}
		
	    
		Paint text = new Paint();
		text.setColor(Color.WHITE);
		text.setStyle(Style.STROKE);
		canvas.drawText("Score: " + worms.get(0).score, 50, canvasHeight - 20, text);

		// canvas.save();
		// canvas.restore();

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

			if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {

				worms.get(0).setTorque(0);

				handled = true;
			}
		}

		return handled;
	}

	boolean onTrackballEvent(MotionEvent motionEvent) {
		boolean handled = false;

		synchronized (surfaceHolder) {
			if (motionEvent.getX() == 0) {
				worms.get(0).setTorque(0);
				handled = true;
			} else if (motionEvent.getX() > 0) {
				worms.get(0).setTorque(0.001);
				handled = true;
			} else if (motionEvent.getX() < 0) {
				worms.get(0).setTorque(-0.001);
				handled = true;
			}
		}

		return handled;
	}

	public boolean onTouchEvent(MotionEvent motionEvent) {
		switch (motionEvent.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (motionEvent.getX() < 40 && motionEvent.getY() > canvasHeight - 40) {
				worms.get(0).setTorque(0.001);
			} else if (motionEvent.getX() > canvasWidth - 40 && motionEvent.getY() > canvasHeight - 40) {
				worms.get(0).setTorque(-0.001);
			}

			return true;
		case MotionEvent.ACTION_UP:
			if (motionEvent.getX() < 40 && motionEvent.getY() > canvasHeight - 40) {
				worms.get(0).setTorque(0);
			} else if (motionEvent.getX() > canvasWidth - 40 && motionEvent.getY() > canvasHeight - 40) {
				worms.get(0).setTorque(0);
			}

			return true;
		}

		return false;
	}
}
