package com.pi.math.matrix;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import com.pi.math.BufferProvider;
import com.pi.math.vector.Vector;
import com.pi.math.vector.VectorBuff3;
import com.pi.math.vector.VectorBuff4;

@SuppressWarnings("rawtypes")
public final class Matrix4 extends Transform<Matrix4> {
	public Matrix4() {
		this(BufferProvider.createFloatBuffer(16), 0);
	}

	public Matrix4(ByteBuffer f, int offset) {
		super(f,offset,4,4);
	}

	public Matrix4(FloatBuffer f, int offset) {
		super(f,offset,4,4);
	}

	// Math operations

	/**
	 * dest = b * a
	 * 
	 * @param dest
	 * @param a
	 * @param b
	 */
	public static void multiplyInto(final Matrix dest, final Matrix a,
			final Matrix b) {
		for (int i = 0; i < 4; i++) {
			final float ai0 = a.get(0,i), ai1 = a.get(1,i), ai2 = a
					.get(2,i), ai3 = a.get(3,i);
			dest.set(0,i, ai0 * b.get(0,0) + ai1 * b.get(0,1)
					+ ai2 * b.get(0,2) + ai3 * b.get(0,3));
			dest.set(1,i, ai0 * b.get(1,0) + ai1 * b.get(1,1)
					+ ai2 * b.get(8 + 1) + ai3 * b.get(1,3));
			dest.set(2,i, ai0 * b.get(2,0) + ai1 * b.get(2,1)
					+ ai2 * b.get(2,2) + ai3 * b.get(2,3));
			dest.set(3,i, ai0 * b.get(3,0) + ai1 * b.get(3,1)
					+ ai2 * b.get(3,2) + ai3 * b.get(3,3));
		}
	}

	/**
	 * this = b * this
	 * 
	 * @param b
	 */
	public Matrix4 multiplyInto(Matrix b) {
		multiplyInto(this, this,  b);
		return this;
	}

	public <E extends Vector> E transform(E outset, final Vector inset) {
		final float inW = inset.dimension() < 4 ? 1 : inset.get(3);
		for (int k = 0; k < outset.dimension(); k++)
			outset.set(k,
					get(k,0) * inset.get(0) + get(k,1) * inset.get(1)
							+ get(k,2) * inset.get(2) + get(k,3)
							* inW);
		return outset;
	}

	public Vector transform3(Vector outset, final Vector inset) {
		for (int k = 0; k < outset.dimension(); k++)
			outset.set(k,
					get(k) * inset.get(0) + get(4 + k) * inset.get(1)
							+ get(8 + k) * inset.get(2));
		return outset;
	}

	public VectorBuff3 transform4(VectorBuff3 outset, final VectorBuff3 inset) {
		for (int k = 0; k < 3; k++)
			outset.set(k,
					get(k) * inset.get(0) + get(4 + k) * inset.get(1)
							+ get(8 + k) * inset.get(2) + get(12 + k));
		return outset;
	}

	public VectorBuff3 transform3(VectorBuff3 outset, final VectorBuff3 inset) {
		for (int k = 0; k < 3; k++)
			outset.set(k,
					get(k) * inset.get(0) + get(4 + k) * inset.get(1)
							+ get(8 + k) * inset.get(2));
		return outset;
	}

