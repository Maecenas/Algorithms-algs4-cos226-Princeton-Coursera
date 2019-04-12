package assignment9;

import assignment9.utils.BoggleBoard;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.ArrayList;
import java.util.TreeSet;

public class BoggleSolver {

    private static final int R = 26;

    private Node root; // R-Way Trie
    private boolean[] marked;
    private TreeSet<String> words;

    /**
     * Initializes the data structure using the given array of strings as the dictionary
     *
     * @param dictionary dictionary of words
     */
    public BoggleSolver(String[] dictionary) {
        for (String word : dictionary) {
            root = put(root, word, 0);
        }
    }

    private static class Node {
        private boolean isWord;
        private Node[] next = new Node[R];
    }

    private Node put(Node x, String key, int d) {
        if (x == null) x = new Node();
        if (d == key.length()) x.isWord = true;
        else {
            char c = key.charAt(d);
            x.next[c - 'A'] = put(x.next[c - 'A'], key, d + 1);
        }
        return x;
    }

    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        char c = key.charAt(d);
        return get(x.next[c - 'A'], key, d + 1);
    }

    private boolean contains(String key) {
        Node x = get(root, key, 0);
        if (x == null) return false;
        return x.isWord;
    }

    /**
     * Returns the set of all valid words in the given Boggle board, as an Iterable.
     *
     * @param board the given Boggle board
     * @return the set of all valid words as an Iterable.
     */
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        words = new TreeSet<>();
        int R = board.rows(), C = board.cols();
        if (R == 0 || C == 0) return words;

        // Calculate for once, passed as reference during DFS
        final ArrayList<Iterable<Integer>> adj = new ArrayList<>(R * C);
        final char[] letters = new char[R * C];
        marked = new boolean[R * C];

        // Calculate adj
        for (int i = 0, idx = 0; i < R; i++) {
            boolean hasUp = i > 0, hasDown = i < R - 1;
            for (int j = 0; j < C; j++, idx++) {
                boolean hasLeft = j > 0, hasRight = j < C - 1;

                Queue<Integer> q = new Queue<>();
                if (hasUp && hasLeft) q.enqueue(idx - C - 1);
                if (hasUp) q.enqueue(idx - C);
                if (hasUp && hasRight) q.enqueue(idx - C + 1);
                if (hasLeft) q.enqueue(idx - 1);
                if (hasRight) q.enqueue(idx + 1);
                if (hasDown && hasLeft) q.enqueue(idx + C - 1);
                if (hasDown) q.enqueue(idx + C);
                if (hasDown && hasRight) q.enqueue(idx + C + 1);

                adj.add(q);
                letters[idx] = board.getLetter(i, j);
            }
        }

        for (int idx = 0; idx < R * C; idx++) {
            dfs(adj, letters, idx, new StringBuilder(), root);
        }
        return words;
    }

    private void dfs(ArrayList<Iterable<Integer>> adj, char[] letters, int idx, StringBuilder pre, Node last) {
        char c = letters[idx];
        Node now = last.next[c - 'A'];
        if (c == 'Q' && now != null) now = now.next['U' - 'A'];
        if (now == null) return;
        if (c == 'Q') pre.append("QU");
        else pre.append(c);

        if (pre.length() >= 3 && now.isWord) words.add(pre.toString());

        marked[idx] = true;
        for (int next : adj.get(idx)) {
            if (!marked[next]) {
                dfs(adj, letters, next, new StringBuilder(pre), now);
            }
        }
        marked[idx] = false;
    }

    /**
     * Returns the score of the given word if it is in the dictionary, zero otherwise
     *
     * @param word the given word
     * @return the score of the given word if it is in the dictionary, zero otherwise
     */
    public int scoreOf(String word) {
        if (!contains(word)) return 0;

        int len = word.length();

        if (len <= 2) return 0;
        else if (len <= 4) return 1;
        else if (len == 5) return 2;
        else if (len == 6) return 3;
        else if (len == 7) return 5;
        else return 11;
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
