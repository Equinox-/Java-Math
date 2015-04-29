package com.pi.math.matrix;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import com.pi.math.BufferProvider;
import com.pi.math.vector.Vector;
import com.pi.math.vector.VectorBuff3;

public final class Matrix4 {
	private static final float[] ZERO = new float[16];
	private static final float[] IDENTITY = { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1,
			0, 0, 0, 0, 1 };

	private final FloatBuffer data;

	public Matrix4() {
		this.data = BufferProvider.createFloatBuffer(16);
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
		data.position(0);
		data.put(ZERO);
		data.position(0);
	}

	public Matrix4 makeIdentity() {
		data.position(0);
		data.put(IDENTITY);
		data.position(0);
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
			data.put(i, data.get(i) + from.data.get(i));
	}

	public void lincom(float a, Matrix4 bm, float b) {
		for (int i = 0; i < 16; i++)
			data.put(i, a * data.get(i) + bm.data.get(i) * b);
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
			final float ai0 = a.data.get(j), ai1 = a.data.get(j + 1), ai2 = a.data
					.get(j + 2), ai3 = a.data.get(j + 3);
			dest.data.put(j, ai0 * b.data.get(0) + ai1 * b.data.get(4 + 0)
					+ ai2 * b.data.get(8 + 0) + ai3 * b.data.get(12 + 0));
			dest.data.put(j + 1, ai0 * b.data.get(1) + ai1 * b.data.get(4 + 1)
					+ ai2 * b.data.get(8 + 1) + ai3 * b.data.get(12 + 1));
			dest.data.put(j + 2, ai0 * b.data.get(2) + ai1 * b.data.get(4 + 2)
					+ ai2 * b.data.get(8 + 2) + ai3 * b.data.get(12 + 2));
			dest.data.put(j + 3, ai0 * b.data.get(3) + ai1 * b.data.get(4 + 3)
					+ ai2 * b.data.get(8 + 3) + ai3 * b.data.get(12 + 3));
		}
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

	public Vector transform4(Vector output, final Vector input) {
		final float inW = input.dimension() < 4 ? 1 : input.get(3);
		for (int k = 0; k < output.dimension(); k++)
			output.set(k,
					data.get(k) * input.get(0) + data.get(4 + k) * input.get(1)
							+ data.get(8 + k) * input.get(2) + data.get(12 + k)
							* inW);
		return output;
	}

	public Vector transform3(Vector output, final Vector input) {
		for (int k = 0; k < output.dimension(); k++)
			output.set(k,
					data.get(k) * input.get(0) + data.get(4 + k) * input.get(1)
							+ data.get(8 + k) * input.get(2));
		return output;
	}

