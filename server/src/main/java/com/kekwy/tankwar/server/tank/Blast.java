package com.kekwy.tankwar.server.tank;


import com.kekwy.tankwar.io.actions.NewBlastAction;
import com.kekwy.tankwar.io.actions.NewBulletAction;
import com.kekwy.tankwar.io.actions.NewObjectAction;
import com.kekwy.tankwar.server.GameObject;
import com.kekwy.tankwar.server.GameScene;

public class Blast extends GameObject {

	public Blast(GameScene parent) {
		super(parent);
	}

//	private static final ObjectPool blastPool = new ObjectPool(Blast.class);

	public static Blast createBlast(GameScene parent, double x, double y) {
//		Blast blast = (Blast)blastPool.getObject();
		Blast blast = new Blast(null);
		blast.setActive(true);
		blast.transform.setX(x);
		blast.transform.setY(y);
		return blast;
	}

	@Override
	public NewObjectAction getNewObjectAction() {
		setActive(false);
		return new NewBlastAction(getIdentity(), transform.getX(), transform.getY());
	}
}
