package com.pi.math;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.pi.math.vector.VectorBuff;
import com.pi.math.vector.VectorBuff3;

public class MathUtil {
	public static final float EPSILON = .00001f;

	@SuppressWarnings("unchecked")
	// 1-4D vectors
	private static final LinkedBlockingQueue<VectorBuff>[] VECTOR_HEAP = new LinkedBlockingQueue[4];
	static {
		for (int d = 0; d < VECTOR_HEAP.length; d++) {
			VECTOR_HEAP[d] = new LinkedBlockingQueue<>(d == 3 ? 128 : 16);
			while (VECTOR_HEAP[d].remainingCapacity() > 0)
				VECTOR_HEAP[d].offer(VectorBuff.make(d + 1));
		}
	}

	public static VectorBuff3 checkout3() {
		return (VectorBuff3) checkout(3);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends VectorBuff> T checkout(int dim) {
		if (dim <= 0 || dim > VECTOR_HEAP.length)
			return (T) VectorBuff.make(dim);
		Queue<VectorBuff> src = VECTOR_HEAP[dim - 1];
		if (src.isEmpty()) {
			return (T) VectorBuff.make(dim);
		}
		return (T) src.poll();
	}

	public static void checkin(VectorBuff v) {
		int dim = v.dimension();
		if (dim <= 0 || dim > VECTOR_HEAP.length)
			return;
		Queue<VectorBuff> src = VECTOR_HEAP[dim - 1];
		if (!src.offer(v)) {
			new Exception().printStackTrace();
		}
	}

	private static <T extends VectorBuff> T subtract(T lhs, T rhs) {
		lhs.check(rhs);
		T dest = MathUtil.checkout(lhs.dimension());
		dest.linearComb(lhs, 1, rhs, -1);
		return dest;
	}

	public static VectorBuff3 getPointOnRay(VectorBuff3 dest,
			VectorBuff3 origin, VectorBuff3 normal, VectorBuff3 near) {
		VectorBuff3 pointNormal = subtract(near, origin);
		float distOnLine = pointNormal.dot(normal);
		dest.linearComb(origin, 1, normal, distOnLine);
		checkin(pointNormal);
		return dest;
	}

	public static VectorBuff3 rayIntersectsTriangle(VectorBuff3 dest,
			VectorBuff3 O, VectorBuff3 D, VectorBuff3 v0, VectorBuff3 v1,
			VectorBuff3 v2) {
		// Moller-Trumbore ray-triangle intersection algorithm
		// http://en.wikipedia.org/wiki/M%C3%B6ller%E2%80%93Trumbore_intersection_algorithm

		VectorBuff3 e1 = null, e2 = null; // Edge1, Edge2
		VectorBuff3 P = null, Q = null, T = null;
		float det, inv_det, u, v;
		float t;

		try {
			// Find vectors for two edges sharing V1
			e1 = subtract(v1, v0);
			e2 = subtract(v2, v0);
			// Begin calculating determinant - also used to calculate u parameter
			P = checkout(3);
			P.cross(D, e2);

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
			Q = checkout(3);
			Q.cross(T, e1);

			// Calculate V parameter and test bound
			v = D.dot(Q) * inv_det;
			// The intersection lies outside of the triangle
			if (v < 0.f || u + v > 1.f)
				return null;

			t = e2.dot(Q) * inv_det;

			if (t > EPSILON) { // ray intersection
				return dest.linearComb(O, 1, D, t);
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

	public static boolean rayIntersectsSphere(VectorBuff3 O, VectorBuff3 D,
			VectorBuff3 center, float radius) {
		VectorBuff3 oMC = subtract(O, center);
		float b = D.dot(oMC);
		float c = D.mag2() * (oMC.mag2() - radius * radius);
		checkin(oMC);
		return (b * b - c) > -MathUtil.EPSILON;
	}

	public static boolean rayIntersectsBox(VectorBuff3 O, VectorBuff3 D,
			VectorBuff3 min, VectorBuff3 max) {
		VectorBuff3 maxT = checkout(3);
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
}
