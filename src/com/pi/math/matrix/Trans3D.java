package com.pi.math.matrix;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import com.pi.math.EpsMath;
import com.pi.math.Heap;
import com.pi.math.vector.Vector;
import com.pi.math.vector.VectorBuff;
import com.pi.math.vector.VectorBuff3;

@SuppressWarnings("unchecked")
public abstract class Trans3D<E extends Trans3D<?>> extends Matrix<E> {

	static final int FLAG_IDENTITY = 0;
	static final int FLAG_ROTATION = 1;
	static final int FLAG_TRANSLATION = 2;
	static final int FLAG_SCALING = 4;
	static final int FLAG_GENERAL = 8;

	static final int FLAG_ROTATION_AND_SCALE = FLAG_ROTATION | FLAG_SCALING;
	int flags;
	final VectorBuff[] column3;

	Trans3D(ByteBuffer f, int offset, final int r, final int c) {
		super(f, offset, r, c);
		flags = FLAG_GENERAL;
		if (r > 3) {
			column3 = new VectorBuff3[c];
			for (int i = 0; i < c; i++)
				column3[i] = new VectorBuff3(column(i).getAccessor(), 0);
		} else
			column3 = super.cols;
	}

	Trans3D(FloatBuffer f, int offset, final int r, final int c) {
		super(f, offset, r, c);
		flags = FLAG_GENERAL;
		if (r > 3) {
			column3 = new VectorBuff3[c];
			for (int i = 0; i < c; i++)
				column3[i] = new VectorBuff3(column(i).getAccessor(), 0);
		} else
			column3 = super.cols;
	}

	public E addTranslation(final float x, final float y, final float z) {
		return (E) SpecialMatrix.translationAdd(this, x, y, z);
	}

	public E addTranslation(final Vector a) {
		return addTranslation(a.get(0), a.get(1), a.get(2));
	}

