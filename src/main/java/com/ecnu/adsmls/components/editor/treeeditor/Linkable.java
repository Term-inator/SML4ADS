package com.ecnu.adsmls.components.editor.treeeditor;

import com.ecnu.adsmls.utils.Position;


public interface Linkable {
    Position getLinkPoint(Position adjacentPoint);
}
