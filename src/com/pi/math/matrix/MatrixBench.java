package com.pi.math.matrix;

@SuppressWarnings("rawtypes")
public class MatrixBench {
	public static float randf() {
		return (float) Math.random();
	}

	public static <E extends Matrix> E rand(Class<E> clazz) {
		E r;
		try {
			r = clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		for (int i = 0; i < r.columns(); i++)
			for (int j = 0; j < r.rows(); j++)
				r.set(j, i, randf());
		return r;
	}

	private static final int COUNT = 100000;

	private static String ttl(Class<? extends Matrix> genA, Class<? extends Matrix> genB, String op) {
		return genA.getSimpleName() + " " + op + " " + genB.getSimpleName() + ": ";
	}

	private static String ttl(Class<? extends Matrix> genA, String op) {
		return genA.getSimpleName() + " " + op + ": ";
	}

	public static void bench(Class<? extends Matrix> genA, Class<? extends Matrix> genB) {
		{
			long begin = System.nanoTime();
			for (int k = 0; k < COUNT; k++) {
				Matrix res = rand(genA).multiplyInto(rand(genB));
			}
			System.out.println(ttl(genA, genB, "multiply") + (System.nanoTime() - begin) / COUNT + " ns/op");
		}
		{
			long begin = System.nanoTime();
			for (int k = 0; k < COUNT; k++) {
				Matrix res = rand(genA).linearComb(randf(), rand(genB), randf());
			}
			System.out.println(ttl(genA, genB, "lincom") + (System.nanoTime() - begin) / COUNT + " ns/op");
		}
		{
			long begin = System.nanoTime();
			for (int k = 0; k < COUNT; k++) {
				Matrix res = rand(genA).invertInto(rand(genB));
			}
			System.out.println(ttl(genA, genB, "invert") + (System.nanoTime() - begin) / COUNT + " ns/op");
		}
		{
			long begin = System.nanoTime();
			for (int k = 0; k < COUNT; k++) {
				Matrix res = rand(genA).copyTo(rand(genB));
			}
			System.out.println(ttl(genA, genB, "copy") + (System.nanoTime() - begin) / COUNT + " ns/op");
		}
	}

	public static void bench(Class<? extends Matrix> genA) {
		{
			long begin = System.nanoTime();
			for (int k = 0; k < COUNT; k++) {
				Matrix res = rand(genA).makeIdentity();
			}
			System.out.println(ttl(genA, "identity") + (System.nanoTime() - begin) / COUNT + " ns/op");
		}
		{
			long begin = System.nanoTime();
			for (int k = 0; k < COUNT; k++) {
				Matrix res = rand(genA).makeZero();
			}
			System.out.println(ttl(genA, "zero") + (System.nanoTime() - begin) / COUNT + " ns/op");
		}
		{
			long begin = System.nanoTime();
			for (int k = 0; k < COUNT; k++) {
				Matrix res = rand(genA).multiply(randf());
			}
			System.out.println(ttl(genA, "scalar") + (System.nanoTime() - begin) / COUNT + " ns/op");
		}
		{
			long begin = System.nanoTime();
			for (int k = 0; k < COUNT; k++) {
				Matrix res = rand(genA).transposeInPlace();
			}
			System.out.println(ttl(genA, "transp") + (System.nanoTime() - begin) / COUNT + " ns/op");
		}
	}

	public static void main(String[] args) {
		bench(Matrix4.class);
		bench(Matrix4.class, Matrix4.class);
	}
}
