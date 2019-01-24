package assignment6;

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {

    private final Digraph G;
    private final int V;

    /**
     * Construct a copy of given Digraph {@code G}
     *
     * @param G a digraph (not necessarily a DAG)
     */
    public SAP(Digraph G) {
        validate(G);
        this.G = new Digraph(G);
        V = G.V();
    }

    /**
     * Calculate the length of shortest ancestral path between two vertices
     *
     * @param v a vertex in {@code G}
     * @param w a vertex in {@code G}
     * @return the length of shortest ancestral path, -1 if no such path
     */
    public int length(int v, int w) {
        validate(v);
        validate(w);
        BreadthFirstDirectedPaths pathV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths pathW = new BreadthFirstDirectedPaths(G, w);

        int shortest = Integer.MAX_VALUE;
        for (int s = 0; s < V; s++) {
            if (pathV.hasPathTo(s) && pathW.hasPathTo(s)) {
                int dist = pathV.distTo(s) + pathW.distTo(s);
                if (dist < shortest) {
                    shortest = dist;
                }
            }
        }
        return shortest != Integer.MAX_VALUE ? shortest : -1;
    }

    /**
     * Calculate a most common ancestor of two vertices that
     * participates in a shortest ancestral path
     *
     * @param v a vertex in {@code G}
     * @param w a vertex in {@code G}
     * @return the ancestor of the shortest ancestral path, -1 if no such ancestor
     */
    public int ancestor(int v, int w) {
        validate(v);
        validate(w);
        BreadthFirstDirectedPaths pathV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths pathW = new BreadthFirstDirectedPaths(G, w);

        int shortest = Integer.MAX_VALUE;
        int ancestor = -1;
        for (int s = 0; s < V; s++) {
            if (pathV.hasPathTo(s) && pathW.hasPathTo(s)) {
                int dist = pathV.distTo(s) + pathW.distTo(s);
                if (dist < shortest) {
                    shortest = dist;
                    ancestor = s;
                }
            }
        }
        return shortest != Integer.MAX_VALUE ? ancestor : -1;
    }

    /**
     * Calculate the length of shortest ancestral path
     * between two vertex groups
     *
     * @param v a groups of vertices in {@code G}
     * @param w a groups of vertices in {@code G}
     * @return the length of shortest ancestral path, -1 if no such path
     */
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        validate(v);
        validate(w);
        BreadthFirstDirectedPaths pathV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths pathW = new BreadthFirstDirectedPaths(G, w);

        int shortest = Integer.MAX_VALUE;
        for (int s = 0; s < V; s++) {
            if (pathV.hasPathTo(s) && pathW.hasPathTo(s)) {
                int dist = pathV.distTo(s) + pathW.distTo(s);
                if (dist < shortest) {
                    shortest = dist;
                }
            }
        }
        return shortest != Integer.MAX_VALUE ? shortest : -1;
    }

    /**
     * Calculate a common ancestor of two vertex groups that
     * participates in a shortest ancestral path
     *
     * @param v a groups of vertices in {@code G}
     * @param w a groups of vertices in {@code G}
     * @return the ancestor of the shortest ancestral path, -1 if no such ancestor
     */
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validate(v);
        validate(w);
        BreadthFirstDirectedPaths pathV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths pathW = new BreadthFirstDirectedPaths(G, w);

        int shortest = Integer.MAX_VALUE;
        int ancestor = -1;
        for (int s = 0; s < V; s++) {
            if (pathV.hasPathTo(s) && pathW.hasPathTo(s)) {
                int dist = pathV.distTo(s) + pathW.distTo(s);
                if (dist < shortest) {
                    shortest = dist;
                    ancestor = s;
                }
            }
        }
        return shortest != Integer.MAX_VALUE ? ancestor : -1;
    }

    private void validate(int v) {
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
    }

    private void validate(Iterable<Integer> v) {
        if (v == null) throw new IllegalArgumentException("argument Iterable<Integer> can not be null");
        for (Object obj : v) {
            if (obj == null) throw new IllegalArgumentException("Element in argument Iterable<Integer> can not be null");
            if ((int) obj < 0 || (int) obj >= V) {
                throw new IllegalArgumentException("vertex " + obj + " is not between 0 and " + (V - 1));
            }
        }
    }

    private void validate(Digraph G) {
        if (G == null) throw new IllegalArgumentException("digraph is not allowed to be null");
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
