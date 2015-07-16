package com.pi.math.matrix;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import com.pi.math.BufferProvider;
import com.pi.math.Heap;
import com.pi.math.vector.Vector;
import com.pi.math.vector.VectorBuff;

public class Matrix34 extends Trans3D<Matrix34> {
	public Matrix34() {
		this(BufferProvider.createFloatBuffer(12), 0);
	}

	public Matrix34(ByteBuffer f, int offset) {
		super(f, offset, 3, 4);
	}

	public Matrix34(FloatBuffer f, int offset) {
		super(f, offset, 3, 4);
	}

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
			outset.set(k, get(k, 0) * inset.get(0) + get(k, 1) * inset.get(1) + get(k, 2) * inset.get(2));
		return outset;
	}

	public Vector transform3(Vector in) {
		VectorBuff out = Heap.checkout(in.dimension());
		transform3(out, in);
		in.set(out);
		Heap.checkin(out);
		return in;
	}

	private Matrix3 m3v;

	public Matrix3 matrix3() {
		if (m3v == null)
			m3v = new Matrix3(accessor(), 0);
		return m3v;
	}
}
