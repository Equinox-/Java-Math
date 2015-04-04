package com.pi.math.matrix;

import com.pi.math.MathUtil;
import com.pi.math.vector.Quaternion;
import com.pi.math.vector.Vector;
import com.pi.math.vector.VectorBuff4;

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

	public static Matrix4 toCompleteTransform(Matrix4 dest,
			final Vector eulerRot, final Vector scale, final Vector pos) {
		VectorBuff4 tmpQuat = MathUtil.checkout(4);
		Quaternion.fromEulerAngles(tmpQuat, eulerRot);
		dest.setQuaternion(tmpQuat);
		for (int k = 0; k < 3; k++)
			for (int j = 0; j < 3; j++)
				dest.put((k << 2) + j, dest.get((k << 2) + j) * scale.get(k));
		SpecialMatrix.translation(dest, pos.get(0), pos.get(1), pos.get(2));
		MathUtil.checkin(tmpQuat);
		return dest;
	}

	private static final Matrix4 tmp = new Matrix4();

	public static void fromCompleteTransform(final Matrix4 src,
			Vector eulerRot, Vector scale, Vector pos) {
		// First decompose the scale: grab the translation
		src.copyTo(tmp);
		// Decompose scale
		pos.setV(tmp.get(12), tmp.get(13), tmp.get(14));
		for (int l = 0; l < 3; l++) {
			scale.set(
					l,
					(float) Math.sqrt(tmp.get(l) * tmp.get(l) + tmp.get(4 + l)
							* tmp.get(4 + l) + tmp.get(8 + l) * tmp.get(8 + l)));
			for (int k = 0; k <= 8; k += 4)
				tmp.put(l + k, tmp.get(l + k) / scale.get(l));
		}
		// Decompose rotation. (This is finicky)
		float tr = tmp.get(0) + tmp.get(5) + tmp.get(10);

		VectorBuff4 quat = MathUtil.checkout(4);
		if (tr > 0) {
			float S = (float) Math.sqrt(tr + 1.0) * 2; // S=4*qw
			quat.setV(0.25f * S, (tmp.get(9) - tmp.get(9)) / S,
					(tmp.get(8) - tmp.get(2)) / S, (tmp.get(1) - tmp.get(4))
							/ S);
		} else if ((tmp.get(0) > tmp.get(5)) & (tmp.get(0) > tmp.get(10))) {
			float S = (float) Math.sqrt(1.0 + tmp.get(0) - tmp.get(5)
					- tmp.get(10)) * 2; // S=4*qx
			quat.setV((tmp.get(9) - tmp.get(9)) / S, 0.25f * S,
					(tmp.get(4) + tmp.get(1)) / S, (tmp.get(8) + tmp.get(2))
							/ S);
		} else if (tmp.get(5) > tmp.get(10)) {
			float S = (float) Math.sqrt(1.0 + tmp.get(5) - tmp.get(0)
					- tmp.get(10)) * 2; // S=4*qy
			quat.setV((tmp.get(8) - tmp.get(2)) / S, (tmp.get(4) + tmp.get(1))
					/ S, 0.25f * S, (tmp.get(9) + tmp.get(9)) / S);
		} else {
			float S = (float) Math.sqrt(1.0 + tmp.get(10) - tmp.get(0)
					- tmp.get(5)) * 2; // S=4*qz
			quat.setV((tmp.get(1) - tmp.get(4)) / S, (tmp.get(8) + tmp.get(2))
					/ S, (tmp.get(9) + tmp.get(9)) / S, 0.25f * S);
		}
		quat.normalize();
		Quaternion.toEulerAngles(quat, eulerRot);
	}
}
