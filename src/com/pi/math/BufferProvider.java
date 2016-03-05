package com.pi.math;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public abstract class BufferProvider {
	private static BufferProvider provider = new DefaultBufferProvider();

	public static ByteBuffer createByteBuffer(int n) {
		return provider.nByteBuffer(n);
	}

	public static FloatBuffer createFloatBuffer(int n) {
		return provider.nFloatBuffer(n);
	}

	public static void provider(BufferProvider pvd) {
		provider = pvd;
	}

	protected abstract ByteBuffer nByteBuffer(int n);

	protected abstract FloatBuffer nFloatBuffer(int n);

	private static class DefaultBufferProvider extends BufferProvider {
		@Override
		protected ByteBuffer nByteBuffer(int n) {
			return ByteBuffer.allocateDirect(n);
		}

		@Override
		protected FloatBuffer nFloatBuffer(int n) {
			return ByteBuffer.allocateDirect(n * 4).asFloatBuffer();
		}
	}
}
