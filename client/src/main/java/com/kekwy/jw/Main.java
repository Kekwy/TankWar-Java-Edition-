package com.kekwy.jw;

import com.kekwy.jw.tankwar.gamescenes.LocalPlayScene;
import com.kekwy.jw.tankwar.gamescenes.MainScene;
import com.kekwy.jw.tankwar.GameScene;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

public class Main extends Application {

//	static final List<String> strings = new LinkedList<>();

	public static void main(String[] args) {
//		 System.out.println(Main.class.getResource("/baseTank.png"));
		launch(args);
	}

	private Stage stage;

	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		Screen screen = Screen.getPrimary();
		stage.setX((screen.getVisualBounds().getWidth() - 1000) / 2.0);
		stage.setY((screen.getVisualBounds().getHeight() - 560) / 2.0);
		stage.setResizable(false);
		stage.getIcons().add(new Image("https://blog.kekwy.com/media/images/logo.png"));
		stage.show();
//		TankWar.LOCAL_PLAY_SCENE.setStage(stage);
//		TankWar.LOCAL_PLAY_SCENE.start();
//		GameScene scene = new OnlinePlayScene();
		GameScene scene;
		// 判断是否存在保存游戏进度的临时文件
		File file = new File("./save/");
		if (file.exists() && file.isDirectory()
				&& Objects.requireNonNull(file.listFiles()).length > 0) {
			// 如果存在则弹出会话窗
			scene = showAlert(Objects.requireNonNull(file.listFiles())[0]);
		} else {
			// 如果不存在则正常加载主场景
			scene = new MainScene();
		}
		scene.setStage(stage);
		scene.start();
	}

	@Override
	public void stop() throws Exception {
		// 若当前 stage 上的场景是单人游戏场景，
		// 则在退出前将游戏进度写入磁盘。
		if (stage.getScene() instanceof LocalPlayScene scene) {
			scene.saveToDisk();
		}
		System.exit(-1);
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	private GameScene showAlert(File file) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setHeaderText("上次关闭游戏时有一场未完成的单人游戏");
		alert.setContentText(
				"""
						选择“确定”恢复上次游戏进度，
						选择“取消”或关闭对话框跳转至主界面,
						并删除保存上次进度的临时文件，
						删除操作无法恢复。"""
		);

		Optional<ButtonType> result = alert.showAndWait();

		GameScene scene;

		if (result.get() == ButtonType.OK) {
			LocalPlayScene playScene = new LocalPlayScene();
			playScene.loadFromDisk(file);
			scene = playScene;
		} else {
			// ... user chose CANCEL or closed the dialog
			scene = new MainScene();
		}
		//noinspection ResultOfMethodCallIgnored
		file.delete();
		return scene;
	}
}
