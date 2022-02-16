package com.ecnu.adsmls.components.editor.treeeditor;

import com.ecnu.adsmls.utils.Position;

/**
 * 是否可以被 TreeLink 连接
 */
public interface Linkable {
    /**
     * 获取连接点
     * @param adjacentPoint 相邻的那一个点
     * @return adjacentPoint 和设定点（一般为图形中点）连线与图形边框的交点
     */
    Position getLinkPoint(Position adjacentPoint);
}
