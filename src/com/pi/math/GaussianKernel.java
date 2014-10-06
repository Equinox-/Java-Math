package com.pi.math;

public class GaussianKernel {
	public float[][] kernel;

	public GaussianKernel(int size) {
		this(size, size);
	}

	public GaussianKernel(int size, float sigma) {
		this(size, size, sigma);
	}

	public GaussianKernel(int w, int h) {
		this(w, h, 0.84089642f);
	}

	public GaussianKernel(int w, int h, float SIGMA) {
		if ((w & 1) != 1 || (h & 1) != 1)
			throw new IllegalArgumentException(
					"Gaussian kernels must have an odd size");

		kernel = new float[w][h];

		float total = 0;
		for (int i = 0; i < kernel.length; i++) {
			for (int j = 0; j < kernel[i].length; j++) {
				final int x = i - 1;
				final int y = j - 1;
				kernel[i][j] = (float) Math.exp(-(x * x + y * y)
						/ (2f * SIGMA * SIGMA));
				total += kernel[i][j];
			}
		}
		for (int i = 0; i < kernel.length; i++) {
			for (int j = 0; j < kernel[i].length; j++) {
				kernel[i][j] /= total;
			}
		}
	}
}
