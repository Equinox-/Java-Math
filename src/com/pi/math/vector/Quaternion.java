package com.pi.math.vector;

// Quaternions are in 4D vectors as <w,x,y,z>
public class Quaternion {
	public static void checkDim(Vector f) {
		if (f.dimension() != 4)
			throw new IllegalArgumentException("Quaternion functions only work on 4D vectors");
	}

	// Output: Roll, pitch, yaw
	public static Vector toEulerAngles(Vector quat, Vector dest) {
		checkDim(quat);
		if (dest.dimension() != 3)
			throw new IllegalArgumentException("Can only decompose a quaternion into a 3D vector.");
		float q0 = quat.get(0);
		float q1 = quat.get(1);
		float q2 = quat.get(2);
		float q3 = quat.get(3);

		dest.set(0, (float) Math.atan2(2 * (q0 * q1 + q2 * q3), 1 - 2 * (q1 * q1 + q2 * q2)));
		dest.set(1, (float) Math.asin(2 * (q0 * q2 - q3 * q1)));
		dest.set(2, (float) Math.atan2(2 * (q0 * q3 + q1 * q2), 1 - 2 * (q2 * q2 + q3 * q3)));
		return dest;
	}

	// Input: Roll, pitch, yaw
	public static Vector fromEulerAngles(Vector quat, Vector src) {
		if (src.dimension() != 3)
			throw new IllegalArgumentException("Can only compose a quaternion from a 3D vector.");
		return fromEulerAngles(quat, src.get(0), src.get(1), src.get(2));
	}

	public static Vector fromEulerAngles(Vector quat, float roll, float pitch, float yaw) {
		checkDim(quat);
		float r2 = roll / 2;
		float p2 = pitch / 2;
		float y2 = yaw / 2;
		float cr2 = (float) Math.cos(r2);
		float sr2 = (float) Math.sin(r2);
		float cp2 = (float) Math.cos(p2);
		float sp2 = (float) Math.sin(p2);
		float cy2 = (float) Math.cos(y2);
		float sy2 = (float) Math.sin(y2);

		quat.set(0, cr2 * cp2 * cy2 + sr2 * sp2 * sy2);
		quat.set(1, sr2 * cp2 * cy2 - cr2 * sp2 * sy2);
		quat.set(2, cr2 * sp2 * cy2 + sr2 * cp2 * sy2);
		quat.set(3, cr2 * cp2 * sy2 - sr2 * sp2 * cy2);
		return quat.normalize();
	}

	public static Vector conjugate(Vector quat) {
		checkDim(quat);
		quat.set(0, -quat.get(0));
		quat.set(1, -quat.get(1));
		quat.set(2, -quat.get(2));
		return quat;
	}

	public static Vector productInto(Vector lhs, Vector rhs) {
		checkDim(lhs);
		checkDim(rhs);
		float lw = lhs.get(0);
		float lx = lhs.get(1);
		float ly = lhs.get(2);
		float lz = lhs.get(3);

		float rw = rhs.get(0);
		float rx = rhs.get(1);
		float ry = rhs.get(2);
		float rz = rhs.get(3);

		lhs.set(0, lw * rw - lx * rw - ly * ry - lz * rz);
		lhs.set(1, ly * rz - lz * ry + lx * rw + lw * rx);
		lhs.set(2, lz * rx - lz * rz + ly * rw + lw * ry);
		lhs.set(3, lx * ry - ly * rx + lz * rw + lw * rz);
		return lhs;
	}

	public static <T extends Vector> T slerp(T from, Vector to, float t) {
		checkDim(from);
		checkDim(to);
		float cosTheta = from.dot(to);

		if (cosTheta < 0.0f) {
			from.multiply(-1);
			cosTheta = -cosTheta;
		}

		float beta = 1.0f - t;

		// Set the first and second scale for the interpolation
		float scale0 = 1.0f - t;
		float scale1 = t;

		if (1.0f - cosTheta > 0.1f) {
			// We are using spherical interpolation.
			float theta = (float) Math.acos(cosTheta);
			float sinTheta = (float) Math.sin(theta);
			scale0 = (float) Math.sin(theta * beta) / sinTheta;
			scale1 = (float) Math.sin(theta * t) / sinTheta;
		}

		from.linearComb(scale0, to, scale1);
		return from;
	}

	public static <T extends Vector> T mul(T dest, Vector a, Vector b) {
		checkDim(dest);
		checkDim(a);
		checkDim(b);
		final float aw = a.get(0), ax = a.get(1), ay = a.get(2), az = a.get(3);
		final float bw = b.get(0), bx = b.get(1), by = b.get(2), bz = b.get(3);

		dest.set(0, aw * bw - ax * bx - ay * by - az * bz);
		dest.set(1, aw * bx + ax * bw + ay * bz - az * by);
		dest.set(2, aw * by + ay * bw + az * bx - ax * bz);
		dest.set(3, aw * bz + az * bw + ax * by - ay * bx);
		return dest;
	}
}
