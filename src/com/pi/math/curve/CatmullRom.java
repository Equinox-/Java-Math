package com.pi.math.curve;

import com.pi.math.Vector3D;

public class CatmullRom implements Curve {
	private final CubicBezier[] arches;

	public CatmullRom(Vector3D... pts) {
		arches = new CubicBezier[pts.length - 1];
		for (int i = 0; i < pts.length - 1; i++) {
			Vector3D b = pts[i], c = pts[i + 1];
			if (i > 0) {
				b = Vector3D.linearCombination(1, pts[i], 1f / 6f, Vector3D
						.linearCombination(1, pts[i + 1], -1, pts[i - 1]));
			}
			if (i < pts.length - 2) {
				c = Vector3D.linearCombination(1, pts[i + 1], -1f / 6f,
						Vector3D.linearCombination(1, pts[i + 2], -1, pts[i]));
			}
			arches[i] = new CubicBezier(pts[i], b, c, pts[i + 1]);
		}
	}

	@Override
	public Vector3D calculate(float t) {
		final int tArch = Math
				.min(arches.length - 1, (int) (t * arches.length));
		return arches[tArch].calculate(t * arches.length - tArch);
	}
}
