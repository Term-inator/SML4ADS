package com.ecnu.adsmls.components.editor;

import com.ecnu.adsmls.utils.Position;


public interface Linkable {
    Position getLinkPoint(Position adjacentPoint);
}
