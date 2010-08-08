package orc.wololo.viper;

public class Line {
	Point start;
	Point stop;
	
	boolean horisontal, vertical;
	double k,m;
	
	public Line(Point start, Point stop) {
		this.start = start;
		this.stop = stop;
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
	
	void calcConstants() {
		horisontal = false;
		vertical = false;

		if (stop.y - start.y == 0) {
			horisontal = true;
			k = 0;
			m = stop.y;
			return;
		}

		if (stop.x - start.x == 0) {
			vertical = true;
			return;
		}
		else {
			k = (stop.y - start.y) / (stop.x - start.x);
			m = start.y - (k * start.x);
		}		
	}
	
	boolean intersects(Line line) {
		calcConstants();
		line.calcConstants();

		double x, y;

		if (vertical && line.vertical) {
			return false;
		}
		else if (vertical) {
			x = start.x;
			y = (x * line.k) + line.m;
		}
		else if (line.vertical) {
			x = line.start.x;
			y = (x * k) + m;
		}
		else if (horisontal && line.horisontal)	{
			return false;
		}
		else if (horisontal) {	
			y = start.y;
			x = (y - line.m) / line.k;
		}
		else {
			x = (m-line.m) / (line.k-k);
			y = ( (k*line.m) - (line.k*m) ) / (k-line.k);
		}
	
		if ( within(x,y) && line.within(x,y) ) {
			return true;
		}
		else {
			return false;
		}
	}
	
	boolean within(double x, double y) {
		boolean result = false;

		double min, max;

		min = start.x;
		max = stop.x;

		if (min > max) {
			double tmp = min;
			min = max;
			max = tmp;
		}

		if (x > min && x < max) result = true;

		min = this.start.y;
		max = this.stop.y;

		if (min > max) {
			double tmp = min;
			min = max;
			max = tmp;
		}

		if (y > min && y < max) result = result && true;

		return result;
	}
}
