import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SeparateChainingHashST;

import java.util.ArrayList;

public class WordNet {

    private final SeparateChainingHashST<String, Bag<Integer>> nounsST;
    private final ArrayList<String> nounsList;
    private final Digraph adj;

    // Constructor
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException("null argument!");
        nounsST = new SeparateChainingHashST<String, Bag<Integer>>();
        nounsList = new ArrayList<String>();
        In synsetsIn = new In(synsets);
        // Loop through the synsets file to store nouns
        while (!synsetsIn.isEmpty()) {
            String[] tempIn = synsetsIn.readLine().split(",");
            nounsList.add(tempIn[1]);
            String[] tempNouns = tempIn[1].split(" ");
            for (int i = 0; i < tempNouns.length; i++) {
                if (nounsST.contains(tempNouns[i])) {
                    nounsST.get(tempNouns[i]).add(Integer.parseInt(tempIn[0]));
                }
                else {
                    Bag<Integer> temp = new Bag<Integer>();
                    temp.add(Integer.parseInt(tempIn[0]));
                    nounsST.put(tempNouns[i], temp);
                }
            }
        }
        // Initialize digraph
        int synSize = nounsList.size();
        adj = new Digraph(synSize);
        In hypernymsIn = new In(hypernyms);
        int rooted = 0;
        // Loop through hypernyms to make adjacency list for each ID
        while (!hypernymsIn.isEmpty()) {
            String[] tempIn = hypernymsIn.readLine().split(",");
            int size = tempIn.length;
            if (size == 1) rooted++;
            for (int i = 1; i < size; i++) {
                adj.addEdge(Integer.parseInt(tempIn[0]), Integer.parseInt(tempIn[i]));
            }
        }
        DirectedCycle temp = new DirectedCycle(adj);
        if (temp.hasCycle()) throw new IllegalArgumentException("Need a rooted DAG!");
        if (rooted != 1) throw new IllegalArgumentException("Need a rooted DAG!");
    }

    // Iterable with all WordNet nouns
    public Iterable<String> nouns() {
        return nounsList;
    }

    // Search for a word in noun list
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException("null argument!");
        return nounsST.contains(word);
    }

    // Distance between nounA and nounB
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new IllegalArgumentException("Null arguments!");
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("Nouns aren't in the synset!");
        Iterable<Integer> v = nounsST.get(nounA);
        Iterable<Integer> w = nounsST.get(nounB);
        DeluxeBFS temp = new DeluxeBFS(adj, v, w);
        return temp.length();
    }

    // Return the 'synset' of the common ancestor of nounA and nounB
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new IllegalArgumentException("Null arguments!");
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("Nouns aren't in the synset!");
        Iterable<Integer> v = nounsST.get(nounA);
        Iterable<Integer> w = nounsST.get(nounB);
        DeluxeBFS temp = new DeluxeBFS(adj, v, w);
        return nounsList.get(temp.ancestor());
    }

    public static void main(String[] args) {
        /* Empty on purpose */
    }

}
