package assignment6;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class WordNet {

    private final HashMap<Integer, String> synset;
    private final HashMap<String, List<Integer>> ids;
    private final SAP sap;

    /**
     * Takes the name of the two input files
     *
     * @param synsets the filename (path) of input synsets
     * @param hypernyms the filename (path) of input hypernyms
     * @throws IllegalArgumentException if argument is null
     *         or inputs do not correspond to a DAG
     */
    public WordNet(String synsets, String hypernyms) {
        validate(synsets);
        validate(hypernyms);
        synset = new HashMap<>();
        ids = new HashMap<>();

        In s = new In(synsets);
        int numOfSynsets = 0;
        while (s.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(s.readLine(), ",");
            int id = Integer.parseInt(st.nextToken());
            String synonymSet = st.nextToken();
            synset.put(id, synonymSet);
            st = new StringTokenizer(synonymSet, " ");
            while (st.hasMoreTokens()) {
                String noun = st.nextToken();
                ids.getOrDefault(noun, new LinkedList<>())
                        .add(id);
            }
            numOfSynsets++;
        }
        s.close();

        Digraph G = new Digraph(numOfSynsets);
        In h = new In(hypernyms);
        while (h.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(h.readLine(), ",");
            int id = Integer.parseInt(st.nextToken());
            while (st.hasMoreTokens()) {
                int hypernym = Integer.parseInt(st.nextToken());
                G.addEdge(id, hypernym);
            }
        }
        h.close();
        validate(G);
        sap = new SAP(G);
    }

    /**
     * Return all WordNet nouns
     *
     * @return Iterable containing all WordNet nouns
     */
    public Iterable<String> nouns() {
        return ids.keySet();
    }

    /**
     * Return is the word a WordNet noun?
     *
     * @param word the given word
     * @return true if word is a WordNet noun
     * @throws IllegalArgumentException if argument is null
     */
    public boolean isNoun(String word) {
        validate(word);
        return ids.containsKey(word);
    }

    /**
     * Calculate the distance between nounA and nounB
     *
     * @param nounA a WordNet noun
     * @param nounB a WordNet noun
     * @return the distance between nounA and nounB
     * @throws IllegalArgumentException if argument is null
     *         or argument is not a WordNet noun
     */
    public int distance(String nounA, String nounB) {
        validate(nounA);
        validate(nounB);
        List<Integer> a = ids.get(nounA), b = ids.get(nounB);
        return sap.length(a, b);
    }

    /**
     * Calculate a synset that is the common ancestor of nounA and nounB
     * in a shortest ancestral path
     *
     * @param nounA a WordNet noun
     * @param nounB a WordNet noun
     * @return a synset in String
     * @throws IllegalArgumentException if argument is null
     *         or argument is not a WordNet noun
     */
    public String sap(String nounA, String nounB) {
        validate(nounA);
        validate(nounB);
        List<Integer> a = ids.get(nounA), b = ids.get(nounB);
        int ancestor = sap.ancestor(a, b);
        return synset.get(ancestor);
    }

    private void validate(String noun) {
        if (noun == null) throw new IllegalArgumentException();
    }

    private void validate(Digraph G) {
        int numberOfRoot = 0;
        for (int i = 0; i < G.V(); i++)
            if (G.outdegree(i) == 0)
                numberOfRoot++;
        if (numberOfRoot != 1)
            throw new IllegalArgumentException("The input does not correspond to a rooted DAG.");
    }

    public static void main(String[] args) {
        String synsets = args[0] != null ? args[0] : "src/test/resources/assignment6/synsets.txt";
        String hypernyms = args[1] != null ? args[1] : "src/test/resources/assignment6/hypernyms.txt";
        WordNet wn = new WordNet(synsets, hypernyms);
        StdOut.println(wn.isNoun("wordnet"));
        StdOut.println(wn.distance("good", "bad"));
        StdOut.println(wn.sap("good", "bad"));
    }
}
