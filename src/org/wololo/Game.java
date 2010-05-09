package org.wololo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceView;

public class Game extends Activity {
    
	Handler handler = new Handler();
	
	List<Worm> worms = new ArrayList<Worm>();
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.SurfaceView01);
        
        worms.add(new Worm());

        Runnable runnable = new Runnable() {
			public void run() {
				
				// TODO: process input
				
				for (Worm worm : worms) {
					worm.move(30);
				}
			}
        };
        
        handler.postDelayed(runnable, 30);
    }
}