package com.pi.math.matrix;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import com.pi.math.BufferProvider;
import com.pi.math.vector.Vector;

public class Matrix34 extends Trans3D<Matrix34> {
	// Matrix3 view. (Changes reflected)
	private Matrix3 m3v;

	public Matrix34() {
		this(BufferProvider.createFloatBuffer(12), 0);
	}

	public Matrix34(ByteBuffer f, int offset) {
		super(f, offset, 3, 4);
	}

	public Matrix34(FloatBuffer f, int offset) {
		super(f, offset, 3, 4);
	}

	public Matrix3 matrix3() {
		if (m3v == null)
			m3v = new Matrix3(accessor(), 0);
		return m3v;
	}

	// Math operations
	@Override
	public <E extends Vector> E transform4(E outset, final Vector inset) {
		for (int k = 0; k < Math.min(3, outset.dimension()); k++)
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
		for (int k = 0; k < Math.min(3, outset.dimension()); k++)
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
