package com.pi.math.matrix;

@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class MatrixBench {
	public static float randf() {
		return (float) Math.random();
	}

	public static <E extends Matrix> E rand(Class<E> clazz) {
		return rand(clazz, 1);
	}

	public static <E extends Matrix> E rand(Class<E> clazz, boolean smart) {
		E l = rand(clazz, 1);
		if (l instanceof Trans3D)
			((Trans3D) l).setFlags();
		if (!smart)
			((Trans3D) l).flags = Trans3D.FLAG_GENERAL;
		return l;
	}

	public static <E extends Matrix> E rand(Class<E> clazz, float cap) {
		E r;
		try {
			r = clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		for (int i = 0; i < r.columns(); i++)
			for (int j = 0; j < r.rows(); j++)
				r.set(j, i, randf() * cap);
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
		for (int s = 0; s <= 1; s++) {
			String tag = "multiply" + (s == 1 ? "S" : "");
			try {
				long begin = System.nanoTime();
				for (int k = 0; k < COUNT; k++) {
					Matrix res = rand(genA, s == 1).preMul(rand(genB, s == 1));
				}
				System.out.println(ttl(genA, genB, tag) + "\t" + ((System.nanoTime() - begin) / COUNT + " ns/op"));
			} catch (Exception e) {
				System.out.println(ttl(genA, genB, tag) + "failure");
			}
		}
		{
			try {
				long begin = System.nanoTime();
				for (int k = 0; k < COUNT; k++) {
					Matrix res = rand(genA).linearComb(randf(), rand(genB), randf());
				}
				System.out.println(ttl(genA, genB, "lincom") + (System.nanoTime() - begin) / COUNT + " ns/op");
			} catch (Exception e) {
				System.out.println(ttl(genA, genB, "lincom") + "failure");
			}
		}
		for (int s = 0; s <= 1; s++) {
			String tag = "invertInto" + (s == 1 ? "S" : "");
			try {
				long begin = System.nanoTime();
				for (int k = 0; k < COUNT; k++) {
					Matrix res = rand(genA, s == 1).invertInto(rand(genB, s == 1));
				}
				System.out.println(ttl(genA, genB, tag) + "\t" + ((System.nanoTime() - begin) / COUNT + " ns/op"));
			} catch (Exception e) {
				System.out.println(ttl(genA, genB, tag) + "failure");
			}
		}
		{
			try {
				long begin = System.nanoTime();
				for (int k = 0; k < COUNT; k++) {
					Matrix res = rand(genA).copyTo(rand(genB));
				}
				System.out.println(ttl(genA, genB, "copy") + (System.nanoTime() - begin) / COUNT + " ns/op");
			} catch (Exception e) {
				System.out.println(ttl(genA, genB, "copy") + "failure");
			}
		}
		if (Trans3D.class.isAssignableFrom(genA) && Trans3D.class.isAssignableFrom(genB))
			for (int s = 0; s <= 1; s++) {
				String tag = "normalInto" + (s == 1 ? "S" : "");
				try {
					long begin = System.nanoTime();
					for (int k = 0; k < COUNT; k++) {
						Matrix res = ((Trans3D) rand(genA, s == 1)).normalInto((Trans3D) rand(genB, s == 1));
					}
					System.out.println(ttl(genA, genB, tag) + "\t" + ((System.nanoTime() - begin) / COUNT + " ns/op"));
				} catch (Exception e) {
					System.out.println(ttl(genA, genB, tag) + "failure");
				}
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
		if (Trans3D.class.isAssignableFrom(genA)) {
			long begin = System.nanoTime();
			for (int k = 0; k < COUNT; k++) {
				((Trans3D) rand(genA)).setFlags();
			}
			System.out.println(ttl(genA, "setFlags") + (System.nanoTime() - begin) / COUNT + " ns/op");
		}
	}

	public static void main(String[] args) {
		bench(Matrix4.class);
		System.out.println();
		bench(Matrix4.class, Matrix4.class);
		System.out.println();
		bench(Matrix4.class, Matrix34.class);
		System.out.println();
		bench(Matrix4.class, Matrix3.class);
		System.out.println();
		System.out.println();
		bench(Matrix3.class);
		System.out.println();
		bench(Matrix3.class, Matrix4.class);
		System.out.println();
		bench(Matrix3.class, Matrix34.class);
		System.out.println();
		bench(Matrix3.class, Matrix3.class);
		System.out.println();
		System.out.println();
		bench(Matrix34.class);
		System.out.println();
		bench(Matrix34.class, Matrix4.class);
		System.out.println();
		bench(Matrix34.class, Matrix34.class);
		System.out.println();
		bench(Matrix34.class, Matrix3.class);
		System.out.println();

		for (int l = 1; l <= 1000; l *= 10)
			System.out.println(
					Matrix.toString(rand(Matrix3.class, l), rand(Matrix34.class, l), rand(Matrix4.class, l)) + "\n");
	}
}
