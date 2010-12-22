package org.wololo.viper;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

public class GameThread extends Thread {
	public static final int STATE_UNINITIALIZED = 0;
	public static final int STATE_LOSE = 1;
	public static final int STATE_PAUSE = 2;
	public static final int STATE_READY = 3;
	public static final int STATE_RUNNING = 4;
	public static final int STATE_DEMO = 6;
	public static final int STATE_WIN = 5;

	public static float heightFactor = 1.0f;

	protected List<Worm> worms = new ArrayList<Worm>();

	protected int state = STATE_UNINITIALIZED;
	long lastTime;

	protected void setState(int state) {
		this.state = state;
	}

	public void newGame(List<Worm> worms) {
		this.worms = worms;

		// make physics calc start in about 1000 ms
		lastTime = System.currentTimeMillis() + 1000;

		setState(STATE_READY);
	};

	public Coordinate getRandomStartCoordinate() {
		double x = (Math.random() * 0.6) + 0.2;
		double y = ((Math.random() * 0.6) + 0.2) * heightFactor;
		Coordinate coordinate = new Coordinate(x, y);

		return coordinate;
	}

	public double getRandomStartDirection() {
		double direction = (Math.random() - 0.5) * 2.0 * Math.PI;

		return direction;
	}

	protected void timestep() {
		long now = System.currentTimeMillis();

		if (lastTime > now)
			return;
		
		long elapsed = now - lastTime;

		for (Worm worm : worms) {
			if (worm.alive) {
				if (worm.move(elapsed) == Worm.MOVEBOUNCE) {
					onBounce();
				}
				collisionTest(worm);
			} else if (worms.get(0) == worm) {
				setState(STATE_LOSE);
			}

			if (worm.aiControlled) {
				worm.wormAI.decideTorque(worms);
			}
		}

		lastTime = now;
	}

	void collisionTest(Worm worm) {
		for (Worm otherWorm : worms) {
			int result = otherWorm.collisionTest(worm);

			if (result == Worm.COLLISIONWORM) {
				worm.alive = false;
				onDeath();
			} else if (result == Worm.COLLISIONHOLE) {
				onScore(++worm.score);
			}
		}
	}

	protected void onScore(int score) {

	}
	
	protected void onDeath() {
		
	}
	
	protected void onBounce() {
		
	}
}
