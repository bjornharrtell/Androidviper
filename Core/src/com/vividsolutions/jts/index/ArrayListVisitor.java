package com.vividsolutions.jts.index;

import java.util.ArrayList;

/**
 * @version 1.7
 */
public class ArrayListVisitor implements ItemVisitor {

	private ArrayList<Object> items = new ArrayList<Object>();

	public ArrayListVisitor() {
	}

	@Override
	public void visitItem(Object item) {
		items.add(item);
	}

	public ArrayList<Object> getItems() {
		return items;
	}

}