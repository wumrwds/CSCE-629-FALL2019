package edu.tamu.wumrwds.entity;

import java.util.List;

public class Graph {

    private List<Vertex> vertices;

    private List<Edge> edges;

    public Graph(List<Vertex> vertices, List<Edge> edges) {
        this.vertices = vertices;
        this.edges = edges;
    }



    public int vertexSize() {
        return vertices.size();
    }

    public int edgeSize() {
        return edges.size();
    }

    public int totalDegree() {
        int degreeSum = 0;
        for (Vertex vertex : vertices) {
            degreeSum += vertex.degree();
        }
        return degreeSum;
    }

    public double averageDegree() {
        return (double) totalDegree() / vertexSize();
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public void setVertices(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    @Override
    public String toString() {
        return "Graph{" +
                "vertices=" + vertices +
                ", edges=" + edges +
                '}';
    }
}
