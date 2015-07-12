package com.pi.math.matrix;

@SuppressWarnings("rawtypes")
public class MatMulAlgs {
	// each i, j entry is given by multiplying the entries
	// Aik (across row i of A) by the entries Bkj (down column j of B)

	static void mul33(Matrix dest, Matrix lhs, Matrix rhs) {
		if (dest.rows() > 3 || dest.columns() > 3)
			dest.makeIdentity();
		if (rhs == dest) {
			for (int i = 0; i < 3; i++) {
				final float bi0 = rhs.get(0, i), bi1 = rhs.get(1, i), bi2 = rhs.get(2, i);
				dest.set(0, i, lhs.get(0, 0) * bi0 + lhs.get(0, 1) * bi1 + lhs.get(0, 2) * bi2);
				dest.set(1, i, lhs.get(1, 0) * bi0 + lhs.get(1, 1) * bi1 + lhs.get(1, 2) * bi2);
				dest.set(2, i, lhs.get(2, 0) * bi0 + lhs.get(2, 1) * bi1 + lhs.get(2, 2) * bi2);
			}
		} else {
			for (int i = 0; i < 3; i++) {
				final float ai0 = lhs.get(i, 0), ai1 = lhs.get(i, 1), ai2 = lhs.get(i, 2);
				dest.set(i, 0, ai0 * rhs.get(0, 0) + ai1 * rhs.get(1, 0) + ai2 * rhs.get(2, 0));
				dest.set(i, 1, ai0 * rhs.get(0, 1) + ai1 * rhs.get(1, 1) + ai2 * rhs.get(2, 1));
				dest.set(i, 2, ai0 * rhs.get(0, 2) + ai1 * rhs.get(1, 2) + ai2 * rhs.get(2, 2));
			}
		}
	}

	static void mul34(Matrix dest, Matrix lhs, Matrix rhs) {
		if (dest.rows() > 3 || dest.columns() > 4)
			dest.makeIdentity();
		if (rhs == dest) {
			for (int i = 0; i < 4; i++) {
				final float bi0 = rhs.get(0, i), bi1 = rhs.get(1, i), bi2 = rhs.get(2, i), bi3 = (i == 3 ? 1 : 0);
				dest.set(0, i, lhs.get(0, 0) * bi0 + lhs.get(0, 1) * bi1 + lhs.get(0, 2) * bi2 + lhs.get(0, 3) * bi3);
				dest.set(1, i, lhs.get(1, 0) * bi0 + lhs.get(1, 1) * bi1 + lhs.get(1, 2) * bi2 + lhs.get(1, 3) * bi3);
				dest.set(2, i, lhs.get(2, 0) * bi0 + lhs.get(2, 1) * bi1 + lhs.get(2, 2) * bi2 + lhs.get(2, 3) * bi3);
			}
		} else {
			for (int i = 0; i < 3; i++) {
				final float ai0 = lhs.get(i, 0), ai1 = lhs.get(i, 1), ai2 = lhs.get(i, 2), ai3 = lhs.get(i, 3);
				dest.set(i, 0, ai0 * rhs.get(0, 0) + ai1 * rhs.get(1, 0) + ai2 * rhs.get(2, 0));
				dest.set(i, 1, ai0 * rhs.get(0, 1) + ai1 * rhs.get(1, 1) + ai2 * rhs.get(2, 1));
				dest.set(i, 2, ai0 * rhs.get(0, 2) + ai1 * rhs.get(1, 2) + ai2 * rhs.get(2, 2));
				dest.set(i, 3, ai0 * rhs.get(0, 3) + ai1 * rhs.get(1, 3) + ai2 * rhs.get(2, 3) + ai3);
			}
		}
	}

	static void mul44(Matrix dest, Matrix lhs, Matrix rhs) {
		if (dest.rows() > 4 || dest.columns() > 4)
			dest.makeIdentity();
		if (rhs == dest) {
			for (int i = 0; i < 4; i++) {
				final float bi0 = rhs.get(0, i), bi1 = rhs.get(1, i), bi2 = rhs.get(2, i), bi3 = rhs.get(3, i);
				dest.set(0, i, lhs.get(0, 0) * bi0 + lhs.get(0, 1) * bi1 + lhs.get(0, 2) * bi2 + lhs.get(0, 3) * bi3);
				dest.set(1, i, lhs.get(1, 0) * bi0 + lhs.get(1, 1) * bi1 + lhs.get(1, 2) * bi2 + lhs.get(1, 3) * bi3);
				dest.set(2, i, lhs.get(2, 0) * bi0 + lhs.get(2, 1) * bi1 + lhs.get(2, 2) * bi2 + lhs.get(2, 3) * bi3);
				dest.set(3, i, lhs.get(3, 0) * bi0 + lhs.get(3, 1) * bi1 + lhs.get(3, 2) * bi2 + lhs.get(3, 3) * bi3);
			}
		} else {
			for (int i = 0; i < 4; i++) {
				final float ai0 = lhs.get(i, 0), ai1 = lhs.get(i, 1), ai2 = lhs.get(i, 2), ai3 = lhs.get(i, 3);
				dest.set(i, 0, ai0 * rhs.get(0, 0) + ai1 * rhs.get(1, 0) + ai2 * rhs.get(2, 0) + ai3 * rhs.get(3, 0));
				dest.set(i, 1, ai0 * rhs.get(0, 1) + ai1 * rhs.get(1, 1) + ai2 * rhs.get(2, 1) + ai3 * rhs.get(3, 1));
				dest.set(i, 2, ai0 * rhs.get(0, 2) + ai1 * rhs.get(1, 2) + ai2 * rhs.get(2, 2) + ai3 * rhs.get(3, 2));
				dest.set(i, 3, ai0 * rhs.get(0, 3) + ai1 * rhs.get(1, 3) + ai2 * rhs.get(2, 3) + ai3 * rhs.get(3, 3));
			}
		}
	}
}
