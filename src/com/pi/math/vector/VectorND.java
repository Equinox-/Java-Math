package com.pi.math.vector;

import java.util.Arrays;

import com.pi.math.FastMath;

public class VectorND extends Vector<VectorND> {
	private float[] v;

	public VectorND(float... values) {
		this.v = values;
	}

	private void check(VectorND n) {
		if (n.v.length != v.length)
			throw new IllegalArgumentException("Dimension mismatch");
	}

	@Override
	public VectorND add(VectorND r) {
		check(r);
		for (int i = 0; i < v.length; i++)
			v[i] += r.v[i];
		return this;
	}

	@Override
	public VectorND subtract(VectorND r) {
		check(r);
		for (int i = 0; i < v.length; i++)
			v[i] -= r.v[i];
		return this;
	}

	@Override
	public VectorND multiply(float f) {
		for (int i = 0; i < v.length; i++)
			v[i] *= f;
		return this;
	}

	@Override
	public float magnitude() {
		return (float) Math.sqrt(mag2());
	}

	@Override
	public float dist(VectorND t) {
		return (float) Math.sqrt(distSquared(t));
	}

	@Override
	public float distSquared(VectorND t) {
		check(t);
		float d2 = 0;
		for (int i = 0; i < v.length; i++)
			d2 += (v[i] - t.v[i]) * (v[i] - t.v[i]);
		return d2;
	}

	@Override
	public VectorND normalize() {
		return multiply(FastMath.Q_rsqrt(mag2()));
	}

	@Override
	public float mag2() {
		float d2 = 0;
		for (int i = 0; i < v.length; i++)
			d2 += v[i] * v[i];
		return d2;
	}

	@Override
	public float get(int d) {
		return v[d];
	}

	@Override
	public VectorND clone() {
		return new VectorND(Arrays.copyOf(v, v.length));
	}

	@Override
	public float dot(VectorND t) {
		check(t);
		float d2 = 0;
		for (int i = 0; i < v.length; i++)
			d2 += v[i] * t.v[i];
		return d2;
	}

	@Override
	public VectorND linearComb(float aC, VectorND b, float bC) {
		check(b);
		float[] res = new float[v.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = aC * v[i] + bC * b.v[i];
		}
		return new VectorND(res);
	}
}
