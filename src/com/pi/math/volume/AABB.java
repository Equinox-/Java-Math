package com.pi.math.volume;

import com.pi.math.vector.Vector;
import com.pi.math.vector.VectorND;

public class AABB {
	private Vector min, max;

	public AABB(Vector min, Vector max) {
		this(min.dimension());
		if (min.dimension() != max.dimension())
			throw new IllegalArgumentException("Mismatch dimensions.");
		include(min);
		include(max);
	}

	public AABB(final int dim) {
		final float[] low = new float[dim];
		final float[] high = new float[dim];
		for (int i = 0; i < dim; i++) {
			low[i] = Float.MAX_VALUE;
			high[i] = -Float.MAX_VALUE;
		}
		min = new VectorND(low);
		max = new VectorND(high);
	}

	public Vector getMin() {
		return min;
	}

	public Vector getMax() {
		return max;
	}

	public void include(Vector v) {
		if (v.dimension() != min.dimension())
			throw new IllegalArgumentException("Mismatch dimensions.");
		for (int i = 0; i < v.dimension(); i++) {
			if (min.get(i) > v.get(i))
				min.set(i, v.get(i));
			if (max.get(i) < v.get(i))
				max.set(i, v.get(i));
		}
	}

	public boolean contains(Vector v) {
		if (v.dimension() != min.dimension())
			throw new IllegalArgumentException("Mismatch dimensions.");
		for (int i = 0; i < v.dimension(); i++) {
			if (min.get(i) > v.get(i))
				return false;
			if (max.get(i) < v.get(i))
				return false;
		}
		return true;
	}
}
