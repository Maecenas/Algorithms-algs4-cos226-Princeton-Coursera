package assignment9;

import assignment9.utils.BoggleBoard;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BoggleSolver {

    private static final int R = 26;

    private Node root; // R-Way Trie
    private boolean[] marked;

    /**
     * Initializes the data structure using the given array of strings as the dictionary
     *
     * @param dictionary dictionary of words
     */
    public BoggleSolver(String[] dictionary) {
        for (String word : dictionary) {
            int len = word.length(), score;

            if (len <= 2) score = 0;
            else if (len <= 4) score = 1;
            else if (len == 5) score = 2;
            else if (len == 6) score = 3;
            else if (len == 7) score = 5;
            else score = 11;

            put(word, score);
        }
    }

    private static class Node {
        private int val;
        private Node[] next = new Node[R];
    }

    private int get(String key) {
        Node x = get(root, key, 0);
        if (x == null) return 0;
        return x.val;
    }

    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        int c = key.charAt(d) - 'A';
        return get(x.next[c], key, d + 1);
    }

    private void put(String key, int val) {
        root = put(root, key, val, 0);
    }

    private Node put(Node x, String key, int val, int d) {
        if (x == null) x = new Node();
        if (d == key.length()) {
            x.val = val;
        } else {
            int c = key.charAt(d) - 'A';
            x.next[c] = put(x.next[c], key, val, d + 1);
        }
        return x;
    }

    /**
     * Returns the set of all valid words in the given Boggle board, as an Iterable.
     *
     * @param board the given Boggle board
     * @return the set of all valid words as an Iterable.
     */
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        HashSet<String> words = new HashSet<>();
        final int R = board.rows(), C = board.cols();
        if (R == 0 || C == 0) return words;

        // Calculate for once, passed as reference during DFS
        final List<Iterable<Integer>> adj = new ArrayList<>(R * C);
        final char[] letters = new char[R * C];
        final StringBuilder sb = new StringBuilder(32);
        marked = new boolean[R * C];

        // Calculate adj
        for (int i = 0, idx = 0; i < R; i++) {
            boolean hasUp = i > 0, hasDown = i < R - 1;
            for (int j = 0; j < C; j++, idx++) {
                boolean hasLeft = j > 0, hasRight = j < C - 1;

                Bag<Integer> q = new Bag<>();
                if (hasUp && hasLeft) q.add(idx - C - 1);
                if (hasUp) q.add(idx - C);
                if (hasUp && hasRight) q.add(idx - C + 1);
                if (hasLeft) q.add(idx - 1);
                if (hasRight) q.add(idx + 1);
                if (hasDown && hasLeft) q.add(idx + C - 1);
                if (hasDown) q.add(idx + C);
                if (hasDown && hasRight) q.add(idx + C + 1);

                adj.add(q);
                letters[idx] = board.getLetter(i, j);
            }
        }

        for (int idx = 0; idx < R * C; idx++) {
            dfs(adj, words, letters, idx, sb, root);
        }
        return words;
    }

    private void dfs(List<Iterable<Integer>> adj, Set<String> words, char[] letters, int idx, StringBuilder sb, Node last) {
        char c = letters[idx];
        Node now = last.next[c - 'A'];
        if (c == 'Q' && now != null) now = now.next['U' - 'A'];
        if (now == null) return;
        if (c == 'Q') sb.append("QU");
        else sb.append(c);

        if (now.val > 0) words.add(sb.toString());

        marked[idx] = true;
        for (int next : adj.get(idx)) {
            if (!marked[next]) {
                dfs(adj, words, letters, next, sb, now);
            }
        }
        marked[idx] = false;
        sb.deleteCharAt(sb.length() - 1);
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == 'Q')
            sb.deleteCharAt(sb.length() - 1);
    }

    /**
     * Returns the score of the given word if it is in the dictionary, zero otherwise
     *
     * @param word the given word
     * @return the score of the given word if it is in the dictionary, zero otherwise
     */
    public int scoreOf(String word) {
        return get(word);
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        if (args.length > 2) {
            int N = Integer.parseInt(args[2]);
            BoggleBoard[] boards = new BoggleBoard[N];
            for (int i = 0; i < N; i++)
                boards[i] = new BoggleBoard();
            Stopwatch sw = new Stopwatch();
            for (int i = 0; i < N; i++)
                solver.getAllValidWords(boards[i]);
            StdOut.println(sw.elapsedTime());
        } else {
            BoggleBoard board = new BoggleBoard(args[1]);
            int score = 0;
            Stopwatch sw = new Stopwatch();
            Iterable<String> words = solver.getAllValidWords(board);
            StdOut.println(sw.elapsedTime() * 1000);
            for (String word : words) {
                StdOut.println(word);
                score += solver.scoreOf(word);
            }
            StdOut.println("Score = " + score);
        }
    }
}
