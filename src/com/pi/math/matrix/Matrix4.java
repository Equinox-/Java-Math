package com.pi.math.matrix;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import com.pi.math.BufferProvider;
import com.pi.math.vector.Vector;
import com.pi.math.vector.VectorBuff2;
import com.pi.math.vector.VectorBuff3;
import com.pi.math.vector.VectorBuff4;

public final class Matrix4 extends Trans3D<Matrix4> {
	public Matrix4() {
		this(BufferProvider.createFloatBuffer(16), 0);
	}

	public Matrix4(ByteBuffer f, int offset) {
		super(f, offset, 4, 4);
	}

	public Matrix4(FloatBuffer f) {
		this(f, 0);
	}

	public Matrix4(FloatBuffer f, int offset) {
		super(f, offset, 4, 4);
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

	// Math operations
	@Override
	public <E extends Vector> E transform4(E outset, final Vector inset) {
		for (int k = 0; k < outset.dimension(); k++)
			switch (inset.dimension()) {
			case 1:
				outset.set(k, get(k, 0) * inset.get(0) + get(k, 3));
				break;
			case 2:
				outset.set(k, get(k, 0) * inset.get(0) + get(k, 1) * inset.get(1) + get(k, 3));
				break;
			case 3:
				outset.set(k,
						get(k, 0) * inset.get(0) + get(k, 1) * inset.get(1) + get(k, 2) * inset.get(2) + get(k, 3));
				break;
			default:
				outset.set(k, get(k, 0) * inset.get(0) + get(k, 1) * inset.get(1) + get(k, 2) * inset.get(2)
						+ get(k, 3) * inset.get(3));
			}
		return outset;
	}

	@Override
	public <E extends Vector> E transform3(E outset, final Vector inset) {
		for (int k = 0; k < outset.dimension(); k++)
			switch (inset.dimension()) {
			case 1:
				outset.set(k, get(k) * inset.get(0));
				break;
			case 2:
				outset.set(k, get(k) * inset.get(0) + get(4 + k) * inset.get(1));
				break;
			default:
				outset.set(k, get(k) * inset.get(0) + get(4 + k) * inset.get(1) + get(8 + k) * inset.get(2));
				break;
			}
		return outset;
	}
}
