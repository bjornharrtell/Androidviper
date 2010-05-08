package org.wololo;

public class Wormsegment extends Line {

	boolean hole = false;
	
	public Wormsegment(Point point1, Point point2, boolean hole) {
		super(point1, point2);
		
		this.hole = hole;
	}
	
	public boolean isHole() {
		return hole;
	}

}
