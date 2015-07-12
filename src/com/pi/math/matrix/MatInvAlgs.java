package com.pi.math.matrix;

import java.util.function.Supplier;

@SuppressWarnings("rawtypes")
// Provided by mesa.
// If the matrix is orthogonal (columns&rows are orthogonal and unit) the
// inverse is the transpose
public class MatInvAlgs {
	private static void inv33Core(Matrix out, Matrix in) {
		float pos, neg, t;
		float det;

		/*
		 * Calculate the determinant of upper left 3x3 submatrix and determine
		 * if the matrix is singular.
		 */
		pos = neg = 0.0f;
		t = in.get(0, 0) * in.get(1, 1) * in.get(2, 2);
		if (t >= 0.0)
			pos += t;
		else
			neg += t;

		t = in.get(1, 0) * in.get(2, 1) * in.get(0, 2);
		if (t >= 0.0)
			pos += t;
		else
			neg += t;

		t = in.get(2, 0) * in.get(0, 1) * in.get(1, 2);
		if (t >= 0.0)
			pos += t;
		else
			neg += t;

		t = -in.get(2, 0) * in.get(1, 1) * in.get(0, 2);
		if (t >= 0.0)
			pos += t;
		else
			neg += t;

		t = -in.get(1, 0) * in.get(0, 1) * in.get(2, 2);
		if (t >= 0.0)
			pos += t;
		else
			neg += t;

		t = -in.get(0, 0) * in.get(2, 1) * in.get(1, 2);
		if (t >= 0.0)
			pos += t;
		else
			neg += t;

		det = pos + neg;

		if (Math.abs(det) < 1e-25)
			throw new IllegalArgumentException("Inv det==0 matrix");

		det = 1.0F / det;
		out.set(0, 0, ((in.get(1, 1) * in.get(2, 2) - in.get(2, 1) * in.get(1, 2)) * det));
		out.set(0, 1, (-(in.get(0, 1) * in.get(2, 2) - in.get(2, 1) * in.get(0, 2)) * det));
		out.set(0, 2, ((in.get(0, 1) * in.get(1, 2) - in.get(1, 1) * in.get(0, 2)) * det));
		out.set(1, 0, (-(in.get(1, 0) * in.get(2, 2) - in.get(2, 0) * in.get(1, 2)) * det));
		out.set(1, 1, ((in.get(0, 0) * in.get(2, 2) - in.get(2, 0) * in.get(0, 2)) * det));
		out.set(1, 2, (-(in.get(0, 0) * in.get(1, 2) - in.get(1, 0) * in.get(0, 2)) * det));
		out.set(2, 0, ((in.get(1, 0) * in.get(2, 1) - in.get(2, 0) * in.get(1, 1)) * det));
		out.set(2, 1, (-(in.get(0, 0) * in.get(2, 1) - in.get(2, 0) * in.get(0, 1)) * det));
		out.set(2, 2, ((in.get(0, 0) * in.get(1, 1) - in.get(1, 0) * in.get(0, 1)) * det));
	}

	static void inv33(Matrix out, Matrix in) {
		if (out.rows() > 3 || out.columns() > 3)
			out.makeIdentity();
		inv33Core(out, in);
	}

	static void inv34(Matrix out, Matrix in) {
		if (out.rows() > 3 || out.columns() > 4)
			out.makeIdentity();
		inv33(out, in);

		/* Do the translation part */
		out.set(0, 3, -(in.get(0, 3) * out.get(0, 0) + in.get(1, 3) * out.get(0, 1) + in.get(2, 3) * out.get(0, 2)));
		out.set(1, 3, -(in.get(0, 3) * out.get(1, 0) + in.get(1, 3) * out.get(1, 1) + in.get(2, 3) * out.get(1, 2)));
		out.set(2, 3, -(in.get(0, 3) * out.get(2, 0) + in.get(1, 3) * out.get(2, 1) + in.get(2, 3) * out.get(2, 2)));
	}

	private static final void swap(float[][] t, int a, int b) {
		float[] k = t[a];
		t[a] = t[b];
		t[b] = k;
	}

	private static final ThreadLocal<float[][]> localRows = ThreadLocal.withInitial(new Supplier<float[][]>() {
		@Override
		public float[][] get() {
			return new float[4][8];
		}
	});

	static void inv44(Matrix out, Matrix m) {
		if (out.rows() > 4 || out.columns() > 4)
			out.makeIdentity();
		float m0, m1, m2, m3, s;

		// GLfloat wtmp[4][8];
		// tmpl[0] = wtmp[0], tmpl[1] = wtmp[1], tmpl[2] = wtmp[2], tmpl[3] =
		// wtmp[3];
		float[][] r = localRows.get();
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++) {
				r[i][j] = m.get(i, j);
				r[i][j + 4] = j == i ? 1 : 0;
			}

		/* choose pivot - or die */
		if (Math.abs(r[3][0]) > Math.abs(r[2][0]))
			swap(r, 3, 2);
		if (Math.abs(r[2][0]) > Math.abs(r[1][0]))
			swap(r, 2, 1);
		if (Math.abs(r[1][0]) > Math.abs(r[0][0]))
			swap(r, 1, 0);
		if (0.0 == r[0][0])
			throw new IllegalArgumentException("Bad input matrix");

