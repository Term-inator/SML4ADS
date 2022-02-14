package com.ecnu.adsmls.components.treeeditor;

import com.ecnu.adsmls.utils.Position;


public interface Linkable {
    Position getLinkPoint(Position adjacentPoint);
}
