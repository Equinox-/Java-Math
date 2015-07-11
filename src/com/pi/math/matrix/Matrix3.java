package com.pi.math.matrix;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import com.pi.math.BufferProvider;
import com.pi.math.vector.Vector;

@SuppressWarnings("rawtypes")
public final class Matrix3 extends Transform<Matrix3> {
	public Matrix3() {
		this(BufferProvider.createFloatBuffer(9), 0);
	}

	public Matrix3(ByteBuffer f, int offset) {
		super(f, offset, 3, 3);
	}

	public Matrix3(FloatBuffer f, int offset) {
		super(f, offset, 3, 3);
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
		for (int i = 0; i < 3; i++) {
			final float ai0 = a.get(0,i), ai1 = a.get(1,i), ai2 = a
					.get(2,i);
			dest.set(0,i, ai0 * b.get(0,0) + ai1 * b.get(0,1)
					+ ai2 * b.get(0,2));
			dest.set(1,i, ai0 * b.get(1,0) + ai1 * b.get(1,1)
					+ ai2 * b.get(1,2));
			dest.set(2,i, ai0 * b.get(2,0) + ai1 * b.get(2,1)
					+ ai2 * b.get(2,2));
		}
	}

	/**
	 * this = b * this
	 * 
	 * @param b
	 */
	@Override
	public Matrix3 multiplyInto(Matrix b) {
		multiplyInto(this, this,  b);
		return this;
	}

	@Override
	public <E extends Vector> E transform(E outset, final Vector inset) {
		for (int k = 0; k < outset.dimension(); k++)
			outset.set(k,
					get(k,0) * inset.get(0) + get(k,1) * inset.get(1)
							+ get(k,2) * inset.get(2));
		return outset;
	}

	private float cofac(int r1, int c1, int r2, int c2) {
		return get(r1, c1) * get(r2, c2) - get(r1, c2) * get(r2, c1);
	}
	
	// Matrix operations
	@Override
	public <R extends Matrix<R>> R invertInto(R res) {
			float co_x = cofac( 1, 1, 2, 2);
			float co_y = cofac( 1, 2, 2, 0);
			float co_z = cofac( 1, 0, 2, 1);

			float det = get(0, 0) * co_x + get(0, 1) * co_y + get(0, 2) * co_z;
			assert(det != 0f);

			float s = 1f / det;
			float m00 = co_x * s;
			float m01 = cofac( 0, 2, 2, 1) * s;
			float m02 = cofac( 0, 1, 1, 2) * s;
			float m10 = co_y * s;
			float m11 = cofac( 0, 0, 2, 2) * s;
			float m12 = cofac( 0, 2, 1, 0) * s;
			float m20 = co_z * s;
			float m21 = cofac( 0, 1, 2, 0) * s;
			float m22 = cofac( 0, 0, 1, 1) * s;

			res.set(0, 0, m00);
			res.set(0, 1, m01);
			res.set(0, 2, m02);
			res.set(1, 0, m10);
			res.set(1, 1, m11);
			res.set(1, 2, m12);
			res.set(2, 0, m20);
			res.set(2, 1, m21);
			res.set(2, 2, m22);
			return res;
	}

	// Stringification
	public static String toString(Matrix3... show) {
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < 3; i++) {
			if (i > 0)
				res.append('\n');
			for (Matrix3 m : show)
				res.append(String.format("%+2.8f %+2.8f %+2.8f        ",
						m.get(i,0), m.get(i,1), m.get(i,2)));
		}
		return res.toString();
	}

	@Override
	public String toString() {
		return toString(this);
	}
}
