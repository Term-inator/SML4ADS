package com.ecnu.adsmls.utils;

import com.ecnu.adsmls.components.editor.TreeArea;
import com.ecnu.adsmls.components.editor.TreeLinkPoint;
import com.ecnu.adsmls.components.editor.impl.*;
import com.ecnu.adsmls.model.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Converter {
    public static Behavior cast(MBehavior mBehavior) {
        long id = mBehavior.getId();
        Position position = cast(mBehavior.getPosition());
        String name = mBehavior.getName();
        LinkedHashMap<String, String> params = mBehavior.getParams();
        double r = TreeAreaRadius.Behavior.getR();
        position.relocate(position.x + r, position.y + r);
        Behavior behavior = new Behavior(id, position);
        behavior.setName(name);
        behavior.setParams(params);
        return behavior;
    }

    public static MBehavior cast(Behavior behavior) {
        long id = behavior.getId();
        MPosition mPosition = cast(behavior.getPosition());
        String name = behavior.getName();
        LinkedHashMap<String, String> params = behavior.getParams();
        return new MBehavior(id, mPosition, name, params);
    }

    public static BranchPoint cast(MBranchPoint mBranchPoint) {
        long id = mBranchPoint.getId();
        Position position = cast(mBranchPoint.getPosition());
        double r = TreeAreaRadius.BranchPoint.getR();
        position.relocate(position.x + r, position.y + r);
        BranchPoint branchPoint = new BranchPoint(id, position);
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
        List<String> guards = mCommonTransition.getGuards();
        CommonTransition commonTransition = new CommonTransition(id);
        commonTransition.setGuards(guards);
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
        for(int i = 1; i < mCommonTransition.getLinkPoints().size() - 1; ++i) {
            MPosition mPosition = mCommonTransition.getLinkPoints().get(i);
            Position position = cast(mPosition);
            position.relocate(position.x + r, position.y + r);
            commonTransition.getLinkPoints().add(new TreeLinkPoint(position, commonTransition));
        }
        commonTransition.setTarget(target);
        return commonTransition;
    }

    public static MCommonTransition cast(CommonTransition commonTransition) {
        long id = commonTransition.getId();
        long sourceId = commonTransition.getSource().getId();
        long targetId = commonTransition.getTarget().getId();
        List<MPosition> mPositionList = new ArrayList<>();
        List<String> guards = commonTransition.getGuards();
        for(TreeLinkPoint treeLinkPoint : commonTransition.getLinkPoints()) {
            mPositionList.add(cast(treeLinkPoint.getPosition()));
        }
        return new MCommonTransition(id, sourceId, targetId, mPositionList, guards);
    }

    public static ProbabilityTransition cast(List<TreeArea> treeAreaList, MProbabilityTransition mProbabilityTransition) {
        long id = mProbabilityTransition.getId();
        long sourceId = mProbabilityTransition.getSourceId();
        long targetId = mProbabilityTransition.getTargetId();
        List<MPosition> mPositionList = mProbabilityTransition.getLinkPoints();
        String weight = mProbabilityTransition.getWeight();
        ProbabilityTransition probabilityTransition = new ProbabilityTransition(id);
        probabilityTransition.setWeight(weight);
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
        for(int i = 1; i < mProbabilityTransition.getLinkPoints().size() - 1; ++i) {
            MPosition mPosition = mProbabilityTransition.getLinkPoints().get(i);
            Position position = cast(mPosition);
            position.relocate(position.x + r, position.y + r);
            probabilityTransition.getLinkPoints().add(new TreeLinkPoint(position, probabilityTransition));
        }
        probabilityTransition.setTarget(target);
        return probabilityTransition;
    }

    public static MProbabilityTransition cast(ProbabilityTransition probabilityTransition) {
        long id = probabilityTransition.getId();
        long sourceId = probabilityTransition.getSource().getId();
        long targetId = probabilityTransition.getTarget().getId();
        List<MPosition> mPositionList = new ArrayList<>();
        String weight = probabilityTransition.getWeight();
        for(TreeLinkPoint treeLinkPoint : probabilityTransition.getLinkPoints()) {
            mPositionList.add(cast(treeLinkPoint.getPosition()));
        }
        return new MProbabilityTransition(id, sourceId, targetId, mPositionList, weight);
    }

    public static Position cast(MPosition mPosition) {
        return new Position(mPosition.getX(), mPosition.getY());
    }

    public static MPosition cast(Position position) {
        return new MPosition(position.x, position.y);
    }
}
