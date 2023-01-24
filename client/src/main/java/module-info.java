module com.kekwy.tankwar {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.desktop;
	requires javafx.media;
	requires org.apache.poi.poi;
	requires org.apache.poi.ooxml;
	requires com.kekwy.tankwar.server.io;

	opens com.kekwy.tankwar to javafx.fxml;
	exports com.kekwy.tankwar;
	exports com.kekwy.tankwar.client;
	exports com.kekwy.tankwar.io.handlers.client;
	opens com.kekwy.tankwar.client to javafx.fxml;
}