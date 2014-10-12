package com.pi.math;

import com.pi.math.vector.Vector3D;

public class VoxelGrid<T> {
	private float gridSize;
	private Object[][][] dataArray;
	private Vector3D minimumPoint;

	public VoxelGrid(float gridSize, int initialSize) {
		this.gridSize = gridSize;
		this.minimumPoint = new Vector3D(0, 0, 0);
		dataArray = new Object[1][1][1];
		ensureLocation(initialSize, initialSize, initialSize);
	}

	public static void main(String[] args) {
		// Run test
		VoxelGrid<Object> voxel = new VoxelGrid<>(0.5f, 2);
		for (int i = 0; i < 25; i++) {
			float x = (float) (Math.random() - 0.5) * 200.0f;
			float y = (float) (Math.random() - 0.5) * 200.0f;
			float z = (float) (Math.random() - 0.5) * 200.0f;
			voxel.getVoxel(x, y, z);
			Object obj = new Object();
			voxel.putVoxel(x, y, z, obj);
			if (voxel.getVoxel(x, y, z) != obj) {
				System.out.println("ERROR @ " + x + "," + y + "," + z);
			}
		}
	}

	public float getGridSize() {
		return gridSize;
	}

	@SuppressWarnings("unchecked")
	public T getVoxel(float x, float y, float z) {
		int vX = (int) Math.floor((x - minimumPoint.x) / gridSize);
		int vY = (int) Math.floor((y - minimumPoint.y) / gridSize);
		int vZ = (int) Math.floor((z - minimumPoint.z) / gridSize);
		if (vX >= 0 && vY >= 0 && vZ >= 0 && vX < dataArray.length
				&& vY < dataArray[vX].length && vZ < dataArray[vX][vY].length) {
			return (T) dataArray[vX][vY][vZ];
		}
		return null;
	}

	public void putVoxel(float x, float y, float z, T obj) {
		ensureLocation(x, y, z);
		int vX = (int) Math.floor((x - minimumPoint.x) / gridSize);
		int vY = (int) Math.floor((y - minimumPoint.y) / gridSize);
		int vZ = (int) Math.floor((z - minimumPoint.z) / gridSize);
		try {
			dataArray[vX][vY][vZ] = obj;
		} catch (Exception e) {
			System.out.println(vX + "," + vY + "," + vZ);
			System.out.println(dataArray.length + "," + dataArray[0].length
					+ "," + dataArray[0][0].length);
			throw new RuntimeException(e);
		}
	}

	private void ensureLocation(float x, float y, float z) {
		int vOX = (int) Math.floor((x - minimumPoint.x) / gridSize);
		int vOY = (int) Math.floor((y - minimumPoint.y) / gridSize);
		int vOZ = (int) Math.floor((z - minimumPoint.z) / gridSize);
		if (vOX >= 0 && vOY >= 0 && vOZ >= 0 && vOX < dataArray.length
				&& vOY < dataArray[vOX].length
				&& vOZ < dataArray[vOX][vOY].length) {
			return;
		}

		// if (minimumPoint.x > x || minimumPoint.y > y || minimumPoint.z > z) {
		// Scale down. First recalculate minPoint
		Vector3D oldMin = minimumPoint;
		Object[][][] oldArray = dataArray;

		int vX = (int) Math.floor(x / gridSize);
		int vY = (int) Math.floor(y / gridSize);
		int vZ = (int) Math.floor(z / gridSize);

		int growX = Math.max(0, 1 + (int) Math.ceil(oldMin.x / gridSize) - vX);
		int growY = Math.max(0, 1 + (int) Math.ceil(oldMin.y / gridSize) - vY);
		int growZ = Math.max(0, 1 + (int) Math.ceil(oldMin.z / gridSize) - vZ);

		// Scale
		int xSize = Math.max(dataArray.length,
				(int) Math.ceil((x - minimumPoint.x) / gridSize) + 1);
		int ySize = Math.max(dataArray[0].length,
				(int) Math.ceil((y - minimumPoint.y) / gridSize) + 1);
		int zSize = Math.max(dataArray[0][0].length,
				(int) Math.ceil((z - minimumPoint.z) / gridSize) + 1);

		dataArray = new Object[xSize + growX][ySize + growY][zSize + growZ];

		for (int vx = 0; vx < oldArray.length; vx++) {
			for (int vy = 0; vy < oldArray[vx].length; vy++) {
				System.arraycopy(oldArray[vx][vy], 0, dataArray[vx + growX][vy
						+ growY], growZ, oldArray[vx][vy].length);
			}
		}

		minimumPoint = new Vector3D(growX == 0 ? minimumPoint.x : vX,
				growY == 0 ? minimumPoint.y : vY, growZ == 0 ? minimumPoint.z
						: vZ);
		// } else {
		// // Scale up
		// int xSize = Math.max(dataArray.length,
		// (int) Math.ceil((x - minimumPoint.x) / gridSize) + 1);
		// int ySize = Math.max(dataArray[0].length,
		// (int) Math.ceil((y - minimumPoint.y) / gridSize) + 1);
		// int zSize = Math.max(dataArray[0][0].length,
		// (int) Math.ceil((z - minimumPoint.z) / gridSize) + 1);
		// Object[][][] oldArray = dataArray;
		// dataArray = new Object[xSize][ySize][zSize];
		// for (int vx = 0; vx < oldArray.length; vx++) {
		// for (int vy = 0; vy < oldArray[vx].length; vy++) {
		// System.arraycopy(oldArray[vx][vy], 0, dataArray[vx][vy], 0,
		// oldArray[vx][vy].length);
		// }
		// }
		// }
	}
}
