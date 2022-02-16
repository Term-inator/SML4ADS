package com.ecnu.adsmls.components.modal;

import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.util.*;

/**
 * 模态弹框
 */
// TODO 弹框内容更新时，大小自适应
public abstract class Modal {
    protected Stage window;

    protected GridPane gridPane;
    // 静态部分
    protected ArrayList<Node[]> staticPage = new ArrayList<>();

    // 确认按钮
    protected Button btConfirm = new Button("Confirm");
    // 取消按钮
    protected Button btCancel = new Button("Cancel");

    private StageStyle stageStyle = StageStyle.DECORATED;
    private double opacity = 1f;
    private Background background = null;

    // 填写是否合法
    protected boolean valid = true;
    // 是否点击了确认
    protected boolean confirm = true;

    public Modal() {}

    public boolean isConfirm() {
        return this.confirm;
    }

    public void setStyle(String key, Object obj) {
        if(Objects.equals(key, "stageStyle")) {
            this.stageStyle = (StageStyle) obj;
        }
        else if(Objects.equals(key, "opacity")) {
            this.opacity = (Double) obj;
        }
        else if(Objects.equals(key, "background")) {
            this.background = (Background) obj;
        }
    }

    protected void createWindow() {
        this.window = new Stage(this.stageStyle);
        this.window.initModality(Modality.APPLICATION_MODAL);
        this.window.setOpacity(this.opacity);

        // Close -> Cancel
        this.window.setOnCloseRequest(e -> {
            this.cancel();
        });

        // Enter -> Confirm
        this.window.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() == KeyCode.ENTER) {
                this.confirm();
            }
        });

        this.gridPane = new GridPane();
        this.gridPane.setPadding(new Insets(15, 20, 15, 20));
        this.gridPane.setVgap(8);
        this.gridPane.setHgap(5);

        this.gridPane.setBackground(this.background);

        this.staticPage.add(new Node[] {btConfirm, btCancel});

        this.bindConfirmCancel();

        Scene scene = new Scene(gridPane);
        scene.setFill(Color.TRANSPARENT);

        this.window.setScene(scene);
        this.window.getScene().getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
    }

    protected void bindConfirmCancel() {
        btConfirm.setOnAction(e -> {
            this.confirm();
        });

        btCancel.setOnAction(e -> {
            this.cancel();
        });
    }

    private void confirm() {
        // 更新 Modal 界面
        this.update();
        // 检查填写是否合法
        this.check();
        if(valid) {
            // 填写合法后做的操作
            this.then();
            this.window.close();
        }
        else {
            this.valid = true;
        }
    }

    private void cancel() {
        this.confirm = false;
        this.window.close();
    }

    /**
     * 更新数据和界面，在 confirm 中调用
     */
    protected abstract void update();

    /**
     * 检查数据是否合法，在 confirm 中调用
     */
    protected abstract void check();

    /**
     * confirm 且数据 valid 后执行的操作
     */
    protected abstract void then();

    protected void updateGridPane() {
        this.gridPane.getChildren().clear();
        for(int r = 0; r < staticPage.size(); ++r) {
            gridPane.addRow(r, staticPage.get(r));
        }

        window.sizeToScene();
    }

    public Stage getWindow() {
        this.createWindow();
        this.updateGridPane();
        return window;
    }
}
