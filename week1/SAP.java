import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {

    // Store the digraph
    private final Digraph graph;

    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException("Null argument!");
        graph = new Digraph(G);
    }

    private boolean checkValidity(int v, int w) {
        if (v >= graph.V() || w >= graph.V()) return false;
        if (v < 0 || w < 0) return false;
        return true;
    }

    private boolean checkValidity(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) return false;
        for (Integer i : v) {
            if (i == null) return false;
            if (i >= graph.V() || i < 0) return false;
        }
        for (Integer i : w) {
            if (i == null) return false;
            if (i >= graph.V() || i < 0) return false;
        }
        return true;
    }

    // Length of the shortest ancestor path between v and w
    public int length(int v, int w) {
        if (!checkValidity(v, w)) throw new IllegalArgumentException("Argument is out of range!");
        if (v == w) return 0;
        DeluxeBFS temp = new DeluxeBFS(graph, v, w);
        return temp.length();
    }

    // Common ancestor of v and w
    public int ancestor(int v, int w) {
        if (!checkValidity(v, w)) throw new IllegalArgumentException("Argument is out of range!");
        if (v == w) return v;
        DeluxeBFS temp = new DeluxeBFS(graph, v, w);
        return temp.ancestor();
    }

    // Length of the shortest ancestral path between two sets v and w
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (!checkValidity(v, w)) throw new IllegalArgumentException("Null argument!");
        DeluxeBFS temp = new DeluxeBFS(graph, v, w);
        return temp.length();
    }

    // Common ancestor between sets v and w
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (!checkValidity(v, w)) throw new IllegalArgumentException("Null argument!");
        DeluxeBFS temp = new DeluxeBFS(graph, v, w);
        return temp.ancestor();
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
