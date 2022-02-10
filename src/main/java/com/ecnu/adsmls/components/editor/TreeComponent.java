package com.ecnu.adsmls.components.editor;

import com.ecnu.adsmls.utils.Position;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * 树组件
 */
public abstract class TreeComponent extends Component {
    private final long id;

    private TreeText treeText;

    public TreeComponent(long id) {
        super();
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public abstract Position getTextPosition();

    public void initTreeText() {
        if(this.treeText == null) {
            this.treeText = new TreeText(this);
            this.treeText.updateNode();
        }
    }

    public TreeText getTreeText() {
        return treeText;
    }

    public void setTreeText(TreeText treeText) {
        this.treeText = treeText;
    }

    public abstract String getInfo();

    public void updateTreeTextPosition() {
        Position treeTextPosition = this.getTextPosition();
        this.treeText.setPosition(new Position(treeTextPosition.x, treeTextPosition.y));
        this.treeText.getNode().relocate(treeTextPosition.x, treeTextPosition.y);
    }

    @Override
    public void active() {
        super.active();
        if(this.treeText != null) {
            this.treeText.active();
        }
    }

    @Override
    public void inactive() {
        super.inactive();
        if(this.treeText != null) {
            this.treeText.inactive();
        }
    }

    public List<Node> remove() {
        List<Node> res = new ArrayList<>();
        res.add(this.graphicNode);
        res.add(this.treeText.getNode());
        return res;
    }
}
