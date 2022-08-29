package com.ecnu.adsmls.views.welcome;

import com.ecnu.adsmls.components.modal.impl.NewProjectModal;
import com.ecnu.adsmls.components.modal.impl.OpenProjectModal;
import com.ecnu.adsmls.router.Route;
import com.ecnu.adsmls.router.Router;
import com.ecnu.adsmls.router.params.CodePageParams;
import com.ecnu.adsmls.utils.FileSystem;
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
        // newProject 事件
        this.newProject.setOnAction(e -> {
            NewProjectModal npm = new NewProjectModal();
            npm.getWindow().showAndWait();
            if (!npm.isConfirm()) {
                return;
            }
            // 文件夹创建失败
            if (!npm.isSucceed()) {
                System.out.println("Directory already exists");
                return;
            }
            CodePageParams.directory = npm.getDirectory();
            CodePageParams.projectName = npm.getProjectName();
            Router.linkTo("codepage");
        });

        // openProject 事件
        this.openProject.setOnAction(e -> {
            OpenProjectModal opm = new OpenProjectModal();
            opm.getWindow().showAndWait();
            if (!opm.isConfirm()) {
                return;
            }
            // 设置 codepage 所需的参数
            CodePageParams.directory = FileSystem.getProjectDir(opm.getDirectory());
            CodePageParams.projectName = FileSystem.getProjectName(opm.getDirectory());
            Router.linkTo("codepage");
        });
    }
}
