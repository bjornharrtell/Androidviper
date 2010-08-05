package org.wololo.viper;

import android.graphics.Paint;
import android.graphics.Canvas;
import orc.wololo.viper.Point;
import orc.wololo.viper.Worm;
import orc.wololo.viper.Wormsegment;

public class AndroidWorm extends Worm {

	Paint paint;
	Paint holePaint;
	
	public AndroidWorm(Point position, float direction, int color) {
		super(position, direction, color);
		
		paint = new Paint();
		paint.setColor(color);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(2);
		
		holePaint = new Paint(paint);
		holePaint.setAlpha(50);
	}

	/**
	 * Draw the last moved segment of the worm
	 * 
	 * @param canvas
	 *            The Canvas to draw on
	 */
	public void draw(Canvas canvas) {
		if (segments.size() < 1)
			return;

		Wormsegment wormsegment = segments.get(segments.size() - 1);

		int width = canvas.getWidth();
		int height = canvas.getHeight();

		double[] points = wormsegment.asArray();

		float startX = (float) points[0] * (width - 1);
		float startY = (float) points[1] * (height - 1);
		float stopX = (float) points[2] * (width - 1);
		float stopY = (float) points[3] * (height - 1);

		canvas.drawLine(startX, startY, stopX, stopY, wormsegment.isHole() ? holePaint : this.paint);
	}
}
