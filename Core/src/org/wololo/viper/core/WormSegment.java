package org.wololo.viper.core;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

public class WormSegment extends LineSegment {

	private static final long serialVersionUID = -1497616774188024818L;
	
	boolean hole = false;

	public WormSegment(Coordinate c1, Coordinate c2, boolean hole) {
		super(c1, c2);
		
		this.hole = hole;
	}
	
	public WormSegment(double x0, double y0, double x1, double y1,boolean hole) {
		super(x0, y0, x1, y1);
		
		this.hole = hole;
	}
	
	public boolean isHole() {
		return hole;
	}
}
