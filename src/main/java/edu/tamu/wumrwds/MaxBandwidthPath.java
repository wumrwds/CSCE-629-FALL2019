package edu.tamu.wumrwds;

import edu.tamu.wumrwds.entity.Graph;
import edu.tamu.wumrwds.entity.MaximumBandwidthResult;
import edu.tamu.wumrwds.entity.Vertex;
import edu.tamu.wumrwds.util.GraphUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

public class MaxBandwidthPath {

    private static final Logger logger = LoggerFactory.getLogger(MaxBandwidthPath.class);

    public static void runMbp(Graph graph, int vertexId1, int vertextId2) {

        logger.info("================================ Start to run 3 algorithms on the given graph ================================\n");

        Vertex s = graph.getVertxById(vertexId1);
        Vertex t = graph.getVertxById(vertextId2);

        logger.info("# of edges: m = {}", graph.edgeSize());
        logger.info("# of vertices: n = {}", graph.vertexSize());
        logger.info("Average degree: average = {}", graph.averageDegree());


        logger.info("\n\n\n\n\n");


        long start, end;
        logger.info("================================ Dijkstra's without heap starts ================================\n");

        start = System.currentTimeMillis();
        logger.info("Start to run Dijkstra's without heap on the graph: startTime = {}", new Timestamp(start));

        MaximumBandwidthResult maxBandwidthWithoutHeap = DijkstraMbpWithoutHeap.dijkstraNotUsingHeap(graph, s, t);

        end = System.currentTimeMillis();
        logger.info("Finish running Dijkstra's without heap on the graph: finishTime = {}", new Timestamp(end));
        logger.info("Total time cost: cost = {} milliseconds", end - start);

        logger.info("Maximum Bandwidth Path: path = {}", maxBandwidthWithoutHeap.printPath());
        logger.info("Maximum Bandwidth: bandwidth = {}\n", maxBandwidthWithoutHeap.getMaximumBandwidth());

        logger.info("================================ Dijkstra's without heap ends ================================\n");



        logger.info("\n\n\n\n\n");



        logger.info("================================ Dijkstra's with heap starts ================================\n");

        start = System.currentTimeMillis();
        logger.info("Start to run Dijkstra's with heap on the graph: startTime = {}", new Timestamp(start));

        MaximumBandwidthResult maxBandwidthWithHeap = DijkstraMbpWithHeap.dijkstraUsingHeap(graph, s, t);

        end = System.currentTimeMillis();
        logger.info("Finish running Dijkstra's with heap on the graph: finishTime = {}", new Timestamp(end));
        logger.info("Total time cost: cost = {} milliseconds", end - start);

        logger.info("Maximum Bandwidth Path: path = {}", maxBandwidthWithHeap.printPath());
        logger.info("Maximum Bandwidth: bandwidth = {}\n", maxBandwidthWithHeap.getMaximumBandwidth());

        logger.info("================================ Dijkstra's with heap ends ================================\n");




        logger.info("\n\n\n\n\n");




        logger.info("================================ Kruskal's MST starts ================================\n");

        start = System.currentTimeMillis();
        logger.info("Start to run Kruskal’s on the graph: startTime = {}", new Timestamp(start));

        MaximumBandwidthResult maxBandwidthWithMst = KruskalMbp.kruskalUsingDisjointSet(graph, s, t);

        end = System.currentTimeMillis();
        logger.info("Finish running Kruskal’s on the graph: finishTime = {}", new Timestamp(end));
        logger.info("Total time cost: cost = {} milliseconds", end - start);

        logger.info("Maximum Bandwidth Path: path = {}", maxBandwidthWithMst.printPath());
        logger.info("Maximum Bandwidth: bandwidth = {}\n", maxBandwidthWithMst.getMaximumBandwidth());

        logger.info("================================ Kruskal's MST ends ================================\n");

    }

    public static void main(String[] args) {
        Graph g1 = GraphUtil.genGraphWithAverageDegree(5000, 6);
        runMbp(g1, 0, 4999);


        logger.info("\n\n\n\n\n");


        Graph g2 = GraphUtil.genGraphWithSpecificAmountOfNeighbors(5000, 0.2, 0.01);
        runMbp(g2, 0, 4999);
    }
}
