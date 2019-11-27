# Report
Personal project for CSCE 629 Algorithms at Texas A&amp;M University.

The GitHub repository URL is https://github.com/wumrwds/CSCE-629-FALL2019 .

<br/>

### Data Structure

In the first step, we need to decide how we store the graph. 

The general ways to store a graph are adjacent list and adjacent matrix. As our graphs are not that dense, it's better to use adjacent list to store the graphs. And because we need to iterate the neighbors of a specific vertex (Dijkstra's) and sort all edges (Kruskal's), I choose to store the graph in a modified adjacent list approach like the following:

```java
class Vertex {
    /** Vertex ID */
    private int id;
    
    /** Neighbors */
    private List<Vertex> neighbors;
}

class Edge {
    /** Vertex 1 */
    private Vertex v1;

    /** Vertex 2 */
    private Vertex v2;

    /** Weight */
    private int weight;
}

class Graph {
	/** Vertex Set */
    private List<Vertex> vertices;

    /** Edge Set */
    private List<Edge> edges;

    /** Edge Dictionary */
    private Map<String, Edge> edgeDict;
}
```

Class `Vertex` is used to store the information for a vertex. We can easily find the neighbors of a vertex in this way.

Class `Edge` is used to store the information for an edge. Field `v1` and `v2` are used to store the two ends of a edge, and in the process of generating the graph we will ensure that `v1`'s ID is less than `v2`. Filed `weight` stores the weight of an edge.

Class `Graph` is used to store the vertex set and edge set for a graph. The filed `edgeDict` is used to store the mapping from two vertex IDs to a specific edge. For example, if we need to find the edge whose ends are '1' and '4999', we can get it by looking up the `edgeDict` using the String pattern `v1-v2` (here is `1-4999`).

<br/>

### Random Graph Generation

According to the project requirement, we are required to implement two kinds of graphs: 

1.  In the first graph $G_1$, the average vertex degree is 6
2.  In the second graph $G_2$, each vertex is adjacent to about 20% of the other vertices, which are randomly chosen

And the vertex size of each graph is 5000.

<br/>

Below is my ways to generate these two kinds of graphs:

For G1, 

1.  We first generate 5,000 vertex instance. 
2.  Then, link these 5,000 vertices one by one to ensure that all the vertices are connected. The average degree would be 2 after finishing this step. (one edge will increase two vertices' degree by 1)
3.  Randomly pick `x` unique pairs of vertex. `x` is equal to `(averageDegree - 2) * size / 2`. Since the average degree requirement is 6 for each vertex, we have already increased the average degree by 2 in the step 2, and one single edge is shared by two vertices, so we need to add `(averageDegree - 2) * size / 2` random unique edges into the graph.
4.  Output the graph.

<br/>

For G2,

1.  We first generate 5,000 vertex instance. 
2.  Then, link these 5,000 vertices one by one to ensure that all the vertices are connected.
3.  Get a random degree distribution whose average degree is $20\% * n$ for all the vertices. So in the next step, what we should do is just to fill edges to approach this distribution.
4.  (Deprecated) Iterate all the vertices, if its degree is less than `distribution[v]`, we need to add random unique edges  started from this vertex until its degree reachs `distribution[v]`. But it will make all the vertices' degree larger than  `distribution[v]` and it's not that good, so I add a slack parameter to make the degrees lower. 
5.   Iterate each vertex `v` in the graph, if its degree is less than `(1-slack) * distribution[v]`, we need to add random unique edges `e`  started from this vertex until its degree reaches `distribution[v]` and if for the other vertex `w` of this edge `e`, its degree is larger than `(1+slack) * distribution[v]`, then we will not insert this edge `e` and instead re-pick one random edge. 
6.  Output the graph.



Below is the examples of these two kinds of random graphs I generated:

-------

$G_1$'s exmaple:

```
================ Start to run 3 algorithms on the given graph ================

# of edges: m = 15000
# of vertices: n = 5000
Average degree: average = 6.0
```

------

$G_2$'s exmaple:

```
================ Start to run 3 algorithms on the given graph ================

# of edges: m = 2524184
# of vertices: n = 5000
Average degree: average = 1009.6736
```

<br/>

### Heap Structure & Union-Find Structure

The heap structure and the Union-Find Structure generally have no differences with the ones Prof. Chen discussed in the class.

The only difference is that for the `delete(v)` of the heap structure, we need to keep track of the index of each vertex, since we need to find the index of the vertex to be deleted. 

Here I used a extra HashMap\<VertexId, Index\> `idxMap` to store the corresponding index of each vertex. And every time I swap two elements in the heap when invoking the `heapify(i)`, I will also modify the index in `idxMap`. See the implementation in `edu.tamu.wumrwds.structure.FringeHeap`.

<br/>

### Routing Algorithms

 sIn this project, we implement 3 different solutions to this Max-Bandwidth-Path problem.

#### Dijkstra's without Heap

```pseudocode
Dijkstra's_without_Heap(G, s)
1. for each vertex v in G do {status[v] = UNSEEN;}
2. status[s] = INTREE;
3. for each edge[s, w] do {
	   status[w] = FRINGE;
	   wt[w] = weight(s, w);
	   dad[w] = s;
   }
4. while (there're fringes) do {
       pick the best fringe v;
       status[v] = INTREE;
       for each edge[v, w] do {
           if (status[w] == UNSEEN) {
               status[w] = FRINGE;
               dad[w] = v;
               wt[w] = min(weight(v, w), wt[v]);
           }
           else (status[w] == FRINGE && wt[w] < min(weight(v, w), wt[v])) {
               dad[w] = v;
               wt[w] = min(weight(v, w), wt[v]);
               update fringe v with wt[w];
           }
       }
   }
5. output dad, wt;
```

I use a Linked List to store all the fringes, so it takes O(n) time to extract the maximum fringe and updated a specific fringe in this Linked List by scanning the whole list.

For each vertex, it will be inserted into the list once and call `extractedMax()` once, and for `updateFringe()`, we will call it at most m times. Therefore, the overall time complexity is O((n+m)n). If I record the index of v in the `v = extractMax()` step, the `update()` operation can be in O(1) time. And the time complexity would be O(n^2+m).

#### Dijkstra's with Heap

```pseudocode
Dijkstra's_with_Heap(G, s)
1. for each vertex v in G do {status[v] = UNSEEN;}
2. status[s] = INTREE;
   heap = new MaxHeap();
3. for each edge[s, w] do {
	   status[w] = FRINGE;
	   wt[w] = weight(s, w);
	   dad[w] = s;
	   heap.insert(w, wt[w]);
   }
4. while (!heap.isEmpty()) do {
       v = heap.extractMax();
       status[v] = INTREE;
       for each edge[v, w] do {
           if (status[w] == UNSEEN) {
               status[w] = FRINGE;
               dad[w] = v;
               wt[w] = min(weight(v, w), wt[v]);
           }
           else (status[w] == FRINGE && wt[w] < min(weight(v, w), wt[v])) {
               heap.delete(w);
               dad[w] = v;
               wt[w] = min(weight(v, w), wt[v]);
               heap.insert(w, wt[w]);
           }
       }
   }
5. output dad, wt;
```

The implementation of this approach is similar to the last one. It just uses Maximum heap to speed up the `extractMax()` step. For this approach, `extractMax()` , `insert()` and `delete()` are all in O(log n) time. Therefore, the overall time complexity is O((m+n) * log n).

#### Kruskal's Maximum Spanning Tree

```pseudocode
Kruskal's_Max_Spanning_Tree(G)
1. sort G.edges in descending order;
2. T = [];
   disjointSet = new DisjointSet(G.edges);
3. for each edge[u, w] in descending order G.edges do {
      r1 = disjointSet.find(u);
      r2 = disjointSet.find(w);
      
      if (r1 != r2) {
          disjointSet.union(r1, r2);
          T += edge[u, w];
      }
   }
4. return T;
```

```pseudocode
Find_Path_by_DFS(MST, curVertex, endVertex, curPath, maxBandwidth)
1. if (curVertex == endVertex) {
       output(curPath, maxBandwidth);
   }
2. isVisited[curVertex] = true;
3. for each vertex v of curVertex's neighors do {
       if (!isVisited[v]) {
           curPath.add(v);
           Find_Path_by_DFS(MST, v, endVertex, curPath, min(v.maxBandwidth, maxBandwidth));
           curPath.remove(v);
       }
   }
```

This algorithm first uses Kruskal's to get the maximum spanning tree, then call dfs on this maximum spanning tree to find a path.

For the procedure of getting a maximum spanning tree, sorting step takes O(m \* log m) and the time complexity of doing the sequence of Union-FInd operations is O(m \* log\*(n)), so the time complexity is O(m \* (log(m) + log\*(n))).

For the procedure of calling dfs on the maximum spanning tree, it only takes O(n) time.

Therefore, the overall time complexity is O(m \* (log(m) + log\*(n)) + n). Since log\* n <= 6 for all practical numbers n, log\* n can be seen as a constant. So the overall time complexity can converted to O(m \* log(m) + n).

<br/>

### Testing & Performance

I built 5 pairs of graphs using the approaches in **Random Graph Generation** Section, randomly select 5 pairs of vertices and call the 3 algorithms in **Routing Algorithms** Section. The result was collected in the file `log.out`. 

The log's format is like below:

```log
================================ Start to run 3 algorithms on the given graph ================================

# of edges: m = 15000
# of vertices: n = 5000
Average degree: average = 6.0

s = 50, t = 195

================================ Dijkstra's without heap starts ================================

Start to run Dijkstra's without heap on the graph: startTime = 2019-11-26 23:23:40.591
Finish running Dijkstra's without heap on the graph: finishTime = 2019-11-26 23:23:40.703
Total time cost: cost = 112 milliseconds
Maximum Bandwidth Path: path = MaximumBandwidthPath = [50->1156->994->995->4767->4768->4931->4932->4933->1783->4227->3625->3624->2721->4975->1049->4142->4143->4144->4145->194->195]
Maximum Bandwidth: bandwidth = 1603041916

================================ Dijkstra's without heap ends ================================



================================ Dijkstra's with heap starts ================================

Start to run Dijkstra's with heap on the graph: startTime = 2019-11-26 23:23:40.704
Finish running Dijkstra's with heap on the graph: finishTime = 2019-11-26 23:23:40.767
Total time cost: cost = 63 milliseconds
Maximum Bandwidth Path: path = MaximumBandwidthPath = [50->1156->994->995->4767->4768->4769->859->3213->3319->2273->142->439->1711->4329->4328->4224->4225->4226->4227->3625->3624->2721->4975->1049->4142->4143->4144->4145->194->195]
Maximum Bandwidth: bandwidth = 1603041916

================================ Dijkstra's with heap ends ================================



================================ Kruskal's MST starts ================================

Start to run Kruskal’s on the graph: startTime = 2019-11-26 23:23:40.767
Finish running Kruskal’s on the graph: finishTime = 2019-11-26 23:23:40.829
Total time cost: cost = 62 milliseconds
Maximum Bandwidth Path: path = MaximumBandwidthPath = [50->1156->994->67->4475->4129->4128->1096->2991->819->4131->4130->2062->2063->1414->1215->4481->4413->4414->1326->1327->4189->4188->1490->1639->1876->68->2205->430->2193->3651->3650->3902->4933->1783->4227->3625->3624->2721->4975->1049->4142->4143->4144->4145->194->195]
Maximum Bandwidth: bandwidth = 1603041916

================================ Kruskal's MST ends ================================
```

The first part shows how many vertices and edges in the randomly generated graph and what's its average degree. 

The second part shows the randomly picked source `s` and destination `t`. 

The third part shows each algorithm's start time, finish time, maximum path and maximum bandwidth.

<br/>

After analysing the log, we can get a rough result about the performance of these 3 algorithms:

1.  the total running time of builting these 10 graphs and calling `Max-Bandwidth-Path()` for 10 * 5 * 3 = 150 times on my machine is about 5 minutes and 30 seconds;
2.  In sparce, Kruskal's is better than Dijkstra's with heap and Dijkstra's with heap is better than Dijkstra's without heap. 
3.  In the dense graph, Kruskal's is better than Dijkstra's with heap and Dijkstra's with heap; But Dijkstra's with heap is just in the same performance level with the Dijkstra's without heap.
4.  Except the first time calling the Kruskal's `Max_Bandwidth_Path()`, the other calling is very fast.



I used the linux command `grep` to collect all data form the `log.out` and process the data in Excel.

The detailed data (milliseconds) is listed in the Table 1 (sparse graph) and Table 2 (dense graph):

-----

Table 1  (sparse graph)

|      #      | Dijkstra's without Heap | Dijkstra's with Heap | Kruskal's |
| :---------: | :---------------------: | :------------------: | :-------: |
|      1      |           99            |          65          |    58     |
|      2      |           90            |          46          |     6     |
|      3      |           93            |          45          |     7     |
|      4      |           67            |          35          |     7     |
|      5      |           59            |          40          |     4     |
|      6      |           81            |          53          |     9     |
|      7      |           115           |          62          |     3     |
|      8      |           66            |          40          |     3     |
|      9      |           66            |          28          |     1     |
|     10      |           57            |          31          |     1     |
|     11      |           67            |          28          |     3     |
|     12      |           57            |          28          |     1     |
|     13      |           68            |          33          |     1     |
|     14      |           62            |          45          |     2     |
|     15      |           92            |          50          |     2     |
|     16      |           79            |          52          |     4     |
|     17      |           102           |          30          |     2     |
|     18      |           56            |          30          |     1     |
|     19      |           65            |          27          |     1     |
|     20      |           52            |          25          |     1     |
|     21      |           72            |          34          |     4     |
|     22      |           66            |          35          |     2     |
|     23      |           73            |          38          |     1     |
|     24      |           58            |          27          |     1     |
|     25      |           65            |          32          |     1     |
|             |                         |                      |           |
| **Average** |          73.08          |        38.36         |   5.04    |



------

Table 2  (dense graph)

|      #      | Dijkstra's without Heap | Dijkstra's with Heap | Kruskal's |
| :---------: | :---------------------: | :------------------: | :-------: |
|      1      |          6555           |         5504         |   1364    |
|      2      |          5621           |         5286         |    466    |
|      3      |          5368           |         5549         |    469    |
|      4      |          5657           |         5356         |    468    |
|      5      |          5489           |         5575         |    478    |
|      6      |          6013           |         5723         |   1386    |
|      7      |          5994           |         5723         |    474    |
|      8      |          5633           |         5703         |    472    |
|      9      |          5713           |         5398         |    475    |
|     10      |          5661           |         5634         |    467    |
|     11      |          6149           |         5891         |   1390    |
|     12      |          5923           |         5633         |    457    |
|     13      |          5655           |         5580         |    455    |
|     14      |          5831           |         5599         |    457    |
|     15      |          5674           |         5673         |    461    |
|     16      |          6159           |         6031         |   1388    |
|     17      |          5835           |         5557         |    457    |
|     18      |          5778           |         5617         |    439    |
|     19      |          5864           |         5599         |    457    |
|     20      |          5804           |         5746         |    457    |
|     21      |          6067           |         5848         |   1412    |
|     22      |          5932           |         5776         |    456    |
|     23      |          5614           |         5751         |    452    |
|     24      |          5778           |         5535         |    455    |
|     25      |          5801           |         5614         |    442    |
|             |                         |                      |           |
| **Average** |         5822.72         |       5636.04        |  646.16   |

<br/>

### Discussion

To conclude, 

1.  In general, Kruskal's has a better time complexity over Dijkstra's based algorithms. 
2.  Kruskal's has an advantage: once generating the maximum spanning tree, the later call of `Max_Bandwidth_Path()` will be in O(n) time.
3.  Dijkstra's heap based algorithms is no longer having a better performance over the non-heap-based Dijsktra's algorithm in the dense graph.

<br/>

### Further Improvements

The first improvement is what I mentioned before in the **Routing Algorithms** Section: for the Dijkstra's without Heap implementation, if I record the index of each `extractMax()` step, the time complexity of `update()` would be improved to O(1).

The second improvement is that I should modify the `dfs()` in Kruskal's Maximum Spanning Tree algorithm to a non-recursion version. That's becasue if the vertex number is larger than 5,000, my implementation will cause a StackOverFlow exception as calling too deep in recursion. But here all the things seem to work well since the max stack limitation in Java is just about 5000.