	private int computeFlags() {
		// Is identity?
		{
			boolean id = true;
			for (int r = 0; r < rows; r++)
				for (int c = 0; c < columns; c++)
					if (get(r, c) != (r == c ? 1 : 0))
						id = false;
			if (id)
				return FLAG_IDENTITY;
		}
		int flags = 0;
		if (columns > 3 && column(3).mag2() > 1)
			flags |= FLAG_TRANSLATION;
		// X cross Y = Z
		VectorBuff3 x = (VectorBuff3) column3[0];
		VectorBuff3 y = (VectorBuff3) column3[1];
		VectorBuff3 z = (VectorBuff3) column3[2];
		if (x.mag2() == 0 || y.mag2() == 0 || z.mag2() == 0)
			return FLAG_GENERAL;
		VectorBuff3 tmp = Heap.checkout3().cross(x, y);
		tmp.multiply(z.magnitude() / tmp.magnitude());
		if (tmp.equals(z)) {
			if (x.get(1) != 0 || x.get(2) != 0 || y.get(0) != 0 || y.get(2) != 0 || z.get(0) != 0 || z.get(1) != 0)
				flags |= FLAG_ROTATION;
		} else
			flags |= FLAG_GENERAL;
		Heap.checkin(tmp);
		if (!EpsMath.eq(x.mag2(), 1) || !EpsMath.eq(y.mag2(), 1) || !EpsMath.eq(z.mag2(), 1))
			flags |= FLAG_SCALING;

		// Is the bottom right for a non-general matrix?
		if (get(3, 0) != 0 || get(3, 1) != 0 || get(3, 2) != 0 || get(3, 3) != 1)
			flags |= FLAG_GENERAL;
		return flags;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public <R extends Matrix<R>> R copyTo(R m) {
		if (m instanceof Trans3D)
			((Trans3D) m).flags = flags;
		return super.copyTo(m);
	}

	public void flagTranslation() {
		flags |= FLAG_TRANSLATION;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public final <R extends Matrix<R>> R invertInto(R k) {
		if (!(k instanceof Trans3D))
			return super.invertInto(k);
		Trans3D m = (Trans3D) k;
		if ((flags & FLAG_GENERAL) == FLAG_GENERAL || (flags & FLAG_SCALING) == FLAG_SCALING) {
			m.flags = FLAG_GENERAL;
			return (R) super.invertInto(m);
		} else if (flags == FLAG_IDENTITY)
			return (R) m.makeIdentity();
		m.makeIdentity();
		if ((flags & FLAG_ROTATION) == FLAG_ROTATION) {
			limitedTransposeInto(m, 3, 3);
			m.flags |= FLAG_ROTATION;
		}
		if ((flags & FLAG_TRANSLATION) == FLAG_TRANSLATION && k.columns() > 3)
			m.preMultiplyTransform(-get(0, 3), -get(1, 3), -get(2, 3));
		return (R) m;
	}

	// Makes this matrix a dirty matrix for low-performance inversions and
	// multiplications.
	public E makeDirty() {
		flags = FLAG_GENERAL;
		return (E) this;
	}

	// Overrides
	@Override
	public E makeIdentity() {
		if (flags == FLAG_IDENTITY)
			return (E) this;
		flags = FLAG_IDENTITY;
		return super.makeIdentity();
	}

	@Override
	public E makeZero() {
		flags = FLAG_GENERAL;
		return super.makeZero();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public final E mul(Matrix lhsm, Matrix rhsm) {
		if (!(lhsm instanceof Trans3D) || !(rhsm instanceof Trans3D)) {
			flags = FLAG_GENERAL;
			return super.mul(lhsm, rhsm);
		}

		Trans3D lhs = (Trans3D) lhsm;
		Trans3D rhs = (Trans3D) rhsm;
		final int flhs = lhs.flags;
		final int frhs = rhs.flags;
		if ((flhs & FLAG_GENERAL) == FLAG_GENERAL || (frhs & FLAG_GENERAL) == FLAG_GENERAL) {
			flags = FLAG_GENERAL;
			super.mul(lhs, rhs);
			return (E) this;
		}
		// Two translations/identities. Result = translation sum
		if ((flhs & ~FLAG_TRANSLATION) == 0 && (frhs & ~FLAG_TRANSLATION) == 0) {
			if ((flhs & FLAG_TRANSLATION) > 0 || (frhs & FLAG_TRANSLATION) > 0)
				setTranslation(lhs.get(0, 3) + rhs.get(0, 3), lhs.get(1, 3) + rhs.get(1, 3),
						lhs.get(2, 3) + rhs.get(2, 3));
			else
				makeIdentity();
			return (E) this;
		}

		VectorBuff3 tmp = null;
		if (rhs.columns > 3)
			tmp = (VectorBuff3) lhs.transform4(Heap.checkout3(), rhs.column(3));
		else if (lhs.columns > 3)
			tmp = Heap.checkout3().set(lhs.column(3));

		if ((flhs & FLAG_ROTATION_AND_SCALE) > 0 && (frhs & FLAG_ROTATION_AND_SCALE) > 0) {
			MatMulAlgs.mul33(this, lhs, rhs);
		} else if ((flhs & FLAG_ROTATION_AND_SCALE) > 0) {
			set(lhs);
		} else if ((frhs & FLAG_ROTATION_AND_SCALE) > 0) {
			set(rhs);
		} else {
			makeIdentity();
		}
		if (tmp != null) {
			SpecialMatrix.translation(this, tmp.get(0), tmp.get(1), tmp.get(2));
			Heap.checkin(tmp);
		} else if (columns > 3) {
			SpecialMatrix.translation(this, 0, 0, 0);
		}
		flags = flhs | frhs;
		return (E) this;
	}

	public final <R extends Trans3D<R>> R normalInto(R m) {
		if ((flags & FLAG_GENERAL) == FLAG_GENERAL || (flags & FLAG_SCALING) == FLAG_SCALING) {
			m.flags = FLAG_GENERAL;
			super.invertInto(m).transposeInPlace();
			if (m.columns() > 3)
				m.setTranslation(0, 0, 0);
			if (m.rows() > 3)
				for (int l = 0; l < 3; l++)
					m.set(3, l, 0);
			return m;
		} else if (flags == FLAG_IDENTITY)
			return m.makeIdentity();
		copyTo(m);
		if (m.columns > 3)
			if (m.rows > 3)
				m.setTranslation(0, 0, 0);
			else
				m.setTranslation(0, 0, 0);
		return m;
	}

	public E postMultiplyScale(float x, float y, float z) {
		if (x != 1 || y != 1 || z != 1)
			flags |= Trans3D.FLAG_SCALING;
		else if (x == 1 && y == 1 && z == 1)
			flags &= ~Trans3D.FLAG_SCALING;

		for (int n = 0; n < 3; n++) {
			set(0, n, get(0, n) * x);
			set(1, n, get(1, n) * y);
			set(2, n, get(2, n) * z);
		}
		return (E) this;
	}

	public E postMultiplyScale(final Vector s) {
		if (s.dimension() != 3)
			throw new IllegalArgumentException("Scaling only allowed by 3D vectors.");
		return postMultiplyScale(s.get(0), s.get(1), s.get(2));
	}

	public E preMultiplyScale(float x, float y, float z) {
		if (x != 1 || y != 1 || z != 1)
			flags |= Trans3D.FLAG_SCALING;
		else if (x == 1 && y == 1 && z == 1)
			flags &= ~Trans3D.FLAG_SCALING;

		for (int n = 0; n < 3; n++) {
			set(n, 0, get(n, 0) * x);
			set(n, 1, get(n, 1) * y);
			set(n, 2, get(n, 2) * z);
		}
		return (E) this;
	}

	public E preMultiplyScale(final Vector s) {
		return preMultiplyScale(s.get(0), s.get(1), s.get(2));
	}

	public E preMultiplyTransform(float x, float y) {
		if (flags == FLAG_IDENTITY) {
			VectorBuff vk = column(3);
			vk.mod(0, x);
			vk.mod(1, y);
		} else {
			set(0, 3, get(0, 0) * x + get(0, 1) * y + get(0, 3));
			set(1, 3, get(1, 0) * x + get(1, 1) * y + get(1, 3));
			set(2, 3, get(2, 0) * x + get(2, 1) * y + get(2, 3));
			set(3, 3, get(3, 0) * x + get(3, 1) * y + get(3, 3));
		}
		if (x != 0 || y != 0)
			flags |= FLAG_TRANSLATION;
		return (E) this;
	}

	public E preMultiplyTransform(float x, float y, float z) {
		if (flags == FLAG_IDENTITY) {
			VectorBuff vk = column(3);
			vk.mod(0, x);
			vk.mod(1, y);
			vk.mod(2, z);
		} else {
			set(0, 3, get(0, 0) * x + get(0, 1) * y + get(0, 2) * z + get(0, 3));
			set(1, 3, get(1, 0) * x + get(1, 1) * y + get(1, 2) * z + get(1, 3));
			set(2, 3, get(2, 0) * x + get(2, 1) * y + get(2, 2) * z + get(2, 3));
			set(3, 3, get(3, 0) * x + get(3, 1) * y + get(3, 2) * z + get(3, 3));
		}
		if (x != 0 || y != 0 || z != 0)
			flags |= FLAG_TRANSLATION;
		return (E) this;
	}

	public E setAxisAngle(final float angle, final float x, final float y, final float z) {
		makeIdentity();
		return (E) SpecialMatrix.axisAngle(this, angle, x, y, z);
	}

	public E setAxisAngle(final float angle, final Vector a) {
		return setAxisAngle(angle, a.get(0), a.get(1), a.get(2));
	}

	public void setFlags() {
		flags = computeFlags();
	}

	public E setQuaternion(final float q0, final float q1, final float q2, final float q3) {
		makeIdentity();
		return (E) SpecialMatrix.quaternion(this, q0, q1, q2, q3);
	}

	public E setQuaternion(Vector q) {
		return setQuaternion(q.get(0), q.get(1), q.get(2), q.get(3));
	}

	public E setScale(float x, float y, float z) {
		makeIdentity();
		return (E) SpecialMatrix.scale(this, x, y, z);
	}

	// Methods for creating special matrices
	public E setScale(final Vector s) {
		if (s.dimension() != 3)
			throw new IllegalArgumentException("Scaling only allowed by 3D vectors.");
		return setScale(s.get(0), s.get(1), s.get(2));
	}

	public E setTranslation(final float x, final float y, final float z) {
		makeIdentity();
		return (E) SpecialMatrix.translation(this, x, y, z);
	}

	public E setTranslation(final Vector a) {
		makeIdentity();
		return (E) SpecialMatrix.translation(this, a);
	}

	public E setTranslation(final VectorBuff3 a) {
		makeIdentity();
		return (E) SpecialMatrix.translation(this, a);
	}

	// private void checkFlags() {
	// if ((flags & FLAG_GENERAL) > 0)
	// return;// Generals have no constraint.
	// int expect = computeFlags();
	// if ((expect | (flags & FLAG_ROTATION)) != flags && expect != 0) {
	// System.err.println("Bad flag control: Have: " +
	// Integer.toBinaryString(flags) + " should be "
	// + Integer.toBinaryString(expect));
	// System.err.println(this);
	// new Exception().printStackTrace();
	// System.err.println();
	// }
	// flags = expect;
	// }
}
