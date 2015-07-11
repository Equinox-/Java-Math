package com.pi.math.matrix;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import com.pi.math.vector.Vector;
import com.pi.math.vector.VectorBuff3;

@SuppressWarnings("unchecked")
public abstract class Transform<E extends Transform<?>> extends Matrix<E> {
	Transform(ByteBuffer f, int offset, final int r, final int c) {
		super(f, offset, r, c);
	}

	Transform(FloatBuffer f, int offset, final int r, final int c) {
		super(f, offset, r, c);
	}

	// Methods for creating special matrices
	public E setScale(final Vector s) {
		if (s.dimension() != 3)
			throw new IllegalArgumentException("Scaling only allowed by 3D vectors.");
		return setScale(s.get(0), s.get(1), s.get(2));
	}

	public E setScale(float x, float y, float z) {
		makeIdentity();
		return (E) SpecialMatrix.scale(this, x, y, z);
	}

	public E preMultiplyScale(final Vector s) {
		if (s.dimension() != 3)
			throw new IllegalArgumentException("Scaling only allowed by 3D vectors.");
		return preMultiplyScale(s.get(0), s.get(1), s.get(2));
	}

	public E preMultiplyScale(float x, float y, float z) {
		for (int n = 0; n < 3; n++) {
			set(n, 0, get(n, 0) * x);
			set(n, 1, get(n, 1) * y);
			set(n, 2, get(n, 2) * z);
		}
		return (E) this;
	}

	public E postMultiplyScale(final Vector s) {
		if (s.dimension() != 3)
			throw new IllegalArgumentException("Scaling only allowed by 3D vectors.");
		return postMultiplyScale(s.get(0), s.get(1), s.get(2));
	}

	public E postMultiplyScale(float x, float y, float z) {
		for (int n = 0; n < 3; n++) {
			set(0, n, get(0, n) * x);
			set(1, n, get(1, n) * y);
			set(2, n, get(2, n) * z);
		}
		return (E) this;
	}

	public E setAxisAngle(final float angle, final Vector a) {
		if (a.dimension() != 3)
			throw new RuntimeException("Rotation is only allowed around 3-D vectors");
		return setAxisAngle(angle, a.get(0), a.get(1), a.get(2));
	}

	public E setAxisAngle(final float angle, final float x, final float y, final float z) {
		makeIdentity();
		return (E) SpecialMatrix.axisAngle(this, angle, x, y, z);
	}

	public E setTranslation(final Vector a) {
		if (a.dimension() > 3)
			throw new RuntimeException("Translation is only allowed for vectors of dimension 3 or less");
		return setTranslation(a.dimension() > 0 ? a.get(0) : 0, a.dimension() > 1 ? a.get(1) : 0,
				a.dimension() > 2 ? a.get(2) : 0);
	}

	public E setTranslation(final VectorBuff3 a) {
		return setTranslation(a.get(0), a.get(1), a.get(2));
	}

	public E setTranslation(final float x, final float y, final float z) {
		makeIdentity();
		return (E) SpecialMatrix.translation(this, x, y, z);
	}

	public E setQuaternion(Vector q) {
		return setQuaternion(q.get(0), q.get(1), q.get(2), q.get(3));
	}

	public E setQuaternion(final float q0, final float q1, final float q2, final float q3) {
		makeIdentity();
		return (E) SpecialMatrix.quaternion(this, q0, q1, q2, q3);
	}

	public E setPerspective(final float left, final float right, final float bottom, final float top, final float near,
			final float far) {
		return (E) SpecialMatrix.perspective(this, left, right, bottom, top, near, far);
	}

	public E setOrthographic(final float left, final float right, final float bottom, final float top, final float near,
			final float far) {
		return (E) SpecialMatrix.orthographic(this, left, right, bottom, top, near, far);
	}

	public E preMultiplyTransform(float x, float y, float z) {
		set(0, 3, get(0, 0) * x + get(0, 1) * y + get(0, 2) * z + get(0, 3));
		set(1, 3, get(1, 0) * x + get(1, 1) * y + get(1, 2) * z + get(1, 3));
		set(2, 3, get(2, 0) * x + get(2, 1) * y + get(2, 2) * z + get(2, 3));
		set(3, 3, get(3, 0) * x + get(3, 1) * y + get(3, 2) * z + get(3, 3));
		return (E) this;
	}

	public E preMultiplyTransform(float x, float y) {
		set(0, 3, get(0, 0) * x + get(0, 1) * y + get(0, 3));
		set(1, 3, get(1, 0) * x + get(1, 1) * y + get(1, 3));
		set(2, 3, get(2, 0) * x + get(2, 1) * y + get(2, 3));
		set(3, 3, get(3, 0) * x + get(3, 1) * y + get(3, 3));
		return (E) this;
	}
}
