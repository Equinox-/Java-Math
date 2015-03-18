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
		y = y * (threehalfs - (x2 * y * y)); // 2nd iteration, this can
		// be removed

		return y;
	}

	public static int nextPowerOf2(int n) {
		n--;
		n |= n >> 1;
		n |= n >> 2;
		n |= n >> 4;
		n |= n >> 8;
		n |= n >> 16;
		n++;
		return n;
	}

	public static boolean isPowerOf2(int i) {
		return (i > 0) && (i & (i - 1)) == 0;
	}

	public static int nextBit(int data, int start, int dir) {
		final int sizeof = (1 << 4);
		for (int k = 1; k < sizeof; k++) {
			int bit = (start + k * dir + sizeof) & (sizeof - 1);
			if ((data & (1 << bit)) != 0)
				return bit;
		}
		return -1;
	}
}
