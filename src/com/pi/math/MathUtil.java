package com.pi.math;

import com.pi.math.vector.Vector;

public class MathUtil {
	public static final float EPSILON = .00001f;

	/**
	 * [distance from segment, distance on line, distance on infinite line, distance from infinite line]
	 */
	public static float[] getRelationToLine(Vector point, Vector lineA,
			Vector lineB) {
		Vector lineNormal = lineB.clone().subtract(lineA);
		Vector pointNormal = point.clone().subtract(lineA);
		float lineMag = lineNormal.magnitude();
		float pointMag = pointNormal.magnitude();
		float baseLen = Vector.dotProduct(lineNormal, pointNormal) / lineMag;
		float angle = (float) Math.acos(baseLen / pointMag);
		float thickness = (float) (Math.sin(angle) * pointMag);
		if (baseLen > lineMag) {
			return new float[] { lineB.dist(point), lineNormal.magnitude(),
					baseLen, thickness };
		} else if (angle > Math.PI / 2) {
			return new float[] { pointMag, 0, baseLen, thickness };
		} else {
			return new float[] { thickness, baseLen, thickness };
		}
	}

	public static Vector getPointOnRay(Vector origin, Vector normal, Vector near) {
		Vector pointNormal = near.clone().subtract(origin);
		float distOnLine = pointNormal.dot(normal);
		return origin.clone().linearComb(1, normal, distOnLine);
	}

	public static Vector rayIntersectsTriangle(Vector O, Vector D, Vector v0,
			Vector v1, Vector v2) {
		// Moller-Trumbore ray-triangle intersection algorithm
		// http://en.wikipedia.org/wiki/M%C3%B6ller%E2%80%93Trumbore_intersection_algorithm

		Vector e1, e2; // Edge1, Edge2
		Vector P, Q, T;
		float det, inv_det, u, v;
		float t;

		// Find vectors for two edges sharing V1
		e1 = v1.clone().subtract(v0);
		e2 = v2.clone().subtract(v0);
		// Begin calculating determinant - also used to calculate u parameter
		P = Vector.crossProduct(D, e2);

		// if determinant is near zero, ray lies in plane of triangle
		det = e1.dot(P);
		// NOT CULLING
		if (det > -EPSILON && det < EPSILON)
			return null;
		inv_det = 1.f / det;

		// calculate distance from V1 to ray origin
		T = O.clone().subtract(v0);

		// Calculate u parameter and test bound
		u = T.dot(P) * inv_det;
		// The intersection lies outside of the triangle
		if (u < 0.f || u > 1.f)
			return null;

		// Prepare to test v parameter
		Q = Vector.crossProduct(T, e1);

		// Calculate V parameter and test bound
		v = D.dot(Q) * inv_det;
		// The intersection lies outside of the triangle
		if (v < 0.f || u + v > 1.f)
			return null;

		t = e2.dot(Q) * inv_det;

		if (t > EPSILON) { // ray intersection
			return O.clone().linearComb(1, D, t);
		}

		// No hit, no win
		return null;
	}

	public static boolean rayIntersectsSphere(Vector O, Vector D,
			Vector center, float radius) {
		Vector oMC = O.clone().subtract(center);
		float b = D.dot(oMC);
		float c = D.mag2() * (oMC.mag2() - radius * radius);
		return Math.abs(b * b - c) > -MathUtil.EPSILON;
	}

	public static boolean rayIntersectsBox(Vector O, Vector D, Vector min,
			Vector max) {
		boolean inside = true;
		Vector maxT = min.clone();
		for (int i = 0; i < maxT.dimension(); i++)
			maxT.set(i, -1);

		// Find candidate planes.
		for (int i = 0; i < 3; i++) {
			if (O.get(i) < min.get(i)) {
				inside = false;
				if (Math.abs(D.get(i)) > EPSILON)
					maxT.set(i, (min.get(i) - O.get(i)) / D.get(i));
			} else if (O.get(i) > max.get(i)) {
				inside = false;
				if (Math.abs(D.get(i)) > EPSILON)
					maxT.set(i, (max.get(i) - O.get(i)) / D.get(i));
			}
		}

		// Ray origin inside bounding box
		if (inside) {
			return true;
		}

		// Get largest of the maxT's for final choice of intersection
		int plane = 0;
		if (maxT.get(1) > maxT.get(plane))
			plane = 1;
		if (maxT.get(2) > maxT.get(plane))
			plane = 2;

		// Check final candidate actually inside box
		if (maxT.get(plane) < 0)
			return false;

		for (int i = 0; i < 3; i++) {
			if (i != plane) {
				float cd = O.get(i) + maxT.get(plane) * D.get(i);
				if (cd < min.get(i) - EPSILON || cd > max.get(i) + EPSILON)
					return false;
			}
		}
		return true; // ray hits box
	}

	public static float getMinDistanceBetweenLines(Vector[] lineA,
			Vector[] lineB) {
		Vector dirA = lineA[1].clone().subtract(lineA[0]).normalize();
		Vector dirB = lineB[1].clone().subtract(lineB[0]).normalize();
		Vector normal = Vector.crossProduct(dirA, dirB).normalize();

		float dA = -Vector.dotProduct(normal, lineA[0]);
		float dB = -Vector.dotProduct(normal, lineB[0]);

		return Math.abs(dA - dB);
	}
}
