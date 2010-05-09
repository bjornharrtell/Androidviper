package org.wololo;

import android.app.Activity;
import android.os.Bundle;

public class Game extends Activity {
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        GameView gameView = (GameView) findViewById(R.id.GameView01);
        GameThread gameThread = gameView.getGameThread();
        
        gameThread.setState(GameThread.STATE_READY);
        
        gameThread.start();
    }
}