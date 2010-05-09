package org.wololo;

public class Point {
	private float x;
	private float y;
	
	public Point(float x, float y) {
		this.setX(x);
		this.setY(y);
	}
	
	public void move(float x, float y) {
		this.setX(x);
		this.setY(y);
	}
	
	public float[] asArray() {
		float[] floats = new float[2];
		
		floats[0] = getX();
		floats[1] = getY();
		
		return floats;
	}

	void setX(float x) {
		this.x = x;
	}

	float getX() {
		return x;
	}

	void setY(float y) {
		this.y = y;
	}

	float getY() {
		return y;
	}
}
