package com.pi.math.vector;

import com.pi.math.FastMath;
import com.pi.math.MathUtil;

public abstract class Vector {
	private static final boolean DIMENSION_CHECK = false;
	public static boolean ALLOW_ALLOCATION = true;

	protected Vector() {
		if (!Vector.ALLOW_ALLOCATION)
			throw new RuntimeException("No allocation atm");
	}

	public abstract float get(int d);

	public abstract void set(int d, float r);

	public void mod(int d, float f) {
		set(d, get(d) + f);
	}

	public abstract int dimension();

	public final void check(Vector t) {
		if (DIMENSION_CHECK) {
			if (dimension() != t.dimension())
				throw new IllegalArgumentException("Mismatch Dimensions");
		}
	}

	public Vector scale(float f, Vector v) {
		check(v);
		for (int i = 0; i < dimension(); i++)
			set(i, f * v.get(i));
		return this;
	}

	public Vector negate(Vector v) {
		check(v);
		for (int i = 0; i < dimension(); i++)
			set(i, -v.get(i));
		return this;
	}

	public Vector sub(Vector lhs, Vector rhs) {
		return linearComb(lhs, 1, rhs, -1);
	}

	public Vector add(Vector lhs, Vector rhs) {
		return linearComb(lhs, 1, rhs, 1);
	}

	public Vector add(Vector r) {
		check(r);
		for (int i = 0; i < dimension(); i++)
			set(i, get(i) + r.get(i));
		return this;
	}

	public Vector subtract(Vector r) {
		check(r);
		for (int i = 0; i < dimension(); i++)
			set(i, get(i) - r.get(i));
		return this;
	}

	public Vector scale(float f) {
		return multiply(f);
	}
	
	public Vector scaleAdd(float s, Vector t1, Vector t2) {
		return linearComb(t1, s, t2, 1);
	}

	public Vector multiply(float f) {
		for (int i = 0; i < dimension(); i++)
			set(i, get(i) * f);
		return this;
	}

	public final float magnitude() {
		return (float) Math.sqrt(mag2());
	}

	public final float dist(Vector t) {
		return (float) Math.sqrt(distSquared(t));
	}

	public float distSquared(Vector t) {
		check(t);
		float r = 0;
		for (int i = 0; i < dimension(); i++) {
			final float delta = get(i) - t.get(i);
			r += delta * delta;
		}
		return r;
	}

	public final Vector normalize(Vector v) {
		return linearComb(0, v, FastMath.Q_rsqrt(v.mag2()));
	}

	public final Vector normalize() {
		return multiply(FastMath.Q_rsqrt(mag2()));
	}

	public float mag2() {
		float r = 0;
		for (int i = 0; i < dimension(); i++) {
			r += get(i) * get(i);
		}
		return r;
	}

	public Vector setV(float... components) {
		for (int i = 0; i < Math.min(dimension(), components.length); i++)
			set(i, components[i]);
		return this;
	}

	public Vector set(Vector t) {
		for (int i = 0; i < dimension(); i++) {
			set(i, t.get(i));
		}
		return this;
	}

	public float dot(Vector v) {
		float r = 0;
		for (int i = 0; i < dimension(); i++) {
			r += get(i) * v.get(i);
		}
		return r;
	}

	public final float angle(Vector v) {
		return (float) Math.acos(dot(v) * FastMath.Q_rsqrt(mag2()) * FastMath.Q_rsqrt(v.mag2()));
	}

	public Vector linearComb(float aC, Vector b, float bC) {
		return linearComb(this, aC, b, bC);
	}

	public static float dotProduct(Vector u, Vector v) {
		u.check(v);
		return u.dot(v);
	}

	public Vector linearComb(Vector a, float aC, Vector b, float bC) {
		check(b);
		for (int i = 0; i < Math.min(a.dimension(), b.dimension()); i++)
			set(i, a.get(i) * aC + b.get(i) * bC);
		return this;
	}

	public static Vector crossProduct(Vector dest, Vector u, Vector v) {
		if (u.dimension() != 3 || v.dimension() != 3 || dest.dimension() != 3)
			throw new IllegalArgumentException("Cross product is only valid in three dimensions");
		return dest.setV((u.get(1) * v.get(2)) - (u.get(2) * v.get(1)), (u.get(2) * v.get(0)) - (u.get(0) * v.get(2)),
				(u.get(0) * v.get(1)) - (u.get(1) * v.get(0)));
	}

	public static VectorBuff3 crossProduct(VectorBuff3 dest, VectorBuff3 u, VectorBuff3 v) {
		if (u.dimension() != 3 || v.dimension() != 3 || dest.dimension() != 3)
			throw new IllegalArgumentException("Cross product is only valid in three dimensions");
		dest.setV((u.get(1) * v.get(2)) - (u.get(2) * v.get(1)), (u.get(2) * v.get(0)) - (u.get(0) * v.get(2)),
				(u.get(0) * v.get(1)) - (u.get(1) * v.get(0)));
		return dest;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Vector) {
			Vector v = (Vector) o;
			if (v.dimension() != dimension())
				return false;
			for (int k = 0; k < dimension(); k++)
				if (Math.abs(v.get(k) - get(k)) > MathUtil.EPSILON)
					return false;
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int code = 7411;
		for (int k = 0; k < dimension(); k++)
			code = (code << 2) ^ (Float.floatToIntBits(get(k)) * 899809343);
		return code;
	}

	@Override
	public String toString() {
		StringBuilder res = new StringBuilder(getClass().getSimpleName());
		res.append('[');
		final int dim = dimension();
		for (int i = 0; i < dim; i++) {
			if (i > 0)
				res.append(',');
			res.append(get(i));
		}
		res.append(']');
		return res.toString();
	}

	/**
	 * Spherical linear interpolation from a to b, at time Vector in [0, 1]
	 */
	public static Vector slerp(Vector dest, Vector a, Vector b, float t) {
		a.check(b);
		a.check(dest);

		final float angle = a.angle(b);
		final float weightA = (float) (Math.sin((1 - t) * angle) / Math.sin(angle));
		final float weightB = (float) (Math.sin(t * angle) / Math.sin(angle));
		return dest.linearComb(a, weightA, b, weightB);
	}
}
