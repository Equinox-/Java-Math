package com.pi.math.matrix;

public final class SpecialMatrix {
	public static Matrix4 axisAngle(final Matrix4 m, final float angle,
			final float x, final float y, final float z) {
		final float c = (float) Math.cos(angle);
		final float s = (float) Math.sin(angle);
		final float c1 = 1 - c;

		m.put(0, c + x * x * c1);
		m.put(1, y * x * c1 + z * s);
		m.put(2, z * x * c1 - y * s);
		m.put(3, 0);

		m.put(4, x * y * c1 - z * s);
		m.put(5, c + y * y * c1);
		m.put(6, z * y * c1 + x * s);
		m.put(7, 0);

		m.put(8, x * z * c1 + y * s);
		m.put(9, y * z * c1 - x * s);
		m.put(10, c + z * z * c1);
		m.put(11, 0);

		m.put(12, 0);
		m.put(13, 0);
		m.put(14, 0);
		m.put(15, 1);
		return m;
	}

	public static Matrix4 scale(Matrix4 m, float x, float y, float z) {
		m.put(0, x);
		m.put(5, y);
		m.put(10, z);
		return m;
	}

	public static Matrix4 translation(final Matrix4 m, final float x,
			final float y, final float z) {
		m.put(12, x);
		m.put(13, y);
		m.put(14, z);
		return m;
	}

	public static Matrix4 quaternion(final Matrix4 m, final float w,
			final float x, final float y, final float z) {
		float xx = x * x;
		float xy = x * y;
		float xz = x * z;
		float yy = y * y;
		float zz = z * z;
		float yz = y * z;
		float wx = w * x;
		float wy = w * y;
		float wz = w * z;

		m.put(0, 1.0f - 2.0f * (yy + zz));
		m.put(4, 2.0f * (xy - wz));
		m.put(8, 2.0f * (xz + wy));

		m.put(1, 2.0f * (xy + wz));
		m.put(5, 1.0f - 2.0f * (xx + zz));
		m.put(9, 2.0f * (yz - wx));

		m.put(2, 2.0f * (xz - wy));
		m.put(6, 2.0f * (yz + wx));
		m.put(10, 1.0f - 2.0f * (xx + yy));
		return m;
	}

	// Projections
	public static Matrix4 perspective(final Matrix4 m, final float left,
			final float right, final float bottom, final float top,
			final float near, final float far) {
		final float near2 = 2 * near;
		final float width = right - left, height = top - bottom, length = far
				- near;

		m.zero();
		m.put(0, near2 / width);
		m.put(5, near2 / height);
		m.put(8, (right + left) / width);
		m.put(9, (top + bottom) / height);
		m.put(10, -(far + near) / length);
		m.put(11, -1);
		m.put(14, -near2 * far / length);
		return m;
	}

	public static Matrix4 orthographic(final Matrix4 m, final float left,
			final float right, final float bottom, final float top,
			final float near, final float far) {
		final float width = right - left, height = top - bottom, length = far
				- near;

		m.zero();
		m.put(0, 2 / width);
		m.put(5, 2 / height);
		m.put(10, -2 / length);
		m.put(12, -(right + left) / width);
		m.put(13, -(top + bottom) / height);
		m.put(14, -(far + near) / length);
		m.put(15, 1);
		return m;
	}
}
