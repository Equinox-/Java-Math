package com.pi.math.vector;

import java.nio.ByteBuffer;

import com.pi.math.BufferProvider;

public class ByteVector extends Vector {
	private static final int RESOLUTION = 0xFF;

	private final ByteBuffer backer;
	private final int dimension;

	public static ByteVector make(ByteBuffer data, int offset, int dim) {
		if (dim == 4)
			return new ByteVector4(data, offset);
		else
			return new ByteVector(data, offset, dim);
	}

	public static ByteVector make(int dim) {
		return make(BufferProvider.createByteBuffer(dim), 0, dim);
	}

	protected ByteVector(ByteBuffer data, int offset, int dim) {
		if (offset == 0)
			backer = data;
		else {
			data.position(offset);
			data.limit(offset + dim);
			backer = data.slice();
		}
		dimension = dim;
	}

	@Override
	public int dimension() {
		return dimension;
	}

	@Override
	public float get(int d) {
		return (backer.get(d) & 0xFF) / (float) RESOLUTION;
	}

	public ByteBuffer getAccessor() {
		backer.position(0);
		return backer;
	}

	public int getB(int d) {
		return backer.get(d) & 0xFF;
	}

	@Override
	public void set(int d, float r) {
		backer.put(d, (byte) (r * RESOLUTION));
	}

	public void setB(int d, int v) {
		backer.put(d, (byte) v);
	}
}
