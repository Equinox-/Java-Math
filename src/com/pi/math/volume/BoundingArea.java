package com.pi.math.volume;

import java.nio.FloatBuffer;

import com.pi.math.BufferProvider;
import com.pi.math.MathUtil;
import com.pi.math.vector.VectorBuff;
import com.pi.math.vector.VectorBuff3;

public class BoundingArea {
	private VectorBuff min, max;
	private VectorBuff center;
	private float radius;

	public BoundingArea(int dimension) {
		FloatBuffer buff = BufferProvider.createFloatBuffer(3 * dimension);
		min = VectorBuff.make(buff, 0, dimension);
		min.setV(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
		max = VectorBuff.make(buff, dimension, dimension);
		max.setV(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
		center = VectorBuff.make(buff, 2 * dimension, dimension);
	}

	public BoundingArea(VectorBuff min, VectorBuff max) {
		this(min.dimension());
		include(min);
		include(max);
	}

	public boolean contains(VectorBuff v) {
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

	public VectorBuff getCenter() {
		return center;
	}

	public float getHeight(int axis) {
		return max.get(axis) - min.get(axis);
	}

	public VectorBuff getMax() {
		return max;
	}

	public VectorBuff getMin() {
		return min;
	}

	public float getRadius() {
		return radius;
	}

	public float getRadius(int axis) {
		float span = 0;
		for (int i = 1; i < min.dimension(); i++) {
			final int c = (i + axis) % min.dimension();
			span = Math.max(span, max.get(c) - center.get(c));
		}
		return span;
	}

	public void include(VectorBuff v) {
		if (v.dimension() != min.dimension())
			throw new IllegalArgumentException("Mismatched dimensions.");
		for (int i = 0; i < v.dimension(); i++) {
			if (min.get(i) > v.get(i))
				min.set(i, v.get(i));
			if (max.get(i) < v.get(i))
				max.set(i, v.get(i));
		}
		center.linearComb(min, .5f, max, .5f);
		radius = center.dist(min);
	}

	public boolean rayIntersects(VectorBuff3 O, VectorBuff3 D) {
		if (min.dimension() != 3)
			throw new IllegalArgumentException("Ray intersection only allowed in 3-space.");
		return MathUtil.rayIntersectsSphere(O, D, (VectorBuff3) center, radius)
				&& MathUtil.rayIntersectsBox(O, D, (VectorBuff3) min, (VectorBuff3) max);
	}

	public void reset() {
		min.setV(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
		max.setV(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
		center.zero();
	}

	public void set(BoundingArea area) {
		this.max.set(area.max);
		this.min.set(area.min);
		this.center.set(area.center);
		this.radius = area.radius;
	}

	@Override
	public String toString() {
		return "BoundingArea[min=" + min + ", max=" + max + ", rad=" + radius + "]";
	}
}
