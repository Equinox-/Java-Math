package com.pi.math.matrix;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import com.pi.math.BufferProvider;
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

	public final E multiply(float af) {
		for (int c = 0; c < columns; c++)
			for (int r = 0; r < rows; r++)
				set(r, c, get(r, c) * af);
		return (E) this;
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

	public abstract E multiplyInto(Matrix m);

	public abstract <R extends Matrix<R>> R invertInto(R m);

	public <R extends Matrix<R>> R copyTo(R m) {
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

	public FloatBuffer accessor() {
		access.position(0);
		return access;
	}
}