package orc.wololo.viper;

public class Point {
	
	double x;
	double y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Point clone() {
		return new Point(x, y);
	}

	public void move(double x, double y) {
		this.x = this.x + x;
		this.y = this.y + y;
	}

	public double[] asArray() {
		double[] doubles = new double[2];

		doubles[0] = x;
		doubles[1] = y;

		return doubles;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getX() {
		return x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getY() {
		return y;
	}

}