	// Matrix operations
	@Override
	public <R extends Matrix> R invertInto(R res) {
		res.set(0, get(5) * get(10) * get(15) - get(5)
				* get(11) * get(14) - get(9) * get(6)
				* get(15) + get(9) * get(7) * get(14)
				+ get(13) * get(6) * get(11) - get(13)
				* get(7) * get(10));

		res.set(
				4,
				-get(4) * get(10) * get(15) + get(4)
						* get(11) * get(14) + get(8)
						* get(6) * get(15) - get(8)
						* get(7) * get(14) - get(12)
						* get(6) * get(11) + get(12)
						* get(7) * get(10));

		res.set(8, get(4) * get(9) * get(15) - get(4)
				* get(11) * get(13) - get(8) * get(5)
				* get(15) + get(8) * get(7) * get(13)
				+ get(12) * get(5) * get(11) - get(12)
				* get(7) * get(9));

		res.set(
				12,
				-get(4) * get(9) * get(14) + get(4)
						* get(10) * get(13) + get(8)
						* get(5) * get(14) - get(8)
						* get(6) * get(13) - get(12)
						* get(5) * get(10) + get(12)
						* get(6) * get(9));

		res.set(
				1,
				-get(1) * get(10) * get(15) + get(1)
						* get(11) * get(14) + get(9)
						* get(2) * get(15) - get(9)
						* get(3) * get(14) - get(13)
						* get(2) * get(11) + get(13)
						* get(3) * get(10));

		res.set(5, get(0) * get(10) * get(15) - get(0)
				* get(11) * get(14) - get(8) * get(2)
				* get(15) + get(8) * get(3) * get(14)
				+ get(12) * get(2) * get(11) - get(12)
				* get(3) * get(10));

		res.set(9, -get(0) * get(9) * get(15) + get(0)
				* get(11) * get(13) + get(8) * get(1)
				* get(15) - get(8) * get(3) * get(13)
				- get(12) * get(1) * get(11) + get(12)
				* get(3) * get(9));

		res.set(13, get(0) * get(9) * get(14) - get(0)
				* get(10) * get(13) - get(8) * get(1)
				* get(14) + get(8) * get(2) * get(13)
				+ get(12) * get(1) * get(10) - get(12)
				* get(2) * get(9));

		res.set(
				2,
				get(1) * get(6) * get(15) - get(1)
						* get(7) * get(14) - get(5)
						* get(2) * get(15) + get(5)
						* get(3) * get(14) + get(13)
						* get(2) * get(7) - get(13)
						* get(3) * get(6));

		res.set(
				6,
				-get(0) * get(6) * get(15) + get(0)
						* get(7) * get(14) + get(4)
						* get(2) * get(15) - get(4)
						* get(3) * get(14) - get(12)
						* get(2) * get(7) + get(12)
						* get(3) * get(6));

		res.set(
				10,
				get(0) * get(5) * get(15) - get(0)
						* get(7) * get(13) - get(4)
						* get(1) * get(15) + get(4)
						* get(3) * get(13) + get(12)
						* get(1) * get(7) - get(12)
						* get(3) * get(5));

		res.set(
				14,
				-get(0) * get(5) * get(14) + get(0)
						* get(6) * get(13) + get(4)
						* get(1) * get(14) - get(4)
						* get(2) * get(13) - get(12)
						* get(1) * get(6) + get(12)
						* get(2) * get(5));

		res.set(
				3,
				-get(1) * get(6) * get(11) + get(1)
						* get(7) * get(10) + get(5)
						* get(2) * get(11) - get(5)
						* get(3) * get(10) - get(9)
						* get(2) * get(7) + get(9) * get(3)
						* get(6));

		res.set(
				7,
				get(0) * get(6) * get(11) - get(0)
						* get(7) * get(10) - get(4)
						* get(2) * get(11) + get(4)
						* get(3) * get(10) + get(8)
						* get(2) * get(7) - get(8) * get(3)
						* get(6));

		res.set(
				11,
				-get(0) * get(5) * get(11) + get(0)
						* get(7) * get(9) + get(4) * get(1)
						* get(11) - get(4) * get(3)
						* get(9) - get(8) * get(1) * get(7)
						+ get(8) * get(3) * get(5));

		res.set(
				15,
				get(0) * get(5) * get(10) - get(0)
						* get(6) * get(9) - get(4) * get(1)
						* get(10) + get(4) * get(2)
						* get(9) + get(8) * get(1) * get(6)
						- get(8) * get(2) * get(5));

		float det = get(0) * res.get(0) + get(1)
				* res.get(4) + get(2) * res.get(8) + get(3)
				* res.get(12);

		if (det == 0) {
			throw new IllegalArgumentException("Invert det=0 mat\n");
		}
		det = 1.0f / det;

		for (int i = 0; i < 16; i++)
			res.set(i, res.get(i) * (det));
		return res;
	}


	public Matrix4 makeMatrix3() {
		set(3, 0);
		set(7, 0);
		set(11, 0);
		set(12, 0);
		set(13, 0);
		set(14, 0);
		set(15, 1);
		return this;
	}

	// Stringification
	public static String toString(Matrix4... show) {
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			if (i > 0)
				res.append('\n');
			for (Matrix4 m : show)
				res.append(String.format("%+2.8f %+2.8f %+2.8f %+2.8f        ",
						m.get(i), m.get(i + 4), m.get(i + 8),
						m.get(i + 12)));
		}
		return res.toString();
	}

	@Override
	public String toString() {
		return toString(this);
	}
}
