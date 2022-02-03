module com.ecnu.adsmls {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.bootstrapfx.core;
    requires fastjson;

    opens com.ecnu.adsmls to javafx.fxml;
    exports com.ecnu.adsmls;
    exports com.ecnu.adsmls.views.codepage;
    opens com.ecnu.adsmls.views.codepage to javafx.fxml;
}