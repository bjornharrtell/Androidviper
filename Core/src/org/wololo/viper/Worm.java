package org.wololo.viper;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.simplify.LineSegmentIndex;

public class Worm {

	public static final int NOCOLLISION = 0;
	public static final int COLLISIONWORM = 1;
	public static final int COLLISIONHOLE = 2;
	
	public static final int MOVENORMAL = 0;
	public static final int MOVEBOUNCE = 1;

	boolean aiControlled = false;
	
	public boolean alive = true;
	int score = 0;

	double velocity = 0.00005f;
	public double torque = 0.0f;
	double direction = 0.0f;
	double distance = 0.0f;
	double holeDistance = 0.0f;
	boolean hole = false;
	int holes = 0;
	Coordinate previousCoordinate = new Coordinate();
	Coordinate coordinate;

	WormSegment[] wormSegmentCache = new WormSegment[3];
	LineSegmentIndex lineSegmentIndex = new LineSegmentIndex();
	
	WormAI wormAI;

	protected int wormSegments = 0;

	public Worm(Coordinate coordinate, double direction, int color, boolean aiControlled) {
		this.coordinate = coordinate;
		this.direction = direction;
		this.aiControlled = aiControlled;
		
		if (aiControlled) {
			wormAI = new WormAI(this);
		}
	}

	public Coordinate predictMove(long time) {
		Coordinate newCoordinate = new Coordinate();

		double direction = this.direction + (torque * time);
		double moveDistance = velocity * time;
		newCoordinate.x = coordinate.x + moveDistance * Math.cos(direction);
		newCoordinate.y = coordinate.y + moveDistance * Math.sin(direction);

		return newCoordinate;
	}

	public int move(long time) {

		direction += torque * time;

		// precalc next move
		boolean wallCollision = true;
		boolean hasCollided = false;
		double x = 0.0f, y = 0.0f, moveDistance = 0.0f;
		while (wallCollision) {
			// calc new potential position

			moveDistance = velocity * time;
			x = coordinate.x + moveDistance * Math.cos(direction);
			y = coordinate.y + moveDistance * Math.sin(direction);

			// find wall collisions and if true reflect direction and redo the
			// move
			if ((x < 0.0f) || (x > 1.0f)) {
				direction = Math.PI - direction;
				wallCollision = true;
				hasCollided = true;
				continue;
			}
			if ((y < 0.0f) || (y > GameThread.heightFactor)) {
				direction = -direction;
				wallCollision = true;
				hasCollided = true;
				continue;
			}

			wallCollision = false;
		}

		// valid move is determined so grow the worm, storing two previous moves
		// used for collissiondetection
		previousCoordinate.x = coordinate.x;
		previousCoordinate.y = coordinate.y;
		coordinate.x = x;
		coordinate.y = y;
		if (wormSegmentCache[1] != null)
			wormSegmentCache[2] = wormSegmentCache[1];
		if (wormSegmentCache[0] != null)
			wormSegmentCache[1] = wormSegmentCache[0];
		wormSegmentCache[0] = new WormSegment(previousCoordinate.x, previousCoordinate.y, coordinate.x, coordinate.y,
				recordHole());
		if (wormSegmentCache[2] != null)
			lineSegmentIndex.add(wormSegmentCache[2]);
		wormSegments++;

		// increase speed
		distance += moveDistance;
		velocity += 0.00000006;
		
		if (hasCollided) {
			return MOVEBOUNCE;
		} else {
			return MOVENORMAL;
		}
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
		return collisionTest(worm.wormSegmentCache[0]);
	}

	public int collisionTest(WormSegment wormSegment) {
		if (wormSegments < 3)
			return NOCOLLISION;

		List<?> intersectingLineSegments = lineSegmentIndex.query(wormSegment);

		if (intersectingLineSegments.isEmpty()) {
			return NOCOLLISION;
		}

		WormSegment intersectingWormSegment = (WormSegment) intersectingLineSegments.get(0);

		if (intersectingWormSegment.hole) {
			return COLLISIONHOLE;
		} else {
			return COLLISIONWORM;
		}
	}

	public WormSegment getCurrentWormSegment() {
		return wormSegmentCache[0];
	}
}
