package com.ecnu.adsmls.verifier.convert.src.main.java.json.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wei
 * @description guard条件的规范名称
 * @date 2022-04-05 21:56
 */
public class GuardType {

    /*
        - double distance_to_end()
        - double distance(car1.name, car2.name)
        - bool can_turn_left(), bool can_turn_right()
        - bool can_change_eft(), bool can_change_right()

     */
    public static final String DISTANCE_TO_END = "distanceToEnd(.*)";
    public static final String DISTANCE = "distance(.*)";
    public static final String CAN_TURN_LEFT = "canTurnLeft(.*)";
    public static final String CAN_TUEN_RIGHT = "canTurnRight(.*)";
    public static final String CAN_CHANGE_LEFT = "canChangeLft(.*)";
    public static final String CAN_CHANGE_RIGHT = "canChangeRight(.*)";
    public static final String IS_JUNCTION = "isJunction(.*)";

    public static final String HAS_OBJ_WITHIN_DIS_IN_LANE = "hasObjWithinDisInLane(.*)";
    public static final String HAS_OBJ_WITHIN_DIS_IN_LEFT_LANE = "hasObjWithinDisInLeftLane(.*)";
    public static final String HAS_OBJ_WITHIN_DIS_IN_RIGHT_LANE = "hasObjWithinDisInRightLane(.*)";
    public static final String WITHIN_DIS_TO_OBJS_IN_LANE = "withinDisToObjsInLane(.*)";
    public static final String WITHIN_DIS_TO_OBJS_IN_ROAD = "withinDisToObjsInRoad(.*)";
    public static final String IS_IN_SAME_LANE = "isInSameLane(.*)";
    public static final String COMPARE_GUARD = ".*[>=<].*";
    public static final String OR_GUARD = ".*||.*";
    public static final String AND_GUARD = ".*&&.*";
    public static final String NOT_GUARD = "!.*";

    public static final List<String> allGuards = new ArrayList<String>() {{
        add(DISTANCE);
        add(DISTANCE_TO_END);
        add(CAN_CHANGE_LEFT);
        add(CAN_CHANGE_RIGHT);
        add(CAN_TUEN_RIGHT);
        add(CAN_TURN_LEFT);
        add(IS_JUNCTION);
        add(HAS_OBJ_WITHIN_DIS_IN_LANE);
        add(HAS_OBJ_WITHIN_DIS_IN_LEFT_LANE);
        add(HAS_OBJ_WITHIN_DIS_IN_RIGHT_LANE);
        add(WITHIN_DIS_TO_OBJS_IN_LANE);
        add(WITHIN_DIS_TO_OBJS_IN_ROAD);
        add(IS_IN_SAME_LANE);
        add(COMPARE_GUARD);
        add(OR_GUARD);
        add(AND_GUARD);
        add(NOT_GUARD);
    }};

}
