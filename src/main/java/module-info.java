module com.ecnu.adsmls {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.ecnu.adsmls to javafx.fxml;
    exports com.ecnu.adsmls;
}