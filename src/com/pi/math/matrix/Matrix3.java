package com.pi.math.matrix;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import com.pi.math.BufferProvider;
import com.pi.math.vector.Vector;

public final class Matrix3 extends Trans3D<Matrix3> {
	public Matrix3() {
		this(BufferProvider.createFloatBuffer(9), 0);
	}

	public Matrix3(ByteBuffer f, int offset) {
		super(f, offset, 3, 3);
	}

	public Matrix3(FloatBuffer f) {
		this(f, 0);
	}

	public Matrix3(FloatBuffer f, int offset) {
		super(f, offset, 3, 3);
	}

	// Math operations
	@Override
	public <E extends Vector> E transform4(E outset, final Vector inset) {
		for (int k = 0; k < Math.min(3, outset.dimension()); k++)
			switch (inset.dimension()) {
			case 1:
				outset.set(k, get(k, 0) * inset.get(0));
				break;
			case 2:
				outset.set(k, get(k, 0) * inset.get(0) + get(k, 1) * inset.get(1));
				break;
			default:
				outset.set(k, get(k, 0) * inset.get(0) + get(k, 1) * inset.get(1) + get(k, 2) * inset.get(2));
			}
		return outset;
	}

	@Override
	public <E extends Vector> E transform3(E outset, final Vector inset) {
		return transform4(outset, inset);
	}
}
