package com.kekwy.tankwar.server;

import com.kekwy.tankwar.server.GameObject;
import com.kekwy.tankwar.server.tank.Bullet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GameScene {

	static final int GRID_SIZE = 50;

	private final double SCENE_WIDTH, SCENE_HEIGHT;

	public GameScene(double width, double height) {

		gridRow = (int) height / GRID_SIZE + 1;
		gridCol = (int) width / GRID_SIZE + 1;
		grid = new GridCell[gridRow][gridCol];
		for (int i = 0; i < gridRow; i++) {
			for (int j = 0; j < gridCol; j++) {
//				grid[i][j] = new GridCell(i, j);
				grid[i][j] = new GridCell();
			}
		}
		SCENE_WIDTH = width;
		SCENE_HEIGHT = height;

	}

	private final int gridRow, gridCol;

	private final ExecutorService service = Executors.newCachedThreadPool();



	public final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	public final Map<String, GameObject> objectMap = new HashMap<>();



	/**
	 * 向当前场景中添加游戏对象
	 *
	 * @param gameObject 待添加的游戏对象
	 */
	public void addGameObject(GameObject gameObject) {
		if (!active) {
			return;
		}
		lock.writeLock().lock();
		objectMap.put(gameObject.getIdentity(), gameObject);
		lock.writeLock().unlock();
		int row = (int) gameObject.transform.getY() / GRID_SIZE;
		int col = (int) gameObject.transform.getX() / GRID_SIZE;
		if (row < gridRow && col < gridCol) {
			gameObject.transform.setGridRow(row);
			gameObject.transform.setGridCol(col);
			synchronized (service) {
				service.execute(() -> grid[row][col].enter(gameObject));
			}
		}
		if (gameObject instanceof Runnable runnable) {
			service.execute(runnable);
		}
	}

	public GameObject findObject(String uuid) {
		GameObject object;
		lock.readLock().lock();
		object = objectMap.get(uuid);
		lock.readLock().unlock();
		return object;
	}

	public static final long REFRESH_INTERVAL = 1000 / 30;
	private boolean active = true;



	private Thread refreshThread;

	public void start() {
		active = true;
	}

	public void stop() {
		active = false;
		try {
			refreshThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}


//	/**
//	 * 设置场景窗口标题
//	 *
//	 * @param title 窗口标题
//	 */
//	protected void setTitle(String title) {
//		stage.setTitle(title);
//	}


	public void update(GameObject object, double x, double y, int offset) {

		if (object instanceof Bullet) {
			if (x < 0 || x > SCENE_WIDTH || y < 0 || y > SCENE_HEIGHT) {
				object.setActive(false);
				return;
			}
		} else {
			x = Math.max(x, offset);
			x = Math.min(x, SCENE_WIDTH - offset);
			y = Math.max(y, offset);
			y = Math.min(y, SCENE_HEIGHT - offset);
		}

		int oldRow = object.transform.getGridRow();
		int oldCol = object.transform.getGridCol();
		int row = (int) y / GRID_SIZE;
		int col = (int) x / GRID_SIZE;
		if (row != oldRow || col != oldCol) {
			object.transform.setGridRow(row);
			object.transform.setGridCol(col);
			synchronized (service) {
				service.execute(() -> grid[row][col].enter(object));
				service.execute(() -> grid[oldRow][oldCol].leave(object));
			}
		}

		object.transform.setX(x);
		object.transform.setY(y);

	}


	private static class GridCell {
		private final List<GameObject> objects = new ArrayList<>();
		private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

		private final List<GameObject> removeBuffer = new ArrayList<>();

//		private final int row, col;
//
//		public GridCell(int row, int col) {
//			this.row = row;
//			this.col = col;
//		}

		public void enter(GameObject object) {
//			if (object.transform.getGridRow() != row
//					|| object.transform.getGridCol() != col) {
//				return;
//			}
			readWriteLock.writeLock().lock();
			if (removeBuffer.contains(object)) {
				removeBuffer.remove(object);
				readWriteLock.writeLock().unlock();
				return;
			}
			objects.add(object);
			readWriteLock.writeLock().unlock();
		}

		public void leave(GameObject object) {
			readWriteLock.writeLock().lock();
			if (!objects.remove(object)) {
				removeBuffer.add(object);
			}
			readWriteLock.writeLock().unlock();
		}

		public void getObjects(List<GameObject> list) {
			readWriteLock.readLock().lock();
			list.addAll(objects);
			readWriteLock.readLock().unlock();
		}
	}

	private GridCell[][] grid = null;

	public void getObjectAroundTheGridCell(GameObject object, List<GameObject> list) {
		int row = object.transform.getGridRow();
		int col = object.transform.getGridCol();
		grid[row][col].getObjects(list);
		if (row - 1 >= 0)
			grid[row - 1][col].getObjects(list);
		if (row + 1 < gridRow)
			grid[row + 1][col].getObjects(list);
		if (col - 1 >= 0)
			grid[row][col - 1].getObjects(list);
		if (col + 1 < gridCol)
			grid[row][col + 1].getObjects(list);
		if (row - 1 >= 0 && col - 1 >= 0)
			grid[row - 1][col - 1].getObjects(list);
		if (row - 1 >= 0 && col + 1 < gridCol)
			grid[row - 1][col + 1].getObjects(list);
		if (row + 1 < gridRow && col - 1 >= 0)
			grid[row + 1][col - 1].getObjects(list);
		if (row + 1 < gridRow && col + 1 < gridCol)
			grid[row + 1][col + 1].getObjects(list);
	}

}
