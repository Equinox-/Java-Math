package com.pi.math.vector;

import java.nio.FloatBuffer;

import com.pi.math.BufferProvider;

public class VectorBuff extends Vector {
	protected final FloatBuffer data;
	private final int dimension;

	public static VectorBuff make(FloatBuffer f, int off, int d) {
		switch (d) {
		case 4:
			return new VectorBuff4(f, off);
		case 3:
			return new VectorBuff3(f, off);
		case 2:
			return new VectorBuff2(f, off);
		default:
			return new VectorBuff(f, off, d);
		}
	}

	public static VectorBuff make(int d) {
		return make(BufferProvider.createFloatBuffer(d), 0, d);
	}

	protected VectorBuff(FloatBuffer data, int offset, int dimension) {
		this.dimension = dimension;
		int ops = data.position();
		int ol = data.limit();
		data.position(offset);
		data.limit(offset + dimension);
		this.data = data.slice();
		data.limit(ol);
		data.position(ops);
	}

	public VectorBuff(VectorBuff f, int dim) {
		this.data = f.data;
		this.dimension = dim;
	}

	@Override
	public final int dimension() {
		return dimension;
	}

	@Override
	public final float get(int d) {
		return data.get(d);
	}

	public FloatBuffer getAccessor() {
		data.position(0);
		return data;
	}

	@Override
	public final void set(int d, float f) {
		data.put(d, f);
	}

	public VectorBuff set(VectorBuff v) {
		if (v == this)
			return this;
		data.position(0);
		v.data.position(0);
		int ol = v.data.limit();
		v.data.limit(Math.min(dimension, v.dimension));
		data.put(v.data);
		v.data.limit(ol);
		data.position(0);
		v.data.position(0);
		return this;
	}

	@Override
	public VectorBuff setV(float... v) {
		data.position(0);
		data.put(v);
		data.position(0);
		return this;
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
}
