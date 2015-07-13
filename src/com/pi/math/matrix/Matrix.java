package com.pi.math.matrix;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import com.pi.math.BufferProvider;
import com.pi.math.Heap;
import com.pi.math.vector.Vector;
import com.pi.math.vector.VectorBuff;

@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class Matrix<E extends Matrix<?>> {
	protected final int rows, columns;
	protected final FloatBuffer access;
	protected final VectorBuff[] cols;

	private static final float[] ZERO_CACHE = new float[4 * 4];
	private static final float[][][] IDENTITY_CACHE = new float[4][4][];

	private static FloatBuffer slit(FloatBuffer a, int o, int s) {
		a.position(o);
		a.limit(o + s);
		return a.slice();
	}

	private static FloatBuffer slit(ByteBuffer a, int o, int s) {
		a.position(o);
		a.limit(o + s * 4);
		return a.asFloatBuffer();
	}

	Matrix(FloatBuffer f, final int r, final int c) {
		this.access = f;
		this.rows = r;
		this.columns = c;
		cols = new VectorBuff[c];
		for (int i = 0; i < c; i++)
			cols[i] = VectorBuff.make(f, i * r, r);
	}

	Matrix(int r, int c) {
		this(BufferProvider.createFloatBuffer(r * c), r, c);
	}

	Matrix(ByteBuffer f, int offset, final int r, final int c) {
		this(slit(f, offset, r * c), r, c);
	}

	Matrix(FloatBuffer f, int offset, final int r, final int c) {
		this(slit(f, offset, r * c), r, c);
	}

	public final int rows() {
		return rows;
	}

	public final VectorBuff column(int i) {
		return cols[i];
	}

	public final VectorBuff getRow(int i, VectorBuff v) {
		for (int k = 0; k < Math.min(v.dimension(), columns); k++)
			v.set(k, get(i, k));
		return v;
	}

	public final E setRow(int i, VectorBuff v) {
		for (int k = 0; k < Math.min(v.dimension(), columns); k++)
			set(i, k, v.get(k));
		return (E) this;
	}

	public final E setRow(int i, float... v) {
		for (int k = 0; k < Math.min(v.length, columns); k++)
			set(i, k, v[k]);
		return (E) this;
	}

	public final int columns() {
		return columns;
	}

	public final float get(int r, int c) {
		if (r >= rows || c >= columns)
			return r == c ? 1 : 0;
		return cols[c].get(r);
	}

	public final void safeSet(int r, int c, float v) {
		if (r >= rows || c >= columns)
			return;
		cols[c].set(r, v);
	}

	public final void set(int r, int c, float v) {
		cols[c].set(r, v);
	}

	public final void mod(int r, int c, float v) {
		cols[c].set(r, cols[c].get(r) + v);
	}

	public final float get(int n) {
		return access.get(n);
	}

	public final void set(int n, float v) {
		access.put(n, v);
	}

	public final E makeZero() {
		if (rows * columns <= ZERO_CACHE.length) {
			access.position(0);
			access.put(ZERO_CACHE, 0, rows * columns);
		} else {
			for (int i = 0; i < rows * columns; i++)
				access.put(i, 0);
		}
		return (E) this;
	}

	public final E transposeInPlace() {
		for (int r = 1; r < rows; r++) {
			for (int c = 0; c < r; c++) {
				float a = get(r, c);
				safeSet(r, c, get(c, r));
				safeSet(c, r, a);
			}
		}
		return (E) this;
	}

	public final E makeIdentity() {
		if (rows <= IDENTITY_CACHE.length && columns <= IDENTITY_CACHE[rows - 1].length) {
			if (IDENTITY_CACHE[rows - 1][columns - 1] == null) {
				IDENTITY_CACHE[rows - 1][columns - 1] = new float[rows * columns];
				for (int k = 0; k < Math.min(rows, columns); k++)
					IDENTITY_CACHE[rows - 1][columns - 1][k * rows + k] = 1;
			}
			access.position(0);
			access.put(IDENTITY_CACHE[rows - 1][columns - 1]);
		} else {
			for (int c = 0; c < columns; c++)
				for (int r = 0; r < rows; r++)
					set(r, c, r == c ? 1 : 0);
		}
		return (E) this;
	}

	public final E multiply(Matrix a, float af) {
		for (int c = 0; c < columns; c++)
			for (int r = 0; r < rows; r++)
				set(r, c, a.get(r, c) * af);
		return (E) this;
	}

	public final E multiply(float af) {
		return multiply(this, af);
	}

	public final E add(Matrix a, Matrix b) {
		for (int c = 0; c < columns; c++)
			for (int r = 0; r < rows; r++)
				set(r, c, a.get(r, c) + b.get(r, c));
		return (E) this;
	}

	public final E add(Matrix b) {
		return add(this, b);
	}

	public final E linearComb(float af, Matrix b, float bf) {
		return linearComb(this, af, b, bf);
	}

	public E linearComb(Matrix a, float af, Matrix b, float bf) {
		for (int c = 0; c < columns; c++)
			for (int r = 0; r < rows; r++)
				set(r, c, a.get(r, c) * af + b.get(r, c) * bf);
		return (E) this;
	}

	/**
	 * this = m * this
	 */
	public E preMul(Matrix m) {
		return mul(m, this);
	}

	/**
	 * this = this * m
	 */
	public E postMul(Matrix m) {
		return mul(this, m);
	}

	/**
	 * this = lhs * rhs
	 */
	public final E mul(Matrix lhs, Matrix rhs) {
		if (lhs instanceof Matrix4 || rhs instanceof Matrix4) {
			MatMulAlgs.mul44(this, lhs, rhs);
		} else if (lhs instanceof Matrix34 || rhs instanceof Matrix34) {
			MatMulAlgs.mul34(this, lhs, rhs);
		} else {
			MatMulAlgs.mul33(this, lhs, rhs);
		}
		return (E) this;
	}

	public final E inverseOf(Matrix a) {
		a.invertInto(this);
		return (E) this;
	}

	public final <R extends Matrix<R>> R invertInto(R m) {
		if (this instanceof Matrix3)
			MatInvAlgs.inv33(m, this);
		else if (this instanceof Matrix34)
			MatInvAlgs.inv34(m, this);
		else
			MatInvAlgs.inv44(m, this);
		return m;
	}

	public E set(Matrix m) {
		m.copyTo(this);
		return (E) this;
	}

	public <R extends Matrix> R copyTo(R m) {
		if (m.rows == rows) {
			if (m.columns > columns) {
				// Copying into more columns. Make identity, then copy.
				m.makeIdentity();
				m.access.position(0);
				access.position(0);
				m.access.put(access);
			} else if (m.columns < columns) {
				// Copying into fewer columns. Limit copy.
				m.access.position(0);
				int alo = access.limit();
				access.position(0);
				access.limit(rows * m.columns);
				m.access.put(access);
				access.limit(alo);
			} else {
				// Column count is equal. Verbatim copy.
				m.access.position(0);
				access.position(0);
				m.access.put(access);
			}
		} else {
			// Rows are unclear. Verbatim copy.
			if (m.rows > rows)
				m.makeIdentity(); // Handle filler.
			for (int c = 0; c < columns; c++)
				for (int r = 0; r < rows; r++)
					m.set(r, c, get(r, c));
		}
		return m;
	}

	public abstract <R extends Vector> R transform(final R output, Vector input);

	public <R extends Vector> R transform(final R input) {
		VectorBuff tmp = Heap.checkout(input.dimension());
		transform(tmp, input);
		input.set(tmp);
		Heap.checkin(tmp);
		return input;
	}

	public FloatBuffer accessor() {
		access.position(0);
		return access;
	}

	// Stringification
	public static String toString(Matrix... show) {
		StringBuilder res = new StringBuilder();
		int mxk = 0;
		for (Matrix m : show)
			mxk = Math.max(mxk, m.rows());

		for (int i = 0; i < mxk; i++) {
			if (i > 0)
				res.append('\n');
			for (Matrix m : show) {
				if (i < m.rows()) {
					for (int c = 0; c < m.columns(); c++)
						res.append(String.format("%+2.8f ", m.get(i, c)));
				} else {
					for (int c = 0; c < m.columns(); c++)
						res.append("            ");
				}
				res.append("      ");
			}
		}
		return res.toString();
	}

	@Override
	public String toString() {
		return toString(this);
	}
}
