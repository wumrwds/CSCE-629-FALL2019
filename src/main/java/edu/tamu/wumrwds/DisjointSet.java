package edu.tamu.wumrwds;

public class DisjointSet {

    private int[] ranks;
    private int[] parents;

    /**
     * Makes sets.
     *
     * @param n
     */
    DisjointSet(int n) {
        ranks = new int[n];
        parents = new int[n];

        for (int i = 0; i < n; i++) {
            ranks[i] = 1;
            parents[i] = i;
        }
    }

    /**
     * Finds set representative with path compression.
     *
     * @param x
     * @return
     */
    private int findSet(int x) {
        if (parents[x] != x) {
            parents[x] = findSet(parents[x]);
        }
        return parents[x];
    }

    /**
     * Links two sets.
     *
     * @param p1
     * @param p2
     */
    private void link(int p1, int p2) {
        if (ranks[p1] < ranks[p2]) {
            // set1 is less than set2
            // make p2 the parent(root) of p1
            parents[p1] = p2;
        } else {
            // set1 is larger than set2
            // make p1 the parent(root) of p2
            parents[p2] = p1;

            // set1 is as the same rank as set2
            // increase the rank of the root p1
            if (ranks[p1] == ranks[p2]) {
                ranks[p1]++;
            }
        }
    }

    public void union(int x, int y) {
        link(findSet(x), findSet(y));
    }
}
