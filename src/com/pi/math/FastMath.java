package com.pi.math;

public class FastMath {
	public static float Q_rsqrt(float number) {
		int i;
		float x2, y;
		final float threehalfs = 1.5F;

		x2 = number * 0.5F;
		y = number;
		i = Float.floatToIntBits(y);
		i = 0x5f3759df - (i >> 1); // what the fuck?
		y = Float.intBitsToFloat(i);
		y = y * (threehalfs - (x2 * y * y)); // 1st iteration
		// y = y * ( threehalfs - ( x2 * y * y ) ); // 2nd iteration, this can
		// be removed

		return y;
	}
}
