package com.ecnu.adsmls.components.choosebutton.impl;


import com.ecnu.adsmls.components.choosebutton.ChooseButton;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;


/**
 * 文件夹选择按钮
 */
public class ChooseDirectoryButton extends ChooseButton {
    public ChooseDirectoryButton(Pane rootLayout) {
        super(rootLayout);
    }

    public ChooseDirectoryButton(Pane rootLayout, String initDir) {
        super(rootLayout, initDir);
    }

    @Override
    protected void createNode() {
        super.createNode();
        this.btChoose.setText("Choose Directory");
        hBox.setUserData(this);
    }

    @Override
    protected void chooseFile() {
        Stage stage = (Stage) this.rootLayout.getScene().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Directory");
        if (this.initDir != null) {
            System.out.println(this.initDir);
            directoryChooser.setInitialDirectory(new File(this.initDir));
        }
        File result = directoryChooser.showDialog(stage);
        // 选择 cancel 不改变结果
        if (result != null) {
            this.setFile(result);
        }
        // 自适应大小
        stage.sizeToScene();
    }
}
