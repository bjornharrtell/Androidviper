package org.wololo;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Worm {

	enum CollisionType { NOCOLLISION, COLLISIONWORM, COLLISIONHOLE };
	
	boolean alive = true;
	int score = 0;
	int color = Color.WHITE;
	
	float velocity;
	float torque = 0;
	float direction;
	Point lastPosition;
	Point position;
	
	List<Wormsegment> segments = new ArrayList<Wormsegment>();
	
	public Worm(Point position, float direction, int color) {
		this.position = position;
		this.direction = direction;
		this.color = color;
	}
	
	/**
	 * Check collision status for a "move"
	 * @param line Expected to represent a "move" for the worm
	 * @return
	 */
	CollisionType collisionTest(Line line) {
		// TODO: implement
		
		return CollisionType.NOCOLLISION;
	}
	
	public void move(int timestep) {
		lastPosition = position.clone();
		
		position.move(0.01f, 0.01f);
		
		segments.add(new Wormsegment(lastPosition, position.clone(), false));
	}
	
	public void draw(Canvas canvas) {
		for (Wormsegment wormsegment: segments) {
			Paint paint = new Paint();
			paint.setColor(color);
			paint.setStrokeWidth(1.0f);
			
			int width = canvas.getWidth();
			int height = canvas.getHeight();

			float[] points = wormsegment.asArray();
			
			points[0] = points[0] * width;
			points[1] = points[1] * height;
			points[2] = points[2] * width;
			points[3] = points[3] * height;
			
			canvas.drawLines(points, paint);
		}
	}
}
