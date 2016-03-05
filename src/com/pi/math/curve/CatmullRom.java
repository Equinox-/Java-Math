package com.pi.math.curve;

import com.pi.math.vector.Vector;

public class CatmullRom implements Curve {
	private final CubicBezier[] arches;

	/**
	 * Creates an arch between two points, with the given secondary biases.
	 * 
	 * @param before
	 *            The first bias
	 * @param pt1
	 *            The first point
	 * @param pt2
	 *            The second point
	 * @param after
	 *            The second bias
	 * @return A curve
	 */
	public static CubicBezier makeArch(Vector before, Vector pt1, Vector pt2,
			Vector after) {
		Vector b = pt1, c = pt2;
		if (before != null) {
			b = pt1.linearComb(1, pt2.linearComb(1, before, -1), 1f / 6f);
			// b = Vector3D.linearCombination(1, pt1, 1f / 6f,
			// Vector3D.linearCombination(1, pt2, -1, before));
		}
		if (after != null) {
			c = pt2.linearComb(1, after.linearComb(1, pt1, -1), -1f / 6f);
		}
		return new CubicBezier(pt1, b, c, pt2);
	}

	public CatmullRom(Vector... pts) {
		arches = new CubicBezier[pts.length - 1];
		for (int i = 0; i < pts.length - 1; i++) {
			arches[i] = makeArch(i > 0 ? pts[i - 1] : null, pts[i], pts[i + 1],
					i < pts.length - 2 ? pts[i + 2] : null);
		}
	}

	@Override
	public Vector calculate(float t) {
		final int tArch = Math
				.min(arches.length - 1, (int) (t * arches.length));
		return arches[tArch].calculate(t * arches.length - tArch);
	}
}
