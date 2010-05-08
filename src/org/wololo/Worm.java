package org.wololo;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;

public class Worm {

	enum CollisionType { NOCOLLISION, COLLISIONWORM, COLLISIONHOLE };
	
	boolean alive = true;
	int score = 0;
	Color color;
	
	double velocity;
	double torque = 0;
	double direction;
	
	List<Wormsegment> segments = new ArrayList<Wormsegment>();
	
	public Worm() {
	
	}
	
	/**
	 * Check collision status for a "move"
	 * @param line Expected to represent a "move" for the worm
	 * @return
	 */
	CollisionType collisionTest(Line line) {
		// implement
		
		return CollisionType.NOCOLLISION;
	}
}
