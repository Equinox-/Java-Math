package com.pi.math.vector;

import java.util.Arrays;

public class VectorND extends Vector {
	private float[] v;

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
	public int dimension() {
		return v.length;
	}

	@Override
	public VectorND clone() {
		return new VectorND(Arrays.copyOf(v, v.length));
	}
}
