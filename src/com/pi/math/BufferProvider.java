package com.pi.math;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public abstract class BufferProvider {
	private static BufferProvider provider = new DefaultBufferProvider();

	public static void provider(BufferProvider pvd) {
		provider = pvd;
	}

	private static class DefaultBufferProvider extends BufferProvider {
		@Override
		protected FloatBuffer nFloatBuffer(int n) {
			return ByteBuffer.allocateDirect(n * 4).asFloatBuffer();
		}

		@Override
		protected ByteBuffer nByteBuffer(int n) {
			return ByteBuffer.allocateDirect(n);
		}
	}

	public static FloatBuffer createFloatBuffer(int n) {
		return provider.nFloatBuffer(n);
	}

	protected abstract FloatBuffer nFloatBuffer(int n);

	public static ByteBuffer createByteBuffer(int n) {
		return provider.nByteBuffer(n);
	}

	protected abstract ByteBuffer nByteBuffer(int n);
}
