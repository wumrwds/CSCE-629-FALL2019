package edu.tamu.wumrwds;

import edu.tamu.wumrwds.entity.*;
import edu.tamu.wumrwds.util.GraphUtil;

import java.util.*;

public class MaxBandwidthPath {
    private static final int UNSEEN = 0;
    private static final int FRINGE = 1;
    private static final int INTREE = 2;

    private static MaximumBandwidthResult dijkstraNotUsingHeap(Graph graph, Vertex s, Vertex t) {
        int n = graph.vertexSize();

        // define status, wt & dad
        int[] status = new int[n];
        int[] wt = new int[n];
        int[] dad = new int[n];

        // set source vertex's status to INTREE
        status[s.getId()] = INTREE;

        //
        List<int[]> fringes = new ArrayList<>();
        for (Vertex w : s.getNeighbors()) {
            status[w.getId()] = FRINGE;
            fringes.add(new int[]{w.getId(), graph.getWeight(s, w)});
            wt[w.getId()] = graph.getWeight(s, w);
            dad[w.getId()] = s.getId();
        }

        while (!fringes.isEmpty()) {
            int[] bestFringe = extractMax(fringes);
            Vertex v = graph.getVertxById(bestFringe[0]);
            status[v.getId()] = INTREE;

            for (Vertex w : v.getNeighbors()) {
                int weight = graph.getWeight(v, w);

                if (status[w.getId()] == UNSEEN) {
                    status[w.getId()] = FRINGE;
                    dad[w.getId()] = v.getId();
                    wt[w.getId()] = Math.min(weight, wt[v.getId()]);
                    fringes.add(new int[]{w.getId(), weight});
                } else if (status[w.getId()] == FRINGE && wt[w.getId()] < Math.min(weight, wt[v.getId()])) {
                    dad[w.getId()] = v.getId();
                    wt[w.getId()] = Math.min(weight, wt[v.getId()]);
                    update(fringes, w.getId(), wt[w.getId()]);
                }
            }
        }

        return new MaximumBandwidthResult(s.getId(), t.getId(), dad, wt[t.getId()]);
    }

    private static MaximumBandwidthResult dijkstraUsingHeap(Graph graph, Vertex s, Vertex t) {
        int n = graph.vertexSize();

        // define status, wt & dad
        int[] status = new int[n];
        int[] wt = new int[n];
        int[] dad = new int[n];

        // set source vertex's status to INTREE
        status[s.getId()] = INTREE;

        // get the initial fringes and use them to construct a heap
        List<Fringe> fringes = new LinkedList<>();
        for (Vertex w : s.getNeighbors()) {
            status[w.getId()] = FRINGE;
            fringes.add(new Fringe(w.getId(), graph.getWeight(s, w)));
            wt[w.getId()] = graph.getWeight(s, w);
            dad[w.getId()] = s.getId();
        }
        FringeHeap maxFringeHeap = new FringeHeap(fringes);


        while (!maxFringeHeap.isEmpty()) {
            // extract the maximum fringe vertex
            Fringe bestFringe = maxFringeHeap.extractMax();
            Vertex v = graph.getVertxById(bestFringe.getVertex());
            status[v.getId()] = INTREE;

            for (Vertex w : v.getNeighbors()) {
                int weight = graph.getWeight(v, w);

                if (status[w.getId()] == UNSEEN) {
                    status[w.getId()] = FRINGE;
                    dad[w.getId()] = v.getId();
                    wt[w.getId()] = Math.min(weight, wt[v.getId()]);
                    maxFringeHeap.insert(new Fringe(w.getId(), weight));
                } else if (status[w.getId()] == FRINGE && wt[w.getId()] < Math.min(weight, wt[v.getId()])) {
                    maxFringeHeap.delete(w.getId());
                    dad[w.getId()] = v.getId();
                    wt[w.getId()] = Math.min(weight, wt[v.getId()]);
                    maxFringeHeap.insert(new Fringe(w.getId(), wt[w.getId()]));
                }
            }
        }

        return new MaximumBandwidthResult(s.getId(), t.getId(), dad, wt[t.getId()]);
    }

    public static MaximumBandwidthResult kruskalUsingDisjointSet(Graph graph, Vertex s, Vertex t) {
        // generate max spanning tree
        Map<Integer, List<int[]>> maxSpanningTree = genMaxSpanningTree(graph);

        // find a unique path from s to v
        boolean[] isVisited = new boolean[graph.vertexSize()];

        // dfs
        MaximumBandwidthResult result = new MaximumBandwidthResult(s.getId(), t.getId(), Collections.emptyList(), 0);
        ArrayList<Integer> curPath = new ArrayList<>();
        curPath.add(s.getId());
        dfs(maxSpanningTree, isVisited, s.getId(), t.getId(), curPath, Integer.MAX_VALUE, result);

        return result;
    }


