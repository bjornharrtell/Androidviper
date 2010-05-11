package org.wololo;

public class Point {
	
	float x;
	float y;

	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void move(float x, float y) {
		this.x = this.x + x;
		this.y = this.y + y;
	}

	public float[] asArray() {
		float[] floats = new float[2];

		floats[0] = x;
		floats[1] = y;

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
