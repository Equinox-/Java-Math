package com.pi.math.curve;

import com.pi.math.Vector3D;

public class CubicBezier implements Curve {
	private final Vector3D a, b, c, d;

	public CubicBezier(Vector3D a, Vector3D b, Vector3D c, Vector3D d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	@Override
	public Vector3D calculate(float t1) {
		final float t2 = t1 * t1;
		final float t3 = t2 * t1;
		final float mt1 = 1 - t1;
		final float mt2 = mt1 * mt1;
		final float mt3 = mt1 * mt2;
		return Vector3D.linearCombination(mt3, a, 3 * mt2 * t1, b).add(
				Vector3D.linearCombination(3 * mt1 * t2, c, t3, d));
	}
}
