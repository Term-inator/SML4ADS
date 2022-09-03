package com.ecnu.adsmls.components.choosebutton.impl;


import com.ecnu.adsmls.components.choosebutton.ChooseButton;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件选择按钮
 */
public class ChooseFileButton extends ChooseButton {
    // 文件过滤器
    protected Map<String, String> fileFilter = new HashMap<>();

    public ChooseFileButton(Pane rootLayout) {
        super(rootLayout);
    }

    public ChooseFileButton(Pane rootLayout, String initDir) {
        super(rootLayout, initDir);
    }

    @Override
    protected void createNode() {
        super.createNode();
        this.btChoose.setText("Choose File");
        hBox.setUserData(this);
    }

    public void setFileFilter(Map<String, String> fileFilter) {
        this.fileFilter = fileFilter;
    }

    @Override
    protected void chooseFile() {
        Stage stage = (Stage) this.rootLayout.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose File");
        if (this.initDir != null) {
            System.out.println(this.initDir);
            fileChooser.setInitialDirectory(new File(this.initDir));
        }
        for (Map.Entry<String, String> filter : fileFilter.entrySet()) {
            String extension = filter.getKey();
            String description = filter.setValue(extension);
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(description, extension)
            );
        }
        File result = fileChooser.showOpenDialog(stage);
        // 选择 cancel 不改变结果
        if (result != null) {
            this.setFile(result);
        }
        // 自适应大小
        stage.sizeToScene();
    }
}
