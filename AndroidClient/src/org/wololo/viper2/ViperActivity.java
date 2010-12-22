package org.wololo.viper2;

import org.wololo.viper.AndroidGameThread;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
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

import com.admob.android.ads.AdView;
import com.admob.android.ads.SimpleAdListener;

public class ViperActivity extends Activity implements OnClickListener {
	SurfaceView surfaceView;
	AdView adView;

	RelativeLayout relativeLayoutMainMenu;
	Button buttonNewGame;
	Button buttonQuit;
	TextView textViewTitle;
	TextView textViewScore;

	AndroidGameThread gameThread;

	AlphaAnimation fadeIn;
	AlphaAnimation fadeOut;
	
	MediaPlayer mediaPlayer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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
		surfaceView = (SurfaceView) findViewById(R.id.SurfaceView01);
		adView = (AdView) findViewById(R.id.AdView);

		relativeLayoutMainMenu = (RelativeLayout) findViewById(R.id.RelativeLayoutMainMenu);
		buttonNewGame = (Button) findViewById(R.id.ButtonNewGame);
		buttonNewGame.setOnClickListener(this);
		buttonQuit = (Button) findViewById(R.id.ButtonQuit);
		buttonQuit.setOnClickListener(this);
		textViewTitle = (TextView) findViewById(R.id.TextViewTitle);
		textViewScore = (TextView) findViewById(R.id.TextViewScore);
		
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

		adView.setAdListener(new SimpleAdListener());

		showMainScreen();
		showAd();
	}

	void handleStateChange(int state) {
		if (state == AndroidGameThread.STATE_LOSE) {
			showMainScreen();
			showAd();
		} else if (state == AndroidGameThread.STATE_RUNNING) {
			//hideMainScreen();
			//hideAd();
		}
	};
	
	void handleScoreChange(int score) {
		textViewScore.setText("Score " + score);
	};

	void showAd() {
		adView.startAnimation(fadeIn);
		adView.setVisibility(View.VISIBLE);
	}

	void hideAd() {
		adView.startAnimation(fadeOut);
		adView.setVisibility(View.INVISIBLE);
	}

	public void showMainScreen() {
		relativeLayoutMainMenu.startAnimation(fadeIn);
		relativeLayoutMainMenu.setVisibility(View.VISIBLE);
		
		mediaPlayer = MediaPlayer.create(this, R.raw.background);
		mediaPlayer.setLooping(true);
		mediaPlayer.start();
	}

	public void hideMainScreen() {
		mediaPlayer.stop();
		
		relativeLayoutMainMenu.startAnimation(fadeOut);
		relativeLayoutMainMenu.setVisibility(View.INVISIBLE);
	}

	public void onClick(View v) {
		if (v.getId() == buttonNewGame.getId()) {
			hideMainScreen();
			hideAd();
			
			gameThread.newGame();
		} else {
			mediaPlayer.stop();
			finish();
		}
	}
}