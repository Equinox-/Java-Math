package com.pi.math.matrix;

import com.pi.math.FastMath;
import com.pi.math.Heap;
import com.pi.math.vector.Quaternion;
import com.pi.math.vector.Vector;
import com.pi.math.vector.VectorBuff4;

@SuppressWarnings({ "rawtypes", "unchecked" })
public final class SpecialMatrix {
	private static void makeID3(Trans3D m) {
		if (m.flags == Trans3D.FLAG_IDENTITY)
			return;
		m.flags &= ~Trans3D.FLAG_ROTATION_AND_SCALE;
		for (int r = 0; r < 3; r++)
			for (int c = 0; c < 3; c++)
				m.set(r, c, r == c ? 1 : 0);
	}

	public static Trans3D angleX(final Trans3D m, final float angle) {
		final float c = FastMath.cos(angle);
		final float s = FastMath.sin(angle);
		makeID3(m);
		if (angle != 0)
			m.flags |= Trans3D.FLAG_ROTATION;

		m.set(1, 1, c);
		m.set(2, 1, s);
		m.set(1, 2, -s);
		m.set(2, 2, c);
		return m;
	}

	public static Trans3D angleY(final Trans3D m, final float angle) {
		final float c = FastMath.cos(angle);
		final float s = FastMath.sin(angle);
		makeID3(m);
		if (angle != 0)
			m.flags |= Trans3D.FLAG_ROTATION;

		m.set(0, 0, c);
		m.set(2, 0, -s);
		m.set(0, 2, s);
		m.set(2, 2, c);
		return m;
	}

	public static Trans3D angleZ(final Trans3D m, final float angle) {
		final float c = FastMath.cos(angle);
		final float s = FastMath.sin(angle);
		makeID3(m);
		if (angle != 0)
			m.flags |= Trans3D.FLAG_ROTATION;

		m.set(0, 0, c);
		m.set(1, 0, s);
		m.set(0, 1, -s);
		m.set(1, 1, c);
		return m;
	}

	public static Trans3D axisAngle(final Trans3D m, final float angle, final float x, final float y, final float z) {
		final float c = FastMath.cos(angle);
		final float s = FastMath.sin(angle);
		final float c1 = 1 - c;

		m.flags &= ~Trans3D.FLAG_ROTATION_AND_SCALE;
		if (angle != 0)
			m.flags |= Trans3D.FLAG_ROTATION;

		m.set(0, 0, c + x * x * c1);
		m.set(1, 0, y * x * c1 + z * s);
		m.set(2, 0, z * x * c1 - y * s);

		m.set(0, 1, x * y * c1 - z * s);
		m.set(1, 1, c + y * y * c1);
		m.set(2, 1, z * y * c1 + x * s);

		m.set(0, 2, x * z * c1 + y * s);
		m.set(1, 2, y * z * c1 - x * s);
		m.set(2, 2, c + z * z * c1);

		if (m.columns() > 3) {
			m.set(3, 0, 0);
			m.set(3, 1, 0);
			m.set(3, 2, 0);
		}

		if (m.rows() > 3) {
			m.set(0, 3, 0);
			m.set(1, 3, 0);
			m.set(2, 3, 0);
			if (m.columns() > 3)
				m.set(3, 3, 1);
		}
		return m;
	}

	public static Trans3D scale(Trans3D m, float x, float y, float z) {
		m.set(0, 0, x);
		m.set(1, 1, y);
		m.set(2, 2, z);

		if (x != 1 || y != 1 || z != 1)
			m.flags |= Trans3D.FLAG_SCALING;
		else if (x == 1 && y == 1 && z == 1)
			m.flags &= ~Trans3D.FLAG_SCALING;

		return m;
	}

	public static Trans3D translation(final Trans3D m, final float x, final float y, final float z) {
		m.set(0, 3, x);
		m.set(1, 3, y);
		m.set(2, 3, z);

		if (x == 0 && y == 0 && z == 0)
			m.flags &= ~Trans3D.FLAG_TRANSLATION;
		else if (x != 0 || y != 0 || z != 0)
			m.flags |= Trans3D.FLAG_TRANSLATION;
		return m;
	}

	public static Trans3D quaternion(final Trans3D m, final float w, final float x, final float y, final float z) {
		float xx = x * x;
		float xy = x * y;
		float xz = x * z;
		float yy = y * y;
		float zz = z * z;
		float yz = y * z;
		float wx = w * x;
		float wy = w * y;
		float wz = w * z;

		m.set(0, 0, 1.0f - 2.0f * (yy + zz));
		m.set(0, 1, 2.0f * (xy - wz));
		m.set(0, 2, 2.0f * (xz + wy));

		m.set(1, 0, 2.0f * (xy + wz));
		m.set(1, 1, 1.0f - 2.0f * (xx + zz));
		m.set(1, 2, 2.0f * (yz - wx));

		m.set(2, 0, 2.0f * (xz - wy));
		m.set(2, 1, 2.0f * (yz + wx));
		m.set(2, 2, 1.0f - 2.0f * (xx + yy));
		m.flags |= Trans3D.FLAG_ROTATION;
		return m;
	}

