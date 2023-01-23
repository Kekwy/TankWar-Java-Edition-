package com.kekwy.tankwar.client.trigger;

import com.kekwy.tankwar.client.GameObject;
import com.kekwy.tankwar.client.GameScene;
import javafx.scene.canvas.GraphicsContext;

public class Trigger extends GameObject {

	private final TriggerHandler handler;

	public Trigger(GameScene parent, TriggerHandler handler, double x, double y, int radius) {
		super(parent);
		transform.setX(x);
		transform.setY(y);
		setRadius(radius);
		this.handler = handler;
	}

	public void doCollide(GameObject object) {
		handler.handle(object);
	}

	@Override
	public void refresh(GraphicsContext g, long timestamp) {}
}
