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
	
	public double[] asArray() {
		double[] doubles = new double[4];
		
		doubles[0] = start.x;
		doubles[1] = start.y;
		doubles[2] = stop.x;
		doubles[3] = stop.y;
		
		return doubles;
	}

	public Point getStart() {
		return start;
	}

	public Point getStop() {
		return stop;
	}
}
