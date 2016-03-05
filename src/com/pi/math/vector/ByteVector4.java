package com.pi.math.vector;

import java.awt.Color;
import java.nio.ByteBuffer;

import com.pi.math.BufferProvider;

public class ByteVector4 extends ByteVector {
	public ByteVector4() {
		this(BufferProvider.createByteBuffer(4), 0);
	}

	public ByteVector4(ByteBuffer data, int offset) {
		super(data, offset, 4);
	}

	public ByteVector4(float r, float g, float b, float a) {
		this();
		setV(r, g, b, a);
	}

	public boolean isColor(int r, int g, int b) {
		return getB(0) == r && getB(1) == g && getB(2) == b;
	}

	public boolean isColor(int r, int g, int b, int a) {
		return getB(0) == r && getB(1) == g && getB(2) == b && getB(3) == a;
	}

	public ByteVector4 set(Color src) {
		return set(src.getRed(), src.getGreen(), src.getBlue(), src.getAlpha());
	}

	public ByteVector4 set(float r, float g, float b) {
		return set(r, g, b, 1);
	}

	public ByteVector4 set(float r, float g, float b, float a) {
		return set((int) (r * 255), (int) (g * 255), (int) (b * 255), (int) (a * 255));
	}

	public ByteVector4 set(int argb) {
		return set((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, (argb >> 24) & 0xFF);
	}

	public ByteVector4 set(int r, int g, int b) {
		return set(r, g, b, 255);
	}

	public ByteVector4 set(int r, int g, int b, int a) {
		setB(0, r);
		setB(1, g);
		setB(2, b);
		setB(3, a);
		return this;
	}
}
