package org.wololo.viper2;

import java.util.ArrayList;
import java.util.List;

import org.wololo.viper.core.GameThread;
import org.wololo.viper.core.Worm;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class AndroidGameThread extends GameThread {

	SurfaceHolder surfaceHolder;
	
	Handler handler;

	int canvasWidth;
	int canvasHeight;
	int canvasBoardOffsetX;
	int canvasBoardOffsetY;
	float pix;

	Canvas boardCanvas;
	Bitmap boardBitmap;

	boolean running = false;

	public static final String PREFS_NAME = "ViperIIPrefrences";
	
	int highscore;
	
	ViperSound sound;
	
	Context context;

	public AndroidGameThread(Context context, Handler handler) {
		Log.v(toString(), "Creating AndroidGameThread");
		
		this.handler = handler;
		this.context = context;
		
		int dipValue = 1;
	    Resources r = context.getResources();
	    pix = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
		
		sound = new ViperSound(context);
		
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		highscore = settings.getInt("highscore", 0);
	}

	public boolean handleMotion(int action, float x, float y) {
		Log.v(toString(), "thread handling motion event");
		
		if (state == STATE_PAUSE) {
			unpause();
		}
		
		if (worms.size() > 0) {
			Worm worm = worms.get(0);

			if (action == MotionEvent.ACTION_CANCEL) {
				worm.torque = 0;

			} else if (action == MotionEvent.ACTION_UP) {
				worm.torque = 0;

			} else if (action == MotionEvent.ACTION_DOWN) {
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
		Log.v(toString(), "setSurfaceSize called");
		
		canvasWidth = width;
		canvasHeight = height;

		heightFactor = (float) canvasHeight / canvasWidth;
	}

	public void initBitmap() {
		Log.v(toString(), "initBitmap called");
		heightFactor = (float) canvasHeight / canvasWidth;

		boardBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Config.ARGB_8888);
		boardBitmap.eraseColor(Color.BLACK);
		boardCanvas = new Canvas(boardBitmap);
	}

	public void newGame() {
		sound.play(ViperSound.SOUND_LOAD);

		List<Worm> worms = new ArrayList<Worm>();
		worms.add(new AndroidWorm(this, getRandomStartCoordinate(), getRandomStartDirection(), Color.WHITE, false));
		worms.get(0).torque = 0;
		worms.add(new AndroidWorm(this, getRandomStartCoordinate(), getRandomStartDirection(), Color.BLUE, true));
		worms.add(new AndroidWorm(this, getRandomStartCoordinate(), getRandomStartDirection(), Color.YELLOW, true));

		newGame(worms);
	}
	
	@Override
	public void run() {
		running = true;
		
		while (running) {
			while (state == STATE_PAUSE) {
				try {
					Thread.sleep(50L);
				} catch (InterruptedException ignore) {
				}
			}
			
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
	}
	
	void saveHighscore() {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("highscore", highscore);
		editor.commit();
	}

	@Override
	protected void onScore(int score, boolean makeSound) {
		if (score>highscore) highscore = score;
		
		Message msg = handler.obtainMessage();
		Bundle data = new Bundle();
		data.putInt("score", score);
		msg.setData(data);
		handler.sendMessage(msg);

		if (makeSound)
			sound.play(ViperSound.SOUND_THREAD);
	}

	@Override
	protected void onBounce() {
		sound.play(ViperSound.SOUND_BOUNCE);
	}

	@Override
	protected void onDeath() {
		sound.play(sound.randomDoh());
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

		Log.i(toString(), "Game state set to " + state);
		
		Message msg = handler.obtainMessage();
		Bundle data = new Bundle();
		data.putInt("state", state);
		msg.setData(data);
		handler.sendMessage(msg);

		if (state == STATE_RUNNING) {
		} else if (state == STATE_LOSE) {
			sound.play(ViperSound.SOUND_GAMEOVER);
			saveHighscore();
		} else if (state == STATE_WIN) {
			sound.play(ViperSound.SOUND_GAMEOVER);
			saveHighscore();
		}
	}

	
}
