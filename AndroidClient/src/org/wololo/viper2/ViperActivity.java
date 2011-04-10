package org.wololo.viper2;

import org.wololo.viper2.R;

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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ViperActivity extends Activity implements OnClickListener, OnSeekBarChangeListener {
	SurfaceView surfaceView;

	RelativeLayout relativeLayoutMainMenu;
	Button buttonNewGame;
	Button buttonQuit;
	TextView textViewTitle;
	TextView textViewScore;

	SeekBar seekBar;

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
		surfaceView = (SurfaceView) findViewById(R.id.SurfaceView);

		relativeLayoutMainMenu = (RelativeLayout) findViewById(R.id.RelativeLayoutMainMenu);
		buttonNewGame = (Button) findViewById(R.id.ButtonNewGame);
		buttonNewGame.setOnClickListener(this);
		buttonQuit = (Button) findViewById(R.id.ButtonQuit);
		buttonQuit.setOnClickListener(this);
		textViewTitle = (TextView) findViewById(R.id.TextViewTitle);
		textViewScore = (TextView) findViewById(R.id.TextViewScore);

		seekBar = (SeekBar) findViewById(R.id.seekBar);
		seekBar.setVisibility(View.GONE);
		seekBar.setOnSeekBarChangeListener(this);

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

		showMainScreen();
	}

	void handleStateChange(int state) {
		if (state == AndroidGameThread.STATE_LOSE) {
			showMainScreen();
		} else if (state == AndroidGameThread.STATE_RUNNING) {
			// hideMainScreen();
		}
	};

	void handleScoreChange(int score) {
		textViewScore.setText("Score: " + score);
	};

	public void showMainScreen() {
		relativeLayoutMainMenu.startAnimation(fadeIn);
		relativeLayoutMainMenu.setVisibility(View.VISIBLE);
		seekBar.setVisibility(View.GONE);

		mediaPlayer = MediaPlayer.create(this, R.raw.background);
		mediaPlayer.setLooping(true);
		mediaPlayer.start();
	}

	public void hideMainScreen() {
		mediaPlayer.stop();

		relativeLayoutMainMenu.startAnimation(fadeOut);
		relativeLayoutMainMenu.setVisibility(View.INVISIBLE);
		seekBar.setVisibility(View.VISIBLE);
	}

	public void onClick(View v) {
		if (v.getId() == buttonNewGame.getId()) {
			hideMainScreen();

			textViewScore.setText("Score: " + 0);
			seekBar.setProgress(50);
			gameThread.newGame();
		} else {
			mediaPlayer.stop();
			finish();
		}
	}

	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		float torque = (arg1 - 50) / 20000.0f;

		gameThread.changeTorque(torque);
	}

	public void onStartTrackingTouch(SeekBar arg0) {
	}

	public void onStopTrackingTouch(SeekBar arg0) {
	}
}