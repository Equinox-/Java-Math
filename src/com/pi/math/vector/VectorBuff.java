package com.pi.math.vector;

import java.nio.FloatBuffer;

public class VectorBuff extends Vector {
	private final FloatBuffer buffer;
	private final int head, dimension;

	public VectorBuff(FloatBuffer buffer, int head, int dimension) {
		this.buffer = buffer;
		this.head = head;
		this.dimension = dimension;
	}

	@Override
	public float get(int d) {
		return buffer.get(head + d);
	}

	@Override
	public void set(int d, float f) {
		buffer.put(head + d, f);
	}

	@Override
	public int dimension() {
		return dimension;
	}

	@Override
	public Vector clone() {
		float[] tmp = new float[dimension];
		int pos = buffer.position();
		buffer.position(head);
		buffer.get(tmp);
		buffer.position(pos);
		return new VectorND(tmp);
	}
}