	// Matrix operations
	public Matrix4 invertInto(Matrix4 res) {
		res.data.put(0, data.get(5) * data.get(10) * data.get(15) - data.get(5)
				* data.get(11) * data.get(14) - data.get(9) * data.get(6)
				* data.get(15) + data.get(9) * data.get(7) * data.get(14)
				+ data.get(13) * data.get(6) * data.get(11) - data.get(13)
				* data.get(7) * data.get(10));

		res.data.put(
				4,
				-data.get(4) * data.get(10) * data.get(15) + data.get(4)
						* data.get(11) * data.get(14) + data.get(8)
						* data.get(6) * data.get(15) - data.get(8)
						* data.get(7) * data.get(14) - data.get(12)
						* data.get(6) * data.get(11) + data.get(12)
						* data.get(7) * data.get(10));

		res.data.put(8, data.get(4) * data.get(9) * data.get(15) - data.get(4)
				* data.get(11) * data.get(13) - data.get(8) * data.get(5)
				* data.get(15) + data.get(8) * data.get(7) * data.get(13)
				+ data.get(12) * data.get(5) * data.get(11) - data.get(12)
				* data.get(7) * data.get(9));

		res.data.put(
				12,
				-data.get(4) * data.get(9) * data.get(14) + data.get(4)
						* data.get(10) * data.get(13) + data.get(8)
						* data.get(5) * data.get(14) - data.get(8)
						* data.get(6) * data.get(13) - data.get(12)
						* data.get(5) * data.get(10) + data.get(12)
						* data.get(6) * data.get(9));

		res.data.put(
				1,
				-data.get(1) * data.get(10) * data.get(15) + data.get(1)
						* data.get(11) * data.get(14) + data.get(9)
						* data.get(2) * data.get(15) - data.get(9)
						* data.get(3) * data.get(14) - data.get(13)
						* data.get(2) * data.get(11) + data.get(13)
						* data.get(3) * data.get(10));

		res.data.put(5, data.get(0) * data.get(10) * data.get(15) - data.get(0)
				* data.get(11) * data.get(14) - data.get(8) * data.get(2)
				* data.get(15) + data.get(8) * data.get(3) * data.get(14)
				+ data.get(12) * data.get(2) * data.get(11) - data.get(12)
				* data.get(3) * data.get(10));

		res.data.put(9, -data.get(0) * data.get(9) * data.get(15) + data.get(0)
				* data.get(11) * data.get(13) + data.get(8) * data.get(1)
				* data.get(15) - data.get(8) * data.get(3) * data.get(13)
				- data.get(12) * data.get(1) * data.get(11) + data.get(12)
				* data.get(3) * data.get(9));

		res.data.put(13, data.get(0) * data.get(9) * data.get(14) - data.get(0)
				* data.get(10) * data.get(13) - data.get(8) * data.get(1)
				* data.get(14) + data.get(8) * data.get(2) * data.get(13)
				+ data.get(12) * data.get(1) * data.get(10) - data.get(12)
				* data.get(2) * data.get(9));

		res.data.put(
				2,
				data.get(1) * data.get(6) * data.get(15) - data.get(1)
						* data.get(7) * data.get(14) - data.get(5)
						* data.get(2) * data.get(15) + data.get(5)
						* data.get(3) * data.get(14) + data.get(13)
						* data.get(2) * data.get(7) - data.get(13)
						* data.get(3) * data.get(6));

		res.data.put(
				6,
				-data.get(0) * data.get(6) * data.get(15) + data.get(0)
						* data.get(7) * data.get(14) + data.get(4)
						* data.get(2) * data.get(15) - data.get(4)
						* data.get(3) * data.get(14) - data.get(12)
						* data.get(2) * data.get(7) + data.get(12)
						* data.get(3) * data.get(6));

		res.data.put(
				10,
				data.get(0) * data.get(5) * data.get(15) - data.get(0)
						* data.get(7) * data.get(13) - data.get(4)
						* data.get(1) * data.get(15) + data.get(4)
						* data.get(3) * data.get(13) + data.get(12)
						* data.get(1) * data.get(7) - data.get(12)
						* data.get(3) * data.get(5));

		res.data.put(
				14,
				-data.get(0) * data.get(5) * data.get(14) + data.get(0)
						* data.get(6) * data.get(13) + data.get(4)
						* data.get(1) * data.get(14) - data.get(4)
						* data.get(2) * data.get(13) - data.get(12)
						* data.get(1) * data.get(6) + data.get(12)
						* data.get(2) * data.get(5));

		res.data.put(
				3,
				-data.get(1) * data.get(6) * data.get(11) + data.get(1)
						* data.get(7) * data.get(10) + data.get(5)
						* data.get(2) * data.get(11) - data.get(5)
						* data.get(3) * data.get(10) - data.get(9)
						* data.get(2) * data.get(7) + data.get(9) * data.get(3)
						* data.get(6));

		res.data.put(
				7,
				data.get(0) * data.get(6) * data.get(11) - data.get(0)
						* data.get(7) * data.get(10) - data.get(4)
						* data.get(2) * data.get(11) + data.get(4)
						* data.get(3) * data.get(10) + data.get(8)
						* data.get(2) * data.get(7) - data.get(8) * data.get(3)
						* data.get(6));

		res.data.put(
				11,
				-data.get(0) * data.get(5) * data.get(11) + data.get(0)
						* data.get(7) * data.get(9) + data.get(4) * data.get(1)
						* data.get(11) - data.get(4) * data.get(3)
						* data.get(9) - data.get(8) * data.get(1) * data.get(7)
						+ data.get(8) * data.get(3) * data.get(5));

		res.data.put(
				15,
				data.get(0) * data.get(5) * data.get(10) - data.get(0)
						* data.get(6) * data.get(9) - data.get(4) * data.get(1)
						* data.get(10) + data.get(4) * data.get(2)
						* data.get(9) + data.get(8) * data.get(1) * data.get(6)
						- data.get(8) * data.get(2) * data.get(5));

		float det = data.get(0) * res.data.get(0) + data.get(1)
				* res.data.get(4) + data.get(2) * res.data.get(8) + data.get(3)
				* res.data.get(12);

		if (det == 0) {
			throw new IllegalArgumentException("Invert det=0 mat\n");
		}
		det = 1.0f / det;

		for (int i = 0; i < 16; i++)
			res.data.put(i, res.data.get(i) * (det));
		return res;
	}

