package com.kekwy.gameengine;

import com.kekwy.gameengine.util.Position;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

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
	 * @param obj 与其碰撞的物体
	 */
	public void collide(GameObject obj) {
		System.exit(-1);
	}

	/**
	 * 按键按下时被调用
	 * @param keyCode 底层提供的键码
	 */
	public void keyPressedEvent(int keyCode) {
		System.exit(-1);
	}

	/**
	 * 按键抬起时被调用
	 * @param keyCode 底层提供的键码
	 */
	public void keyReleasedEvent(int keyCode) {
		System.exit(-1);
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


		if(classAttribute.containsKey(c)) {
			attribute = classAttribute.get(c);
			return;
		}

		System.out.println(this.getClass());

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
				c.getDeclaredMethod("collide", GameObject.class);
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
		classAttribute.put(c, attribute);

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
		setActive(true);
	}

	public GameScene getParent() {
		return parent;
	}

	public void setParent(GameScene parent) {
		this.parent = parent;
	}


	/* ====================================================
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
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}









	//=============================================================


}
