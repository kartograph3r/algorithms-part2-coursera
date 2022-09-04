import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;

public class BaseballElimination {

    // Instance variables
    private final ArrayList<String> teamNames;
    private final int[] wins;
    private final int[] losses;
    private final int[] remaining;
    private final int[][] games;
    private final HashMap<String, Integer> teamID;
    private final ArrayList<String> certificates;

    // Constructor - read and store info
    public BaseballElimination(String filename) {
        In input = new In(filename);
        int n = Integer.parseInt(input.readLine());
        teamNames = new ArrayList<String>(n);
        wins = new int[n];
        losses = new int[n];
        remaining = new int[n];
        games = new int[n][n];
        teamID = new HashMap<String, Integer>();
        certificates = new ArrayList<>();
        int counter = 0;
        while (!input.isEmpty()) {
            teamNames.add(input.readString());
            wins[counter] = input.readInt();
            losses[counter] = input.readInt();
            remaining[counter] = input.readInt();
            for (int i = 0; i < n; i++) {
                games[counter][i] = input.readInt();
            }
            teamID.put(teamNames.get(counter), counter);
            counter++;
        }
    }

    // Number of teams
    public int numberOfTeams() {
        return wins.length;
    }

    // Iterable with all the team names
    public Iterable<String> teams() {
        return teamNames;
    }

    // Helper function to check validity
    private void checkValidity(String team) {
        if (!teamID.containsKey(team)) throw new IllegalArgumentException("Team does not exist!");
    }

    // Wins for a given team
    public int wins(String team) {
        checkValidity(team);
        return wins[teamID.get(team)];
    }

    // Losses for a given team
    public int losses(String team) {
        checkValidity(team);
        return losses[teamID.get(team)];
    }

    // Remaining games for a given team
    public int remaining(String team) {
        checkValidity(team);
        return remaining[teamID.get(team)];
    }

    // Games remaining between two teams
    public int against(String team1, String team2) {
        checkValidity(team1);
        checkValidity(team2);
        return games[teamID.get(team1)][teamID.get(team2)];
    }

    // Check if a particular team is eliminated
    public boolean isEliminated(String team) {
        checkValidity(team);
        int teamNumber = teamID.get(team);
        int upperBound = wins[teamNumber] + remaining[teamNumber];
        // Perform trivial check
        boolean trivial = false;
        for (int i = 0; i < numberOfTeams(); i++) {
            if (upperBound < wins[i]) {
                trivial = true;
                certificates.clear();
                certificates.add(teamNames.get(i));
            }
        }
        if (trivial) return true;
        int comb = combination(numberOfTeams() - 1);
        int expectedValue = 0;
        FlowNetwork gameNetwork = new FlowNetwork(
                numberOfTeams() + comb + 2); // Initialize the flow network
        int vertice = 1;
        for (int i = 0; i < numberOfTeams(); i++) {
            if (i == teamNumber) continue;
            for (int j = i + 1; j < numberOfTeams(); j++) {
                if (j == teamNumber) continue;
                expectedValue += games[i][j];
                // Sink to game vertices
                gameNetwork.addEdge(new FlowEdge(0, vertice, games[i][j]));
                // Game vertices to team vertices
                gameNetwork.addEdge(new FlowEdge(vertice, comb + i + 1, Double.POSITIVE_INFINITY));
                gameNetwork.addEdge(new FlowEdge(vertice, comb + j + 1, Double.POSITIVE_INFINITY));
                vertice++;
            }
        }
        // Team vertices to sink
        for (int i = 0; i < numberOfTeams(); i++) {
            if (i == teamNumber) continue;
            gameNetwork.addEdge(
                    new FlowEdge(comb + i + 1, comb + numberOfTeams() + 1, upperBound - wins[i]));
        }
        // Run FordFulkerson
        FordFulkerson flow = new FordFulkerson(gameNetwork, 0, comb + numberOfTeams() + 1);
        if (expectedValue == flow.value()) return false;
        certificates.clear();
        for (int i = 0; i < numberOfTeams(); i++) {
            if (flow.inCut(comb + i + 1)) certificates.add(teamNames.get(i));
        }
        return true;
    }

    // Certificate of elimination
    public Iterable<String> certificateOfElimination(String team) {
        checkValidity(team);
        if (isEliminated(team)) {
            return certificates;
        }
        return null;
    }

    // Private helper function to calculate (n-1)C(2)
    private int combination(int n) {
        return (n * (n - 1)) / 2;
    }


    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
