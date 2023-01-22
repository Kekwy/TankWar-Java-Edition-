package com.kekwy.jw.tankwar;

import com.kekwy.jw.tankwar.util.TankWarUtil;
import com.kekwy.tankwar.server.io.Protocol;
import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;


public abstract class GameObject implements Serializable {

	public void update(Protocol p) {
	}

	boolean destroyed = false;

	public void waitFor() {
		try {
			synchronized (this) {
				if (!destroyed) {
					this.wait();
				}
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}


	/* =============================================================================
	     ______                              ______                        __
	    / ____/  ____ _   ____ ___   ___    / ____/ _   __  ___    ____   / /_
	   / / __   / __ `/  / __ `__ \ / _ \  / __/   | | / / / _ \  / __ \ / __/
	  / /_/ /  / /_/ /  / / / / / //  __/ / /___   | |/ / /  __/ / / / // /_
	  \____/   \__,_/  /_/ /_/ /_/ \___/ /_____/   |___/  \___/ /_/ /_/ \__/
	   =============================================================================
	 */

	/**
	 * 游戏实体的渲染样式
	 *
	 * @param g 底层提供的画笔对象
	 */
	public abstract void refresh(GraphicsContext g, long timestamp);

	public void destroy() {
		synchronized (this) {
			destroyed = true;
			this.notify();
		}
	}

	/**
	 * 默认每各0.02会被调用的方法
	 */
	@Deprecated
	public void fixedUpdate() {
		System.exit(-1);
	}

	/**
	 * 当产生碰撞时被调用
	 *
	 * @param gameObjects 与其碰撞的游戏对象
	 */
	@Deprecated
	public void collide(List<GameObject> gameObjects) {
		System.exit(-1);
	}

	/**
	 * 按键按下时被调用
	 *
	 * @param keyCode 底层提供的键码
	 */
	@Deprecated
	public void keyPressedEvent(int keyCode) {
		System.exit(-1);
	}

	/**
	 * 按键抬起时被调用
	 *
	 * @param keyCode 底层提供的键码
	 */
	@Deprecated
	public void keyReleasedEvent(int keyCode) {
		System.exit(-1);
	}

	protected boolean isCollide(GameObject object) {
		double x1, y1, x2, y2, radius1, radius2;
		if (this.getRadius() >= object.getRadius()) {
			x1 = this.transform.getX();
			y1 = this.transform.getY();
			x2 = object.transform.getX();
			y2 = object.transform.getY();
			radius1 = radius;
			radius2 = object.radius;
		} else {
			x2 = this.transform.getX();
			y2 = this.transform.getY();
			x1 = object.transform.getX();
			y1 = object.transform.getY();
			radius2 = radius;
			radius1 = object.radius;
		}
		return TankWarUtil.isCollide(x1, y1, radius1, x2 + radius2, y2 + radius2)
				|| TankWarUtil.isCollide(x1, y1, radius1, x2 - radius2, y2 - radius2)
				|| TankWarUtil.isCollide(x1, y1, radius1, x2 - radius2, y2 + radius2)
				|| TankWarUtil.isCollide(x1, y1, radius1, x2 + radius2, y2 - radius2)
				|| TankWarUtil.isCollide(x1, y1, radius1, x2, y2 - radius2)
				|| TankWarUtil.isCollide(x1, y1, radius1, x2 + radius2, y2)
				|| TankWarUtil.isCollide(x1, y1, radius1, x2 - radius2, y2)
				|| TankWarUtil.isCollide(x1, y1, radius1, x2, y2 + radius2);
	}


	public static class Transform implements Serializable {

		private double x, y;
		private int gridRow, gridCol;

		public Transform() {
			this.x = 0;
			this.y = 0;
		}

		public Transform(double x, double y) {
			this.x = x;
			this.y = y;
		}

		public void setX(double x) {
			// toWrite();
			// parent.updatePositionMap(GameObject.this, this.x, this.y, x, y);
			this.x = x;
			// finishWrite();
		}

		public double getX() {
			double x;
			// toRead();
			x = this.x;
			// finishRead();
			return x;
		}

		public double getY() {
			double y;
			// toRead();
			y = this.y;
			// finishRead();
			return y;
		}

		public void setY(double y) {
			// toWrite();
			// parent.updatePositionMap(GameObject.this, this.x, this.y, x, y);
			this.y = y;
			// finishWrite();
		}

		public int getGridRow() {
			return gridRow;
		}

		public void setGridRow(int gridRow) {
			this.gridRow = gridRow;
		}

		public int getGridCol() {
			return gridCol;
		}

		public void setGridCol(int gridCol) {
			this.gridCol = gridCol;
		}


		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Transform transform = (Transform) o;
			return Math.abs(Double.compare(transform.x, x)) <= 10 && Math.abs(Double.compare(transform.y, y)) <= 10 && gridRow == transform.gridRow && gridCol == transform.gridCol;
		}

		@Override
		public int hashCode() {
			return Objects.hash(x, y, gridRow, gridCol);
		}
	}


	public final Transform transform = new Transform();

	/* ====================================================
	      __
	     / /   ____ _   __  __  ___    _____
	    / /   / __ `/  / / / / / _ \  / ___/
	   / /___/ /_/ /  / /_/ / /  __/ / /
	  /_____/\__,_/   \__, /  \___/ /_/
	                 /____/
	  =====================================================
	 */

	/**
	 * 渲染层
	 */
	private int layer;

	public int getLayer() {
		// toRead();
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}



	/* ====================================================
	      ____                                  __
	     / __ \  ____ _   _____  ___    ____   / /_
	    / /_/ / / __ `/  / ___/ / _ \  / __ \ / __/
	   / ____/ / /_/ /  / /    /  __/ / / / // /_
	  /_/      \__,_/  /_/     \___/ /_/ /_/ \__/
	  =====================================================
	 */


	/**
	 * 游戏对象所在的场景
	 */
	transient GameScene parent;

	public GameObject(GameScene parent) {
		this.parent = parent;
		setColliderType(ColliderType.COLLIDER_TYPE_NULL);
	}

	public GameScene getParent() {
		GameScene parent;
		// toRead();
		parent = this.parent;
		// finishRead();
		return parent;
	}

	public void setParent(GameScene parent) {
		// toWrite();
		this.parent = parent;
		// finishWrite();
	}


	/*=====================================================
	      ___           __     _
	     /   |  _____  / /_   (_) _   __  ___
	    / /| | / ___/ / __/  / / | | / / / _ \
	   / ___ |/ /__  / /_   / /  | |/ / /  __/
	  /_/  |_|\___/  \__/  /_/   |___/  \___/
	  =====================================================
	 */

	/**
	 * 游戏对象的活动状态
	 */
	private boolean active = false;

	public boolean isActive() {
		boolean active;
		// toRead();
		active = this.active;
		// finishRead();
		return active;
	}

	public void setActive(boolean active) {
		// toWrite();
		this.active = active;
		// finishWrite();
	}

	/*======================================================
	     ______           __    __    _        __
	    / ____/  ____    / /   / /   (_)  ____/ /  ___
	   / /      / __ \  / /   / /   / /  / __  /  / _ \
	  / /___   / /_/ / / /   / /   / /  / /_/ /  /  __/
	  \____/   \____/ /_/   /_/   /_/   \__,_/   \___/
	  ======================================================
	 */

	public enum ColliderType implements Serializable {
		COLLIDER_TYPE_NULL,
		COLLIDER_TYPE_RECT,
		COLLIDER_TYPE_CIRCLE,
	}

	ColliderType colliderType;
	int radius = 0;


	public ColliderType getColliderType() {
		return colliderType;
	}

	public void setColliderType(ColliderType colliderType) {
		this.colliderType = colliderType;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}


	private final ReentrantLock lock = new ReentrantLock();

	public ReentrantLock collideLock() {
		return lock;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GameObject object = (GameObject) o;
		return layer == object.layer && active == object.active && radius == object.radius && transform.equals(object.transform) && colliderType == object.colliderType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(transform, layer, active, colliderType, radius);
	}
}
