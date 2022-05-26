package com.ecnu.adsmls.verifier.convert.src.main.java.json.exporter;

import com.ecnu.adsmls.verifier.convert.src.main.java.json.tree.BehaviorType;
import com.ecnu.adsmls.verifier.convert.src.main.java.json.tree.GuardType;
import com.ecnu.adsmls.verifier.convert.src.main.java.json.tree.TreeDataContainer;
import com.ecnu.adsmls.verifier.convert.src.main.java.json.tree.entity.*;
import com.ecnu.adsmls.verifier.convert.src.main.java.xodr.exporter.BufferWriter;
import com.ecnu.adsmls.verifier.convert.src.main.java.xodr.importer.XODRInputReader;
import com.ecnu.adsmls.verifier.convert.src.main.java.xodr.importer.XODRParser;
import com.ecnu.adsmls.verifier.convert.src.main.java.xodr.map.MapDataContainer;
import com.ecnu.adsmls.verifier.convert.src.main.java.xodr.map.entity.Lane;
import com.ecnu.adsmls.verifier.convert.src.main.java.xodr.map.entity.LaneSection;
import com.ecnu.adsmls.verifier.convert.src.main.java.xodr.map.entity.Road;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ecnu.adsmls.verifier.convert.src.main.java.util.UppaalUtil.*;

@Slf4j
public class XMLWriter {

    private static List<Car> cars;
    private static String map;
    private static double timeStep;
    private static String scenarioEndTrigger;

    private static int endId;

    // car映射: name -> Index
    private static Map<String, Integer> carNameIndexMap;

    // （一）对应XML声明头
    private static final String XML_HEAD = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
    private static final String UPPAAL_HEAD = "<!DOCTYPE nta PUBLIC '-//Uppaal Team//DTD Flat System 1.1//EN' 'http://www.it.uu.se/research/group/darts/uppaal/flat-1_2.dtd'>\n";

    // （二）对应nta一个整体部分，XML头下面就是这一大块，涵盖1234部分
    private static void addNta(StringBuffer buffer) {
        buffer.append("<nta>\n");

        addDeclaration(buffer);
        addTimer(buffer); //已定义好的自动机
        for (int i = 0; i < cars.size(); i++) { // 车辆的自动机
            addTemplate(buffer, i);
        }
        addEndTrigger(buffer);
        addSystem(buffer);
        addQueries(buffer);

        buffer.append("</nta>\n");
    }

    // 1 对应declaration部分，即代码编写处（需注意Uppaal中函数在后变量在前）
    private static void addDeclaration(StringBuffer buffer) {
        buffer.append("\t<declaration>\n");

        // 1）已定义的数据结构和变量信息
        addDefined(buffer);
        buffer.append("const int TIME_STEP = " + f(timeStep) + ";\n");
        addMap(buffer);
        // 3) 声明车辆，初始化
        addCar(buffer);
        // 4）已定义好的函数部分
        addFunction(buffer);

        buffer.append("\n\t</declaration>\n");
    }

