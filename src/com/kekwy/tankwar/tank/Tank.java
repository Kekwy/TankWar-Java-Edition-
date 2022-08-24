package com.kekwy.tankwar.tank;

import com.kekwy.gameengine.GameObject;
import com.kekwy.gameengine.GameScene;

import java.awt.*;

public abstract class Tank extends GameObject {

	private int hp, atk, speed;
	private Color color;


	protected Tank(GameScene parent) {
		super(parent);
	}


	public void fire() {

	}



}
