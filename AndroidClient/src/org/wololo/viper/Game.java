package org.wololo.viper;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.admob.android.ads.AdView;
import com.admob.android.ads.SimpleAdListener;

public class Game extends Activity {

	static final int MENU_NEW_GAME1 = 0;
	static final int MENU_NEW_GAME2 = 1;
	static final int MENU_QUIT = 2;

	SurfaceView surfaceView;
	AdView adView;
	TextView textView;

	GameThread gameThread;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		initLayout();

		gameThread = new GameThread(this, new Handler() {
			@Override
			public void handleMessage(Message m) {
				Bundle data = m.getData();

				int state = data.getInt("state");

				if (state == GameThread.STATE_LOSE) {
					showMainScreen("Game over");
				}
			}});
		surfaceView.getHolder().addCallback(gameThread);

		adView.setAdListener(new SimpleAdListener());
		
		showMainScreen("Viper 1.6.0 (c) 2010 Björn Harrtell\nPress MENU");
	}

	void initLayout() {
		FrameLayout.LayoutParams frameLayoutParams;
		RelativeLayout.LayoutParams relativeLayoutParams;

		FrameLayout frameLayout = new FrameLayout(this);

		surfaceView = new SurfaceView(this);
		frameLayoutParams = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		frameLayout.addView(surfaceView, frameLayoutParams);

		RelativeLayout relativeLayout = new RelativeLayout(this);
		relativeLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		frameLayout.addView(relativeLayout, relativeLayoutParams);
		textView = new TextView(this);
		relativeLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		relativeLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		relativeLayout.addView(textView, relativeLayoutParams);

		relativeLayout = new RelativeLayout(this);
		relativeLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		frameLayout.addView(relativeLayout, relativeLayoutParams);
		adView = new AdView(this);
		relativeLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		relativeLayout.addView(adView, relativeLayoutParams);

		setContentView(frameLayout);
	}

	void showAd() {
		adView.setVisibility(View.VISIBLE);

		// The ad will fade in over 0.4 seconds.
		AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(400);
		animation.setFillAfter(true);
		animation.setInterpolator(new AccelerateInterpolator());
		adView.startAnimation(animation);
	}
	
	void hideAd() {
		adView.setVisibility(View.GONE);
		textView.setVisibility(View.GONE);
	}
	
	public void showMainScreen(String text) {
		adView.setVisibility(View.VISIBLE);
		textView.setBackgroundColor(Color.DKGRAY);
		textView.setText(text);
		textView.setVisibility(View.VISIBLE);
	}
	
	public void hideMainScreen() {
		adView.setVisibility(View.GONE);
		textView.setVisibility(View.GONE);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_NEW_GAME1, 0, "Start game");
		// menu.add(0, MENU_NEW_GAME2, 0, "Start 2 players game");
		// menu.add(0, MENU_QUIT, 0, "Quit");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_NEW_GAME1:
			gameThread.setState(GameThread.STATE_READY);
			gameThread.newGame(adView);
			return true;
			/*
			 * case MENU_NEW_GAME2:
			 * gameView.gameThread.setState(GameThread.STATE_READY);
			 * gameView.gameThread.newGame(adView); return true;
			 */
		}
		return false;
	}
}