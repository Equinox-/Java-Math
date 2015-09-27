package com.pi.math;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.pi.math.matrix.Matrix;
import com.pi.math.matrix.Matrix3;
import com.pi.math.matrix.Matrix34;
import com.pi.math.matrix.Matrix4;
import com.pi.math.vector.VectorBuff;
import com.pi.math.vector.VectorBuff3;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Heap {
	// 1-4D vectors
	private static final int[] VECTOR_HEAP_SIZE = new int[4];
	private static final VectorBuff[][] VECTOR_HEAP = new VectorBuff[4][];

	private static final boolean HEAP_WATCH = false;

	private static class Data {
		StackTraceElement[] d;
		Object o;

		public Data(Object o) {
			this.o = o;
			this.d = Thread.currentThread().getStackTrace();
		}
	}

	private static final Map<Integer, Data> owner = new HashMap<>();

	private static void watch(Object o) {
		owner.put(System.identityHashCode(o), new Data(o));
	}

	private static void unwatch(Object o) {
		owner.remove(System.identityHashCode(o));
	}

	static {
		for (int d = 0; d < VECTOR_HEAP.length; d++) {
			VECTOR_HEAP[d] = new VectorBuff[d + 1 == 3 ? 128 : 16];
			while (VECTOR_HEAP_SIZE[d] < VECTOR_HEAP[d].length)
				VECTOR_HEAP[d][VECTOR_HEAP_SIZE[d]++] = VectorBuff.make(d + 1);
		}
	}

	public static <T extends VectorBuff> T checkout(int dim) {
		if (dim <= 0 || dim > VECTOR_HEAP.length || VECTOR_HEAP_SIZE[dim - 1] <= 0) {
			T res = (T) VectorBuff.make(dim);
			System.out.println("Vector heap miss " + res.getClass().getSimpleName());
			return res;
		}
		T tt = (T) VECTOR_HEAP[dim - 1][--VECTOR_HEAP_SIZE[dim - 1]];
		if (HEAP_WATCH)
			watch(tt);
		return tt;
	}

	public static void checkin(VectorBuff... vs) {
		for (VectorBuff v : vs) {
			if (v == null)
				continue;
			int dim = v.dimension();
			if (dim <= 0 || dim > VECTOR_HEAP.length || VECTOR_HEAP_SIZE[dim - 1] >= VECTOR_HEAP[dim - 1].length)
				continue;
			VECTOR_HEAP[dim - 1][VECTOR_HEAP_SIZE[dim - 1]++] = v;
			if (HEAP_WATCH)
				unwatch(v);
		}
	}

	public static VectorBuff3 checkout3() {
		return (VectorBuff3) checkout(3);
	}

	// 3D and 4D matrices
	// 0 -> Matrix4, 1 -> Matrix3
	private static final int[] MATRIX_HEAP_SIZE = new int[4];
	private static final Matrix[][] MATRIX_HEAP = new Matrix[4][];

	private static Matrix makeM(int dim) {
		switch (dim) {
		case 0:
			return new Matrix4();
		case 1:
			return new Matrix3();
		case 2:
			return new Matrix34();
		default:
			return null;
		}
	}

	static {
		for (int d = 0; d < MATRIX_HEAP.length; d++) {
			MATRIX_HEAP[d] = new Matrix[8];
			while (MATRIX_HEAP_SIZE[d] < MATRIX_HEAP[d].length)
				MATRIX_HEAP[d][MATRIX_HEAP_SIZE[d]++] = makeM(d);
		}
	}

	public static Matrix checkoutM(int dim) {
		if (dim < 0 || dim >= MATRIX_HEAP.length || MATRIX_HEAP_SIZE[dim] <= 0) {
			Matrix res = makeM(dim);
			System.out.println("Matrix heap miss " + res.getClass().getSimpleName());
			return res;
		}
		Matrix o = MATRIX_HEAP[dim][--MATRIX_HEAP_SIZE[dim]];
		if (HEAP_WATCH)
			watch(o);
		return o;
	}

	public static void checkin(Matrix m, int dim) {
		if (dim < 0 || dim >= MATRIX_HEAP.length || MATRIX_HEAP_SIZE[dim] >= MATRIX_HEAP[dim].length)
			return;
		MATRIX_HEAP[dim][MATRIX_HEAP_SIZE[dim]++] = m;
		if (HEAP_WATCH)
			unwatch(m);
	}

	public static Matrix4 checkoutM4() {
		return (Matrix4) checkoutM(0);
	}

	public static Matrix3 checkoutM3() {
		return (Matrix3) checkoutM(1);
	}

	public static Matrix34 checkoutM34() {
		return (Matrix34) checkoutM(2);
	}

	public static void checkin(Matrix... vs) {
		for (Matrix v : vs) {
			if (v == null)
				continue;
			else if (v instanceof Matrix4)
				checkin(v, 0);
			else if (v instanceof Matrix3)
				checkin(v, 1);
			else if (v instanceof Matrix34)
				checkin(v, 2);
		}
	}

	public static void printHeapDebug() {
		// System.out.print(" - Vector Heaps \t");
		// for (int k = 0; k < VECTOR_HEAP.length; k++)
		// System.out.print(VECTOR_HEAP_SIZE[k] + "\t");
		// System.out.println();
		// System.out.print(" - Matrix Heaps \t");
		// for (int k = 0; k < MATRIX_HEAP.length; k++)
		// System.out.print(MATRIX_HEAP_SIZE[k] + "\t");
		// System.out.println();
		if (HEAP_WATCH) {
			for (Entry<Integer, Data> e : owner.entrySet()) {
				System.err.println(e.getValue().o.getClass().getSimpleName());
				for (int k = 3; k < Math.min(10, e.getValue().d.length); k++) {
					System.err.println(" " + e.getValue().d[k]);
				}
			}
		}
	}
}
