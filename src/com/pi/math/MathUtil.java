package com.pi.math;

import com.pi.math.vector.Vector;
import com.pi.math.vector.VectorND;

public class MathUtil {
	private static final float EPSILON = .00001f;

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

	public static boolean rayIntersectsTriangle(Vector O, Vector D, Vector v0,
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
			return false;
		inv_det = 1.f / det;

		// calculate distance from V1 to ray origin
		T = O.clone().subtract(v0);

		// Calculate u parameter and test bound
		u = T.dot(P) * inv_det;
		// The intersection lies outside of the triangle
		if (u < 0.f || u > 1.f)
			return false;

		// Prepare to test v parameter
		Q = Vector.crossProduct(T, e1);

		// Calculate V parameter and test bound
		v = D.dot(Q) * inv_det;
		// The intersection lies outside of the triangle
		if (v < 0.f || u + v > 1.f)
			return false;

		t = e2.dot(Q) * inv_det;

		if (t > EPSILON) { // ray intersection
			return true;
		}

		// No hit, no win
		return false;
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
