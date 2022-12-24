package com.kekwy.jw.tankwar;

import com.kekwy.jw.tankwar.tank.Tank;
import com.kekwy.jw.tankwar.util.TankWarUtil;
import javafx.scene.canvas.GraphicsContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;


public abstract class GameObject {


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

	public void destroy() {}

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
		if(this.getRadius() >= object.getRadius()) {
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
	}


	public final Transform transform = new Transform();



	/* ==================================================================
	     _____            __     ______    __
	    / ___/  __  __   / /_   / ____/   / /  ____ _   _____   _____
	    \__ \  / / / /  / __ \ / /       / /  / __ `/  / ___/  / ___/
	   ___/ / / /_/ /  / /_/ // /___    / /  / /_/ /  (__  )  (__  )
	  /____/  \__,_/  /_.___/ \____/   /_/   \__,_/  /____/  /____/
	  ===================================================================
	 */

	public static final int RELOAD_render = 32;
	public static final int RELOAD_keyReleasedEvent = 16;
	public static final int RELOAD_keyPressedEvent = 8;
	public static final int RELOAD_collide = 4;
	public static final int RELOAD_fixUpdate = 2;
	public static final int RELOAD_update = 1;


	/**
	 * 记录当前子类重载了[Game]中的哪些函数
	 */
	private int attribute;
	private static final Map<Class<? extends GameObject>, Integer> classAttribute = new HashMap<>();

	@Deprecated
	public int getAttribute() {
		return attribute;
	}

	@SuppressWarnings("unchecked")
	@Deprecated
	private void setAttribute() {

		attribute = 0;

		Class<? extends GameObject> c = this.getClass();


		if (classAttribute.containsKey(c)) {
			attribute = classAttribute.get(c);
			return;
		}

		// System.out.println(this.getClass());

		while (!c.equals(GameObject.class)) {
			boolean temp = true;

			try {
				c.getDeclaredMethod("keyReleasedEvent", int.class);
			} catch (NoSuchMethodException e) {
				temp = false; // System.out.println("null");// throw new RuntimeException(e);
			}
			if (temp) {
				attribute |= RELOAD_keyReleasedEvent;
			}

			temp = true;
			try {
				c.getDeclaredMethod("keyPressedEvent", int.class);
			} catch (NoSuchMethodException e) {
				temp = false;
			}
			if (temp) {
				attribute |= RELOAD_keyPressedEvent;
			}

			temp = true;
			try {
				c.getDeclaredMethod("collide", List.class);
			} catch (NoSuchMethodException e) {
				temp = false;
			}
			if (temp) {
				attribute |= RELOAD_collide;
			}

			temp = true;
			try {
				c.getDeclaredMethod("fixedUpdate");
			} catch (NoSuchMethodException e) {
				temp = false;
			}
			if (temp) {
				attribute |= RELOAD_fixUpdate;
			}

			temp = true;
			try {
				c.getDeclaredMethod("update");
			} catch (NoSuchMethodException e) {
				temp = false;
			}
			if (temp) {
				attribute |= RELOAD_update;
			}

			c = (Class<? extends GameObject>) c.getSuperclass();
		}
		classAttribute.put(this.getClass(), attribute);

	}


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
	GameScene parent;

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

	public boolean isDestroyed() {
		boolean res;
		// toRead();
		res = (destroyed & RELOAD_render) != 0 && (destroyed & 31) == this.attribute;
		// finishRead();
		return res;
	}

	private void setDestroyed(boolean b) {
		if(b){
			this.destroyed = RELOAD_render | this.attribute;
		}
		else {
			this.destroyed = 0;
		}
	}

	public void setDestroyed(int cycle) {
		// toWrite();
		this.destroyed |= cycle;
		// finishWrite();
	}

	private int destroyed = 0;

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
		setDestroyed(false);
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

}
