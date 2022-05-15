package com.ecnu.adsmls.components.editor.treeeditor;

import com.ecnu.adsmls.utils.Position;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * 依附于树节点的文字
 */
public class TreeText extends Area {
    private TreeComponent component;

    public TreeText(TreeComponent component) {
        super(component.getTextPosition());
        this.component = component;
        component.setTreeText(this);

        this.createNode();
        this.inactive();
        this.enableDrag();
    }

    public Text getText() {
        return (Text) this.shape;
    }

    public void setText(String text) {
        ((Text) this.shape).setText(text);
    }

    @Override
    public Position getCenterPoint() {
        double width = this.graphicNode.getBoundsInLocal().getWidth();
        double height = this.graphicNode.getBoundsInLocal().getHeight();
        return new Position(this.position.x + width, this.position.y + height);
    }

    @Override
    public void active() {
        this.shape.setFill(Color.ORANGE);
    }

    @Override
    public void inactive() {
        // 依附的节点被选中则不进入 inactive 状态
        if (this.component.isSelected()) {
            return;
        }
        this.shape.setFill(Color.BLACK);
    }

    @Override
    public void createNode() {
        Text text = new Text();
        text.setX(this.position.x);
        text.setY(this.position.y + text.getBaselineOffset());

        this.shape = text;
    }

    @Override
    public void updatePosition() {
        ((Text) this.shape).setX(this.position.x);
        ((Text) this.shape).setY(this.position.y + this.shape.getBaselineOffset());
    }

    @Override
    public void updateNode() {
        this.updatePosition();
        this.addNode(this.shape);
    }
}