		/* eliminate first variable */
		m1 = r[1][0] / r[0][0];
		m2 = r[2][0] / r[0][0];
		m3 = r[3][0] / r[0][0];
		s = r[0][1];
		r[1][1] -= m1 * s;
		r[2][1] -= m2 * s;
		r[3][1] -= m3 * s;
		s = r[0][2];
		r[1][2] -= m1 * s;
		r[2][2] -= m2 * s;
		r[3][2] -= m3 * s;
		s = r[0][3];
		r[1][3] -= m1 * s;
		r[2][3] -= m2 * s;
		r[3][3] -= m3 * s;
		s = r[0][4];
		if (s != 0.0) {
			r[1][4] -= m1 * s;
			r[2][4] -= m2 * s;
			r[3][4] -= m3 * s;
		}
		s = r[0][5];
		if (s != 0.0) {
			r[1][5] -= m1 * s;
			r[2][5] -= m2 * s;
			r[3][5] -= m3 * s;
		}
		s = r[0][6];
		if (s != 0.0) {
			r[1][6] -= m1 * s;
			r[2][6] -= m2 * s;
			r[3][6] -= m3 * s;
		}
		s = r[0][7];
		if (s != 0.0) {
			r[1][7] -= m1 * s;
			r[2][7] -= m2 * s;
			r[3][7] -= m3 * s;
		}

		/* choose pivot - or die */
		if (Math.abs(r[3][1]) > Math.abs(r[2][1]))
			swap(r, 3, 2);
		if (Math.abs(r[2][1]) > Math.abs(r[1][1]))
			swap(r, 2, 1);
		if (0.0 == r[1][1])
			throw new IllegalArgumentException("Bad input matrix");

		/* eliminate second variable */
		m2 = r[2][1] / r[1][1];
		m3 = r[3][1] / r[1][1];
		r[2][2] -= m2 * r[1][2];
		r[3][2] -= m3 * r[1][2];
		r[2][3] -= m2 * r[1][3];
		r[3][3] -= m3 * r[1][3];
		s = r[1][4];
		if (0.0 != s) {
			r[2][4] -= m2 * s;
			r[3][4] -= m3 * s;
		}
		s = r[1][5];
		if (0.0 != s) {
			r[2][5] -= m2 * s;
			r[3][5] -= m3 * s;
		}
		s = r[1][6];
		if (0.0 != s) {
			r[2][6] -= m2 * s;
			r[3][6] -= m3 * s;
		}
		s = r[1][7];
		if (0.0 != s) {
			r[2][7] -= m2 * s;
			r[3][7] -= m3 * s;
		}

		/* choose pivot - or die */
		if (Math.abs(r[3][2]) > Math.abs(r[2][2]))
			swap(r, 3, 2);
		if (0.0 == r[2][2])
			throw new IllegalArgumentException("Bad input matrix");

		/* eliminate third variable */
		m3 = r[3][2] / r[2][2];
		r[3][3] -= m3 * r[2][3];
		r[3][4] -= m3 * r[2][4];
		r[3][5] -= m3 * r[2][5];
		r[3][6] -= m3 * r[2][6];
		r[3][7] -= m3 * r[2][7];

		/* last check */
		if (0.0 == r[3][3])
			throw new IllegalArgumentException("Bad input matrix");

		s = 1.0F / r[3][3]; /* now back substitute row 3 */
		r[3][4] *= s;
		r[3][5] *= s;
		r[3][6] *= s;
		r[3][7] *= s;

		m2 = r[2][3]; /* now back substitute row 2 */
		s = 1.0F / r[2][2];
		r[2][4] = s * (r[2][4] - r[3][4] * m2);
		r[2][5] = s * (r[2][5] - r[3][5] * m2);
		r[2][6] = s * (r[2][6] - r[3][6] * m2);
		r[2][7] = s * (r[2][7] - r[3][7] * m2);
		m1 = r[1][3];
		r[1][4] -= r[3][4] * m1;
		r[1][5] -= r[3][5] * m1;
		r[1][6] -= r[3][6] * m1;
		r[1][7] -= r[3][7] * m1;
		m0 = r[0][3];
		r[0][4] -= r[3][4] * m0;
		r[0][5] -= r[3][5] * m0;
		r[0][6] -= r[3][6] * m0;
		r[0][7] -= r[3][7] * m0;

		m1 = r[1][2]; /* now back substitute row 1 */
		s = 1.0F / r[1][1];
		r[1][4] = s * (r[1][4] - r[2][4] * m1);
		r[1][5] = s * (r[1][5] - r[2][5] * m1);
		r[1][6] = s * (r[1][6] - r[2][6] * m1);
		r[1][7] = s * (r[1][7] - r[2][7] * m1);
		m0 = r[0][2];
		r[0][4] -= r[2][4] * m0;
		r[0][5] -= r[2][5] * m0;
		r[0][6] -= r[2][6] * m0;
		r[0][7] -= r[2][7] * m0;

		m0 = r[0][1]; /* now back substitute row 0 */
		s = 1.0F / r[0][0];
		r[0][4] = s * (r[0][4] - r[1][4] * m0);
		r[0][5] = s * (r[0][5] - r[1][5] * m0);
		r[0][6] = s * (r[0][6] - r[1][6] * m0);
		r[0][7] = s * (r[0][7] - r[1][7] * m0);

		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				out.set(i, j, r[i][j + 4]);
	}
}
