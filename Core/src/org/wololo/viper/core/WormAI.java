package org.wololo.viper.core;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

public class WormAI {
	Worm worm;
	
	long random = 0;

	public WormAI(Worm worm) {
		this.worm = worm;
	}
	
	public void decideTorque(List<Worm> worms) {
		long now = System.currentTimeMillis();
		
		Coordinate newCoordinate = worm.predictMove(1500);

		WormSegment wormSegment = new WormSegment(worm.coordinate, newCoordinate, false);

		int result = Worm.NOCOLLISION;
		for (Worm otherWorm : worms) {
			result = otherWorm.collisionTest(wormSegment);

			if (result == Worm.COLLISIONWORM)
				break;
		}
		
		if (result == Worm.NOCOLLISION && random > now) {
			return;
		} else {
			worm.torque = 0.0f;
		}
		
		if (result == Worm.NOCOLLISION && Math.random()>0.95f) {
			result = Worm.COLLISIONWORM;
			random = now + 500;
		}

		if (result == Worm.COLLISIONWORM) {
			if (worm.torque == 0.0f) {
				worm.torque = Math.random() > 0.5f ? 0.0015f + (Math.random() * 0.001f) : -0.0015f
						+ (Math.random() * 0.001f);
			}
		} else {
			worm.torque = 0.0f;
		}
	}
}
