package com.pi.math.matrix;

import com.pi.math.FastMath;
import com.pi.math.Heap;
import com.pi.math.vector.Quaternion;
import com.pi.math.vector.Vector;
import com.pi.math.vector.VectorBuff;
import com.pi.math.vector.VectorBuff3;
import com.pi.math.vector.VectorBuff4;

@SuppressWarnings({ "rawtypes", "unchecked" })
public final class SpecialMatrix {
	private static final Matrix4 fromCompleteTransform_TEMP = new Matrix4();

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

	public static Trans3D translation(final Trans3D m, final Vector v) {
		return translation(m, v.get(0), v.dimension() > 1 ? v.get(1) : 0, v.dimension() > 2 ? v.get(2) : 0);
	}

	public static Trans3D translation(final Trans3D m, final VectorBuff3 v) {
		return translation(m, v.get(0), v.get(1), v.get(2));
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

	public static Trans3D translationAdd(final Trans3D m, final float x, final float y, final float z) {
		m.mod(0, 3, x);
		m.mod(1, 3, y);
		m.mod(2, 3, z);

		if (m.get(0, 3) == 0 && m.get(1, 3) == 0 && m.get(2, 3) == 0)
			m.flags &= ~Trans3D.FLAG_TRANSLATION;
		else if (m.get(0, 3) != 0 || m.get(1, 3) != 0 || m.get(2, 3) != 0)
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

	public static VectorBuff matrixToEuler(final Trans3D in, VectorBuff out) {
		// phi, theta, psi
		final float psi = (float) Math.atan2(-in.get(1, 0), in.get(0, 0));
		final float theta = (float) Math.asin(in.get(2, 0));
		final float phi = (float) Math.atan2(-in.get(2, 1), in.get(2, 2));
		out.setV(phi, theta, psi);
		return out;
	}

	public static Trans3D eulerToMatrix(final VectorBuff in, Trans3D out) {
		makeID3(out);
		// phi, theta, psi
		final float sinPhi = FastMath.sin(in.get(0));
		final float cosPhi = FastMath.cos(in.get(0));
		final float sinTheta = FastMath.sin(in.get(1));
		final float cosTheta = FastMath.cos(in.get(1));
		final float sinPsi = FastMath.sin(in.get(2));
		final float cosPsi = FastMath.cos(in.get(2));

		out.flags |= Trans3D.FLAG_ROTATION;
		out.set(0, 0, cosTheta * cosPsi);
		out.set(1, 0, -cosTheta * sinPsi);
		out.set(2, 0, sinTheta);
		out.set(0, 1, cosPhi * sinPsi + sinPhi * sinTheta * cosPsi);
		out.set(1, 1, cosPhi * cosPsi - sinPhi * sinTheta * sinPsi);
		out.set(2, 1, -sinPhi * cosTheta);
		out.set(0, 2, sinPhi * sinPsi - cosPhi * sinTheta * cosPsi);
		out.set(1, 2, sinPhi * cosPsi + cosPhi * sinTheta * sinPsi);
		out.set(2, 2, cosPhi * cosTheta);
		return out;
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

	/**
	 * @param src
	 *            Must have no scaling
	 * @param quat
	 *            Must be length 4
	 */
	public static void matrixToQuaternion(final Trans3D src, Vector quat) {
		// http://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToQuaternion/
		// Method suggested by Christian.
		final float m00 = src.get(0, 0);
		final float m11 = src.get(1, 1);
		final float m22 = src.get(2, 2);
		quat.setV(1 + m00 + m11 + m22, 1 + m00 - m11 - m22, 1 - m00 + m11 - m22, 1 - m00 - m11 + m22);

		// Max is theoretically un-needed; but it safeguards against rounding
		// errors
		for (int i = 0; i < quat.dimension(); i++)
			quat.set(i, FastMath.sqrt(Math.max(0, quat.get(i))) / 2);
		// Copy sign for xyz
		quat.set(1, Math.copySign(quat.get(1), src.get(2, 1) - src.get(1, 2)));
		quat.set(2, Math.copySign(quat.get(2), src.get(0, 2) - src.get(2, 0)));
		quat.set(3, Math.copySign(quat.get(3), src.get(1, 0) - src.get(0, 1)));

		quat.normalize();
	}

	public static void fromCompleteTransform(final Trans3D src, Vector eulerRot, Vector scale, Vector pos) {
		// First decompose the scale: grab the translation
		src.copyTo(fromCompleteTransform_TEMP);
		// Decompose scale
		pos.setV(fromCompleteTransform_TEMP.get(0, 3), fromCompleteTransform_TEMP.get(1, 3), fromCompleteTransform_TEMP.get(2, 3));
		for (int l = 0; l < 3; l++) {
			scale.set(l, (float) Math.sqrt(
					fromCompleteTransform_TEMP.get(l, 0) * fromCompleteTransform_TEMP.get(l, 0) + fromCompleteTransform_TEMP.get(l, 1) * fromCompleteTransform_TEMP.get(l, 1) + fromCompleteTransform_TEMP.get(l, 2) * fromCompleteTransform_TEMP.get(l, 2)));
			for (int k = 0; k < 3; k++)
				fromCompleteTransform_TEMP.set(l, k, fromCompleteTransform_TEMP.get(l, k) / scale.get(l));
		}

		VectorBuff4 quat = Heap.checkout(4);
		matrixToQuaternion(fromCompleteTransform_TEMP, quat);

		Quaternion.toEulerAngles(quat, eulerRot);
	}
}
