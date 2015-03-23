package com.pi.math.vector;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class VectorBuff2 extends VectorBuff {
	public VectorBuff2(FloatBuffer data, int offset) {
		super(data, offset, 2);
	}

	public VectorBuff2() {
		this(BufferUtils.createFloatBuffer(2), 0);
	}

	public VectorBuff2 add(VectorBuff2 r) {
		data.put(0, data.get(0) + r.data.get(0));
		data.put(1, data.get(1) + r.data.get(1));
		return this;
	}

	public VectorBuff2 subtract(VectorBuff2 r) {
		data.put(0, data.get(0) - r.data.get(0));
		data.put(1, data.get(1) - r.data.get(1));
		return this;
	}

	@Override
	public VectorBuff2 multiply(float f) {
		data.put(0, data.get(0) * f);
		data.put(1, data.get(1) * f);
		return this;
	}

	public float distSquared(VectorBuff2 t) {
		float dx = data.get(0) - t.data.get(0);
		float dy = data.get(1) - t.data.get(1);
		return dx * dx + dy * dy;
	}

	@Override
	public float mag2() {
		float x = data.get(0);
		float y = data.get(1);
		return x * x + y * y;
	}

	public float dot(VectorBuff2 v) {
		return data.get(0) * v.data.get(0) + data.get(1) * v.data.get(1);
	}

	public VectorBuff2 linearComb(float aC, VectorBuff2 b, float bC) {
		return linearComb(this, aC, b, bC);
	}

	public VectorBuff2 linearComb(VectorBuff2 a, float aC, VectorBuff2 b,
			float bC) {
		data.put(0, aC * a.data.get(0) + bC * b.data.get(0));
		data.put(1, aC * a.data.get(1) + bC * b.data.get(1));
		return this;
	}
}