    // 1.1 添加已经定义好的地图数据结构（包括一些变量信息）
    private static void addDefined(StringBuffer buffer) {
        try {
            String definedContent = FileUtils.readFileToString(new File(DEFINED_PATH), "UTF-8");
            buffer.append(definedContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 1.2 根据JSON中提取到的信息，创建声明变量的语句
    private static void addMap(StringBuffer buffer) {
        // 1. 读取
        String input = XODRInputReader.readFromFile(map);
        // 2. 解析
        MapDataContainer container = XODRParser.parse(input);
        // 3. 写入buffer
        BufferWriter.write(container, buffer);
        // 4. 根据地图的解析结果对车辆的道路信息索引进行更新
        initCarFromMap(container);
    }

    // 1.2.4 根据地图信息更新车辆的索引；另外，通过偏移重新计算laneId
    private static void initCarFromMap(MapDataContainer container) {
        if (container == null) {
//            log.warn("地图解析发生错误，返回了null对象！");
            log.warn("An error occurred while paring tha map. Received null.");

            return;
        }

//        log.info("开始解析车辆(包括地图带来的索引更新)...");
        log.info("Start parsing cars(including index update brought by the map).");
        List<Road> roads = container.getRoads();
        List<LaneSection> laneSections = container.getLaneSections();
        List<Lane> lanes = container.getLanes();

        // 地图映射：id -> Road/LaneSection/Lane
        Map<Integer, Road> roadMap = new HashMap<>();
        Map<Integer, LaneSection> laneSectionMap = new HashMap<>();
        Map<Integer, Lane> laneMap = new HashMap<>();

        // 初始化映射: id -> index，便于查找
        for (Road road : roads) {
            roadMap.put(road.getRoadId(), road);
        }
        for (LaneSection laneSection : laneSections) {
            laneSectionMap.put(laneSection.getLaneSectionId(), laneSection);
        }
        for (Lane lane : lanes) {
            laneMap.put(lane.getSingleId(), lane);
        }

        for (Car car : cars) {
            // 1、解析laneSectionId和laneSingleId
            int laneSectionId = 0;

            Road road;
            if (roadMap.containsKey(car.getRoadId())) {
                road = roadMap.get(car.getRoadId());
                car.setRoadIndex(roadMap.get(car.getRoadId()).getIndex());
            } else {
//                log.error("{}车的道路信息roadId({})不存在！", car.getName(), car.getRoadId());
                log.error("roadId({}) of the car {} doesn't exist!", car.getRoadId(), car.getName());
                break;
            }

            // 这里会有点问题，因为偏移是不确定的，导致初始laneSectionId无法确定，因此这里用了最小偏移来替代；在uppaal中需要再判定一次
            for (LaneSection laneSection : road.getLaneSections()) {
                if (car.getMinOffset() >= laneSection.getStartPosition()) { // 偏移在该段起始位置后和下一个起始位置之前即为相应的laneSection
                    laneSectionId = laneSection.getLaneSectionId();
                    car.setLaneSectionId(laneSectionId);
                    car.setLaneSectionIndex(laneSectionMap.get(laneSectionId).getIndex());
                } else {
                    break;
                }
            }

            // 同样也可能因为不确定性导致初始化出现一些问题
            LaneSection laneSection = laneSectionMap.get(laneSectionId);
            List<Lane> currentLanes = laneSection.getLanes();
            for (Lane lane : currentLanes) { // 这里还需要根据偏移更新laneId
                if (lane.getLaneId() == car.getLaneId()) {
                    int singleId = lane.getSingleId();
                    car.setLaneSingleId(singleId);
                    car.setLaneIndex(laneMap.get(singleId).getIndex());
                    break;
                }
            }

            // 1 计算laneId相对中心线偏移
            double totalLateralOffset = 0.0;
            int laneId = car.getLaneId() == 0 ? -1 : car.getLaneId();
            int direction = -laneId / Math.abs(laneId); // 索引增加的方向：-1为左，1为右（因为解析时先解析左再解析右）
            int centerLaneIndex = 0;
            for (Lane lane : currentLanes) { //找到中心线车道的索引
                if (lane.getLaneId() == 0) {
                    centerLaneIndex = lane.getIndex();
                }
            }
            for (int i = centerLaneIndex; i < lanes.size() && i < LANESECTION_LANE; i += direction) { // lane的singleId和index相同，往两边叠加
                if (i != laneId) {
                    totalLateralOffset += currentLanes.get(i).getWidth();
                } else {
                    totalLateralOffset += currentLanes.get(i).getWidth() / 2; // 如果对应的lane，则实际偏移取中心线位置的
                    break;
                }
            }
            // 2 计算lane通过Related Position计算后的偏移
            double lateralOffset = totalLateralOffset + car.getMinLateralOffset();
            // 3 根据偏移比较，计算相应的lane
            double tempOffset = 0.0;
            for (int i = 0; i < lanes.size() && i < LANESECTION_LANE; i += direction) {
                if (lateralOffset >= tempOffset) {
                    int singleId = currentLanes.get(i).getSingleId();
                    car.setLaneId(currentLanes.get(i).getLaneId());
                    car.setLaneSingleId(singleId);
                    car.setLaneIndex(laneMap.get(singleId).getIndex());
                }
                tempOffset += currentLanes.get(i).getWidth();
            }

//            log.info("{}车的道路信息为: Road(id={}, index={}), LaneSection(id={}, index={}), Lane(id={}, index={})",
//                    car.getName(), car.getRoadId(), car.getRoadIndex(), car.getLaneSectionId(),
//                    car.getLaneSectionIndex(), car.getLaneId(), car.getLaneIndex());
            log.info("Road info of car {}: Road(id={}, index={}), LaneSection(id={}, index={}), Lane(id={}, index={}).",
                    car.getName(), car.getRoadId(), car.getRoadIndex(), car.getLaneSectionId(),
                    car.getLaneSectionIndex(), car.getLaneId(), car.getLaneIndex());
        }

//        log.info("车辆解析完成！");
        log.info("Parse of cars is completed.");
    }

    // 1.3 添加车辆声明
    private static void addCar(StringBuffer buffer) {
        int countOfCar = cars.size();
        buffer.append("\n//id, width, length, heading, speed, acceleration, maxSpeed, ..., offset\n");
        buffer.append("int countOfCars = " + countOfCar + ";\n");
        buffer.append("Car cars[" + countOfCar + "] = {\n");
        for (int i = 0; i < countOfCar; i++) {
//            System.out.println(cars[i].toString());
            buffer.append("{");
            buffer.append(i + ", ");
            buffer.append(f(cars.get(i).getWidth()) + ", ");
            buffer.append(f(cars.get(i).getLength()) + ", ");

            buffer.append(cars.get(i).isHeading() + ", ");
            buffer.append(f(cars.get(i).getInitSpeed()) + ", ");
            buffer.append(0 + ", ");
            buffer.append(f(cars.get(i).getMaxSpeed()) + ", ");

            buffer.append(cars.get(i).getRoadId() + ", ");
            buffer.append(cars.get(i).getLaneSectionId() + ", ");
            buffer.append(cars.get(i).getLaneId() + ", ");
            buffer.append(cars.get(i).getRoadIndex() + ", ");
            buffer.append(cars.get(i).getLaneSectionIndex() + ", ");
            buffer.append(cars.get(i).getLaneIndex() + ", ");
            buffer.append(f(cars.get(i).getOffset()));
            buffer.append("}");

            buffer.append(i != countOfCar - 1 ? ",\n" : "\n");
        }
        buffer.append("};\n");
    }

    // 1.4 添加定义好的函数部分：行为的操作实现、地图查询方法、车辆查询方法等
    private static void addFunction(StringBuffer buffer) {
        try {
            String definedContent = FileUtils.readFileToString(new File(FUNCTION_PATH), "UTF-8");
            buffer.append(definedContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 2.0 Timer自动机，用于同步时间
    private static void addTimer(StringBuffer buffer) {
        try {
            String definedContent = FileUtils.readFileToString(new File(AUTOMATON_PATH), "UTF-8");
            buffer.append(definedContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // EndTrigger自动机，用于检测安全
    private static void addEndTrigger(StringBuffer buffer) {
        try {
            String definedContent = FileUtils.readFileToString(new File(END_TRIGGER_PATH), "UTF-8");
            String endGuard = resolveGuard(scenarioEndTrigger);
            if (endGuard.equals("")) {
                endGuard = "false";
            } else {
                endGuard = "(true " + endGuard + ")";
            }
            definedContent = definedContent.replaceAll("%placeholder%", endGuard);
            buffer.append(definedContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 2 对应template部分，即每辆车的行为树
    private static void addTemplate(StringBuffer buffer, int index) {
        buffer.append("\t<template>\n");

        addName(buffer, index);
        addLocalDeclaration(buffer);
        addLocations(buffer, index);
        addBranchPoints(buffer, index);
        addInit(buffer);
        addTransitions(buffer, index);
        addSelfTransitions(buffer, index);
        addTransitionToEnd(buffer, index);

        buffer.append("\t</template>\n");
    }

    // 2.1 template名称，即车辆名称
    private static void addName(StringBuffer buffer, int index) {
        String name = cars.get(index).getName();

        buffer.append("\t\t<name>");
        buffer.append(name);
        buffer.append("</name>\n");
    }

    // 2.2 局部变量的声明: 包含三元组算法和自循环加锁算法的变量
    private static void addLocalDeclaration(StringBuffer buffer) {
        buffer.append("\t\t<declaration>\n");

        try {
            String content = FileUtils.readFileToString(new File(TRANSITION_PATH), "UTF-8");
            buffer.append(content);
        } catch (IOException e) {
            e.printStackTrace();
        }

        buffer.append("\t\t</declaration>\n");
    }

    // 2.3 自动机的每个location，每个状态
    private static void addLocations(StringBuffer buffer, int index) {
        // 新增一个初始状态
        buffer.append("\t\t<location id=\"id0\">\n" +
                "\t\t\t<name>Start</name>\n" +
                "\t\t\t<committed/>\n" +
                "\t\t</location>\n");
        // 再加一个结束状态
        endId = cars.get(index).getMTree().getBehaviors().size() + cars.get(index).getMTree().getBranchPoints().size() + 1; // 最后一个节点名id + 1
        buffer.append("\t\t<location id=\"id" + endId + "\">\n" +
                "\t\t\t<name>End</name>\n" +
                "\t\t</location>\n");

        List<Behavior> behaviors = cars.get(index).getMTree().getBehaviors();
        Map<Integer, Boolean> exist = new HashMap<>();
        for (Behavior behavior : behaviors) {
            if (!exist.containsKey(behavior.getId())) {
                exist.put(behavior.getId(), true);
                String id = "id" + behavior.getId(), name = behavior.getName();

                buffer.append("\t\t<location id=\"" + id + "\">\n");

                buffer.append("\t\t\t<name>");
                buffer.append(name);
                buffer.append("</name>\n");

                buffer.append("\t\t</location>\n");
            }
        }

    }

    // 2.4 转换点branch point
    private static void addBranchPoints(StringBuffer buffer, int index) {
        List<BranchPoint> branchPoints = cars.get(index).getMTree().getBranchPoints();
        for (BranchPoint branchPoint : branchPoints) {
            String id = "id" + branchPoint.getId();
            double x = branchPoint.getPosition().getX(), y = branchPoint.getPosition().getY();
            buffer.append("\t\t<branchpoint id=\"" + id + "\" x=\"" + x + "\" y=\"" + y + "\">\n");
            buffer.append("\t\t</branchpoint>\n");
        }
    }

    // 2.5 初始节点
    private static void addInit(StringBuffer buffer) {
        String id = "id0";
        buffer.append("\t\t<init ref=\"" + id + "\"/>\n");
    }

    // 2.6 连线
    private static void addTransitions(StringBuffer buffer, int index) {
        List<CommonTransition> commonTransitions = cars.get(index).getMTree().getCommonTransitions();
        List<ProbabilityTransition> probabilityTransitions = cars.get(index).getMTree().getProbabilityTransitions();

        // Start到行为树的根结点
        buffer.append("\t\t<transition>\n" +
                "\t\t\t<source ref=\"id0\"/>\n" +
                "\t\t\t<target ref=\"id1\"/>\n" +
                "\t\t\t<label kind=\"select\">offset:int[" + f(cars.get(index).getMinOffset()) + "," + f(cars.get(index).getMaxOffset()) + "]</label>\n" +
                "\t\t\t<label kind=\"assignment\">initCar(cars[" + index + "], offset), modifyRoadLane(cars[" + index + "])</label>\n" +
                "\t\t</transition>\n");

        for (CommonTransition commonTransition : commonTransitions) {
            String from = "id" + commonTransition.getSourceId(), to = "id" + commonTransition.getTargetId();
            buffer.append("\t\t<transition>\n");
            buffer.append("\t\t\t<source ref=\"" + from + "\"/>\n");
            buffer.append("\t\t\t<target ref=\"" + to + "\"/>\n");

            // select 在这里可以妙用，（i,j,k)作为该边在树中的坐标
            buffer.append("\t\t\t<label kind=\"select\">i: int[" +
                    commonTransition.getLevel() + "," + commonTransition.getLevel() + "], j:int[" +
                    commonTransition.getGroup() + "," + commonTransition.getGroup() + "], k:int[" +
                    commonTransition.getNumber() + "," + commonTransition.getNumber() + "]</label>\n");

            // guard 这里需先比对边是否衔接（坐标对应），再比较其他条件
            buffer.append("\t\t\t<label kind=\"guard\">" +
                    "level == i &amp;&amp; group == j &amp;&amp; !lock" +
                    addGuard(commonTransition.getGuard()) + "</label>\n");

            // sync 普通迁移也需要信号，否则在验证时可能会无法迁出
            buffer.append("\t\t\t<label kind=\"synchronisation\">update?</label>\n");

            // update/assignment 先更新边的坐标，再更新其他信息
            buffer.append("\t\t\t<label kind=\"assignment\">level = level+1, group = (group-1)*N+k, number=k, lock=true, t=0</label>\n");
            buffer.append("\t\t</transition>\n");
        }

        for (ProbabilityTransition probabilityTransition : probabilityTransitions) {
            String from = "id" + probabilityTransition.getSourceId(), to = "id" + probabilityTransition.getTargetId();
            buffer.append("\t\t<transition>\n");
            buffer.append("\t\t\t<source ref=\"" + from + "\"/>\n");
            buffer.append("\t\t\t<target ref=\"" + to + "\"/>\n");

            // select 在这里可以妙用，（i,j,k)作为该边在树中的坐标
            buffer.append("\t\t\t<label kind=\"select\">i: int[" +
                    probabilityTransition.getLevel() + "," + probabilityTransition.getLevel() + "], j:int[" +
                    probabilityTransition.getGroup() + "," + probabilityTransition.getGroup() + "], k:int[" +
                    probabilityTransition.getNumber() + "," + probabilityTransition.getNumber() + "]</label>\n");

            // sync 普通迁移也需要信号，否则在验证时可能会无法迁出
//            buffer.append("\t\t\t<label kind=\"synchronisation\">update?</label>\n");

            // update/assignment 先更新边的坐标，再更新其他信息
            buffer.append("\t\t\t<label kind=\"assignment\">level = level+1, group = (group-1)*N+k, number=k</label>\n");

            buffer.append("\t\t\t<label kind=\"probability\">" + probabilityTransition.getWeight() + "</label>\n");
            buffer.append("\t\t</transition>\n");
        }

    }

    // 2.6.1 添加guards条件的辅助函数
    /*
        guard条件命名参照GuardType
     */
    private static String addGuard(List<String> guards) {
        StringBuffer buffer = new StringBuffer();
        if (guards == null) {
            return "";
        }

        for (String guard : guards) {
            buffer.append(resolveGuard(guard));
        }
        return buffer.toString();
    }

    private static String resolveGuard(String guard) {
        if (guard == null || guard.equals("")) {
            return "";
        }

        String buffer = "";

        boolean isMatch = false;
        for (String guardType : GuardType.allGuards) {
            if (guard.matches(guardType)) {
                isMatch = true;
                String s = guard;
                for (String name : carNameIndexMap.keySet()) {
                    s = s.replaceAll(name, "cars[" + carNameIndexMap.get(name) + "]");
                }

                // guard参数中的数字放大十倍（这里很乱，需要改一下）
                Pattern numberPattern = Pattern.compile("[+-]?\\d+.?\\d*\\)"); //最后加一个右括号，只匹配最后一个distance的数字
                Matcher m = numberPattern.matcher(s);
                if (m.find()) {
                    String result = m.group();
                    String number = result.substring(0, result.length() - 1);
                    s = s.replaceAll(result.replaceAll("\\)", "\\\\)"), f(Double.parseDouble(number)) + ")");
                }

                // 替换会影响XML语法的符号
                s = s.replaceAll("&", "&amp;").
                        replaceAll(">", "&gt;").
                        replaceAll("<", "&lt;");

                buffer = " &amp;&amp; " + s;
//                log.info("Guard解析成功：原guard：{}, 转化后guard：{}", guard, s);
                log.info("Parse of Guard is completed: original guard: {}, after being parsed: {}.", guard, s);
                break;
            }
        }
        if (!isMatch) {
//            log.error("Guard条件不合法：{}", guard);
            log.error("Invalid Guard {}.", guard);
        }

        return buffer;
    }

    // 2.7 自循环边
    /*
        keep: 自循环，当"时钟达到duration"时跳出
        accelerate: 自循环，当"时钟到到达duration"或"速度到达targetSpeed时退出"
        turnLeft: 瞬时动作，完成后左转道
        turnRight: 同上
        changeLeft: 同上
        changeRight: 同上
     */
    private static void addSelfTransitions(StringBuffer buffer, int index) {
        List<Behavior> behaviors = cars.get(index).getMTree().getBehaviors();
        for (Behavior behavior : behaviors) {
            resolveBehavior(buffer, behavior, index);
        }
    }

    // 2.7.1 操作执行：自循环边不需要k也不需要更新group和level
    private static void resolveBehavior(StringBuffer buffer, Behavior behavior, int index) {
        String from = "id" + behavior.getId(), to = "id" + behavior.getId();
        buffer.append("\t\t<transition>\n");
        buffer.append("\t\t\t<source ref=\"" + from + "\"/>\n");
        buffer.append("\t\t\t<target ref=\"" + to + "\"/>\n");

        // select 在这里可以妙用，（i,j,k)作为该边在树中的坐标
        buffer.append("\t\t\t<label kind=\"select\">" +
                "i: int[" + behavior.getLevel() + "," + behavior.getLevel() + "], " +
                "j:int[" + behavior.getGroup() + "," + behavior.getGroup() + "]" +
//                "k:int[" + behavior.getNumber() + "," + behavior.getNumber() + "]" +
                "</label>\n");

        // guard 这里需先比对边是否衔接（坐标对应），再比较其他条件
        buffer.append("\t\t\t<label kind=\"guard\">" +
                "level == i &amp;&amp; group == j &amp;&amp; lock" +
                "</label>\n");

        buffer.append("\t\t\t<label kind=\"synchronisation\">update?</label>\n");

        // update/assignment 先更新边的坐标，再更新其他信息
        buffer.append("\t\t\t<label kind=\"assignment\">" +
//                "level = level+1, group = (group-1)*N+k, number=k, " +
                "t=t+TIME_STEP" + operate(behavior, index) +
                "</label>\n");
        buffer.append("\t\t</transition>\n");

    }

    // 2.7.2 update部分，根据行为类型更改
    private static String operate(Behavior behavior, int index) {
        StringBuffer buffer = new StringBuffer();

        Map<String, Double> params = behavior.getParams();
        if (params == null) {
            return "";
        }

        // 不存在则设置为最大值
        Double targetSpeed = params.getOrDefault("target speed", INT16_MAX * 1.0 / K);
        targetSpeed = (targetSpeed == null ? INT16_MAX * 1.0 / K : targetSpeed);
        Double acceleration = params.getOrDefault("acceleration", 0.0);
        acceleration = (acceleration == null ? 0.0 : acceleration);
        Double duration = params.getOrDefault("duration", INT16_MAX * 1.0 / K);
        duration = (duration == null ? INT16_MAX * 1.0 / K : duration);

        if (behavior.getName().equals(BehaviorType.ACCELERATE.getValue())) {
            // *acceleration, *target speed, duration
            buffer.append(", cars[" + index + "].acceleration = " + f(acceleration));
            buffer.append(", speedUp(cars[" + index + "]," + f(targetSpeed) + ")");
            String lock = nextGuardAndNot(behavior, index);
            if (lock == null) {
                buffer.append(", lock = (t&lt;" + f(duration) + " &amp;&amp; cars[" + index + "].speed&lt;" + f(targetSpeed) + ")");
            } else {
                buffer.append(", lock = " + lock); // 加速减速匀速自循环的条件是：其后面所有迁移guard的和的非
            }

        } else if (behavior.getName().equals(BehaviorType.DECELERATE.getValue())) {
            // *acceleration, *target speed, duration
            buffer.append(", cars[" + index + "].acceleration = " + f(acceleration));
            buffer.append(", speedDown(cars[" + index + "]," + f(targetSpeed) + ")");
            String lock = nextGuardAndNot(behavior, index);
            if (lock == null) {
                buffer.append(", lock = (t&lt;" + f(duration) + " &amp;&amp; cars[" + index + "].speed&gt;" + f(targetSpeed) + ")");
            } else {
                buffer.append(", lock = " + lock); // 加速减速匀速自循环的条件是：其后面所有迁移guard的和的非
            }

        } else if (behavior.getName().equals(BehaviorType.KEEP.getValue())) {
            // duration
            // , keep(cars[0])
            //, lock=(t<5)
            buffer.append(", keep(cars[" + index + "])");
            String lock = nextGuardAndNot(behavior, index);
            if (lock == null) {
                buffer.append(", lock = (t&lt;" + f(duration) + ")");
            } else {
                buffer.append(", lock = " + lock); // 加速减速匀速自循环的条件是：其后面所有迁移guard的和的非
            }

        } else if (behavior.getName().equals(BehaviorType.TURN_LEFT.getValue())) {
            // *acceleration, *target speed
            buffer.append(", cars[" + index + "].acceleration = " + f(acceleration));
            buffer.append(", turnLeft(cars[" + index + "])");
//            buffer.append(", lock = (cars[" + index + "].speed&lt;" + f(targetSpeed) + ")");
            buffer.append(", lock = false");
        } else if (behavior.getName().equals(BehaviorType.TURN_RIGHT.getValue())) {
            // *acceleration, *target speed
            buffer.append(", cars[" + index + "].acceleration = " + f(acceleration));
            buffer.append(", turnRight(cars[" + index + "])");
//            buffer.append(", lock = (cars[" + index + "].speed&lt;" + f(targetSpeed) + ")");
            buffer.append(", lock = false");
        } else if (behavior.getName().equals(BehaviorType.CHANGE_LEFT.getValue())) {
            // acceleration, target speed
            targetSpeed = (targetSpeed == INT16_MAX * 1.0 / K ? -1.0 / K : targetSpeed);
            buffer.append(", cars[" + index + "].acceleration = " + f(acceleration));
            buffer.append(", changeLeft(cars[" + index + "], " + f(targetSpeed) + ")");
//            buffer.append(", lock = (cars[" + index + "].speed&lt;" + f(targetSpeed) + ")");
            buffer.append(", lock = false");
        } else if (behavior.getName().equals(BehaviorType.CHANGE_RIGHT.getValue())) {
            // acceleration, target speed
            targetSpeed = (targetSpeed == INT16_MAX * 1.0 / K ? -1.0 / K : targetSpeed);
            buffer.append(", cars[" + index + "].acceleration = " + f(acceleration));
            buffer.append(", changeRight(cars[" + index + "], " + f(targetSpeed) + ")");
//            buffer.append(", lock = (cars[" + index + "].speed&lt;" + f(targetSpeed) + ")");
            buffer.append(", lock = false");
        } else if (behavior.getName().equals(BehaviorType.IDLE.getValue())) {
            // duration
            buffer.append(", cars[" + index + "].speed = 0)");
//            buffer.append(", lock = (cars[" + index + "].speed&lt;" + f(targetSpeed) + ")");
            buffer.append(", lock = (t&lt;" + f(duration) + ")");
        } else if (behavior.getName().equals(BehaviorType.LANE_OFFSET.getValue())) {
            // *offset, acceleration, target speed, duration
            buffer.append(", lock = false");
        }

        return buffer.toString();
    }

    // 2.7.3 后面guard条件的 和的非；没有条件则返回null
    private static String nextGuardAndNot(Behavior behavior, int index) {
        int level = behavior.getLevel();
        int group = behavior.getGroup();
        boolean existGuard = false;

        List<CommonTransition> commonTransitions = behavior.getNextTransitions();

        if (commonTransitions.size() == 0) { // 是最后一个节点了，那么不迁出，继续走
            return "true";
        }

        StringBuffer buffer = new StringBuffer("!(true"); // 下面会多一个&&
        for (CommonTransition commonTransition : commonTransitions) {
            // 找后面所有的迁移，进行"&&"操作
            if (level == commonTransition.getLevel() && group == commonTransition.getGroup()) {
                if (commonTransition.getGuard() != null) {
                    existGuard = true;
                    buffer.append(addGuard(commonTransition.getGuard()));
                }
            }
        }
        buffer.append(")");

        if (!existGuard) { // 后续不存在guard则返回null
            return null;
        }
        return buffer.toString();
    }

    // 2.8 指向End的迁移，接收一个End信号，发生错误时结束
    private static void addTransitionToEnd(StringBuffer buffer, int index) {
        Map<Integer, Integer> behaviorMap = new HashMap<>();
        for (Behavior behavior : cars.get(index).getMTree().getBehaviors()) {
            if (behaviorMap.containsKey(behavior.getId())) {
                continue;
            }
            behaviorMap.put(behavior.getId(), 1);
            buffer.append("\t\t<transition>\n" +
                    "\t\t\t<source ref=\"id" + behavior.getId() + "\"/>\n" +
                    "\t\t\t<target ref=\"id" + endId + "\"/>\n" +
                    "\t\t\t<label kind=\"synchronisation\">end?</label>\n" +
                    "\t\t</transition>\n");
        }
    }

    // 3 对应system部分，即系统模版声明处
    private static void addSystem(StringBuffer buffer) {
        buffer.append("\t<system>\n");

        buffer.append("system Timer");
        for (Car car : cars) {
            buffer.append(", " + car.getName());
        }
        buffer.append(", EndTrigger;\n");

        buffer.append("\t</system>\n");
    }

    // 4 对应queries部分，即性质规约
    private static void addQueries(StringBuffer buffer) {
        buffer.append("\t<queries>\n");

        // 可能有多个query
        addQuery(buffer);

        buffer.append("\t</queries>\n");
    }

    // 4.1
    private static void addQuery(StringBuffer buffer) {
        buffer.append("\t\t<query>\n");

        buffer.append("\t\t\t<formula>");
        // 这里是formula的内容
        buffer.append("</formula>\n");

        buffer.append("\t\t\t<comment>");
        // 这里是comment的内容
        buffer.append("</comment>\n");

        buffer.append("\t\t</query>\n");
    }

    // 初始化，便于使用
    private static void init(TreeDataContainer container) {
        cars = container.getCars();
        map = container.getMap();
        timeStep = container.getTimeStep();
        scenarioEndTrigger = container.getScenarioEndTrigger();

        carNameIndexMap = new HashMap<>();
        for (int i = 0; i < cars.size(); i++) {
            carNameIndexMap.put(cars.get(i).getName(), i);
        }
    }

    // 从这里开始
    public static void write(TreeDataContainer container, String XMLpath) {
//        log.info("开始输出到文件：{}", XMLpath);
        log.info("Start writing to file {}.", XMLpath);

        init(container);
        try {
            StringBuffer buffer = new StringBuffer();

            // uppaal两大组成：头和nta
            buffer.append(XML_HEAD);
            buffer.append(UPPAAL_HEAD);
            addNta(buffer);

            String result = buffer.toString();

            // 输出到XML文件
            File f = new File(XMLpath);
            FileOutputStream fop = new FileOutputStream(f);
            OutputStreamWriter writer = new OutputStreamWriter(fop, StandardCharsets.UTF_8);

            writer.append(result);

            writer.close();
            fop.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        log.info("输出结束，Uppaal SMC的XML格式的随机混成自动机已转化完成！");
        log.info("File writing is finished. The parse of random hybrid automaton in Uppaal SMC style is completed.");
    }

}
