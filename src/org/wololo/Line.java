package org.wololo;

public class Line {
	private Point start;
	private Point stop;
	
	public Line(Point start, Point stop) {
		this.start = start;
		this.stop = stop;
	}
	
	public boolean intersects(Line line) {
		// TODO implement (got c++ code)
		
		return true;
	}
	
	public float[] asArray() {
		float[] floats = new float[4];
		
		floats[0] = start.asArray()[0];
		floats[1] = start.asArray()[1];
		floats[2] = stop.asArray()[0];
		floats[3] = stop.asArray()[1];
		
		return floats;
	}

	public Point getStart() {
		return start;
	}

	public Point getStop() {
		return stop;
	}
}
