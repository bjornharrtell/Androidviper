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

public class Game extends Activity implements AdListener {
    
	static final int MENU_NEW_GAME1 = 0;
	static final int MENU_NEW_GAME2 = 1;
	static final int MENU_QUIT = 2;
	
	GameView gameView;
	AdView ad;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.game);
        
        gameView = (GameView) findViewById(R.id.GameView01);
        
        ad = (AdView) findViewById(R.id.ad);
        ad.setAdListener(new LunarLanderListener());
    }
    
    private class LunarLanderListener extends SimpleAdListener
    {
		@Override
		public void onFailedToReceiveAd(AdView adView)
		{
			super.onFailedToReceiveAd(adView);
		}

		@Override
		public void onFailedToReceiveRefreshedAd(AdView adView)
		{
			super.onFailedToReceiveRefreshedAd(adView);
		}

		@Override
		public void onReceiveAd(AdView adView)
		{
			super.onReceiveAd(adView);
		}

		@Override
		public void onReceiveRefreshedAd(AdView adView)
		{
			super.onReceiveRefreshedAd(adView);
		}
    	
    }

	public void onFailedToReceiveAd(AdView adView)
	{
	}

	public void onFailedToReceiveRefreshedAd(AdView adView)
	{
	}

	public void onReceiveAd(AdView adView)
	{
	}

	public void onReceiveRefreshedAd(AdView adView)
	{
	}
    
    /* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_NEW_GAME1, 0, "Start game");
        //menu.add(0, MENU_NEW_GAME2, 0, "Start 2 players game");
        menu.add(0, MENU_QUIT, 0, "Quit");
        return true;
    }

    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_NEW_GAME1:
        	gameView.gameThread.setState(GameThread.STATE_READY);
            gameView.gameThread.newGame(ad);
            return true;
        case MENU_NEW_GAME2:
        	gameView.gameThread.setState(GameThread.STATE_READY);
            gameView.gameThread.newGame(ad);
            return true;
        case MENU_QUIT:
            gameView.gameThread.setRunning(false);
            return true;
        }
        return false;
    }
}