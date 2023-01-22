module com.kekwy.jw {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.desktop;
	requires javafx.media;
	requires org.apache.poi.poi;
	requires org.apache.poi.ooxml;
	requires com.kekwy.tankwar.server.io;

	opens com.kekwy.jw to javafx.fxml;
	exports com.kekwy.jw;
	exports com.kekwy.jw.tankwar;
	exports com.kekwy.jw.tankwar.gamescenes;
	exports com.kekwy.jw.tankwar.tank;
}