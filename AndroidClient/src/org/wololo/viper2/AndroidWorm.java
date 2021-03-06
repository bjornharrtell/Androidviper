package org.wololo.viper2;

import org.wololo.viper.core.Worm;
import org.wololo.viper.core.WormSegment;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.vividsolutions.jts.geom.Coordinate;

public class AndroidWorm extends Worm {

	Paint paint;
	Paint holePaint;

	public AndroidWorm(AndroidGameThread gameThread, Coordinate position, double direction, int color,
			boolean aiControlled) {
		super(gameThread, position, direction, color, aiControlled);

		paint = new Paint();
		paint.setColor(color);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(gameThread.pix*2);

		holePaint = new Paint(paint);
		holePaint.setAlpha(75);
	}

	/**
	 * Draw the last moved segment of the worm
	 * 
	 * @param canvas
	 *            The Canvas to draw on
	 */
	public void draw(Canvas canvas) {
		if (wormSegments < 1)
			return;

		int width = canvas.getWidth();
		int height = canvas.getHeight();

		WormSegment wormSegment = getCurrentWormSegment();

		float startX = (float) (wormSegment.p0.x) * (width - 1);
		float startY = (float) (wormSegment.p0.y / gameThread.heightFactor) * (height - 1);
		float stopX = (float) (wormSegment.p1.x) * (width - 1);
		float stopY = (float) (wormSegment.p1.y / gameThread.heightFactor) * (height - 1);

		canvas.drawLine(startX, startY, stopX, stopY, wormSegment.isHole() ? holePaint : paint);
	}
}
