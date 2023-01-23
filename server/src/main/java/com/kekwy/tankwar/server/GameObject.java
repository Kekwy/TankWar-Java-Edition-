package com.kekwy.tankwar.server;

import com.kekwy.tankwar.io.actions.GameAction;
import com.kekwy.tankwar.io.actions.NewObjectAction;
import com.kekwy.tankwar.io.actions.UpdateObjectAction;

import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;


public abstract class GameObject {


	private boolean flag = true;

	public boolean isNew() {
		return flag;
	}

	public void setNewFalse() {
		flag = false;
	}

	private String uuid = UUID.randomUUID().toString();

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getIdentity() {
		return uuid;
	}

	public void destroy() {
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
		return isCollide(x1, y1, radius1, x2 + radius2, y2 + radius2)
				|| isCollide(x1, y1, radius1, x2 - radius2, y2 - radius2)
				|| isCollide(x1, y1, radius1, x2 - radius2, y2 + radius2)
				|| isCollide(x1, y1, radius1, x2 + radius2, y2 - radius2)
				|| isCollide(x1, y1, radius1, x2, y2 - radius2)
				|| isCollide(x1, y1, radius1, x2 + radius2, y2)
				|| isCollide(x1, y1, radius1, x2 - radius2, y2)
				|| isCollide(x1, y1, radius1, x2, y2 + radius2);
	}

	private static boolean isCollide(double rectX, double rectY, double radius, double pointX, double pointY) {
		double disX = Math.abs(rectX - pointX);
		double disY = Math.abs(rectY - pointY);
		return (disX < radius && disY < radius);
	}

	public static class Transform {

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
			double y = this.y;
			return y;
		}

		public void setY(double y) {
			this.y = y;
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
	}


	public final Transform transform = new Transform();

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
	GameScene parent;

	private final GameServer server;

	public GameObject(GameScene parent, GameServer server) {
		this.parent = parent;
		this.server = server;
		setColliderType(ColliderType.COLLIDER_TYPE_NULL);
	}

	boolean dirty = false;

	public void setDirty() {
		this.dirty = true;
	}

	public void clearDirty() {
		this.dirty = false;
	}

	public boolean isDirty() {
		return dirty;
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
		active = this.active;
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	/*======================================================
	     ______           __    __    _        __
	    / ____/  ____    / /   / /   (_)  ____/ /  ___
	   / /      / __ \  / /   / /   / /  / __  /  / _ \
	  / /___   / /_/ / / /   / /   / /  / /_/ /  /  __/
	  \____/   \____/ /_/   /_/   /_/   \__,_/   \___/
	  ======================================================
	 */

	public enum ColliderType {
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

	public void recvPackage(GameAction p) {

	}

	public NewObjectAction getNewObjectAction() {
		return null;
	}

	public UpdateObjectAction getUpdateObjectAction() {
		return null;
	}

}
