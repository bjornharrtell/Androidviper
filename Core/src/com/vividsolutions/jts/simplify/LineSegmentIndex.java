package com.vividsolutions.jts.simplify;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.index.ItemVisitor;
import com.vividsolutions.jts.index.quadtree.Quadtree;

/**
 * An spatial index on a set of {@link LineSegment}s. Supports adding and
 * removing items.
 * 
 * @author Martin Davis
 */
public class LineSegmentIndex {
	private Quadtree index = new Quadtree();

	public LineSegmentIndex() {
	}

	public void add(LineSegment seg) {
		index.insert(new Envelope(seg.p0, seg.p1), seg);
	}

	public void remove(LineSegment seg) {
		index.remove(new Envelope(seg.p0, seg.p1), seg);
	}

	public List query(LineSegment querySeg) {
		Envelope env = new Envelope(querySeg.p0, querySeg.p1);

		LineSegmentVisitor visitor = new LineSegmentVisitor(querySeg);
		index.query(env, visitor);
		List itemsFound = visitor.getItems();

		return itemsFound;
	}
}

/**
 * ItemVisitor subclass to reduce volume of query results.
 */
class LineSegmentVisitor implements ItemVisitor {
	// MD - only seems to make about a 10% difference in overall time.

	private LineSegment querySeg;
	private ArrayList items = new ArrayList();

	public LineSegmentVisitor(LineSegment querySeg) {
		this.querySeg = querySeg;
	}

	public void visitItem(Object item) {
		LineSegment seg = (LineSegment) item;
		if (Envelope.intersects(seg.p0, seg.p1, querySeg.p0, querySeg.p1))
			items.add(item);
	}

	public ArrayList getItems() {
		return items;
	}
}
