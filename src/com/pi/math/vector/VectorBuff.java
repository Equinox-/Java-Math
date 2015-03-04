package com.pi.math.vector;

import java.nio.FloatBuffer;

public class VectorBuff extends Vector {
	private final FloatBuffer data;
	private final int offset, dimension;

	public VectorBuff(FloatBuffer data, int offset, int dimension) {
		this.data = data;
		this.offset = offset;
		this.dimension = dimension;
	}

	@Override
	public float get(int d) {
		return data.get(offset + d);
	}

	@Override
	public void set(int d, float f) {
		data.put(offset + d, f);
	}

	@Override
	public int dimension() {
		return dimension;
	}

	@Override
	public Vector clone() {
		float[] tmp = new float[dimension];
		int pos = data.position();
		data.position(offset);
		data.get(tmp);
		data.position(pos);
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
		int ops = data.position();
		int ol = data.limit();
		data.position(offset);
		data.limit(offset + dimension);
		FloatBuffer slice = data.slice();
		data.limit(ol);
		data.position(ops);
		return slice;
	}
}
