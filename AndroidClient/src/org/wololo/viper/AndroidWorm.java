package org.wololo.viper;

import org.wololo.viper.GameThread;
import org.wololo.viper.Worm;
import org.wololo.viper.WormSegment;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.vividsolutions.jts.geom.Coordinate;

public class AndroidWorm extends Worm {

	Paint paint;
	Paint holePaint;
	
	public AndroidWorm(Coordinate position, double direction, int color, boolean aiControlled) {
		super(position, direction, color, aiControlled);
		
		paint = new Paint();
		paint.setColor(color);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(2);
		
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
		float startY = (float) (wormSegment.p0.y/GameThread.heightFactor) * (height - 1);
		float stopX = (float) (wormSegment.p1.x) * (width - 1);
		float stopY = (float) (wormSegment.p1.y/GameThread.heightFactor) * (height - 1);

		canvas.drawLine(startX, startY, stopX, stopY, wormSegment.isHole() ? holePaint : this.paint);
	}
}
