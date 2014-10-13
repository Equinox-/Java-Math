package com.pi.math.vector;

public class Vector3D extends Vector {
	public float x, y, z;

	public Vector3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3D() {
		this(0, 0, 0);
	}

	@Override
	public float get(int d) {
		switch (d) {
		case 0:
			return x;
		case 1:
			return y;
		case 2:
			return z;
		default:
			return -1;
		}
	}

	@Override
	public void set(int d, float r) {
		switch (d) {
		case 0:
			x = r;
			return;
		case 1:
			y = r;
			return;
		case 2:
			z = r;
			return;
		default:
		}
	}

	@Override
	public int dimension() {
		return 3;
	}

	@Override
	public Vector3D multiply(float scalar) {
		this.x *= scalar;
		this.y *= scalar;
		this.z *= scalar;
		return this;
	}

	@Override
	public Vector3D clone() {
		return new Vector3D(x, y, z);
	}

	@Override
	public int hashCode() {
		return (int) x << 24 ^ (int) y << 12 ^ (int) z << 6;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Vector3D) {
			Vector3D p = (Vector3D) o;
			float xD = p.x - x, yD = p.y - y, zD = p.z - z;
			return xD == 0 && yD == 0 && zD == 0;
		}
		return false;
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + "," + z + ")";
	}

	public void set(float i, float j, float k) {
		this.x = i;
		this.y = j;
		this.z = k;
	}

	@Override
	public float mag2() {
		return (x * x) + (y * y) + (z * z);
	}
}
