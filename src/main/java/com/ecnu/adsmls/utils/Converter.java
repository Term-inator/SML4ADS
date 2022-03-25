package com.ecnu.adsmls.utils;

import com.ecnu.adsmls.components.editor.treeeditor.TreeArea;
import com.ecnu.adsmls.components.editor.treeeditor.TreeLinkPoint;
import com.ecnu.adsmls.components.editor.treeeditor.impl.*;
import com.ecnu.adsmls.model.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Tree 中涉及到的 Model 和 TreeComponent 之间的相互转化
 */
public class Converter {
    public static Behavior cast(MBehavior mBehavior) {
        long id = mBehavior.getId();
        Position position = cast(mBehavior.getPosition());
        String name = mBehavior.getName();
        LinkedHashMap<String, String> params = mBehavior.getParams();
        Position treeTextPosition = cast(mBehavior.getTreeTextPosition());
        double r = TreeAreaRadius.Behavior.getR();
        position.relocate(position.x + r, position.y + r);

        Behavior behavior = new Behavior(id, position);
        behavior.updateNode();

        behavior.initTreeText();
        behavior.getTreeText().setPosition(treeTextPosition);

        behavior.setName(name);
        behavior.setParams(params);
        return behavior;
    }

    public static MBehavior cast(Behavior behavior) {
        long id = behavior.getId();
        MPosition mPosition = cast(behavior.getPosition());
        String name = behavior.getName();
        LinkedHashMap<String, String> params = behavior.getParams();
        MPosition mTreeTextPosition = cast(behavior.getTreeText().getPosition());
        return new MBehavior(id, mPosition, name, params, mTreeTextPosition);
    }

    public static BranchPoint cast(MBranchPoint mBranchPoint) {
        long id = mBranchPoint.getId();
        Position position = cast(mBranchPoint.getPosition());
        double r = TreeAreaRadius.BranchPoint.getR();
        position.relocate(position.x + r, position.y + r);

        BranchPoint branchPoint = new BranchPoint(id, position);
        branchPoint.updateNode();

        // BranchPoint 不创建 TreeText

        return branchPoint;
    }

    public static MBranchPoint cast(BranchPoint branchPoint) {
        long id = branchPoint.getId();
        MPosition mPosition = cast(branchPoint.getPosition());
        return new MBranchPoint(id, mPosition);
    }

    public static CommonTransition cast(List<TreeArea> treeAreaList, MCommonTransition mCommonTransition) {
        long id = mCommonTransition.getId();
        long sourceId = mCommonTransition.getSourceId();
        long targetId = mCommonTransition.getTargetId();
        List<MPosition> mPositionList = mCommonTransition.getLinkPoints();
        String guard = mCommonTransition.getGuard();
        Position treeTextPosition = cast(mCommonTransition.getTreeTextPosition());
        CommonTransition commonTransition = new CommonTransition(id);
        TreeArea source = null;
        TreeArea target = null;
        for(TreeArea treeArea : treeAreaList) {
            if(source != null && target != null) {
                break;
            }
            if(treeArea.getId() == sourceId) {
                source = treeArea;
            }
            if(treeArea.getId() == targetId) {
                target = treeArea;
            }
        }
        commonTransition.setSource(source);
        double r = TreeAreaRadius.TreeLinkPoint.getR();
        for(int i = 1; i < mPositionList.size() - 1; ++i) {
            MPosition mPosition = mCommonTransition.getLinkPoints().get(i);
            Position position = cast(mPosition);
            position.relocate(position.x + r, position.y + r);
            commonTransition.getLinkPoints().add(new TreeLinkPoint(position, commonTransition));
        }
        commonTransition.setTarget(target);

        commonTransition.updateNode();
        commonTransition.initTreeText();
        commonTransition.getTreeText().setPosition(treeTextPosition);

        commonTransition.setGuard(guard);

        commonTransition.finish();
        return commonTransition;
    }

    public static MCommonTransition cast(CommonTransition commonTransition) {
        long id = commonTransition.getId();
        long sourceId = commonTransition.getSource().getId();
        long targetId = commonTransition.getTarget().getId();
        List<MPosition> mPositionList = new ArrayList<>();
        String guard = commonTransition.getGuard();
        MPosition mTreeTextPosition = cast(commonTransition.getTreeText().getPosition());
        for(TreeLinkPoint treeLinkPoint : commonTransition.getLinkPoints()) {
            mPositionList.add(cast(treeLinkPoint.getPosition()));
        }
        return new MCommonTransition(id, sourceId, targetId, mPositionList, guard, mTreeTextPosition);
    }

    public static ProbabilityTransition cast(List<TreeArea> treeAreaList, MProbabilityTransition mProbabilityTransition) {
        long id = mProbabilityTransition.getId();
        long sourceId = mProbabilityTransition.getSourceId();
        long targetId = mProbabilityTransition.getTargetId();
        List<MPosition> mPositionList = mProbabilityTransition.getLinkPoints();
        String weight = mProbabilityTransition.getWeight();
        Position treeTextPosition = cast(mProbabilityTransition.getTreeTextPosition());
        ProbabilityTransition probabilityTransition = new ProbabilityTransition(id);
        TreeArea source = null;
        TreeArea target = null;
        for(TreeArea treeArea : treeAreaList) {
            if(treeArea.getId() == sourceId) {
                source = treeArea;
            }
            else if(treeArea.getId() == targetId) {
                target = treeArea;
            }
        }
        probabilityTransition.setSource(source);
        double r = TreeAreaRadius.TreeLinkPoint.getR();
        for(int i = 1; i < mPositionList.size() - 1; ++i) {
            MPosition mPosition = mProbabilityTransition.getLinkPoints().get(i);
            Position position = cast(mPosition);
            position.relocate(position.x + r, position.y + r);
            probabilityTransition.getLinkPoints().add(new TreeLinkPoint(position, probabilityTransition));
        }
        probabilityTransition.setTarget(target);

        probabilityTransition.updateNode();
        probabilityTransition.initTreeText();
        probabilityTransition.getTreeText().setPosition(treeTextPosition);

        probabilityTransition.setWeight(weight);

        probabilityTransition.finish();
        return probabilityTransition;
    }

    public static MProbabilityTransition cast(ProbabilityTransition probabilityTransition) {
        long id = probabilityTransition.getId();
        long sourceId = probabilityTransition.getSource().getId();
        long targetId = probabilityTransition.getTarget().getId();
        List<MPosition> mPositionList = new ArrayList<>();
        String weight = probabilityTransition.getWeight();
        MPosition mTreeTextPosition = cast(probabilityTransition.getTreeText().getPosition());
        for(TreeLinkPoint treeLinkPoint : probabilityTransition.getLinkPoints()) {
            mPositionList.add(cast(treeLinkPoint.getPosition()));
        }
        return new MProbabilityTransition(id, sourceId, targetId, mPositionList, weight, mTreeTextPosition);
    }

    public static Position cast(MPosition mPosition) {
        return new Position(mPosition.getX(), mPosition.getY());
    }

    public static MPosition cast(Position position) {
        return new MPosition(position.x, position.y);
    }
}
