package com.pi.math.matrix;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import com.pi.math.vector.Vector;

public final class Matrix4 {
	private final FloatBuffer data;

	public Matrix4() {
		this.data = BufferUtils.createFloatBuffer(16);
	}

	public Matrix4(ByteBuffer f, int offset) {
		int ops = f.position();
		f.position(offset);
		this.data = f.asFloatBuffer();
		this.data.limit(16);
		f.position(ops);
	}

	public Matrix4(FloatBuffer f, int offset) {
		int ops = f.position();
		f.position(offset);
		this.data = f.slice();
		this.data.limit(16);
		f.position(ops);
	}

	public void zero() {
		for (int i = 0; i < 16; i++)
			this.put(i, 0);
	}

	public Matrix4 makeIdentity() {
		zero();
		put(0, 1);
		put(5, 1);
		put(10, 1);
		put(15, 1);
		return this;
	}

	public final float get(int i) {
		return data.get(i);
	}

	public final void put(int i, float f) {
		data.put(i, f);
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

	/**
	 * dest = b * a
	 * 
	 * @param dest
	 * @param a
	 * @param b
	 */
	public static void multiplyInto(final Matrix4 dest, final Matrix4 a,
			final Matrix4 b) {
		for (int i = 0; i < 4; i++) {
			final int j = i << 2;
			final float ai0 = a.get(j), ai1 = a.get(j + 1), ai2 = a.get(j + 2), ai3 = a
					.get(j + 3);
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

	/**
	 * this = b * this
	 * 
	 * @param b
	 */
	public Matrix4 multiplyInto(Matrix4 b) {
		multiplyInto(this, this, b);
		return this;
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
		return invertInto(new Matrix4());
	}

	public Matrix4 invertInto(Matrix4 res) {
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

	private final void swap(int a, int b) {
		float v = get(a);
		put(a, get(b));
		put(b, v);
	}

	public Matrix4 transposeInPlace() {
		swap(1, 4);
		swap(2, 8);
		swap(3, 12);

		swap(6, 9);
		swap(7, 13);

		swap(11, 14);
		return this;
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

	public Matrix4 copyTo(Matrix4 res) {
		for (int i = 0; i < 16; i++)
			res.put(i, get(i));
		return res;
	}

	public Matrix4 copy() {
		return copyTo(new Matrix4());
	}

	public Matrix4 makeMatrix3() {
		put(3, 0);
		put(7, 0);
		put(11, 0);
		put(12, 0);
		put(13, 0);
		put(14, 0);
		put(15, 1);
		return this;
	}

	public static Matrix4 identity() {
		return new Matrix4().makeIdentity();
	}

	// Methods for creating special matrices
	public Matrix4 setScale(final Vector s) {
		if (s.dimension() != 3)
			throw new IllegalArgumentException(
					"Scaling only allowed by 3D vectors.");
		return setScale(s.get(0), s.get(1), s.get(2));
	}

	public Matrix4 setScale(float x, float y, float z) {
		return SpecialMatrix.scale(this, x, y, z);
	}

	public Matrix4 setAxisAngle(final float angle, final Vector a) {
		if (a.dimension() != 3)
			throw new RuntimeException(
					"Rotation is only allowed around 3-D vectors");
		return SpecialMatrix.axisAngle(this, angle, a.get(0), a.get(1),
				a.get(2));
	}

	public Matrix4 setAxisAngle(final float angle, final float x,
			final float y, final float z) {
		return SpecialMatrix.axisAngle(this, angle, x, y, z);
	}

	public Matrix4 setTranslation(final Vector a) {
		if (a.dimension() > 3)
			throw new RuntimeException(
					"Translation is only allowed for vectors of dimension 3 or less");
		return SpecialMatrix.translation(this,
				a.dimension() > 0 ? a.get(0) : 0, a.dimension() > 1 ? a.get(1)
						: 0, a.dimension() > 2 ? a.get(2) : 0);
	}

	public Matrix4 setTranslation(final float x, final float y, final float z) {
		return SpecialMatrix.translation(this, x, y, z);
	}

	public Matrix4 setQuaternion(Vector q) {
		return SpecialMatrix.quaternion(this, q.get(0), q.get(1), q.get(2),
				q.get(3));
	}

	public Matrix4 setQuaternion(final float q0, final float q1,
			final float q2, final float q3) {
		return SpecialMatrix.quaternion(this, q0, q1, q2, q3);
	}

	public Matrix4 setPerspective(final float left, final float right,
			final float bottom, final float top, final float near,
			final float far) {
		return SpecialMatrix.perspective(this, left, right, bottom, top, near,
				far);
	}

	public Matrix4 setOrthographic(final float left, final float right,
			final float bottom, final float top, final float near,
			final float far) {
		return SpecialMatrix.orthographic(this, left, right, bottom, top, near,
				far);
	}

	public Matrix4 preMultiplyTransform(float x, float y, float z) {
		put(12, get(0) * x + get(4) * y + get(8) * z + get(12));
		put(13, get(1) * x + get(5) * y + get(9) * z + get(13));
		put(14, get(2) * x + get(6) * y + get(10) * z + get(14));
		put(15, get(3) * x + get(7) * y + get(11) * z + get(15));
		return this;
	}

	public FloatBuffer getAccessor() {
		return data;
	}

	// Stringification

	public static String toString(Matrix4... show) {
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			if (i > 0)
				res.append('\n');
			for (Matrix4 m : show)
				res.append(String.format("%+2.8f %+2.8f %+2.8f %+2.8f    ",
						m.get(i), m.get(i + 4), m.get(i + 8), m.get(i + 12)));
		}
		return res.toString();
	}

	@Override
	public String toString() {
		return toString(this);
	}
}
