package org.wololo.viper;

import org.wololo.viper.R;

import com.admob.android.ads.AdListener;
import com.admob.android.ads.AdView;
import com.admob.android.ads.SimpleAdListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

public class Game extends Activity implements AdListener {

	static final int MENU_NEW_GAME1 = 0;
	static final int MENU_NEW_GAME2 = 1;
	static final int MENU_QUIT = 2;

	GameView gameView;
	AdView adView;
	TextView textView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.game);

		gameView = (GameView) findViewById(R.id.GameView01);
		textView = (TextView) findViewById(R.id.TextView01);
		textView.setText("Viper 1.5.0 (c) 2010 Björn Harrtell\nPress MENU");
		adView = (AdView) findViewById(R.id.ad);
		adView.setAdListener(new ViperListener());

		gameView.setAdView(adView);
		gameView.setTextView(textView);
	}

	private class ViperListener extends SimpleAdListener {
		@Override
		public void onFailedToReceiveAd(AdView adView) {
			super.onFailedToReceiveAd(adView);
		}

		@Override
		public void onFailedToReceiveRefreshedAd(AdView adView) {
			super.onFailedToReceiveRefreshedAd(adView);
		}

		@Override
		public void onReceiveAd(AdView adView) {
			super.onReceiveAd(adView);
		}

		@Override
		public void onReceiveRefreshedAd(AdView adView) {
			super.onReceiveRefreshedAd(adView);
		}

	}

	public void onFailedToReceiveAd(AdView adView) {
	}

	public void onFailedToReceiveRefreshedAd(AdView adView) {
	}

	public void onReceiveAd(AdView adView) {
	}

	public void onReceiveRefreshedAd(AdView adView) {
	}

	/* Creates the menu items */
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_NEW_GAME1, 0, "Start game");
		// menu.add(0, MENU_NEW_GAME2, 0, "Start 2 players game");
		// menu.add(0, MENU_QUIT, 0, "Quit");
		return true;
	}

	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_NEW_GAME1:
			gameView.gameThread.setState(GameThread.STATE_READY);
			gameView.gameThread.newGame(adView);
			return true;
		/*case MENU_NEW_GAME2:
			gameView.gameThread.setState(GameThread.STATE_READY);
			gameView.gameThread.newGame(adView);
			return true;*/
		}
		return false;
	}
}