package org.wololo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;

public class Game extends Activity {
    
	List<Worm> worms = new ArrayList<Worm>();
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        worms.add(new Worm());
        
        // TODO: implement game tick timer loop that advances state and processes input
    }
}