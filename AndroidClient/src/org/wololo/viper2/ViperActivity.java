package org.wololo.viper2;

import org.wololo.viper.core.GameThread;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ViperActivity extends Activity implements OnClickListener {
	AdView adView;
	
	View menu;
	View game;
	
	ViperView viperView;
	
	Button buttonNewGame;
	Button buttonQuit;
	TextView textViewTitle;
	TextView textViewScore;
	TextView textViewHighscore;

	AlphaAnimation fadeIn;
	AlphaAnimation fadeOut;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		System.out.println("onCreate");

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		fadeIn = new AlphaAnimation(0.0f, 1.0f);
		fadeIn.setDuration(400);
		fadeIn.setFillAfter(true);
		fadeIn.setInterpolator(new AccelerateInterpolator());
		fadeOut = new AlphaAnimation(1.0f, 0.0f);
		fadeOut.setDuration(400);
		fadeOut.setFillAfter(true);
		fadeOut.setInterpolator(new AccelerateInterpolator());

		setContentView(R.layout.viper);

		adView = (AdView) this.findViewById(R.id.adView);
		AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
		adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
		adRequestBuilder.addTestDevice("788E31F82926400BC2D98E7633646348");
		adView.loadAd(adRequestBuilder.build());

		menu = findViewById(R.id.Menu);
		game = findViewById(R.id.Game);
		
		viperView = (ViperView) findViewById(R.id.ViperView);
		viperView.activity = this;
		
		buttonNewGame = (Button) findViewById(R.id.ButtonNewGame);
		buttonNewGame.setOnClickListener(this);
		buttonQuit = (Button) findViewById(R.id.ButtonQuit);
		buttonQuit.setOnClickListener(this);
		textViewTitle = (TextView) findViewById(R.id.TextViewTitle);
		textViewHighscore = (TextView) findViewById(R.id.TextViewHighscore);
		textViewScore = (TextView) findViewById(R.id.TextViewScore);
		
		OnTouchListener onTouchListener = new OnTouchListener(viperView.thread);
		viperView.setOnTouchListener(onTouchListener);
		
		textViewHighscore.setText("Highscore: " + viperView.thread.highscore);
		
		showMainScreen();
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.v(toString(), "onStart called");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.v(toString(), "onResume called");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.v(toString(), "onPause called");
		if (viperView.thread.state == GameThread.STATE_RUNNING) {
			textViewScore.setText("Game paused, touch screen to resume");
			viperView.thread.pause();
		}
			
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.v(toString(), "onStop called");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.v(toString(), "onDestroy called");
		viperView.thread.sound.soundPool.release();
	}

	void handleStateChange(int state) {
		if (state == GameThread.STATE_LOSE || state == GameThread.STATE_WIN) {
			showMainScreen();
		} else if (state == GameThread.STATE_RUNNING) {
			textViewScore.setText("Score: " + viperView.thread.worms.get(0).score);
		}
	};

	void handleScoreChange(int score) {
		textViewScore.setText("Score: " + score);
	};

	public void showMainScreen() {
		textViewHighscore.setText("Highscore: " + viperView.thread.highscore);
		
		menu.startAnimation(fadeIn);
		menu.setVisibility(View.VISIBLE);
		menu.setEnabled(true);
		
		buttonNewGame.setClickable(true);
		buttonQuit.setClickable(true);
	}

	public void hideMainScreen() {
		menu.startAnimation(fadeOut);
		menu.setVisibility(View.INVISIBLE);
		
		buttonNewGame.setClickable(false);
		buttonQuit.setClickable(false);
	}

	public void onClick(View v) {
		if (v.getId() == buttonNewGame.getId()) {
			hideMainScreen();

			textViewScore.setText("Score: " + 0);
			viperView.thread.newGame();
		} else {
			finish();
		}
	}
}