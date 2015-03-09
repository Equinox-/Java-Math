package com.pi.math;

import com.pi.math.vector.Vector;

public class Quaternion {
	private static void checkDim(Vector f) {
		if (f.dimension() != 4)
			throw new IllegalArgumentException(
					"Quaternion functions only work on 4D vectors");
	}

	public static Vector setRotation(Vector f, float roll, float pitch,
			float yaw) {
		checkDim(f);

		float cr = (float) Math.cos(roll / 2);
		float cp = (float) Math.cos(pitch / 2);
		float cy = (float) Math.cos(yaw / 2);

		float sr = (float) Math.sin(roll / 2);
		float sp = (float) Math.sin(pitch / 2);
		float sy = (float) Math.sin(yaw / 2);

		float cpcy = cp * cy;
		float spsy = sp * sy;
		float cpsy = cp * sy;
		float spcy = sp * cy;

		f.setV(sr * cpcy - cr * spsy, cr * spcy + sr * cpsy, cr * cpsy - sr
				* spcy, cr * cpcy + sr * spsy);
		return f;
	}

	public static Vector slerp(Vector dest, Vector from, Vector to, float t) {
		checkDim(dest);
		checkDim(from);
		checkDim(to);
		double omega, cosom, sinom;
		float scale0, scale1;

		// calc cosine
		cosom = from.dot(to);

		// calculate coefficients
		if ((1.0 - cosom) > MathUtil.EPSILON) {
			// standard case (slerp)
			omega = Math.acos(cosom);
			sinom = Math.sin(omega);
			scale0 = (float) (Math.sin((1.0 - t) * omega) / sinom);
			scale1 = (float) (Math.sin(t * omega) / sinom);
		} else {
			// "from" and "to" quaternions are very close
			// ... so we can do a linear interpolation
			scale0 = 1.0f - t;
			scale1 = t;
		}
		if (cosom < 0.0)
			scale1 *= -1;

		// calculate final values
		return dest.set(from).linearComb(scale0, to, scale1);
	}

	public static Vector multiply(Vector dest, Vector a, Vector by) {
		checkDim(a);
		checkDim(by);

		float E, F, G, H;
		float tw = a.get(3);
		float tx = a.get(0);
		float ty = a.get(1);

		E = (tx + a.get(2)) * (by.get(0) + by.get(1));
		F = (tx - a.get(2)) * (by.get(0) - by.get(1));
		G = (tw + ty) * (by.get(3) - by.get(2));
		H = (tw - ty) * (by.get(3) + by.get(2));

		dest.setV(
				(tw + tx) * (by.get(3) + by.get(0)) - (E + F + G + H) / 2,
				(tw - tx) * (by.get(1) + by.get(2)) + (E - F + G - H) / 2,
				(ty + a.get(2)) * (by.get(3) - by.get(0)) + (E - F - G + H) / 2,
				(a.get(2) - ty) * (by.get(1) - by.get(2)) + (-E - F + G + H)
						/ 2);
		return dest;
	}
}
