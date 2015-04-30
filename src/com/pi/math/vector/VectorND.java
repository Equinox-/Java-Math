package com.pi.math.vector;

import java.util.Arrays;

public final class VectorND extends Vector {
	private final float[] v;

	public VectorND(float... values) {
		this.v = values;
	}

	@Override
	public float get(int d) {
		return v[d];
	}

	@Override
	public void set(int d, float f) {
		v[d] = f;
	}

	@Override
	public Vector setV(float... v) {
		System.arraycopy(v, 0, this.v, 0, Math.min(this.v.length, v.length));
		return this;
	}

	@Override
	public int dimension() {
		return v.length;
	}

	@Override
	public VectorND clone() {
		return new VectorND(Arrays.copyOf(v, v.length));
	}
}
