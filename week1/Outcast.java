import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {

    // Store the WordNet
    private final WordNet storedWN;

    public Outcast(WordNet wordNet) {
        storedWN = wordNet;
    }

    public String outcast(String[] nouns) {
        int size = nouns.length;
        int dtWinner = 0;
        int dtWinnerIndex = 0;
        for (int i = 0; i < size; i++) {
            int dt = 0;
            for (int j = 0; j < size; j++) {
                if (i == j) continue;
                dt += storedWN.distance(nouns[i], nouns[j]);
            }
            if (dt > dtWinner) {
                dtWinner = dt;
                dtWinnerIndex = i;
            }
        }

        return nouns[dtWinnerIndex];
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
