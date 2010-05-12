package org.wololo;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Worm {

	enum CollisionType {
		NOCOLLISION, COLLISIONWORM, COLLISIONHOLE
	};

	boolean alive = true;
	int score = 0;
	Paint paint;
	Paint holePaint;

	double velocity = 0.00005;
	double torque = 0.0001;
	double direction;
	double distance = 0;
	double holeDistance = 0;
	boolean hole = false;
	int holes = 0;
	Point lastPosition;
	Point position;

	List<Wormsegment> segments = new ArrayList<Wormsegment>();

	public Worm(Point position, float direction, int color) {
		this.position = position;
		this.direction = direction;

		paint = new Paint();
		paint.setColor(color);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(2);
		// paint.setStyle(Paint.Style.STROKE);
		// paint.setStrokeJoin(Paint.Join.ROUND);
		// paint.setStrokeCap(Paint.Cap.ROUND);

		holePaint = new Paint(paint);
		holePaint.setAlpha(50);
		/*holePaint.setColor(Color.BLUE);
		holePaint.setAntiAlias(true);
		holePaint.setStrokeWidth(2);*/
	}

	/**
	 * Check collision status for a "move"
	 * 
	 * @param line
	 *            Expected to represent a "move" for the worm
	 * @return
	 */
	CollisionType collisionTest(Line line) {
		// TODO implement (got c++ code)

		return CollisionType.NOCOLLISION;
	}

	public void move(long time) {
		lastPosition = position.clone();

		// precalc next move
		boolean wallCollision = true;
		double x = 0, y = 0, distance = 0;
		while (wallCollision) {
			// calc new potential position
			direction += torque * time;
			distance = velocity * time;
			x = position.getX() + distance * Math.cos(direction);
			y = position.getY() + distance * Math.sin(direction);

			// find wall collisions and if true reflect direction and redo the
			// move
			if ((x < 0.0) || (x > 1.0)) {
				direction = Math.PI - direction;
				wallCollision = true;
				break;
			}
			if ((y < 0.0) || (y > 0.95)) {
				direction = -direction;
				wallCollision = true;
				break;
			}

			wallCollision = false;
		}

		// valid move is determined so grow the worm..
		position = new Point(x, y);
		segments.add(new Wormsegment(lastPosition, position, recordHole()));

		// increase speed
		this.distance += distance;
		velocity += 0.00000006f;
	}

	/**
	 * Determine if a hole is to be created and update hole status with each call
	 * @return
	 */
	boolean recordHole() {
		double holeInterval = 0.3f;
		// calc hole length to make it longer as the velocity increases
		double holeLength = holeInterval
				/ (14.0 * (1.0 - ((velocity - 0.00005) * 2500.0)));

		if ((distance > (holeDistance + holeInterval)) && (hole == false)) {
			hole = true;
			holes++;
		}

		if ((distance > (holeDistance + holeInterval + holeLength))
				&& (hole == true)) {
			hole = false;
			holeDistance = distance;
		}

		return hole;
	}

	/**
	 * Draw the complete worm from start
	 * 
	 * TODO: optimize... using Path? cached bitmap? I don't know :(
	 * 
	 * @param canvas
	 */
	public void draw(Canvas canvas) {
		for (Wormsegment wormsegment : segments) {

			int width = canvas.getWidth();
			int height = canvas.getHeight();

			double[] points = wormsegment.asArray();

			float startX = (float) points[0] * width;
			float startY = (float) points[1] * height;
			float stopX = (float) points[2] * width;
			float stopY = (float) points[3] * height;

			// doesn't seem better :/
			/*
			 * Path path = new Path(); path.moveTo(startX, startY);
			 * path.lineTo(stopX, stopY); canvas.drawPath(path, paint);
			 */

			canvas.drawLine(startX, startY, stopX, stopY, wormsegment.isHole() ? holePaint : this.paint);
		}
	}
}
