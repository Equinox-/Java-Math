package com.pi.math.vector;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class VectorBuff4 extends VectorBuff {
	public VectorBuff4(FloatBuffer data, int offset) {
		super(data, offset, 4);
	}

	public VectorBuff4() {
		this(BufferUtils.createFloatBuffer(4), 0);
	}

	public VectorBuff4 add(VectorBuff4 r) {
		data.put(0, data.get(0) + r.data.get(0));
		data.put(1, data.get(1) + r.data.get(1));
		data.put(2, data.get(2) + r.data.get(2));
		data.put(3, data.get(3) + r.data.get(3));
		return this;
	}

	public VectorBuff4 subtract(VectorBuff4 r) {
		data.put(0, data.get(0) - r.data.get(0));
		data.put(1, data.get(1) - r.data.get(1));
		data.put(2, data.get(2) - r.data.get(2));
		data.put(3, data.get(3) - r.data.get(3));
		return this;
	}

	@Override
	public VectorBuff4 multiply(float f) {
		data.put(0, data.get(0) * f);
		data.put(1, data.get(1) * f);
		data.put(2, data.get(2) * f);
		data.put(3, data.get(3) * f);
		return this;
	}

	public float distSquared(VectorBuff4 t) {
		float dx = data.get(0) - t.data.get(0);
		float dy = data.get(1) - t.data.get(1);
		float dz = data.get(2) - t.data.get(2);
		float dw = data.get(3) - t.data.get(3);
		return dx * dx + dy * dy + dz * dz + dw * dw;
	}

	@Override
	public float mag2() {
		float x = data.get(0);
		float y = data.get(1);
		float z = data.get(2);
		float w = data.get(3);
		return x * x + y * y + z * z + w * w;
	}

	public float dot(VectorBuff4 v) {
		return data.get(0) * v.data.get(0) + data.get(1) * v.data.get(1)
				+ data.get(2) * v.data.get(2) + data.get(3) * v.data.get(3);
	}

	public VectorBuff4 linearComb(float aC, VectorBuff4 b, float bC) {
		return linearComb(this, aC, b, bC);
	}

	public VectorBuff4 linearComb(VectorBuff4 a, float aC, VectorBuff4 b,
			float bC) {
		data.put(0, aC * a.data.get(0) + bC * b.data.get(0));
		data.put(1, aC * a.data.get(1) + bC * b.data.get(1));
		data.put(2, aC * a.data.get(2) + bC * b.data.get(2));
		data.put(3, aC * a.data.get(3) + bC * b.data.get(3));
		return this;
	}
}
