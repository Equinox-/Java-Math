package com.pi.math.curve;

import com.pi.math.vector.Vector;

public class CubicBezier implements Curve {
	private final Vector a, b, c, d;

	public CubicBezier(Vector a, Vector b, Vector c, Vector d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	@Override
	public Vector calculate(float t1) {
		final float t2 = t1 * t1;
		final float t3 = t2 * t1;
		final float mt1 = 1 - t1;
		final float mt2 = mt1 * mt1;
		final float mt3 = mt1 * mt2;
		return a.linearComb(mt3, b, 3 * mt2 * t1).add(
				c.linearComb(3 * mt1 * t2, d, t3));
		// return Vector3D.linearCombination(mt3, a, 3 * mt2 * t1, b).add(
		// Vector3D.linearCombination(3 * mt1 * t2, c, t3, d));
	}
}
