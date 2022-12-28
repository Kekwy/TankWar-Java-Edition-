package com.kekwy.jw;

import com.kekwy.jw.tankwar.GameScene;
import com.kekwy.jw.tankwar.gamescenes.OnlinePlayScene;
import com.kekwy.jw.tankwar.util.ResourceUtil;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import com.kekwy.jw.tankwar.TankWar;

import java.util.LinkedList;
import java.util.List;

import static com.kekwy.jw.tankwar.TankWar.MAIN_SCENE;

public class Main extends Application {

//	static final List<String> strings = new LinkedList<>();

	public static void main(String[] args) {
//		 System.out.println(Main.class.getResource("/baseTank.png"));
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		// GameScene scene = new MainMenuScene();
		Screen screen = Screen.getPrimary();
		stage.setX((screen.getVisualBounds().getWidth() - 1000) / 2.0);
		stage.setY((screen.getVisualBounds().getHeight() - 560) / 2.0);
		stage.setResizable(false);
		stage.getIcons().add(new Image("https://blog.kekwy.com/media/images/logo.png"));
		stage.show();
//		TankWar.LOCAL_PLAY_SCENE.setStage(stage);
//		TankWar.LOCAL_PLAY_SCENE.start();
		GameScene scene = new OnlinePlayScene();
		scene.setStage(stage);
		scene.start();
	}

	@Override
	public void stop() throws Exception {
		System.exit(-1);
	}
}
