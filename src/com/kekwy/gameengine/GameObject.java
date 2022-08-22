package com.kekwy.gameengine;

import com.kekwy.gameengine.util.Position;

public abstract class GameObject {

	private Position position;

	/**
	 * 游戏实体的渲染样式
	 */
	public abstract void render();

	/**
	 * 渲染每一帧时会被调用的方法
	 */
	public void update() {}

	/**
	 * 默认每各0.02会被调用的方法
	 */
	public void fixedUpdate() {}

	/**
	 * 当产生碰撞时被调用
	 * @param obj 与其碰撞的物体
	 */
	public void collide(GameObject obj) {}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}













	public GameObject() {
		this.position = new Position();
	}



}
