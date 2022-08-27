package com.kekwy.gameengine;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

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
	public abstract void render(Graphics g);

	/**
	 * 渲染每一帧时会被调用的方法
	 */
	public void update() {
		System.exit(-1);
	}

	/**
	 * 默认每各0.02会被调用的方法
	 */
	public void fixedUpdate() {
		System.exit(-1);
	}

	/**
	 * 当产生碰撞时被调用
	 *
	 * @param gameObjects 与其碰撞的游戏对象
	 */
	public void collide(List<GameObject> gameObjects) {
		System.exit(-1);
	}

	/**
	 * 按键按下时被调用
	 *
	 * @param keyCode 底层提供的键码
	 */
	public void keyPressedEvent(int keyCode) {
		System.exit(-1);
	}

	/**
	 * 按键抬起时被调用
	 *
	 * @param keyCode 底层提供的键码
	 */
	public void keyReleasedEvent(int keyCode) {
		System.exit(-1);
	}


	public class Position {

		private int x, y;

		public Position() {
			this.x = 0;
			this.y = 0;
		}

		public Position(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public void setX(int x) {
			// toWrite();
			// parent.updatePositionMap(GameObject.this, this.x, this.y, x, y);
			this.x = x;
			// finishWrite();
		}

		public int getX() {
			int x;
			// toRead();
			x = this.x;
			// finishRead();
			return x;
		}

		public int getY() {
			int y;
			// toRead();
			y = this.y;
			// finishRead();
			return y;
		}

		public void setY(int y) {
			// toWrite();
			// parent.updatePositionMap(GameObject.this, this.x, this.y, x, y);
			this.y = y;
			// finishWrite();
		}
	}


	public final Position position = new Position();



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

	public int getAttribute() {
		return attribute;
	}

	@SuppressWarnings("unchecked")
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
		setAttribute();
		setDestroyed(true);
		setColliderType(ColliderType.COLLIDER_TYPE_NULL);
		// setActive(true);
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
	private boolean active;

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
	    ______    __                               __
	   /_  __/   / /_    _____  ___   ____ _  ____/ /
	    / /     / __ \  / ___/ / _ \ / __ `/ / __  /
	   / /     / / / / / /    /  __// /_/ / / /_/ /
	  /_/     /_/ /_/ /_/     \___/ \__,_/  \__,_/
	  ======================================================
	 */


	private final Semaphore mutex_reader = new Semaphore(1);
	private final Semaphore mutex_writer = new Semaphore(1);
	private final Semaphore mutex_rw = new Semaphore(1);

	private int count_reader = 0;
	private int count_writer = 0;

	protected void toRead() {
		try {
			mutex_reader.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		count_reader++;
		// System.out.println(count_reader);
		if (count_reader == 1) {
			try {
				mutex_rw.acquire();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		try {
			mutex_writer.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		if (count_writer > 0) {
			mutex_writer.release();
			try {
				mutex_rw.acquire();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		} else
			mutex_writer.release();

		mutex_reader.release();


	}

	protected void finishRead() {
		try {
			mutex_reader.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		count_reader--;
		if (count_reader == 0) {
			mutex_rw.release();
		}
		mutex_reader.release();
	}

	protected void toWrite() {
		try {
			mutex_writer.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		count_writer++;
		mutex_writer.release();

		try {
			mutex_rw.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	protected void finishWrite() {
		try {
			mutex_writer.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		count_writer--;
		// System.out.println(count_writer);
		mutex_writer.release();

		mutex_rw.release();
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
}
