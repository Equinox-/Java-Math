package com.pi.math;

import com.pi.math.vector.Vector;

public class MathUtil {
	/**
	 * [distance from segment, distance on line, distance on infinite line,
	 * distance from infinite line]
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
