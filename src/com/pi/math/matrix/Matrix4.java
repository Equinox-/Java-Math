package com.pi.math.matrix;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import com.pi.math.Quaternion;
import com.pi.math.vector.Vector;

public class Matrix4 {
	public final FloatBuffer data;
	public final int offset;

	public Matrix4() {
		this(BufferUtils.createFloatBuffer(16), 0);
	}

	public Matrix4(FloatBuffer f, int offset) {
		this.data = f;
		this.offset = offset;
	}

	public void zero() {
		for (int i = 0; i < 16; i++)
			this.put(i, 0);
	}

	public void makeIdentity() {
		zero();
		put(0, 1);
		put(5, 1);
		put(10, 1);
		put(15, 1);
	}

	private final float get(int i) {
		return data.get(i + offset);
	}

	private final void put(int i, float f) {
		data.put(i + offset, f);
	}

	// Math operations

	public void add(Matrix4 from) {
		for (int i = 0; i < 16; i++)
			this.put(i, this.get(i) + from.get(i));
	}

	public void lincom(Matrix4 from, float f) {
		for (int i = 0; i < 16; i++)
			this.put(i, this.get(i) + from.get(i) * f);
	}

	private static void multiplyInto(final Matrix4 dest, final Matrix4 a,
			final Matrix4 b) {
		for (int i = 0; i < 4; i++) {
			final int j = i << 2;
			final float ai0 = a.get(j), ai1 = a.get(j + 1), ai2 = a.data
					.get(j + 2), ai3 = a.get(j + 3);
			dest.put(j,
					ai0 * b.get(0) + ai1 * b.get(4 + 0) + ai2 * b.get(8 + 0)
							+ ai3 * b.get(12 + 0));
			dest.put(j + 1,
					ai0 * b.get(1) + ai1 * b.get(4 + 1) + ai2 * b.get(8 + 1)
							+ ai3 * b.get(12 + 1));
			dest.put(j + 2,
					ai0 * b.get(2) + ai1 * b.get(4 + 2) + ai2 * b.get(8 + 2)
							+ ai3 * b.get(12 + 2));
			dest.put(j + 3,
					ai0 * b.get(3) + ai1 * b.get(4 + 3) + ai2 * b.get(8 + 3)
							+ ai3 * b.get(12 + 3));
		}
	}

	public static Matrix4 multiply(final Matrix4 a, final Matrix4 b) {
		Matrix4 res = new Matrix4();
		multiplyInto(res, a, b);
		return res;
	}

	public void multiplyInto(Matrix4 b) {
		multiplyInto(this, this, b);
	}

	public static Vector multiply(final Matrix4 a, final Vector v,
			final Vector dest) {
		final int cols = Math.min(4, v.dimension());
		final int rows = Math.min(4, dest.dimension());
		for (int row = 0; row < rows; row++) {
			float comp = 0;
			for (int col = 0; col < cols; col++)
				comp += a.get(row + (col << 2)) * v.get(col);
			dest.set(row, comp);
		}
		return dest;
	}

	// Matrix operations

	public Matrix4 inverse() {
		Matrix4 res = new Matrix4();
		res.put(0, get(5) * get(10) * get(15) - get(5) * get(11) * get(14)
				- get(9) * get(6) * get(15) + get(9) * get(7) * get(14)
				+ get(13) * get(6) * get(11) - get(13) * get(7) * get(10));

		res.put(4, -get(4) * get(10) * get(15) + get(4) * get(11) * get(14)
				+ get(8) * get(6) * get(15) - get(8) * get(7) * get(14)
				- get(12) * get(6) * get(11) + get(12) * get(7) * get(10));

		res.put(8, get(4) * get(9) * get(15) - get(4) * get(11) * get(13)
				- get(8) * get(5) * get(15) + get(8) * get(7) * get(13)
				+ get(12) * get(5) * get(11) - get(12) * get(7) * get(9));

		res.put(12, -get(4) * get(9) * get(14) + get(4) * get(10) * get(13)
				+ get(8) * get(5) * get(14) - get(8) * get(6) * get(13)
				- get(12) * get(5) * get(10) + get(12) * get(6) * get(9));

		res.put(1, -get(1) * get(10) * get(15) + get(1) * get(11) * get(14)
				+ get(9) * get(2) * get(15) - get(9) * get(3) * get(14)
				- get(13) * get(2) * get(11) + get(13) * get(3) * get(10));

		res.put(5, get(0) * get(10) * get(15) - get(0) * get(11) * get(14)
				- get(8) * get(2) * get(15) + get(8) * get(3) * get(14)
				+ get(12) * get(2) * get(11) - get(12) * get(3) * get(10));

		res.put(9, -get(0) * get(9) * get(15) + get(0) * get(11) * get(13)
				+ get(8) * get(1) * get(15) - get(8) * get(3) * get(13)
				- get(12) * get(1) * get(11) + get(12) * get(3) * get(9));

		res.put(13, get(0) * get(9) * get(14) - get(0) * get(10) * get(13)
				- get(8) * get(1) * get(14) + get(8) * get(2) * get(13)
				+ get(12) * get(1) * get(10) - get(12) * get(2) * get(9));

		res.put(2, get(1) * get(6) * get(15) - get(1) * get(7) * get(14)
				- get(5) * get(2) * get(15) + get(5) * get(3) * get(14)
				+ get(13) * get(2) * get(7) - get(13) * get(3) * get(6));

		res.put(6, -get(0) * get(6) * get(15) + get(0) * get(7) * get(14)
				+ get(4) * get(2) * get(15) - get(4) * get(3) * get(14)
				- get(12) * get(2) * get(7) + get(12) * get(3) * get(6));

		res.put(10, get(0) * get(5) * get(15) - get(0) * get(7) * get(13)
				- get(4) * get(1) * get(15) + get(4) * get(3) * get(13)
				+ get(12) * get(1) * get(7) - get(12) * get(3) * get(5));

		res.put(14, -get(0) * get(5) * get(14) + get(0) * get(6) * get(13)
				+ get(4) * get(1) * get(14) - get(4) * get(2) * get(13)
				- get(12) * get(1) * get(6) + get(12) * get(2) * get(5));

		res.put(3, -get(1) * get(6) * get(11) + get(1) * get(7) * get(10)
				+ get(5) * get(2) * get(11) - get(5) * get(3) * get(10)
				- get(9) * get(2) * get(7) + get(9) * get(3) * get(6));

		res.put(7, get(0) * get(6) * get(11) - get(0) * get(7) * get(10)
				- get(4) * get(2) * get(11) + get(4) * get(3) * get(10)
				+ get(8) * get(2) * get(7) - get(8) * get(3) * get(6));

		res.put(11, -get(0) * get(5) * get(11) + get(0) * get(7) * get(9)
				+ get(4) * get(1) * get(11) - get(4) * get(3) * get(9) - get(8)
				* get(1) * get(7) + get(8) * get(3) * get(5));

		res.put(15, get(0) * get(5) * get(10) - get(0) * get(6) * get(9)
				- get(4) * get(1) * get(10) + get(4) * get(2) * get(9) + get(8)
				* get(1) * get(6) - get(8) * get(2) * get(5));

		float det = get(0) * res.get(0) + get(1) * res.get(4) + get(2)
				* res.get(8) + get(3) * res.get(12);

		if (det == 0) {
			throw new IllegalArgumentException("Invert det=0 mat\n");
		}
		det = 1.0f / det;

		for (int i = 0; i < 16; i++)
			res.put(i, res.get(i) * (det));
		return res;
	}

