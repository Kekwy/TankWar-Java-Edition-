package com.kekwy.jw.tankwar.trigger;

import com.kekwy.jw.tankwar.GameObject;
import com.kekwy.jw.tankwar.GameScene;
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
