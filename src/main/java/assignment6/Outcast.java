package assignment6;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {

    private final WordNet wn;

    public Outcast(WordNet wordnet) {
        wn = wordnet;
    }

    /**
     * Given an array of nouns, return an outcast
     *
     * @param nouns a given array of nouns in the WordNet
     * @return an outcast of WordNet nouns
     */
    public String outcast(String[] nouns) {
        int dist, minDist = 0, min = 0;
        for (int i = 0; i < nouns.length; i++) {
            dist = 0;
            for (String noun : nouns) {
                dist += wn.distance(nouns[i], noun);
            }
            if (dist > minDist) {
                minDist = dist;
                min = i;
            }
        }
        return nouns[min];
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