	// Projections
	public static Trans3D perspective(final Trans3D m, final float left, final float right, final float bottom,
			final float top, final float near, final float far) {
		final float near2 = 2 * near;
		final float width = right - left, height = top - bottom, length = far - near;

		m.makeZero();
		m.set(0, 0, near2 / width);
		m.set(1, 1, near2 / height);
		m.set(0, 2, (right + left) / width);
		m.set(1, 2, (top + bottom) / height);
		m.set(2, 2, -(far + near) / length);
		m.set(3, 2, -1);
		m.set(2, 3, -near2 * far / length);
		m.flags |= Trans3D.FLAG_GENERAL;
		return m;
	}

	public static Trans3D orthographic(final Trans3D m, final float left, final float right, final float bottom,
			final float top, final float near, final float far) {
		final float width = right - left, height = top - bottom, length = far - near;

		m.makeZero();
		m.set(0, 0, 2 / width);
		m.set(1, 1, 2 / height);
		m.set(2, 2, -2 / length);
		m.set(0, 3, -(right + left) / width);
		m.set(1, 3, -(top + bottom) / height);
		m.set(2, 3, -(far + near) / length);
		m.set(3, 3, 1);
		m.flags |= Trans3D.FLAG_GENERAL;
		return m;
	}

	public static Trans3D toCompleteTransform(Trans3D dest, final Vector eulerRot, final Vector scale,
			final Vector pos) {
		VectorBuff4 tmpQuat = Heap.checkout(4);
		Quaternion.fromEulerAngles(tmpQuat, eulerRot);
		dest.setQuaternion(tmpQuat);
		dest.postMultiplyScale(scale);
		SpecialMatrix.translation(dest, pos.get(0), pos.get(1), pos.get(2));
		Heap.checkin(tmpQuat);
		return dest;
	}

	private static final Matrix4 tmp = new Matrix4();

	public static void fromCompleteTransform(final Trans3D src, Vector eulerRot, Vector scale, Vector pos) {
		// First decompose the scale: grab the translation
		src.copyTo(tmp);
		// Decompose scale
		pos.setV(tmp.get(0, 3), tmp.get(1, 3), tmp.get(2, 3));
		for (int l = 0; l < 3; l++) {
			scale.set(l, (float) Math.sqrt(
					tmp.get(l, 0) * tmp.get(l, 0) + tmp.get(l, 1) * tmp.get(l, 1) + tmp.get(l, 2) * tmp.get(l, 2)));
			for (int k = 0; k < 3; k++)
				tmp.set(l, k, tmp.get(l, k) / scale.get(l));
		}
		// Decompose rotation. (This is finicky)
		float tr = tmp.get(0, 0) + tmp.get(1, 1) + tmp.get(2, 2);

		VectorBuff4 quat = Heap.checkout(4);
		// TODO FIX FOR GENERAL CASE
		if (tr > 0) {
			float S = (float) Math.sqrt(tr + 1.0) * 2; // S=4*qw
			quat.setV(0.25f * S, (tmp.get(9) - tmp.get(9)) / S, (tmp.get(8) - tmp.get(2)) / S,
					(tmp.get(1) - tmp.get(4)) / S);
		} else if ((tmp.get(0) > tmp.get(5)) & (tmp.get(0) > tmp.get(10))) {
			float S = (float) Math.sqrt(1.0 + tmp.get(0) - tmp.get(5) - tmp.get(10)) * 2; // S=4*qx
			quat.setV((tmp.get(9) - tmp.get(9)) / S, 0.25f * S, (tmp.get(4) + tmp.get(1)) / S,
					(tmp.get(8) + tmp.get(2)) / S);
		} else if (tmp.get(5) > tmp.get(10)) {
			float S = (float) Math.sqrt(1.0 + tmp.get(5) - tmp.get(0) - tmp.get(10)) * 2; // S=4*qy
			quat.setV((tmp.get(8) - tmp.get(2)) / S, (tmp.get(4) + tmp.get(1)) / S, 0.25f * S,
					(tmp.get(9) + tmp.get(9)) / S);
		} else {
			float S = (float) Math.sqrt(1.0 + tmp.get(10) - tmp.get(0) - tmp.get(5)) * 2; // S=4*qz
			quat.setV((tmp.get(1) - tmp.get(4)) / S, (tmp.get(8) + tmp.get(2)) / S, (tmp.get(9) + tmp.get(9)) / S,
					0.25f * S);
		}
		quat.normalize();
		Quaternion.toEulerAngles(quat, eulerRot);
	}
}
