package assignment6;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {

    private final Digraph G;
    private final int V;
    private int length;                       // length of the shortest path between V and W
    private int ancestor;                     // the nearest ancestor of V and W
    private int[] distTo1;                    // distTo1[v] = length of shortest V->v path
    private int[] distTo2;                    // distTo2[v] = length of shortest W->v path
    private boolean[] marked1;                // marked1[v] = is there an V->v path?
    private boolean[] marked2;                // marked2[v] = is there an W->v path?
    private final Queue<Integer> visited1;    // visited entries when accessing v
    private final Queue<Integer> visited2;    // visited entries when accessing w

    /**
     * Construct a copy of given Digraph {@code G}
     *
     * @param G a digraph (not necessarily a DAG)
     */
    public SAP(Digraph G) {
        validate(G);
        this.G = new Digraph(G);
        V = G.V();
        distTo1 = new int[G.V()];
        distTo2 = new int[G.V()];
        marked1 = new boolean[G.V()];
        marked2 = new boolean[G.V()];
        visited1 = new Queue<>();
        visited2 = new Queue<>();
    }

    /**
     * Calculate the length of shortest ancestral path between two vertices
     *
     * @param v a vertex in {@code G}
     * @param w a vertex in {@code G}
     * @return the length of shortest ancestral path, -1 if no such path
     */
    public int length(int v, int w) {
        computeSAP(v, w);
        return length;
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
        computeSAP(v, w);
        return ancestor;
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
        computeSAP(v, w);
        return length;
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
        computeSAP(v, w);
        return ancestor;
    }

    /**
     * Compute the length and ancestor of given {@code v} and {@code w}
     *
     * @param v a vertex in {@code G}
     * @param w a vertex in {@code G}
     */
    private void computeSAP(int v, int w) {
        validate(v);
        validate(w);
        length = -1;
        ancestor = -1;
        distTo1[v] = 0;
        distTo2[w] = 0;
        marked1[v] = true;
        marked2[w] = true;
        visited1.enqueue(v);
        visited2.enqueue(w);
        Queue<Integer> q1 = new Queue<>();
        Queue<Integer> q2 = new Queue<>();
        q1.enqueue(v);
        q2.enqueue(w);
        bfs(q1, q2);
    }

    /**
     * Compute the length and ancestor of given {@code v} and {@code w}
     *
     * @param v a groups of vertices in {@code G}
     * @param w a groups of vertices in {@code G}
     */
    private void computeSAP(Iterable<Integer> v, Iterable<Integer> w) {
        validate(v);
        validate(w);
        length = -1;
        ancestor = -1;
        Queue<Integer> q1 = new Queue<>();
        Queue<Integer> q2 = new Queue<>();
        for (int v0 : v) {
            marked1[v0] = true;
            visited1.enqueue(v0);
            distTo1[v0] = 0;
            q1.enqueue(v0);
        }
        for (int w0 : w) {
            marked2[w0] = true;
            visited2.enqueue(w0);
            distTo2[w0] = 0;
            q2.enqueue(w0);
        }
        bfs(q1, q2);
    }

    /**
     * Run lockstep BFS from {@code v} and {@code w}
     * (alternating back and forth between exploring vertices in each of the two searches)
     *
     * @param q1 bfs search queue from {@code v}
     * @param q2 bfs search queue from {@code w}
     */
    private void bfs(Queue<Integer> q1, Queue<Integer> q2) {
        while (!q1.isEmpty() || !q2.isEmpty()) {
            bfsHelper(q1, distTo1, distTo2, marked1, marked2, visited1);
            bfsHelper(q2, distTo2, distTo1, marked2, marked1, visited2);
        }
        reInit();
    }

    private void bfsHelper(Queue<Integer> q1, int[] distTo1, int[] distTo2, boolean[] marked1, boolean[] marked2, Queue<Integer> visited1) {
        if (q1.isEmpty()) return;
        int v = q1.dequeue();
        if (marked2[v]) {
            if (length == -1 || distTo1[v] + distTo2[v] < length) {
                ancestor = v;
                length = distTo1[v] + distTo2[v];
            }
        }
        // early return
        // terminate the BFS from v (or w) as soon as the distance
        // exceeds the length of the best ancestral path found so far
        if (length == -1 || distTo1[v] < length) {
            for (int w : G.adj(v)) {
                if (!marked1[w]) {
                    distTo1[w] = distTo1[v] + 1;
                    marked1[w] = true;
                    visited1.enqueue(w);
                    q1.enqueue(w);
                }
            }
        }

    }

    /**
     * Re-initialize only those entries of auxiliary arrays
     * that changed in the previous computation
     */
    private void reInit() {
        while (!visited1.isEmpty()) marked1[visited1.dequeue()] = false;
        while (!visited2.isEmpty()) marked2[visited2.dequeue()] = false;
    }

    private void validate(int v) {
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
    }

    private void validate(Iterable<Integer> v) {
        if (v == null) throw new IllegalArgumentException("argument Iterable<Integer> can not be null");
        for (Object obj : v) {
            if (obj == null)
                throw new IllegalArgumentException("Element in argument Iterable<Integer> can not be null");
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