    private static Map<Integer, List<int[]>> genMaxSpanningTree(Graph graph) {
        int m = graph.edgeSize();
        int n = graph.vertexSize();
        List<Edge> edges = graph.getEdges();

        // sort all edges in descending order
        Collections.sort(edges, (e1, e2) -> (e2.getWeight() - e1.getWeight()));

        // make disjoint set
        DisjointSet disjointSet = new DisjointSet(n);

        Map<Integer, List<int[]>> maxSpanningTree = new HashMap<>(n);
        for (int i = 0; i < n; i++) {
            maxSpanningTree.put(i, new LinkedList<>());
        }

        for (Edge edge : edges) {

            Vertex v1 = edge.getV1();
            Vertex v2 = edge.getV2();

            int r1 = disjointSet.findSet(v1.getId());
            int r2 = disjointSet.findSet(v2.getId());

            if (r1 != r2) {
                disjointSet.union(v1.getId(), v2.getId());

                maxSpanningTree.get(v1.getId()).add(new int[]{v2.getId(), edge.getWeight()});
                maxSpanningTree.get(v2.getId()).add(new int[]{v1.getId(), edge.getWeight()});
            }
        }

        return maxSpanningTree;
    }


    private static void dfs(Map<Integer, List<int[]>> maxSpanningTree, boolean[] isVisited, int cur, int end,
                            List<Integer> curPath, int maxBandwidth, MaximumBandwidthResult result) {

        if (result.isCompleted()) {
            return;
        }

        if (cur == end) {
            result.setPath(new ArrayList<>(curPath));
            result.setMaximumBandwidth(maxBandwidth);
            return;
        }

        isVisited[cur] = true;

        List<int[]> neighbors = maxSpanningTree.get(cur);
        for (int[] neighbor : neighbors) {


            if (!isVisited[neighbor[0]]) {
                curPath.add(neighbor[0]);
                dfs(maxSpanningTree, isVisited, neighbor[0], end, curPath, Math.min(maxBandwidth, neighbor[1]), result);
                curPath.remove(curPath.size() - 1);
            }
        }
    }


    private static int[] extractMax(List<int[]> fringes) {
        int[] max = new int[]{-1, -1};
        int maxIdx = -1;
        for (int i = 0; i < fringes.size(); i++) {
            if (max[1] < fringes.get(i)[1]) {
                max[0] = fringes.get(i)[0];
                max[1] = fringes.get(i)[1];
                maxIdx = i;
            }
        }

        // remove the best fringe
        fringes.remove(maxIdx);

        return max;
    }

    private static void update(List<int[]> fringes, int vertexId, int updatedWt) {
        for (int[] fringe : fringes) {
            if (vertexId == fringe[0]) {
                fringe[1] = updatedWt;
                return;
            }
        }
    }

    public static void main(String[] args) {
        Graph g1 = GraphUtil.genGraphWithAverageDegree(5000, 6);
        Vertex s = g1.getVertxById(0);
        Vertex t = g1.getVertxById(4999);

        System.out.println("m = " + g1.edgeSize());
        System.out.println("n = " + g1.vertexSize());
//
        MaximumBandwidthResult maxBandwidthWithoutHeap = dijkstraNotUsingHeap(g1, s, t);
        MaximumBandwidthResult maxBandwidthWithHeap = dijkstraUsingHeap(g1, s, t);
        MaximumBandwidthResult maxBandwidthWithMST = kruskalUsingDisjointSet(g1, s, t);
//
        System.out.println(maxBandwidthWithoutHeap.printPath());
        System.out.println(maxBandwidthWithoutHeap.getMaximumBandwidth());

        System.out.println("===========");

        System.out.println(maxBandwidthWithHeap.printPath());
        System.out.println(maxBandwidthWithHeap.getMaximumBandwidth());
//
        System.out.println("===========");

        System.out.println(maxBandwidthWithMST.printPath());
        System.out.println(maxBandwidthWithMST.getMaximumBandwidth());


        System.out.println("\n++++++++++++++++\n");

        Graph g2 = GraphUtil.genGraphWithSpecificAmountOfNeighbors(5000, 0.2, 0.01);
        Vertex s2 = g2.getVertxById(0);
        Vertex t2 = g2.getVertxById(4999);

        System.out.println("m = " + g2.edgeSize());
        System.out.println("n = " + g2.vertexSize());

        MaximumBandwidthResult maxBandwidthWithoutHeap2 = dijkstraNotUsingHeap(g2, s2, t2);
        MaximumBandwidthResult maxBandwidthWithHeap2 = dijkstraUsingHeap(g2, s2, t2);
        MaximumBandwidthResult maxBandwidthWithMST2 = kruskalUsingDisjointSet(g2, s2, t2);

        System.out.println(maxBandwidthWithoutHeap2.printPath());
        System.out.println(maxBandwidthWithoutHeap2.getMaximumBandwidth());

        System.out.println("===========");

        System.out.println(maxBandwidthWithHeap2.printPath());
        System.out.println(maxBandwidthWithHeap2.getMaximumBandwidth());

        System.out.println("===========");

        System.out.println(maxBandwidthWithMST2.printPath());
        System.out.println(maxBandwidthWithMST2.getMaximumBandwidth());
    }
}
