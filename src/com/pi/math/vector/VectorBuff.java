package com.pi.math.vector;

import java.nio.FloatBuffer;

public final class VectorBuff extends Vector {
	private final FloatBuffer data;
	private final int dimension;

	public VectorBuff(FloatBuffer data, int offset, int dimension) {
		this.dimension = dimension;
		int ops = data.position();
		int ol = data.limit();
		data.position(offset);
		data.limit(offset + dimension);
		this.data = data.slice();
		data.limit(ol);
		data.position(ops);
	}

	@Override
	public float get(int d) {
		return data.get(d);
	}

	@Override
	public void set(int d, float f) {
		data.put(d, f);
	}

	@Override
	public int dimension() {
		return dimension;
	}

	@Override
	public Vector setV(float... v) {
		data.position(0);
		data.put(v);
		data.position(0);
		return this;
	}

	@Override
	public Vector clone() {
		float[] tmp = new float[dimension];
		data.position(0);
		data.get(tmp);
		data.position(0);
		return new VectorND(tmp);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append('[');
		for (int i = 0; i < dimension; i++) {
			if (i > 0)
				b.append(", ");
			b.append(get(i));
		}
		b.append(']');
		return b.toString();
	}

	public FloatBuffer getAccessor() {
		return data;
	}
}
