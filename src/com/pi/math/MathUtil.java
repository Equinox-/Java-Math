package com.pi.math;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.pi.math.vector.Vector;
import com.pi.math.vector.VectorND;

public class MathUtil {
	public static final float EPSILON = .00001f;

	@SuppressWarnings("unchecked")
	// 1-4D vectors
	private static final LinkedBlockingQueue<Vector>[] VECTOR_HEAP = new LinkedBlockingQueue[4];
	static {
		for (int d = 0; d < VECTOR_HEAP.length; d++) {
			VECTOR_HEAP[d] = new LinkedBlockingQueue<>(d == 3 ? 128 : 16);
			while (VECTOR_HEAP[d].remainingCapacity() > 0)
				VECTOR_HEAP[d].offer(new VectorND(new float[d + 1]));
		}
	}

	public static Vector checkout(int dim) {
		if (dim <= 0 || dim > VECTOR_HEAP.length)
			return new VectorND(new float[dim]);
		Queue<Vector> src = VECTOR_HEAP[dim - 1];
		if (src.isEmpty()) {
			System.out.println("Heap D" + dim + " missed");
			return new VectorND(new float[dim]);
		}
		return src.poll();
	}

	public static void checkin(Vector v) {
		int dim = v.dimension();
		if (dim <= 0 || dim > VECTOR_HEAP.length)
			return;
		Queue<Vector> src = VECTOR_HEAP[dim - 1];
		if (!src.offer(v)) {
			System.out.println("Heap overfill: ");
			new Exception().printStackTrace();
		}
	}

	private static Vector subtract(Vector lhs, Vector rhs) {
		lhs.check(rhs);
		Vector dest = MathUtil.checkout(lhs.dimension());
		for (int k = 0; k < lhs.dimension(); k++)
			dest.set(k, lhs.get(k) - rhs.get(k));
		return dest;
	}

	/**
	 * [distance from segment, distance on line, distance on infinite line, distance from infinite line]
	 */
	public static float[] getRelationToLine(Vector point, Vector lineA,
			Vector lineB) {
		Vector lineNormal = subtract(lineB, lineA);
		Vector pointNormal = subtract(point, lineA);
		float lineMag = lineNormal.magnitude();
		float pointMag = pointNormal.magnitude();
		float baseLen = Vector.dotProduct(lineNormal, pointNormal) / lineMag;
		float angle = (float) Math.acos(baseLen / pointMag);
		float thickness = (float) (Math.sin(angle) * pointMag);

		float[] res;
		if (baseLen > lineMag) {
			res = new float[] { lineB.dist(point), lineNormal.magnitude(),
					baseLen, thickness };
		} else if (angle > Math.PI / 2) {
			res = new float[] { pointMag, 0, baseLen, thickness };
		} else {
			res = new float[] { thickness, baseLen, thickness };
		}

		checkin(lineNormal);
		checkin(pointNormal);

		return res;
	}

	public static Vector getPointOnRay(Vector dest, Vector origin,
			Vector normal, Vector near) {
		Vector pointNormal = subtract(near, origin);
		float distOnLine = pointNormal.dot(normal);
		Vector.linearComb(dest, origin, 1, normal, distOnLine);
		checkin(pointNormal);
		return dest;
	}

	public static Vector rayIntersectsTriangle(Vector dest, Vector O, Vector D, Vector v0,
			Vector v1, Vector v2) {
		// Moller-Trumbore ray-triangle intersection algorithm
		// http://en.wikipedia.org/wiki/M%C3%B6ller%E2%80%93Trumbore_intersection_algorithm

		Vector e1 = null, e2 = null; // Edge1, Edge2
		Vector P = null, Q = null, T = null;
		float det, inv_det, u, v;
		float t;

		try {
			// Find vectors for two edges sharing V1
			e1 = subtract(v1, v0);
			e2 = subtract(v2, v0);
			// Begin calculating determinant - also used to calculate u parameter
			P = Vector.crossProduct(checkout(3), D, e2);

			// if determinant is near zero, ray lies in plane of triangle
			det = e1.dot(P);
			if (det > -EPSILON && det < EPSILON)
				return null;
			inv_det = 1.f / det;

			// calculate distance from V1 to ray origin
			T = subtract(O, v0);

			// Calculate u parameter and test bound
			u = T.dot(P) * inv_det;
			// The intersection lies outside of the triangle
			if (u < 0.f || u > 1.f)
				return null;

			// Prepare to test v parameter
			Q = Vector.crossProduct(checkout(3), T, e1);

			// Calculate V parameter and test bound
			v = D.dot(Q) * inv_det;
			// The intersection lies outside of the triangle
			if (v < 0.f || u + v > 1.f)
				return null;

			t = e2.dot(Q) * inv_det;

			if (t > EPSILON) { // ray intersection
				return Vector.linearComb(dest, O, 1, D, t);
			}

			// No hit, no win
			return null;
		} finally {
			checkin(e1);
			checkin(e2);
			checkin(P);
			if (T != null)
				checkin(T);
			if (Q != null)
				checkin(Q);
		}
	}

	public static boolean rayIntersectsSphere(Vector O, Vector D,
			Vector center, float radius) {
		Vector oMC = subtract(O, center);
		float b = D.dot(oMC);
		float c = D.mag2() * (oMC.mag2() - radius * radius);
		checkin(oMC);
		return Math.abs(b * b - c) > -MathUtil.EPSILON;
	}

	public static boolean rayIntersectsBox(Vector O, Vector D, Vector min,
			Vector max) {
		Vector maxT = checkout(3);
		try {
			boolean inside = true;
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
		} finally {
			checkin(maxT);
		}
	}

	public static float getMinDistanceBetweenLines(Vector[] lineA,
			Vector[] lineB) {
		Vector dirA = subtract(lineA[1], lineA[0]).normalize();
		Vector dirB = subtract(lineB[1], lineB[0]).normalize();
		Vector normal = Vector.crossProduct(checkout(3), dirA, dirB)
				.normalize();

		float dA = -Vector.dotProduct(normal, lineA[0]);
		float dB = -Vector.dotProduct(normal, lineB[0]);

		checkin(dirA);
		checkin(dirB);
		checkin(normal);

		return Math.abs(dA - dB);
	}
}
