module com.kekwy.jw {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.desktop;
	requires javafx.media;
	requires org.apache.poi.poi;
	requires org.apache.poi.ooxml;

	opens com.kekwy.jw to javafx.fxml;
	exports com.kekwy.jw;
}