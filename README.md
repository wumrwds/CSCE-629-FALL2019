# CSCE-629-FALL2019
Personal repository for CSCE 629 Algorithms at Texas A&amp;M University



### Random Graph Generation

For G1, 

1.  we first generate 5,000 vertices. 
2.  Then, link these 5,000 vertices one by one to ensure it's connected. The average degree would 2 after finishing this step. (one edge will increase two vertices' degree by 1)
3.  Add 2 edges which start from the current vertex for each vertex. The average degree requirement is 6 for each vertex, so we need to increase the average degree by 4. That means we still need to add 2 edges for each vertex (one edge will increase two vertices' degree by 1).
4.  Output the graph.



For G2,

1.  we first generate 5,000 vertices. 
2.  Then, link these 5,000 vertices one by one to ensure it's connected. The average degree would 2 after finishing this step. (one edge will increase two vertices' degree by 1)
3.  Add 500 edges for each vertex. Each vertex connects to the other 20% of the total vertex amount vertices. It means 1 vertex will connect to other 1,000 vertices and the total degree of the graph is 1000 * 5000, so the total edge count is 1000\*5000/2 and for each vertex it will have 1000\*5000/2/5000 = 500 edges.
4.  