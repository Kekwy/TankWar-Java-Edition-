package com.kekwy.gameengine;

import com.kekwy.gameengine.util.Position;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public abstract class GameObject {

	private Position position = new Position();
	;


	/**
	 * 游戏实体的渲染样式
	 */
	public abstract void render(Graphics g);

	/**
	 * 渲染每一帧时会被调用的方法
	 */
	public void update() {
	}

	/**
	 * 默认每各0.02会被调用的方法
	 */
	public void fixedUpdate() {
	}

	/**
	 * 当产生碰撞时被调用
	 *
	 * @param obj 与其碰撞的物体
	 */
	public void collide(GameObject obj) {
	}

	/**
	 * 按键按下时被调用
	 * @param keyCode 底层传入的键码
	 */
	public void keyPressedEvent(int keyCode) {
	}

	/**
	 * 按键抬起时被调用
	 * @param keyCode 底层传入的键码
	 */
	public void keyReleasedEvent(int keyCode) {
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}




	public static final int RELOAD_keyReleasedEvent = 16;
	public static final int RELOAD_keyPressedEvent = 8;
	public static final int RELOAD_collide = 4;
	public static final int RELOAD_fixUpdate = 2;
	public static final int RELOAD_update = 1;


	public int getAttribute() {
		return attribute;
	}

	private int attribute;


	private int layer;

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}


	GameScene parent;

	private static final Map<Class<? extends GameObject>, Integer> classAttribute = new HashMap<>();

	private void setAttribute() {

		attribute = 0;

		Class<? extends GameObject> c = this.getClass();

		if(classAttribute.containsKey(c)) {
			attribute = classAttribute.get(c);
			return;
		}

		boolean temp = true;

		try {
			c.getMethod("keyReleasedEvent", int.class);
		} catch (NoSuchMethodException e) {
			temp = false; // System.out.println("null");// throw new RuntimeException(e);
		}
		if (temp) {
			attribute |= RELOAD_keyReleasedEvent;
		}

		temp = true;
		try {
			c.getMethod("keyPressedEvent", int.class);
		} catch (NoSuchMethodException e) {
			temp = false;
		}
		if (temp) {
			attribute |= RELOAD_keyPressedEvent;
		}

		temp = true;
		try {
			c.getMethod("collide", GameObject.class);
		} catch (NoSuchMethodException e) {
			temp = false;
		}
		if (temp) {
			attribute |= RELOAD_collide;
		}

		temp = true;
		try {
			c.getMethod("fixedUpdate");
		} catch (NoSuchMethodException e) {
			temp = false;
		}
		if (temp) {
			attribute |= RELOAD_fixUpdate;
		}

		temp = true;
		try {
			c.getMethod("update");
		} catch (NoSuchMethodException e) {
			temp = false;
		}
		if (temp) {
			attribute |= RELOAD_update;
		}

		classAttribute.put(c, attribute);

	}

	public GameObject(GameScene parent) {

		System.out.println(this.getClass());
		this.parent = parent;
		setAttribute();
	}

	public GameScene getParent() {
		return parent;
	}

	public void setParent(GameScene parent) {
		this.parent = parent;
	}

	public GameObject() {
		setAttribute();
	}


}
