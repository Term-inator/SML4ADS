package com.ecnu.adsmls.views.welcome;

import com.ecnu.adsmls.components.modal.NewProjectModal;
import com.ecnu.adsmls.router.Route;
import com.ecnu.adsmls.router.Router;
import com.ecnu.adsmls.router.params.CodePageParams;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class WelcomeController implements Initializable, Route {
    @FXML
    private Button newProject;
    @FXML
    private Button openProject;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initPane();
    }

    @Override
    public void loadParams() {

    }

    private void initPane() {
        this.newProject.setOnAction(e -> {
            NewProjectModal npm = new NewProjectModal();
            npm.getWindow().showAndWait();
            if(!npm.isConfirm()) {
                return;
            }
            CodePageParams.directory = npm.getDirectory();
            CodePageParams.projectName = npm.getProjectName();
            Router.linkTo("codepage");
        });
    }
}
