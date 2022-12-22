package com.kekwy.jw.tankwar;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GameScene extends Scene {

//	private final double sceneWidth, sceneHeight;

	static final int GRID_SIZE = 50;

	private final String title;

	//	private final Canvas canvas;
	private final GraphicsContext g;

	public GameScene(double width, double height, String title) {
		super(new AnchorPane(), width, height);
		this.title = title;
//		this.canvas = new Canvas(width, height);
		Canvas canvas = new Canvas(width, height);
		g = canvas.getGraphicsContext2D();
		g.setImageSmoothing(true);
		if (this.getRoot() instanceof AnchorPane anchorPane) {
			anchorPane.getChildren().add(canvas);
		} else {
			throw new RuntimeException("未知异常");
		}
//		sceneWidth = width;
//		sceneHeight = height;
		gridRow = (int) height / GRID_SIZE + 1;
		gridCol = (int) width / GRID_SIZE + 1;
		grid = new GridCell[gridRow][gridCol];
		for (int i = 0; i < gridRow; i++) {
			for (int j = 0; j < gridCol; j++) {
				grid[i][j] = new GridCell(i, j);
			}
		}
	}

	private final int gridRow, gridCol;

	private final ExecutorService service = Executors.newCachedThreadPool();

	private final List<GameObject> objectLayer0 = new LinkedList<>();
	private ListIterator<GameObject> iteratorLayer0 = objectLayer0.listIterator();

	private final List<GameObject> objectLayer1 = new LinkedList<>();
	private ListIterator<GameObject> iteratorLayer1 = objectLayer1.listIterator();

	private final List<GameObject> objectLayer2 = new LinkedList<>();
	private ListIterator<GameObject> iteratorLayer2 = objectLayer2.listIterator();


	/**
	 * 向当前场景中添加游戏对象
	 *
	 * @param gameObject 待添加的游戏对象
	 */
	public void addGameObject(GameObject gameObject) {
		if (!active) {
			return;
		}
		int row = (int) gameObject.transform.getY() / GRID_SIZE;
		int col = (int) gameObject.transform.getX() / GRID_SIZE;
		gameObject.transform.setGridRow(row);
		gameObject.transform.setGridCol(col);
		synchronized (service) {
			service.execute(() -> grid[row][col].enter(gameObject));
		}
		synchronized (objectLayer0) {
			switch (gameObject.getLayer()) {
				case 0 -> iteratorLayer0.add(gameObject);
				case 1 -> iteratorLayer1.add(gameObject);
				case 2 -> iteratorLayer2.add(gameObject);
				default -> throw new RuntimeException("未知图层: " + gameObject.getLayer());
			}
			if (gameObject instanceof Runnable runnable) {
				service.execute(runnable);
			}
		}
	}

	public static final long REFRESH_INTERVAL = 1000 / 30;
	private boolean active = true;

	private void doRefresh(ListIterator<GameObject> iterator, ReentrantLock lock) {
		while (iterator.hasNext() && active) {
			lock.lock();
			GameObject object = iterator.next();
			lock.unlock();
			if (!object.isActive()) {
				object.destroy();
				lock.lock();
				iterator.remove();
				lock.unlock();
				continue;
			}
			object.refresh(g, System.currentTimeMillis());
		}
	}

	private final ReentrantLock lock0 = new ReentrantLock();
	private final ReentrantLock lock1 = new ReentrantLock();
	private final ReentrantLock lock2 = new ReentrantLock();


	@SuppressWarnings("BusyWait")
	private void refreshLoop() {
		while (active) {
			lock0.lock();
			iteratorLayer0 = objectLayer0.listIterator();
			lock0.unlock();
			doRefresh(iteratorLayer0, lock0);

			lock1.lock();
			iteratorLayer1 = objectLayer1.listIterator();
			lock1.unlock();
			doRefresh(iteratorLayer1, lock1);

			lock2.lock();
			iteratorLayer2 = objectLayer2.listIterator();
			lock2.unlock();
			doRefresh(iteratorLayer2, lock2);

			try {
				Thread.sleep(REFRESH_INTERVAL);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private Thread refreshThread;

	public void start() {
		active = true;
		refreshThread = new Thread(this::refreshLoop);
		refreshThread.start();
	}

	public void stop() {
		active = false;
		try {
			refreshThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		synchronized (objectLayer0) {
			objectLayer0.clear();
			objectLayer1.clear();
			objectLayer2.clear();
		}
	}

	private Stage stage;

	/**
	 * 设置场景所在的窗口
	 *
	 * @param stage 搭载场景的窗口
	 */
	public void setStage(Stage stage) {
		stage.setScene(this);
		// stage.setWidth(sceneWidth);
		// stage.setHeight(sceneHeight);
		stage.setTitle(title);
		this.stage = stage;
	}

//	/**
//	 * 设置场景窗口标题
//	 *
//	 * @param title 窗口标题
//	 */
//	protected void setTitle(String title) {
//		stage.setTitle(title);
//	}

	protected void changeScene(GameScene nextScene) {
		nextScene.setStage(stage);
		nextScene.start();
		this.stop();
	}

	public void update(GameObject object, double x, double y, int offset) {
		int oldRow = object.transform.getGridRow();
		int oldCol = object.transform.getGridCol();
		int row = (int) y / GRID_SIZE;
		int col = (int) x / GRID_SIZE;
		if (row != oldRow || col != oldCol) {
			object.transform.setGridRow(row);
			object.transform.setGridCol(col);
			synchronized (service) {
				service.execute(()->grid[row][col].enter(object));
				service.execute(()->grid[oldRow][oldCol].leave(object));
			}
		}
	}


	private static class GridCell {
		private final List<GameObject> objects = new LinkedList<>();
		private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

		private final int row, col;

		public GridCell(int row, int col) {
			this.row = row;
			this.col = col;
		}

		public void enter(GameObject object) {
			if (object.transform.getGridRow() != row
					|| object.transform.getGridCol() != col) {
				return;
			}
			readWriteLock.writeLock().lock();
			objects.add(object);
			readWriteLock.writeLock().unlock();
		}

		public void leave(GameObject object) {
			if (object.transform.getGridRow() == row
					&& object.transform.getGridCol() == col) {
				return;
			}
			readWriteLock.writeLock().lock();
			objects.remove(object);
			readWriteLock.writeLock().unlock();
		}

		public void getObjects(List<GameObject> list) {
			readWriteLock.readLock().lock();
			list.addAll(objects);
			readWriteLock.readLock().unlock();
		}
	}

	private final GridCell[][] grid;

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
