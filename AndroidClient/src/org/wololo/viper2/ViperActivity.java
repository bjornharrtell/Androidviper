package org.wololo.viper2;

import java.util.HashMap;

import org.wololo.viper.core.GameThread;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class ViperActivity extends Activity implements OnClickListener {
	SurfaceView surfaceView;
	RelativeLayout relativeLayoutSurfaceView;

	RelativeLayout relativeLayoutMainMenu;
	Button buttonNewGame;
	Button buttonQuit;
	TextView textViewTitle;
	TextView textViewScore;
	TextView textViewHighscore;

	AndroidGameThread gameThread;

	AlphaAnimation fadeIn;
	AlphaAnimation fadeOut;

	static AudioManager audioManager;
	static SoundPool soundPool = null;

	static int SOUND_BACKGROUND = 0;
	static int SOUND_BOUNCE = 1;
	static int SOUND_DOH1 = 2;
	static int SOUND_DOH2 = 3;
	static int SOUND_DOH3 = 4;
	static int SOUND_DOH4 = 5;
	static int SOUND_DOH5 = 6;
	static int SOUND_DOH6 = 7;
	static int SOUND_GAMEOVER = 8;
	static int SOUND_LAUGH = 9;
	static int SOUND_LOAD = 10;
	static int SOUND_START = 11;
	static int SOUND_THREAD = 12;
	static int SOUND_WOHOO = 13;
	static HashMap<Integer, Integer> soundPoolMap = new HashMap<Integer, Integer>();

	public static final String PREFS_NAME = "ViperIIPrefrences";
	private static int highscore;

	static void playSound(int id) {
		float streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		float streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = streamVolume / streamMaxVolume;
		soundPool.play(soundPoolMap.get(id), volume, volume, 0, 0, 1);
	}

	static void playSoundLoop(int id) {
		float streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		float streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = streamVolume / streamMaxVolume;
		soundPool.play(soundPoolMap.get(id), volume, volume, 0, -1, 1);
	}

	static void stopSoundLoop(int id) {
		soundPool.stop(soundPoolMap.get(id));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		fadeIn = new AlphaAnimation(0.0f, 1.0f);
		fadeIn.setDuration(400);
		fadeIn.setFillAfter(true);
		fadeIn.setInterpolator(new AccelerateInterpolator());
		fadeOut = new AlphaAnimation(1.0f, 0.0f);
		fadeOut.setDuration(400);
		fadeOut.setFillAfter(true);
		fadeOut.setInterpolator(new AccelerateInterpolator());

		setContentView(R.layout.viper);

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		highscore = settings.getInt("highscore", 0);

		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		AdView adView = (AdView) this.findViewById(R.id.adView);

		AdRequest adRequest = new AdRequest();

		adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
		adRequest.addTestDevice("43423531314B4A554155");
		adView.loadAd(adRequest);

		surfaceView = (SurfaceView) findViewById(R.id.SurfaceView);

		relativeLayoutSurfaceView = (RelativeLayout) findViewById(R.id.RelativeLayoutSurfaceView);
		relativeLayoutMainMenu = (RelativeLayout) findViewById(R.id.RelativeLayoutMainMenu);
		buttonNewGame = (Button) findViewById(R.id.ButtonNewGame);
		buttonNewGame.setOnClickListener(this);
		buttonQuit = (Button) findViewById(R.id.ButtonQuit);
		buttonQuit.setOnClickListener(this);
		textViewTitle = (TextView) findViewById(R.id.TextViewTitle);
		textViewHighscore = (TextView) findViewById(R.id.TextViewHighscore);
		textViewScore = (TextView) findViewById(R.id.TextViewScore);

		textViewHighscore.setText("Highscore: " + getHighscore());

		gameThread = new AndroidGameThread(this, new Handler() {
			@Override
			public void handleMessage(Message m) {
				Bundle data = m.getData();

				if (data.containsKey("state")) {
					int state = data.getInt("state");
					handleStateChange(state);
				} else if (data.containsKey("score")) {
					int score = data.getInt("score");
					handleScoreChange(score);
				}
			}
		});
		surfaceView.getHolder().addCallback(gameThread);

		OnTouchListener onTouchListener = new OnTouchListener(gameThread.handler);
		surfaceView.setOnTouchListener(onTouchListener);
	}

	public void initSound() {
		soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 1);
		soundPoolMap.put(SOUND_BACKGROUND, soundPool.load(this, R.raw.background, 1));
		soundPoolMap.put(SOUND_BOUNCE, soundPool.load(this, R.raw.bounce, 1));
		soundPoolMap.put(SOUND_DOH1, soundPool.load(this, R.raw.doh1, 1));
		soundPoolMap.put(SOUND_DOH2, soundPool.load(this, R.raw.doh2, 1));
		soundPoolMap.put(SOUND_DOH3, soundPool.load(this, R.raw.doh3, 1));
		soundPoolMap.put(SOUND_DOH4, soundPool.load(this, R.raw.doh4, 1));
		soundPoolMap.put(SOUND_DOH5, soundPool.load(this, R.raw.doh5, 1));
		soundPoolMap.put(SOUND_DOH6, soundPool.load(this, R.raw.doh6, 1));
		soundPoolMap.put(SOUND_GAMEOVER, soundPool.load(this, R.raw.gameover, 1));
		soundPoolMap.put(SOUND_LAUGH, soundPool.load(this, R.raw.laugh, 1));
		soundPoolMap.put(SOUND_LOAD, soundPool.load(this, R.raw.load, 1));
		soundPoolMap.put(SOUND_START, soundPool.load(this, R.raw.start, 1));
		soundPoolMap.put(SOUND_THREAD, soundPool.load(this, R.raw.thread, 1));
		soundPoolMap.put(SOUND_WOHOO, soundPool.load(this, R.raw.wohoo, 1));
	}

	@Override
	public void onStart() {
		initSound();

		showMainScreen();

		super.onStart();
	}

	@Override
	public void onResume() {
		initSound();

		super.onResume();
	}

	@Override
	public void onPause() {
		stopSoundLoop(SOUND_BACKGROUND);
		soundPool.release();
		super.onPause();
	}

	@Override
	public void onStop() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("highscore", highscore);
		editor.commit();

		stopSoundLoop(SOUND_BACKGROUND);
		soundPool.release();
		super.onStop();
	}

	@Override
	public void onDestroy() {
		stopSoundLoop(SOUND_BACKGROUND);
		soundPool.release();
		super.onDestroy();
	}

	void handleStateChange(int state) {
		if (state == GameThread.STATE_LOSE || state == GameThread.STATE_WIN) {
			showMainScreen();
		} else if (state == GameThread.STATE_RUNNING) {
			// hideMainScreen();
		}
	};

	void handleScoreChange(int score) {
		textViewScore.setText("Score: " + score);

		if (score > getHighscore()) {
			setHighscore(score);
		}
	};

	public void showMainScreen() {
		textViewHighscore.setText("Highscore: " + getHighscore());

		relativeLayoutMainMenu.startAnimation(fadeIn);
		relativeLayoutMainMenu.setVisibility(View.VISIBLE);
		relativeLayoutMainMenu.setEnabled(true);
		buttonNewGame.setClickable(true);
		buttonQuit.setClickable(true);
		playSoundLoop(SOUND_BACKGROUND);
	}

	public void hideMainScreen() {
		stopSoundLoop(SOUND_BACKGROUND);

		relativeLayoutMainMenu.startAnimation(fadeOut);
		relativeLayoutMainMenu.setVisibility(View.INVISIBLE);
		buttonNewGame.setClickable(false);
		buttonQuit.setClickable(false);
	}

	public void onClick(View v) {
		if (v.getId() == buttonNewGame.getId()) {
			hideMainScreen();

			textViewScore.setText("Score: " + 0);
			gameThread.newGame();
		} else {
			stopSoundLoop(SOUND_BACKGROUND);
			soundPool.release();
			finish();
		}
	}

	public static int getHighscore() {
		return highscore;
	}

	public static void setHighscore(int highscore) {
		ViperActivity.highscore = highscore;
	}
}