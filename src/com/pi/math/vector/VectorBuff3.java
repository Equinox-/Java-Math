package com.pi.math.vector;

import java.nio.FloatBuffer;

import com.pi.math.BufferProvider;

public class VectorBuff3 extends VectorBuff {
	public VectorBuff3() {
		this(BufferProvider.createFloatBuffer(3), 0);
	}

	public VectorBuff3(FloatBuffer f) {
		this(f, 0);
	}

	public VectorBuff3(FloatBuffer data, int offset) {
		super(data, offset, 3);
	}

	public VectorBuff3 add(VectorBuff3 r) {
		data.put(0, data.get(0) + r.data.get(0));
		data.put(1, data.get(1) + r.data.get(1));
		data.put(2, data.get(2) + r.data.get(2));
		return this;
	}

	public VectorBuff3 cross(VectorBuff3 a, VectorBuff3 b) {
		if (this == a) {
			final float a0 = a.get(0);
			final float a1 = a.get(1);
			data.put(0, (a.get(1) * b.get(2)) - (a.get(2) * b.get(1)));
			data.put(1, (a.get(2) * b.get(0)) - (a0 * b.get(2)));
			data.put(2, (a.get(0) * b.get(1)) - (a1 * b.get(0)));
		} else if (this == b) {
			final float b0 = b.get(0);
			final float b1 = b.get(1);
			data.put(0, (a.get(1) * b.get(2)) - (a.get(2) * b.get(1)));
			data.put(1, (a.get(2) * b0) - (a.get(0) * b.get(2)));
			data.put(2, (a.get(0) * b1) - (a.get(1) * b.get(0)));
		} else {
			data.put(0, (a.get(1) * b.get(2)) - (a.get(2) * b.get(1)));
			data.put(1, (a.get(2) * b.get(0)) - (a.get(0) * b.get(2)));
			data.put(2, (a.get(0) * b.get(1)) - (a.get(1) * b.get(0)));
		}
		return this;
	}

	public float distSquared(VectorBuff3 t) {
		float dx = data.get(0) - t.data.get(0);
		float dy = data.get(1) - t.data.get(1);
		float dz = data.get(2) - t.data.get(2);
		return dx * dx + dy * dy + dz * dz;
	}

	public float dot(VectorBuff3 v) {
		return data.get(0) * v.data.get(0) + data.get(1) * v.data.get(1) + data.get(2) * v.data.get(2);
	}

	public VectorBuff3 linearComb(float aC, VectorBuff3 b, float bC) {
		return linearComb(this, aC, b, bC);
	}

	public VectorBuff3 linearComb(VectorBuff3 a, float aC, VectorBuff3 b, float bC) {
		data.put(0, aC * a.data.get(0) + bC * b.data.get(0));
		data.put(1, aC * a.data.get(1) + bC * b.data.get(1));
		data.put(2, aC * a.data.get(2) + bC * b.data.get(2));
		return this;
	}

	@Override
	public float mag2() {
		float x = data.get(0);
		float y = data.get(1);
		float z = data.get(2);
		return x * x + y * y + z * z;
	}

	@Override
	public VectorBuff3 multiply(float f) {
		data.put(0, data.get(0) * f);
		data.put(1, data.get(1) * f);
		data.put(2, data.get(2) * f);
		return this;
	}

	@Override
	public VectorBuff3 set(VectorBuff v) {
		super.set(v);
		return this;
	}

	@Override
	public VectorBuff3 setV(float... f) {
		super.setV(f);
		return this;
	}

	public VectorBuff3 sub(VectorBuff3 r) {
		data.put(0, data.get(0) - r.data.get(0));
		data.put(1, data.get(1) - r.data.get(1));
		data.put(2, data.get(2) - r.data.get(2));
		return this;
	}
}
