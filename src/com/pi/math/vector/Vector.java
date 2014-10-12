package com.pi.math.vector;

@SuppressWarnings("rawtypes")
public abstract class Vector<T extends Vector> {
	public abstract T add(T r);

	public abstract T subtract(T r);

	public abstract T multiply(float f);

	public abstract float magnitude();

	public abstract float dist(T t);

	public abstract float distSquared(T t);

	public abstract T normalize();

	public abstract float mag2();

	public abstract float get(int d);

	@Override
	public abstract T clone();

	public abstract float dot(T v);

	public abstract T linearComb(float aC, T b, float bC);
}
