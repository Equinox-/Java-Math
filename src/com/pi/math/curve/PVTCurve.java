package com.pi.math.curve;

import com.pi.math.vector.Vector;
import com.pi.math.vector.VectorND;

/**
 * A curve defined by two endpoints constrained by position, velocity, and time.
 * 
 * @author Westin Miller
 *
 */
public class PVTCurve {
	public final Vector jerk, accel;
	private final Vector p0, v0;

	public PVTCurve(int dimension) {
		this.jerk = new VectorND(new float[dimension]);
		this.accel = new VectorND(new float[dimension]);
		this.p0 = new VectorND(new float[dimension]);
		this.v0 = new VectorND(new float[dimension]);
	}

	public void update(Vector p0, Vector p1, float dt) {
		this.p0.set(p0);
		this.v0.linearComb(p1, 1 / dt, p0, -1 / dt);
		jerk.zero();
		accel.zero();
	}

	public void update(Vector p0, Vector v0, Vector p1, Vector v1, float dt) {
		this.p0.set(p0);
		this.v0.set(v0);

		final float idt = 1 / dt;
		jerk.linearComb(v0, 0.5f, v1, 0.5f, p0, idt, p1, -idt);
		jerk.multiply(2 / dt / dt);
		accel.linearComb(v1, idt / 2, v0, -idt / 2, jerk, -3 * dt / 2);
	}

	public Vector position(Vector out, float dt) {
		return out.linearComb(p0, 1, v0, dt, accel, dt * dt, jerk, dt * dt * dt);
	}

	public Vector velocity(Vector out, float dt) {
		return out.linearComb(v0, 1, accel, 2 * dt, jerk, 3 * dt * dt);
	}
}
