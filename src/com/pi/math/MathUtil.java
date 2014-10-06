package com.pi.math;

public class MathUtil {
	/**
	 * [distance from segment, distance on line, distance on infinite line,
	 * distance from infinite line]
	 */
	public static float[] getRelationToLine(Vector3D point, Vector3D lineA,
			Vector3D lineB) {
		Vector3D lineNormal = lineB.clone().subtract(lineA);
		Vector3D pointNormal = point.clone().subtract(lineA);
		float lineMag = lineNormal.magnitude();
		float pointMag = pointNormal.magnitude();
		float baseLen = Vector3D.dotProduct(lineNormal, pointNormal) / lineMag;
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

	public static float getMinDistanceBetweenLines(Vector3D[] lineA,
			Vector3D[] lineB) {
		Vector3D dirA = lineA[1].clone().subtract(lineA[0]).normalize();
		Vector3D dirB = lineB[1].clone().subtract(lineB[0]).normalize();
		Vector3D normal = Vector3D.crossProduct(dirA, dirB).normalize();

		float dA = -Vector3D.dotProduct(normal, lineA[0]);
		float dB = -Vector3D.dotProduct(normal, lineB[0]);

		return Math.abs(dA - dB);
	}
}
