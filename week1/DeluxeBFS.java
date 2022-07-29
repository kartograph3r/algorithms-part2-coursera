import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;

public class DeluxeBFS {

    private boolean[] isMarkedRed;
    private boolean[] isMarkedBlue;
    private int[] distToRed;
    private int[] distToBlue;
    private int length;
    private int ancestor;
    private final Queue<Integer> queue;

    // Two node BFS constructor
    public DeluxeBFS(Digraph G, int v, int w) {
        int size = G.V();
        isMarkedRed = new boolean[size];
        isMarkedBlue = new boolean[size];
        distToRed = new int[size];
        distToBlue = new int[size];
        queue = new Queue<Integer>();
        length = -1;
        ancestor = -1;
        twoNodeBFS(G, v, w);
    }

    public DeluxeBFS(Digraph G, Iterable<Integer> v, Iterable<Integer> w) {
        int size = G.V();
        isMarkedRed = new boolean[size];
        isMarkedBlue = new boolean[size];
        distToRed = new int[size];
        distToBlue = new int[size];
        queue = new Queue<Integer>();
        length = -1;
        ancestor = -1;
        multiNodeBFS(G, v, w);
    }

    public int length() {
        return this.length;
    }

    public int ancestor() {
        return this.ancestor;
    }

    private void multiNodeBFS(Digraph G, Iterable<Integer> v, Iterable<Integer> w) {
        // v = RED
        for (int i : v) {
            queue.enqueue(i);
            isMarkedRed[i] = true;
            distToRed[i] = 0;
            for (int j : w) {
                if (i == j) {
                    length = 0;
                    ancestor = i;
                    return;
                }
                queue.enqueue(j);
                isMarkedBlue[j] = true;
                distToBlue[j] = 0;
            }
        }

        while (!queue.isEmpty()) {
            int x = queue.dequeue();
            if (isMarkedRed[x]) {
                for (int i : G.adj(x)) {
                    if (isMarkedBlue[i]) {
                        if (length == -1) {
                            length = distToRed[x] + distToBlue[i] + 1;
                            ancestor = i;
                        }
                        int temp = distToRed[x] + distToBlue[i] + 1;
                        if (temp < length) {
                            length = temp;
                            ancestor = i;
                        }
                    }
                    if (!isMarkedRed[i]) {
                        queue.enqueue(i);
                        isMarkedRed[i] = true;
                        distToRed[i] = distToRed[x] + 1;
                    }
                }
            }
            if (isMarkedBlue[x]) {
                for (int i : G.adj(x)) {
                    if (isMarkedRed[i]) {
                        if (length == -1) {
                            length = distToBlue[x] + distToRed[i] + 1;
                            ancestor = i;
                        }
                        int temp = distToRed[i] + distToBlue[x] + 1;
                        if (temp < length) {
                            length = temp;
                            ancestor = i;
                        }
                    }
                    if (!isMarkedBlue[i]) {
                        queue.enqueue(i);
                        isMarkedBlue[i] = true;
                        distToBlue[i] = distToBlue[x] + 1;
                    }
                }
            }
        }
    }

    // Two node BFS helper function
    private void twoNodeBFS(Digraph G, int v, int w) {
        isMarkedRed[v] = true;
        isMarkedBlue[w] = true;
        distToRed[v] = 0;
        distToBlue[w] = 0;
        queue.enqueue(v);
        queue.enqueue(w);
        while (!queue.isEmpty()) {
            int x = queue.dequeue();
            if (isMarkedRed[x]) {
                for (int i : G.adj(x)) {
                    if (isMarkedBlue[i]) {
                        if (length == -1) {
                            length = distToRed[x] + distToBlue[i] + 1;
                            ancestor = i;
                        }
                        int temp = distToRed[x] + distToBlue[i] + 1;
                        if (temp < length) {
                            length = temp;
                            ancestor = i;
                        }
                    }
                    if (!isMarkedRed[i]) {
                        queue.enqueue(i);
                        isMarkedRed[i] = true;
                        distToRed[i] = distToRed[x] + 1;
                    }
                }
            }
            if (isMarkedBlue[x]) {
                for (int i : G.adj(x)) {
                    if (isMarkedRed[i]) {
                        if (length == -1) {
                            length = distToBlue[x] + distToRed[i] + 1;
                            ancestor = i;
                        }
                        int temp = distToRed[i] + distToBlue[x] + 1;
                        if (temp < length) {
                            length = temp;
                            ancestor = i;
                        }
                    }
                    if (!isMarkedBlue[i]) {
                        queue.enqueue(i);
                        isMarkedBlue[i] = true;
                        distToBlue[i] = distToBlue[x] + 1;
                    }
                }
            }
        }
    }

}
