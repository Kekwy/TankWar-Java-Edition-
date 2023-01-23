package com.kekwy.jw.tankwar;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

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
//				grid[i][j] = new GridCell(i, j);
				grid[i][j] = new GridCell();
			}
		}
	}

	private final int gridRow, gridCol;

	private final ExecutorService service = Executors.newCachedThreadPool();

	public void setPlayerUUid(String s) {
	}

	private class ShowLayer {
		private final List<GameObject> objectList = new LinkedList<>();
		private final List<GameObject> removeBuffer = new ArrayList<>();
		private ListIterator<GameObject> iterator = objectList.listIterator();

		private final Consumer<GameObject> helper = (object) -> {
			object.setActive(false);
			int row = object.transform.getGridRow();
			int col = object.transform.getGridCol();
			grid[row][col].leave(object);
		};

		public void add(GameObject object) {
			synchronized (objectList) {
				iterator.add(object);
			}
		}

		public void refresh() {
			while (active) {
				GameObject object;
				synchronized (objectList) {
					if (!iterator.hasNext()) {
						break;
					}
					object = iterator.next();
				}
//				System.out.println(object);
				if (!object.isActive()) {
					removeBuffer.add(object);
					object.destroy();
					// TODO
					final int row = object.transform.getGridRow();
					final int col = object.transform.getGridCol();
					final GameObject objectToRemove = object;
					service.execute(() -> grid[row][col].leave(objectToRemove));

					synchronized (GameScene.this.objectList) {
						GameScene.this.objectList.remove(objectToRemove);
					}

					continue;
				}
				object.refresh(g, System.currentTimeMillis());
			}
			synchronized (objectList) {
				removeBuffer.forEach(objectList::remove);
				removeBuffer.clear();
				iterator = objectList.listIterator();
			}
		}

		public void clear() {
			synchronized (objectList) {
				objectList.forEach(helper);
			}
		}

	}

	private final ShowLayer[] layers = {
			new ShowLayer(),
			new ShowLayer(),
			new ShowLayer(),
			new ShowLayer(),
			new ShowLayer(),
			new ShowLayer(),
			new ShowLayer()
	};


	protected final List<GameObject> objectList = new LinkedList<>();

	protected final Map<String, GameObject> objectMap = new HashMap<>();

	ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	public GameObject findObject(String identity) {
		GameObject object;
		lock.readLock().lock();
		object = objectMap.get(identity);
		lock.readLock().unlock();
		return object;
	}

	/**
	 * 向当前场景中添加游戏对象
	 *
	 * @param gameObject 待添加的游戏对象
	 */
	public void addGameObject(GameObject gameObject) {

		if (!active) {
			return;
		}

		// 加入总对象列表
		synchronized (objectList) {
			objectList.add(gameObject);
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
		// 将游戏对象加入对应的渲染图层
		layers[gameObject.getLayer()].add(gameObject);
		if (!online && gameObject instanceof Runnable runnable) {
			service.execute(runnable);
		}
	}

	boolean online = false;

	public boolean isOnline() {
		return online;
	}

	public void setOnline() {
		this.online = true;
	}

	public static final long REFRESH_INTERVAL = 1000 / 30;
	private boolean active = true;

	@SuppressWarnings("BusyWait")
	private void refreshLoop() {
		while (active) {

			for (ShowLayer layer : layers) {
				layer.refresh();
			}

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
		for (GameObject object : objectList) {
			object.setActive(false);
//			object.destroy();
		}
//		for (ShowLayer layer : layers) {
//			layer.clear();
//		}
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
				service.execute(() -> {
					grid[row][col].enter(object);
					grid[oldRow][oldCol].leave(object);
				});
			}
		}
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


	protected void clear() {
		for (ShowLayer layer : layers) {
			layer.clear();
		}
	}

	public boolean isActive() {
		return active;
	}

}