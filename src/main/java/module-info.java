module com.ecnu.adsmls {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.bootstrapfx.core;
    requires fastjson;

    exports com.ecnu.adsmls;
    opens com.ecnu.adsmls to javafx.fxml;

    exports com.ecnu.adsmls.model;
    opens com.ecnu.adsmls.model to fastjson;

    exports com.ecnu.adsmls.views.codepage;
    opens com.ecnu.adsmls.views.codepage to javafx.fxml;
    exports com.ecnu.adsmls.components.modal;
    opens com.ecnu.adsmls.components.modal to javafx.fxml;
}