	public static Matrix4 transpose(final Matrix4 mat) {
		Matrix4 res = mat.copy();
		res.put(1, mat.get(4));
		res.put(2, mat.get(8));
		res.put(3, mat.get(12));

		res.put(6, mat.get(9));
		res.put(7, mat.get(13));

		res.put(11, mat.get(14));

		res.put(4, mat.get(1));
		res.put(8, mat.get(2));
		res.put(12, mat.get(3));

		res.put(9, mat.get(6));
		res.put(13, mat.get(7));

		res.put(14, mat.get(11));
		return res;
	}

	public Matrix4 copy() {
		Matrix4 res = new Matrix4();
		for (int i = 0; i < 16; i++)
			res.put(i, get(i));
		return res;
	}

	public Matrix4 asMatrix3(Matrix4 mat) {
		Matrix4 res = mat.copy();
		res.put(3, 0);
		res.put(7, 0);
		res.put(11, 0);
		res.put(12, 0);
		res.put(13, 0);
		res.put(14, 0);
		res.put(15, 1);
		return res;
	}

	public static Matrix4 identity() {
		Matrix4 res = new Matrix4();
		res.put(0, 1);
		res.put(5, 1);
		res.put(10, 1);
		res.put(15, 1);
		return res;
	}

	// Methods for creating special matrices
	public Matrix4 setAxisAngle(final float angle, final Vector a) {
		final float c = (float) Math.cos(angle);
		final float s = (float) Math.sin(angle);
		final float c1 = 1 - c;

		if (a.dimension() != 3)
			throw new RuntimeException(
					"Rotation is only allowed around 3-D vectors");

		final float x = a.get(0);
		final float y = a.get(1);
		final float z = a.get(2);

		put(0, c + x * x * c1);
		put(1, y * x * c1 + z * s);
		put(2, z * x * c1 - y * s);
		put(3, 0);

		put(4, x * y * c1 - z * s);
		put(5, c + y * y * c1);
		put(6, z * y * c1 + x * s);
		put(7, 0);

		put(8, x * z * c1 + y * s);
		put(9, y * z * c1 - x * s);
		put(10, c + z * z * c1);
		put(11, 0);

		put(12, 0);
		put(13, 0);
		put(14, 0);
		put(15, 1);
		return this;
	}

	public Matrix4 setTranslation(final float x, final float y, final float z) {
		put(12, x);
		put(13, y);
		put(14, z);
		return this;
	}

	public void setRotation(Quaternion q) {
		float x2 = q.x + q.x;
		float y2 = q.y + q.y;
		float z2 = q.z + q.z;

		float wx = q.w * x2;
		float wy = q.w * y2;
		float wz = q.w * z2;

		float xx = q.x * x2;
		float xy = q.x * y2;
		float xz = q.x * z2;

		float yy = q.y * y2;
		float yz = q.y * z2;

		float zz = q.z * z2;
		put(0, 1 - (yy + zz));
		put(4, xy - wz);
		put(8, xz + wy);

		put(1, xy + wz);
		put(5, 1 - (xx + zz));
		put(9, yz - wx);

		put(2, xz - wy);
		put(6, yz + wx);
		put(10, 1 - (xx + yy));
	}

	// Stringification
	public String toString() {
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			if (i > 0)
				res.append('\n');
			res.append(get(i) + " " + get(i + 4) + " " + get(i + 8) + " "
					+ get(i + 12));
		}
		return res.toString();
	}
}
