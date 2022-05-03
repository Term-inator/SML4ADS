package com.ecnu.adsmls.verifier.convert.src.main.java.xodr.exporter;

import lombok.extern.slf4j.Slf4j;
import com.ecnu.adsmls.verifier.convert.src.main.java.xodr.map.MapDataContainer;
import com.ecnu.adsmls.verifier.convert.src.main.java.xodr.map.entity.*;

import java.util.List;

import static com.ecnu.adsmls.verifier.convert.src.main.java.util.UppaalUtil.*;

@Slf4j
public class BufferWriter {

    private static List<Road> roads;
    private static List<Junction> junctions;

    private static List<LaneSection> laneSections;
    private static List<Lane> lanes;
    private static List<Connection> connections;
    private static List<LaneLink> laneLinks;

    public static void write(MapDataContainer container, StringBuffer buffer) {
//        log.info("开始写入地图声明...");
        log.info("Start writing map declaration.");
        
        init(container);

        addRoad(buffer);
        addLaneSection(buffer);
        addLane(buffer);
        addJunction(buffer);
        addConnection(buffer);
        addLaneLink(buffer);

//        log.info("地图声明写入已完成！");
        log.info("The write of map declaration is completed.");
    }
    
    private static void init(MapDataContainer container) {
        roads = container.getRoads();
        junctions = container.getJunctions();
        laneSections = container.getLaneSections();
        lanes = container.getLanes();
        connections = container.getConnections();
        laneLinks = container.getLaneLinks();
    }

    private static void addRoad(StringBuffer buffer) {
        buffer.append("Road roads[" + roads.size() + "] = {\n");
        for (Road road : roads) {
            // 开始road
            log.info(road.toString());
            buffer.append("{");

            buffer.append(road.getElementType() + ",");
            buffer.append(road.getRoadId() + ",");
            buffer.append(road.getJunctionIndex() + ",");
            buffer.append(road.getJunctionId() + ",");
            buffer.append(f(road.getLength()) + ",");
            buffer.append(road.getPredecessorElementType() + ",");
            buffer.append(road.getPredecessorIndex() + ",");
            buffer.append(road.getSuccessorElementType() + ",");
            buffer.append(road.getSuccessorIndex() + ",");
            buffer.append(f(road.getMaxSpeed()) + ",");

            // +laneSections索引
            buffer.append("{");
            // 存放索引
            List<Integer> laneSectionsIndex = road.getLaneSectionsIndex();
            int countOfLaneSection = Math.min(laneSectionsIndex.size(), ROAD_LANESECTION);
            buffer.append(laneSectionsIndex.get(0));
            for (int i = 1; i < countOfLaneSection; i++) {
                buffer.append("," + i);
            }
            // 空位填-1
            for (int i = countOfLaneSection; i < ROAD_LANESECTION; i++) {
                buffer.append("," + (-1));
            }

            buffer.append("}");
            // +laneSections

            buffer.append("}" + (road.getIndex()==roads.size()-1?"":",") + "\n");
            // 结束road
        }

        // roads结束
        buffer.append("};\n");

    }

    private static void addLaneSection(StringBuffer buffer) {
        buffer.append("LaneSection laneSections[" + laneSections.size() + "] = {\n");
        for (LaneSection laneSection : laneSections) {
            // +laneSection开始
            log.info(laneSection.toString());
            buffer.append("{");

            buffer.append(laneSection.getElementType() + ",");
            buffer.append(laneSection.getRoadIndex() + ",");
            buffer.append(laneSection.getRoadId() + ",");
            buffer.append(laneSection.getLaneSectionId() + ",");
            buffer.append(f(laneSection.getStartPosition()) + ",");

            // +lanes索引
            buffer.append("{");
            // 存放索引
            List<Integer> lanesIndex = laneSection.getLanesIndex();
            int countOfLane = Math.min(lanesIndex.size(), LANESECTION_LANE);
            buffer.append(lanesIndex.get(0));
            for (int i = 1; i < countOfLane; i++) {
                buffer.append("," + i);
            }
            // 空位填-1
            for (int i = countOfLane; i < LANESECTION_LANE; i++) {
                buffer.append("," + (-1));
            }

            buffer.append("}");
            // +lanes

            buffer.append("," + f(laneSection.getLength()));

            buffer.append("}" + (laneSection.getIndex()==laneSections.size()-1?"":",") + "\n");
            // +laneSection结束
        }

        buffer.append("};\n");
        // LaneSections结束
    }

