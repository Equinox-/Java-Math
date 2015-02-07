package com.pi.math.curve;

import com.pi.math.vector.Vector;

public interface Curve {
	/**
	 * Gets the vector located at time <em>t</em> in this line, where t=0 for the first point and t=1 for the last point.
	 * 
	 * @param t
	 *            The curve time, [0,1]
	 * @return The position
	 */
	public Vector calculate(float t);
}
