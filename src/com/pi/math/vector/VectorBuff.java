package com.pi.math.vector;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class VectorBuff extends Vector {
	protected final FloatBuffer data;
	private final int dimension;

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

	@Override
	public final float get(int d) {
		return data.get(d);
	}

	@Override
	public final void set(int d, float f) {
		data.put(d, f);
	}

	@Override
	public final int dimension() {
		return dimension;
	}

	@Override
	public VectorBuff setV(float... v) {
		data.position(0);
		data.put(v);
		data.position(0);
		return this;
	}

	public VectorBuff set(VectorBuff v) {
		check(v);
		data.position(0);
		v.data.position(0);
		data.put(v.data);
		data.position(0);
		v.data.position(0);
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

	public FloatBuffer getAccessor() {
		return data;
	}

	public static VectorBuff make(int d) {
		return make(BufferUtils.createFloatBuffer(d), 0, d);
	}

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
}
