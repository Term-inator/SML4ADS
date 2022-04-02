package com.ecnu.adsmls;

import com.ecnu.adsmls.utils.Geometry;
import org.junit.jupiter.api.Test;

public class GeometryTest {
    @Test
    public void betweenTest() {
        System.out.println(Geometry.between(1.2, 1.2, Double.POSITIVE_INFINITY, "(]"));
    }
}
