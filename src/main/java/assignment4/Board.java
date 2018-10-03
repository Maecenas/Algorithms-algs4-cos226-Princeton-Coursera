package assignment4;

import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.Stack;

public class Board {
    /**
     * Corner cases.  You may assume that the constructor receives an n-by-n array containing the n2 integers between 0 and n2 − 1, where 0 represents the blank square.
     *
     * Performance requirements.  Your implementation should support all Board methods in time proportional to n2 (or better) in the worst case.
     * @param blocks
     */

    private final int[][] blocks;
    private final int n;
    private int hammingNumber = -1;
    private int manhattanNumber = -1;

    // (where blocks[i][j] = block in row i, column j)
    public Board(int[][] blocks) {
        n = blocks.length;
        this.blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            this.blocks[i] = Arrays.copyOf(blocks[i], n);
    }

    // board dimension n
    public int dimension() {
        return n;
    }

    // number of blocks out of place
    public int hamming() {
        if (hammingNumber == -1) {
            hammingNumber = 0;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    int value = blocks[i][j];
                    if (value != 0 && value != getGoalVal(i, j)) {
                        hammingNumber++;
                    }
                }
            }
        }
        return hammingNumber;
    }

    // sum of Manhattan distances between blocks and goal
    public int manhattan() {
        if (manhattanNumber == -1) {
            manhattanNumber = 0;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    int value = blocks[i][j];
                    if (value != 0 && value != getGoalVal(i, j)) {
                        int ii = (value - 1) / n;
                        int jj = value - ii * n - 1;
                        int distance = Math.abs(i - ii) + Math.abs(j - jj);
                        manhattanNumber += distance;
                    }
                }
            }
        }
        return manhattanNumber;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hamming() == 0;
    }

    // a board that is obtained by exchanging any pair of blocks
    public Board twin() {
        Board board = new Board(blocks);
        for (int i = 0; i < 2; i++) {
            if (board.blocks[i][0] != 0 && board.blocks[i][1] != 0) {
                board.exch(i, 0, i, 1);
                return board;
            }
        }
        throw new IllegalArgumentException();
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        Board that = (Board) y;
        return Arrays.deepEquals(this.blocks, that.blocks);
    }

    /**
     * Add the items you want to a Stack<Board> or Queue<Board> and return that.
     * @return Iterable<Board>
     */
    // all neighboring boards
    public Iterable<Board> neighbors() {
        int i0 = 0, j0 = 0;
        isFindBlank:
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks.length; j++) {
                if (blocks[i][j] == 0) {
                    i0 = i;
                    j0 = j;
                    break isFindBlank;
                }
            }
        }
        Stack<Board> stack = new Stack<>();
        Board board;
        if (i0 != 0) {
            board = new Board(blocks);
            board.exch(i0, j0, i0 - 1, j0);
            stack.push(board);
        }
        if (i0 != n - 1) {
            board = new Board(blocks);
            board.exch(i0, j0, i0 + 1, j0);
            stack.push(board);
        }
        if (j0 != 0) {
            board = new Board(blocks);
            board.exch(i0, j0, i0, j0 - 1);
            stack.push(board);
        }
        if (j0 != n - 1) {
            board = new Board(blocks);
            board.exch(i0, j0, i0, j0 + 1);
            stack.push(board);
        }
        return stack;
    }

    // string representation of this board (in the output format specified below)
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(n).append("\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                sb.append(String.format("%2d ", blocks[i][j]));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private int getGoalVal(int i, int j) {
        if (i == n - 1 && j == n - 1) return 0;
        return i * n + j + 1;
    }

    private void exch(int x1, int y1, int x2, int y2) {
        assert (x1 >= 0 && x1 < n && y1 >= 0 && y1 < n && x2 >= 0 && x2 < n && y2 >= 0 && y2 < n);

        int swap = blocks[x1][y1];
        blocks[x1][y1] = blocks[x2][y2];
        blocks[x2][y2] = swap;
    }

    public static void main(String[] args) {
        // unit tests (not graded)
        int n = Integer.parseInt(args[0]);
        int[][] init = new int[n][n];
        int start = n * n - 1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                init[i][j] = start--;
            }
        }
        Board b = new Board(init);
        StdOut.println(b);
    }
}
