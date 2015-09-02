package com.pi.math;

import com.pi.math.vector.VectorBuff3;

public class MathUtil {
	private static VectorBuff3 subtract(VectorBuff3 lhs, VectorBuff3 rhs) {
		VectorBuff3 dest = Heap.checkout(3);
		dest.linearComb(lhs, 1, rhs, -1);
		return dest;
	}

	public static VectorBuff3 getPointOnRay(VectorBuff3 dest, VectorBuff3 origin, VectorBuff3 normal,
			VectorBuff3 near) {
		VectorBuff3 pointNormal = subtract(near, origin);
		float distOnLine = pointNormal.dot(normal);
		dest.linearComb(origin, 1, normal, distOnLine);
		Heap.checkin(pointNormal);
		return dest;
	}

	// axis, collision rad, height
	public static float lineIntersectsSegment(final VectorBuff3 o, final VectorBuff3 d, final int axis, final float rad,
			final float height) {
		final int ax1 = (axis + 1) % 3;
		final int ax2 = (axis + 2) % 3;

		final float a = d.get(ax1) * d.get(ax1) + d.get(ax2) * d.get(ax2);
		final float b = 2 * (o.get(ax1) * d.get(ax1) + o.get(ax2) * d.get(ax2));
		final float c = o.get(ax1) * o.get(ax1) + o.get(ax2) * o.get(ax2) - rad * rad;
		final float t = (-b + (float) Math.sqrt(b * b - 4 * a * c)) / (2 * a);
		if (Float.isNaN(t))
			return t; // Doesn't hit x-axis
		final float cX = o.get(axis) + d.get(axis) * t;
		if (cX < 0 || cX > height)
			return Float.NaN; // Doesn't hit segment.
		return t;
	}

	public static float segmentDistanceSegment(final VectorBuff3 rayA, final VectorBuff3 rayB, final VectorBuff3 segA,
			final VectorBuff3 segB) {
		// http://geomalgorithms.com/a07-_distance.html#dist3D_Segment_to_Segment()
		VectorBuff3 u = subtract(rayB, rayA);
		VectorBuff3 v = subtract(segB, segA);
		VectorBuff3 w = subtract(rayA, segA);
		float uMag2 = u.mag2(); // always >= 0
		float uDotV = u.dot(v);
		float vMag2 = v.mag2(); // always >= 0
		float uDotW = u.dot(w);
		float vDotW = v.dot(w);
		float D = uMag2 * vMag2 - uDotV * uDotV; // always >= 0
		float sc, sN, sD = D; // sc = sN / sD, default sD = D >= 0
		float tc, tN, tD = D; // tc = tN / tD, default tD = D >= 0

		// compute the line parameters of the two closest points
		if (EpsMath.zero(D)) { // the lines are almost parallel
			sN = 0.0f; // force using point P0 on segment S1
			sD = 1.0f; // to prevent possible division by 0.0 later
			tN = vDotW;
			tD = vMag2;
		} else { // get the closest points on the infinite lines
			sN = (uDotV * vDotW - vMag2 * uDotW);
			tN = (uMag2 * vDotW - uDotV * uDotW);
			if (sN < 0.0) { // sc < 0 => the s=0 edge is visible
				sN = 0.0f;
				tN = vDotW;
				tD = vMag2;
			} else if (sN > sD) { // sc > 1 => the s=1 edge is visible
				sN = sD;
				tN = vDotW + uDotV;
				tD = vMag2;
			}
		}

		if (tN < 0.0) { // tc < 0 => the t=0 edge is visible
			tN = 0.0f;
			// recompute sc for this edge
			if (-uDotW < 0.0f)
				sN = 0.0f;
			else if (-uDotW > uMag2)
				sN = sD;
			else {
				sN = -uDotW;
				sD = uMag2;
			}
		} else if (tN > tD) { // tc > 1 => the t=1 edge is visible
			tN = tD;
			// recompute sc for this edge
			if ((-uDotW + uDotV) < 0.0)
				sN = 0;
			else if ((-uDotW + uDotV) > uMag2)
				sN = sD;
			else {
				sN = (-uDotW + uDotV);
				sD = uMag2;
			}
		}
		// finally do the division to get sc and tc
		sc = (EpsMath.zero(sN) ? 0.0f : sN / sD);
		tc = (EpsMath.zero(tN) ? 0.0f : tN / tD);

		VectorBuff3 tmp = Heap.checkout3().linearComb(w,1,u,sc);
		tmp.linearComb(1, v, -tc);
		float fv = tmp.magnitude();
		Heap.checkin(tmp, u, v, w);
		return fv;
	}

	public static VectorBuff3 rayIntersectsTriangle(VectorBuff3 dest, VectorBuff3 O, VectorBuff3 D, VectorBuff3 v0,
			VectorBuff3 v1, VectorBuff3 v2) {
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
			// Begin calculating determinant - also used to calculate u
			// parameter
			P = Heap.checkout(3);
			P.cross(D, e2);

			// if determinant is near zero, ray lies in plane of triangle
			det = e1.dot(P);
			if (det > -EpsMath.EPSILON && det < EpsMath.EPSILON)
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
			Q = Heap.checkout(3);
			Q.cross(T, e1);

			// Calculate V parameter and test bound
			v = D.dot(Q) * inv_det;
			// The intersection lies outside of the triangle
			if (v < 0.f || u + v > 1.f)
				return null;

			t = e2.dot(Q) * inv_det;

			if (t > EpsMath.EPSILON) { // ray intersection
				return dest.linearComb(O, 1, D, t);
			}

			// No hit, no win
			return null;
		} finally {
			Heap.checkin(e1);
			Heap.checkin(e2);
			Heap.checkin(P);
			if (T != null)
				Heap.checkin(T);
			if (Q != null)
				Heap.checkin(Q);
		}
	}

	public static boolean rayIntersectsSphere(VectorBuff3 O, VectorBuff3 D, VectorBuff3 center, float radius) {
		VectorBuff3 oMC = subtract(O, center);
		float b = D.dot(oMC);
		float c = D.mag2() * (oMC.mag2() - radius * radius);
		Heap.checkin(oMC);
		return (b * b - c) > -EpsMath.EPSILON;
	}

	public static boolean rayIntersectsBox(VectorBuff3 O, VectorBuff3 D, VectorBuff3 min, VectorBuff3 max) {
		VectorBuff3 maxT = Heap.checkout(3);
		try {
			boolean inside = true;
			for (int i = 0; i < maxT.dimension(); i++)
				maxT.set(i, -1);

			// Find candidate planes.
			for (int i = 0; i < 3; i++) {
				if (O.get(i) < min.get(i)) {
					inside = false;
					if (Math.abs(D.get(i)) > EpsMath.EPSILON)
						maxT.set(i, (min.get(i) - O.get(i)) / D.get(i));
				} else if (O.get(i) > max.get(i)) {
					inside = false;
					if (Math.abs(D.get(i)) > EpsMath.EPSILON)
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
					if (cd < min.get(i) - EpsMath.EPSILON || cd > max.get(i) + EpsMath.EPSILON)
						return false;
				}
			}
			return true; // ray hits box
		} finally {
			Heap.checkin(maxT);
		}
	}
}
