package orc.wololo.viper;

import java.util.ArrayList;
import java.util.List;

public class Worm {

	public static final int NOCOLLISION = 0;
	public static final int COLLISIONWORM = 1;
	public static final int COLLISIONHOLE = 2;

	private boolean alive = true;
	private int score = 0;

	double velocity = 0.00005;
	double torque = 0;
	double direction;
	double distance = 0;
	double holeDistance = 0;
	boolean hole = false;
	int holes = 0;
	Point lastPosition;
	Point position;

	protected List<Wormsegment> segments = new ArrayList<Wormsegment>();

	public Worm(Point position, float direction, int color) {
		this.position = position;
		this.direction = direction;
	}

	public void move(long time) {
		lastPosition = position.clone();

		// precalc next move
		boolean wallCollision = true;
		double x = 0, y = 0, moveDistance = 0;
		while (wallCollision) {
			// calc new potential position
			direction += torque * time;
			moveDistance = velocity * time;
			x = position.getX() + moveDistance * Math.cos(direction);
			y = position.getY() + moveDistance * Math.sin(direction);

			// find wall collisions and if true reflect direction and redo the
			// move
			if ((x < 0) || (x > 1)) {
				direction = Math.PI - direction;
				wallCollision = true;
				continue;
			}
			if ((y < 0) || (y > 1)) {
				direction = -direction;
				wallCollision = true;
				continue;
			}

			wallCollision = false;
		}

		// valid move is determined so grow the worm..
		position = new Point(x, y);
		segments.add(new Wormsegment(lastPosition, position, recordHole()));

		// increase speed
		distance += moveDistance;
		velocity += 0.00000006;
	}

	/**
	 * Determine if a hole is to be created and update hole status with each
	 * call
	 * 
	 * @return
	 */
	boolean recordHole() {
		double holeInterval = 0.2;
		double holeLengthFactor = 6;
		// calc hole length to make it longer as the velocity increases
		// double holeLength = 0.1; //
		double holeLength = holeInterval / (holeLengthFactor * (1.0 - ((velocity - 0.00005) * 3500.0)));

		if ((distance > (holeDistance + holeInterval)) && (hole == false)) {
			hole = true;
			holes++;
		}

		if ((distance > (holeDistance + holeInterval + holeLength)) && (hole == true)) {
			hole = false;
			holeDistance = distance;
		}

		return hole;
	}

	public int collisionTest(Worm worm) {
		Line line = worm.segments.get(worm.segments.size() - 1);
		
		return collisionTest(line);
	}
	
	int collisionTest(Line line) {
		if (segments.size() < 3)
			return NOCOLLISION;

		for (int i = 0; i < segments.size() - 2; i++) {
			Wormsegment segment = segments.get(i);

			if (line.intersects(segment)) {
				if (segment.hole) {
					return COLLISIONHOLE;
				} else {
					return COLLISIONWORM;
				}
			}
		}

		return NOCOLLISION;
	}

	public void setTorque(double torque) {
		this.torque = torque;
	}

	double getTorque() {
		return torque;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getScore() {
		return score;
	}
}
