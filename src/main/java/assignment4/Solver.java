package assignment4;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.Comparator;

public class Solver {
    /**
     *  Find a solution to the initial 8-puzzle board using the A* algorithm with ManhattanPriority.
     *
     *  You may not call any library functions other those in java.lang, java.util, and algs4.jar.
     *  You must use MinPQ for the priority queue(s).
     */

    private Node goalNode;
    private int moves;

    public Solver(Board initial) {
        // Required by current API to detect infeasibility
        // using two synchronized A* searches
        if (initial == null) throw new IllegalArgumentException();
        MinPQ<Node> pq = new MinPQ<>(getManhattanPriority());
        MinPQ<Node> qp = new MinPQ<>(getManhattanPriority());
        pq.insert(new Node(initial, 0, null));
        qp.insert(new Node(initial.twin(), 0, null));
        Node cur, kur;
        while (!pq.min().board.isGoal() && !qp.min().board.isGoal()) {
            cur = pq.delMin();
            kur = qp.delMin();
            for (Board nb : cur.board.neighbors()) {
                if (cur.prev == null || !nb.equals(cur.prev.board)) {
                    pq.insert(new Node(nb, cur.moves + 1, cur));
                }
            }
            for (Board nb : kur.board.neighbors()) {
                if (kur.prev == null || !nb.equals(kur.prev.board)) {
                    qp.insert(new Node(nb, kur.moves + 1, kur));
                }
            }
        }
        if (pq.min().board.isGoal()) {
            moves = pq.min().moves;
            goalNode = pq.min();
        } else {
            moves = -1;
        }
    }

    /**
     * Formula for determining solvability
     * http://www.cs.bham.ac.uk/~mdr/teaching/modules04/java2/TilesSolvability.html
     * ( (grid width odd) && (#inversions even) )  ||  ( (grid width even) && ((blank on odd row from bottom) == (#inversions even)) )
     * @return true if the initial board is solvable
     */
    public boolean isSolvable() {
        return moves != -1;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return moves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (!isSolvable()) return null;
        Stack<Board> solution = new Stack<>();
        Node node = goalNode;
        while (node != null) {
            solution.push(node.board);
            node = node.prev;
        }
        return solution;
    }


    private class Node {

        private final Board board;
        private final int moves;
        private final Node prev;
        private int manhattanPriority = -1;

        Node(Board board, int moves, Node prev) {
            this.board = board;
            this.moves = moves;
            this.prev = prev;
        }

        private int getManhattanPriority() {
            if (manhattanPriority == -1) {
                manhattanPriority = board.manhattan() + moves;
            }
            return manhattanPriority;
        }
    }

    private static Comparator<Node> getManhattanPriority() {
        return Comparator.comparing(Node::getManhattanPriority);
    }

    // solve a slider puzzle
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
