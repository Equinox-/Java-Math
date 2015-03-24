package com.pi.math;

import java.util.ArrayList;
import java.util.Iterator;

import com.pi.math.vector.Positionable;
import com.pi.math.vector.Vector;
import com.pi.math.vector.VectorBuff3;
import com.pi.math.volume.BoundingArea;

public class DimensionalTree<E extends Positionable<VectorBuff3>> {
	final BoundingArea area;
	private final int maxElements;
	ArrayList<E> elements;
	DimensionalTree<E>[] subTree = null;

	public DimensionalTree(BoundingArea base, int maxE) {
		this.area = base;
		this.maxElements = maxE;
		this.elements = new ArrayList<>(maxE);
	}

	public void insert(E item) {
		if (subTree == null) {
			if (this.elements.size() < maxElements) {
				// We are a leaf, and don't have an item
				this.elements.add(item);
				return;
			} else {
				// We are a leaf, we have an item. Need to subdivide and
				// redistribute the items
				divide();
			}
		}
		// We aren't a leaf. Decide on the tree, and map it out.
		Vector pos = item.position();
		int id = 0;
		for (int k = 0; k < area.getCenter().dimension(); k++) {
			id = (id << 1) | (pos.get(k) < area.getCenter().get(k) ? 1 : 0);
		}
		subTree[id].insert(item);
	}

	public Iterable<E> getContents() {
		return getContents(null);
	}

	public Iterable<E> getContents(Filter<BoundingArea> filter) {
		return new Iterable<E>() {
			@Override
			public Iterator<E> iterator() {
				return new Iterator<E>() {
					private E next = nextInternal();

					private boolean illegal = !filter.accept(area);
					private int head = 0;
					private Iterator<E> currentItr;

					private E nextInternal() {
						if (illegal)
							return null;
						if (elements != null) {
							if (currentItr == null)
								currentItr = elements.iterator();
							if (currentItr.hasNext())
								return currentItr.next();
							return null;
						} else {
							while (currentItr == null || !currentItr.hasNext()) {
								if (head >= subTree.length)
									return null;
								currentItr = subTree[head++]
										.getContents(filter).iterator();
							}
							return currentItr.next();
						}
					}

					@Override
					public boolean hasNext() {
						return next != null;
					}

					@Override
					public E next() {
						E tmp = next;
						next = nextInternal();
						return tmp;
					}
				};
			}
		};
	}

	@SuppressWarnings("unchecked")
	private void divide() {
		subTree = new DimensionalTree[1 << area.getCenter().dimension()];

		for (int k = 0; k < subTree.length; k++) {
			VectorBuff3 other = new VectorBuff3();
			other.set(area.getMax());
			for (int c = 0; c < other.dimension(); c++) {
				final int mask = 1 << (other.dimension() - c - 1);
				if ((k & mask) == mask) {
					other.set(c, area.getMin().get(c));
				}
			}
			subTree[k] = new DimensionalTree<>(new BoundingArea(other,
					area.getCenter()), maxElements);
		}

		ArrayList<E> prev = this.elements;
		this.elements = null;
		for (E e : prev)
			insert(e);
	}
}
