package com.pi.math.matrix;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import com.pi.math.BufferProvider;
import com.pi.math.vector.Vector;
import com.pi.math.vector.VectorBuff3;

public final class Matrix4 extends Trans3D<Matrix4> {
	public Matrix4() {
		this(BufferProvider.createFloatBuffer(16), 0);
	}

	public Matrix4(ByteBuffer f, int offset) {
		super(f, offset, 4, 4);
	}

	public Matrix4(FloatBuffer f, int offset) {
		super(f, offset, 4, 4);
	}

	// Math operations
	@Override
	public <E extends Vector> E transform(E outset, final Vector inset) {
		final float inW = inset.dimension() < 4 ? 1 : inset.get(3);
		for (int k = 0; k < outset.dimension(); k++)
			outset.set(k,
					get(k, 0) * inset.get(0) + get(k, 1) * inset.get(1) + get(k, 2) * inset.get(2) + get(k, 3) * inW);
		return outset;
	}

	public Vector transform3(Vector outset, final Vector inset) {
		for (int k = 0; k < outset.dimension(); k++)
			outset.set(k, get(k) * inset.get(0) + get(4 + k) * inset.get(1) + get(8 + k) * inset.get(2));
		return outset;
	}

	public VectorBuff3 transform4(VectorBuff3 outset, final VectorBuff3 inset) {
		for (int k = 0; k < 3; k++)
			outset.set(k, get(k) * inset.get(0) + get(4 + k) * inset.get(1) + get(8 + k) * inset.get(2) + get(12 + k));
		return outset;
	}

	public VectorBuff3 transform3(VectorBuff3 outset, final VectorBuff3 inset) {
		for (int k = 0; k < 3; k++)
			outset.set(k, get(k) * inset.get(0) + get(4 + k) * inset.get(1) + get(8 + k) * inset.get(2));
		return outset;
	}

	// Matrix operations
	public Matrix4 makeMatrix3() {
		set(3, 0);
		set(7, 0);
		set(11, 0);
		set(12, 0);
		set(13, 0);
		set(14, 0);
		set(15, 1);
		return this;
	}
}
