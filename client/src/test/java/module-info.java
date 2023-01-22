module com.kekwy.jwtest.tankwar.gamescenes {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.desktop;
	requires javafx.media;
	requires org.junit.jupiter.api;
	requires com.kekwy.jw;

	opens com.kekwy.jwtest.tankwar.gamescenes to javafx.fxml, org.junit.platform.commons;
	exports com.kekwy.jwtest.tankwar.gamescenes;
}