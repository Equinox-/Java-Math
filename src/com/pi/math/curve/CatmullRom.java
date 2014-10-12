package com.pi.math.curve;

import com.pi.math.vector.Vector;

public class CatmullRom<T extends Vector<T>> implements Curve<T> {
	private final CubicBezier<T>[] arches;

	@SuppressWarnings("unchecked")
	public CatmullRom(T... pts) {
		arches = new CubicBezier[pts.length - 1];
		for (int i = 0; i < pts.length - 1; i++) {
			arches[i] = makeArch(i > 0 ? pts[i - 1] : null, pts[i], pts[i + 1],
					i < pts.length - 2 ? pts[i + 2] : null);
		}
	}

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
	public static <T extends Vector<T>> CubicBezier<T> makeArch(T before, T pt1,
			T pt2, T after) {
		T b = pt1, c = pt2;
		if (before != null) {
			b = pt1.linearComb(1,pt2.linearComb(1,before,-1),1f/6f);
			// b = Vector3D.linearCombination(1, pt1, 1f / 6f,
			// Vector3D.linearCombination(1, pt2, -1, before));
		}
		if (after != null) {
			c = pt2.linearComb(1, after.linearComb(1, pt1, -1), -1f / 6f);
		}
		return new CubicBezier<>(pt1, b, c, pt2);
	}

	@Override
	public T calculate(float t) {
		final int tArch = Math
				.min(arches.length - 1, (int) (t * arches.length));
		return arches[tArch].calculate(t * arches.length - tArch);
	}
}