	private final void swap(int a, int b) {
		float v = data.get(a);
		data.put(a, data.get(b));
		data.put(b, v);
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

	public Matrix4 copyTo(Matrix4 res) {
		res.data.position(0);
		data.position(0);
		res.data.put(data);
		res.data.position(0);
		data.position(0);
		return res;
	}

	public Matrix4 makeMatrix3() {
		data.put(3, 0);
		data.put(7, 0);
		data.put(11, 0);
		data.put(12, 0);
		data.put(13, 0);
		data.put(14, 0);
		data.put(15, 1);
		return this;
	}

	// Methods for creating special matrices
	public Matrix4 setScale(final Vector s) {
		if (s.dimension() != 3)
			throw new IllegalArgumentException(
					"Scaling only allowed by 3D vectors.");
		return setScale(s.get(0), s.get(1), s.get(2));
	}

	public Matrix4 setScale(float x, float y, float z) {
		return SpecialMatrix.scale(this.makeIdentity(), x, y, z);
	}

	public Matrix4 postMultiplyScale(final Vector s) {
		if (s.dimension() != 3)
			throw new IllegalArgumentException(
					"Scaling only allowed by 3D vectors.");
		return postMultiplyScale(s.get(0), s.get(1), s.get(2));
	}

	public Matrix4 postMultiplyScale(float x, float y, float z) {
		for (int n = 0; n < 12; n += 4) {
			data.put(n, data.get(n) * x);
			data.put(n + 1, data.get(n + 1) * y);
			data.put(n + 2, data.get(n + 2) * z);
		}
		return this;
	}

	public Matrix4 setAxisAngle(final float angle, final Vector a) {
		if (a.dimension() != 3)
			throw new RuntimeException(
					"Rotation is only allowed around 3-D vectors");
		return setAxisAngle(angle, a.get(0), a.get(1), a.get(2));
	}

	public Matrix4 setAxisAngle(final float angle, final float x,
			final float y, final float z) {
		return SpecialMatrix.axisAngle(this.makeIdentity(), angle, x, y, z);
	}

	public Matrix4 setTranslation(final Vector a) {
		if (a.dimension() > 3)
			throw new RuntimeException(
					"Translation is only allowed for vectors of dimension 3 or less");
		return setTranslation(a.dimension() > 0 ? a.get(0) : 0,
				a.dimension() > 1 ? a.get(1) : 0, a.dimension() > 2 ? a.get(2)
						: 0);
	}

	public Matrix4 setTranslation(final VectorBuff3 a) {
		return setTranslation(a.get(0), a.get(1), a.get(2));
	}

	public Matrix4 setTranslation(final float x, final float y, final float z) {
		return SpecialMatrix.translation(this.makeIdentity(), x, y, z);
	}

	public Matrix4 setQuaternion(Vector q) {
		return setQuaternion(q.get(0), q.get(1), q.get(2), q.get(3));
	}

	public Matrix4 setQuaternion(final float q0, final float q1,
			final float q2, final float q3) {
		return SpecialMatrix.quaternion(this.makeIdentity(), q0, q1, q2, q3);
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
		data.put(
				12,
				data.get(0) * x + data.get(4) * y + data.get(8) * z
						+ data.get(12));
		data.put(
				13,
				data.get(1) * x + data.get(5) * y + data.get(9) * z
						+ data.get(13));
		data.put(14, data.get(2) * x + data.get(6) * y + data.get(10) * z
				+ data.get(14));
		data.put(15, data.get(3) * x + data.get(7) * y + data.get(11) * z
				+ data.get(15));
		return this;
	}

	public Matrix4 preMultiplyTransform(float x, float y) {
		data.put(12, data.get(0) * x + data.get(4) * y + data.get(12));
		data.put(13, data.get(1) * x + data.get(5) * y + data.get(13));
		data.put(14, data.get(2) * x + data.get(6) * y + data.get(14));
		data.put(15, data.get(3) * x + data.get(7) * y + data.get(15));
		return this;
	}

	public FloatBuffer getAccessor() {
		data.position(0);
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
						m.data.get(i), m.data.get(i + 4), m.data.get(i + 8),
						m.data.get(i + 12)));
		}
		return res.toString();
	}

	@Override
	public String toString() {
		return toString(this);
	}
}