    private static void addLane(StringBuffer buffer) {
        buffer.append("Lane lanes[" + lanes.size() + "] = {\n");
        for (Lane lane : lanes) {
            // +lane开始
            log.info(lane.toString());
            buffer.append("{");

            buffer.append(lane.getElementType() + ",");
            buffer.append(lane.getRoadId() + ",");
            buffer.append(lane.getRoadIndex() + ",");
            buffer.append(lane.getLaneSectionIndex() + ",");
            buffer.append(lane.getLaneId() + ",");
            buffer.append(lane.getType() + ",");
            buffer.append(lane.getPredecessorIndex() + ",");
            buffer.append(lane.getSuccessorIndex() + ",");
            buffer.append(lane.getLaneChange());

            buffer.append("}" + (lane.getIndex()==lanes.size()-1?"":",") + "\n");
            // +lane结束
        }

        buffer.append("};\n");
        // LaneSections结束
    }

    private static void addJunction(StringBuffer buffer) {
        if(junctions.size() == 0) {
            buffer.append("Junction junctions[2];\n");
            return;
        }
        buffer.append("Junction junctions[" + junctions.size() + "] = {\n");
        for (Junction junction : junctions) {
            // 开始junction
            log.info(junction.toString());
            buffer.append("{");

            buffer.append(junction.getElementType() + ",");
            buffer.append(junction.getJunctionId() + ",");

            // +connections索引
            buffer.append("{");
            // 存放索引
            List<Integer> connectionsIndex = junction.getConnectionsIndex();
            int countOfConnection = Math.min(connectionsIndex.size(), JUNCTION_CONNECTION);
            buffer.append(connectionsIndex.get(0));
            for (int i = 1; i < countOfConnection; i++) {
                buffer.append("," + i);
            }
            // 空位填-1
            for (int i = countOfConnection; i < JUNCTION_CONNECTION; i++) {
                buffer.append("," + (-1));
            }

            buffer.append("}");
            // +connections

            buffer.append("}" + (junction.getIndex()==junctions.size()-1?"":",") + "\n");
            // 结束junction
        }

        // junctions结束
        buffer.append("};\n");
    }

    private static void addConnection(StringBuffer buffer) {
        if(connections.size() == 0) {
            buffer.append("Connection connections[2];\n");
            return;
        }
        buffer.append("Connection connections[" + connections.size() + "] = {\n");
        for (Connection connection : connections) {
            // 开始connection
            log.info(connection.toString());
            buffer.append("{");

            buffer.append(connection.getDirection() + ",");
            buffer.append(connection.getIncomingRoadId() + ",");
            buffer.append(connection.getConnectingRoadId() + ",");
            buffer.append(connection.getIncomingRoadIndex() + ",");
            buffer.append(connection.getConnectingRoadIndex() + ",");

            // +laneLinks索引
            buffer.append("{");
            // 存放索引
            List<Integer> laneLinksIndex = connection.getLaneLinksIndex();
            int countOfLaneLink = Math.min(laneLinksIndex.size(), CONNECTION_LANELINK);
            buffer.append(laneLinksIndex.get(0));
            for (int i = 1; i < countOfLaneLink; i++) {
                buffer.append("," + i);
            }
            // 空位填-1
            for (int i = countOfLaneLink; i < CONNECTION_LANELINK; i++) {
                buffer.append("," + (-1));
            }

            buffer.append("}");
            // +laneLinks

            buffer.append("}" + (connection.getIndex()==connections.size()-1?"":",") + "\n");
            // 结束connection
        }

        // connections结束
        buffer.append("};\n");
    }

    private static void addLaneLink(StringBuffer buffer) {
        if(laneLinks.size() == 0) {
            buffer.append("LaneLink laneLinks[2];\n");
            return;
        }
        buffer.append("LaneLink laneLinks[" + laneLinks.size() + "] = {\n");
        for (LaneLink laneLink : laneLinks) {
            // +laneLink开始
            log.info(laneLink.toString());
            buffer.append("{");

            buffer.append(laneLink.getFrom() + ",");
            buffer.append(laneLink.getTo());

            buffer.append("}" + (laneLink.getIndex()==laneLinks.size()-1?"":",") + "\n");
            // +laneLink结束
        }

        buffer.append("};\n");
        // LaneLinks结束
    }

}
