package com.ecnu.adsmls.verifier.convert.src.main.java.json.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author wei
 * @description guard条件的规范名称，TODO 添加博哥的GUARD规范
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

    /*
        偷个懒，将普通变量命名方式改写为常量命名方式：hasObjWithinDisInLane -> HAS_OBJ_WITHIN_DIS_IN_LANE
        靠！原来refactor可以一键生成。。。白写了
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String str = scanner.next();
            StringBuffer result = new StringBuffer("");
            for(int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if(c >= 'a' && c <= 'z') {
                    result.append((char)(c + 'A' - 'a'));
                } else {
                    result.append("_" + c);
                }
            }
            System.out.println(result);
        }
        scanner.close();
    }

    public static final List<String> allGuards = new ArrayList<String>(){{
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
    }};

}
