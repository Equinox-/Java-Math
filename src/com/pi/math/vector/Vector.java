package com.pi.math.vector;

import com.pi.math.FastMath;

public abstract class Vector {
	public abstract float get(int d);

	public abstract void set(int d, float r);

	public abstract int dimension();

	@Override
	public abstract Vector clone();

	void check(Vector t) {
		if (dimension() != t.dimension())
			throw new IllegalArgumentException("Mismatch Dimensions");
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

	public Vector multiply(float f) {
		for (int i = 0; i < dimension(); i++)
			set(i, get(i) * f);
		return this;
	}

	public float magnitude() {
		return (float) Math.sqrt(mag2());
	}

	public float dist(Vector t) {
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

	public Vector normalize() {
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
		if (components.length != dimension())
			throw new RuntimeException("Mismatched dimensions.");
		for (int i = 0; i < components.length; i++)
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

	public float angle(Vector v) {
		return (float) Math.acos(dot(v) * FastMath.Q_rsqrt(mag2())
				* FastMath.Q_rsqrt(v.mag2()));
	}

	public Vector linearComb(float aC, Vector b, float bC) {
		return this.clone().multiply(aC).add(b.clone().multiply(bC));
	}

	public static Vector projectOntoPlane(Vector planeNormal, Vector vector) {
		planeNormal.check(vector);
		return vector.clone().subtract(
				planeNormal.clone().multiply(vector.dot(planeNormal)));
	}

	public static Vector normalize(Vector p) {
		return p.clone().normalize();
	}

	public static Vector negative(Vector p) {
		return p.clone().multiply(-1f);
	}

	public static float dotProduct(Vector u, Vector v) {
		u.check(v);
		return u.dot(v);
	}

	public static Vector crossProduct(Vector u, Vector v) {
		if (u.dimension() != 3 || v.dimension() != 3)
			throw new IllegalArgumentException(
					"Cross product is only valid in three dimensions");
		return new VectorND((u.get(1) * v.get(2)) - (u.get(2) * v.get(1)),
				(u.get(2) * v.get(0)) - (u.get(0) * v.get(2)),
				(u.get(0) * v.get(1)) - (u.get(1) * v.get(0)));
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Vector) {
			Vector v = (Vector) o;
			if (v.dimension() != dimension())
				return false;
			for (int k = 0; k < dimension(); k++)
				if (v.get(k) != get(k))
					return false;
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int code = 7411;
		for (int k=0; k<dimension(); k++)
			code = (code << 2) ^ (Float.floatToIntBits(get(k)) * 899809343);
		return code;
	}

	/**
	 * Spherical linear interpolation from a to b, at time Vector in [0, 1]
	 */
	public static Vector slerp(Vector a, Vector b, float t) {
		a.check(b);
		final float angle = a.angle(b);
		final float weightA = (float) (Math.sin((1 - t) * angle) / Math
				.sin(angle));
		final float weightB = (float) (Math.sin(t * angle) / Math.sin(angle));
		return a.clone().multiply(weightA).add(b.clone().multiply(weightB));
	}
}
