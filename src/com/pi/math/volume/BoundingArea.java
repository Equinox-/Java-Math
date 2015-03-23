package com.pi.math.volume;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import com.pi.math.MathUtil;
import com.pi.math.vector.VectorBuff;
import com.pi.math.vector.VectorBuff3;

public class BoundingArea {
	private VectorBuff3 min, max;
	private VectorBuff3 center;
	private float radius;

	public BoundingArea(VectorBuff3 min, VectorBuff3 max) {
		this();
		include(min);
		include(max);
	}

	public BoundingArea() {
		FloatBuffer buff = BufferUtils.createFloatBuffer(9);
		min = new VectorBuff3(buff, 0);
		min.setV(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
		max = new VectorBuff3(buff, 3);
		max.setV(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
		center = new VectorBuff3(buff, 6);
	}

	public void reset() {
		min.setV(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
		max.setV(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
		center.multiply(0);
	}

	public VectorBuff3 getMin() {
		return min;
	}

	public VectorBuff3 getMax() {
		return max;
	}

	public VectorBuff3 getCenter() {
		return center;
	}

	public float getRadius() {
		return radius;
	}

	public void include(VectorBuff v) {
		for (int i = 0; i < v.dimension(); i++) {
			if (min.get(i) > v.get(i))
				min.set(i, v.get(i));
			if (max.get(i) < v.get(i))
				max.set(i, v.get(i));
		}
		center.linearComb(min, .5f, max, .5f);
		radius = center.dist(min);
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

	public boolean rayIntersects(VectorBuff3 O, VectorBuff3 D) {
		return MathUtil.rayIntersectsSphere(O, D, center, radius)
				&& MathUtil.rayIntersectsBox(O, D, min, max);
	}
}
