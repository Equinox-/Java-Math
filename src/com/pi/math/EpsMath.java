package com.pi.math;

public class EpsMath {
	public static final float EPSILON = .0001f;

	public static boolean eq(float a, float b) {
		return eq(a, b, EPSILON);
	}

	public static boolean eq(float a, float b, float eps) {
		return Math.abs(a - b) <= eps;
	}

	public static boolean zero(float a) {
		return zero(a, EPSILON);
	}

	public static boolean zero(float a, float eps) {
		return Math.abs(a) <= eps;
	}

	public static boolean lt(float a, float b) {
		return a + EPSILON < b;
	}

	public static boolean le(float a, float b) {
		return a <= b + EPSILON;
	}
}
