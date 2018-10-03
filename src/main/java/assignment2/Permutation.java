package assignment2;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class Permutation {
    public static void main(String[] args) {
        final int k = Integer.parseInt(args[0]);
        RandomizedQueue<String> rq = new RandomizedQueue<>();
        double n = 1.0;
        while (!StdIn.isEmpty()) {
            String s = StdIn.readString();
            if (k == 0) {
                return;
            } else if (rq.size() < k) {
                rq.enqueue(s);
            } else if (StdRandom.uniform() < (k / n)) {
                rq.dequeue();
                rq.enqueue(s);
            }
            n++;
        }
        while (!rq.isEmpty()) {
            StdOut.println(rq.dequeue());
        }
    }
}
