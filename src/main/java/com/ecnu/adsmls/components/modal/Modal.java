package com.ecnu.adsmls.components.modal;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.util.Objects;

/**
 * 模态弹框
 */
public abstract class Modal {
    protected Stage window;

    protected GridPane gridPane = new GridPane();
    // 静态部分
//    protected ArrayList<Node[]> staticPage = new ArrayList<>();
    protected GridPane slot = new GridPane();

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

    public Modal() {
    }

    public boolean isConfirm() {
        return this.confirm;
    }

    public void setStyle(String key, Object obj) {
        if (Objects.equals(key, "stageStyle")) {
            this.stageStyle = (StageStyle) obj;
        } else if (Objects.equals(key, "opacity")) {
            this.opacity = (Double) obj;
        } else if (Objects.equals(key, "background")) {
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
            if (e.getCode() == KeyCode.ENTER) {
                this.confirm();
            }
        });

        this.gridPane.setPadding(new Insets(15, 20, 15, 20));
        this.gridPane.setVgap(8);
        this.gridPane.setHgap(5);

        this.gridPane.setBackground(this.background);

        this.slot.setVgap(8);
        this.slot.setHgap(5);

        this.gridPane.add(slot, 0, 0, 2, 1);
        this.gridPane.addRow(1, this.btConfirm, this.btCancel);

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
        if (valid) {
            // 填写合法后做的操作
            this.then();
            this.window.close();
        } else {
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

    public Stage getWindow() {
        this.createWindow();
        return window;
    }
}
