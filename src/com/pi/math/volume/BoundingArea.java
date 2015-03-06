package com.pi.math.volume;

import com.pi.math.MathUtil;
import com.pi.math.vector.Vector;
import com.pi.math.vector.VectorND;

public class BoundingArea {
	private Vector min, max;
	private Vector center;
	private float radius;

	public BoundingArea(Vector min, Vector max) {
		this(min.dimension());
		if (min.dimension() != max.dimension())
			throw new IllegalArgumentException("Mismatch dimensions.");
		include(min);
		include(max);
	}

	public BoundingArea(final int dim) {
		final float[] low = new float[dim];
		final float[] high = new float[dim];
		for (int i = 0; i < dim; i++) {
			low[i] = Float.MAX_VALUE;
			high[i] = -Float.MAX_VALUE;
		}
		min = new VectorND(low);
		max = new VectorND(high);
		center = new VectorND(new float[dim]);
	}

	public void reset() {
		int dim = min.dimension();
		final float[] low = new float[dim];
		final float[] high = new float[dim];
		for (int i = 0; i < dim; i++) {
			low[i] = Float.MAX_VALUE;
			high[i] = -Float.MAX_VALUE;
		}
		min.setV(low);
		max.setV(high);
		center.multiply(0);
	}

	public Vector getMin() {
		return min;
	}

	public Vector getMax() {
		return max;
	}

	public Vector getCenter() {
		return center;
	}

	public float getRadius() {
		return radius;
	}

	public void include(Vector v) {
		if (v.dimension() != min.dimension())
			throw new IllegalArgumentException("Mismatched dimensions.");
		for (int i = 0; i < v.dimension(); i++) {
			if (min.get(i) > v.get(i))
				min.set(i, v.get(i));
			if (max.get(i) < v.get(i))
				max.set(i, v.get(i));
		}
		center.set(min);
		center.linearComb(-.5f, max, .5f);
		radius = center.dist(min);
	}

	public boolean contains(Vector v) {
		if (v.dimension() != min.dimension())
			throw new IllegalArgumentException("Mismatched dimensions.");
		for (int i = 0; i < v.dimension(); i++) {
			if (min.get(i) > v.get(i))
				return false;
			if (max.get(i) < v.get(i))
				return false;
		}
		return true;
	}

	public boolean rayIntersects(Vector O, Vector D) {
		return MathUtil.rayIntersectsSphere(O, D, center, radius)
				&& MathUtil.rayIntersectsBox(O, D, min, max);
	}
}
