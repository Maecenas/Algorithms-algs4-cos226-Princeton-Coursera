package assignment8;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;

public class BaseballElimination {

    private static final double INFINITY = Double.POSITIVE_INFINITY;
    private final ArrayList<String> teams;
    private final HashMap<String, Integer> teamIndex;
    private final int N;
    private final int[] wins, losses, remaining;
    private final int[][] against;
    private final ArrayList<ArrayList<String>> coe;    // pre-calculated certificate of elimination

    /**
     * Create a baseball division from given filename
     *
     * @param filename the given filename
     */
    public BaseballElimination(String filename) {
        In in = new In(filename);
        N = in.readInt();
        teams = new ArrayList<>(N);
        teamIndex = new HashMap<>(N);
        wins = new int[N];
        losses = new int[N];
        remaining = new int[N];
        against = new int[N][N];
        coe = new ArrayList<>(N);

        for (int i = 0; i < N; i++) {
            String team = in.readString();
            teams.add(team);
            teamIndex.put(team, i);
            wins[i] = in.readInt();
            losses[i] = in.readInt();
            remaining[i] = in.readInt();
            for (int j = 0; j < N; j++) {
                against[i][j] = in.readInt();
            }
        }
        coeHelper();
    }

    /**
     * Eager-calculate the certificate of elimination
     */
    private void coeHelper() {
        int bestTeamSoFar = 0;
        byte[] numberOfGames = new byte[N];  // boolean mask of against[][]
        for (int i = 0; i < N; i++) {
            if (wins[i] > wins[bestTeamSoFar]) bestTeamSoFar = i;
            for (int j = 0; j < N; j++) {
                if (against[i][j] > 0) {
                    numberOfGames[i]++;
                }
            }
        }
        int matches = 0;
        for (int i = 0; i < N; i++) matches += numberOfGames[i];
        matches /= 2;

        // calculate the certificate of elimination for each team
        for (int n = 0; n < N; n++) {
            coe.add(new ArrayList<>());
            int maxWins = wins[n] + remaining[n];
            // if w[n] + r[n] < maxWinsSoFar, then team n is mathematically eliminated.
            if (maxWins < wins[bestTeamSoFar]) {
                coe.get(n).add(teams.get(bestTeamSoFar));
                continue;
            }
            int gameVertices = matches - numberOfGames[n];
            // Keep an extra team vertex n for simplicity
            FlowNetwork fn = new FlowNetwork(gameVertices + N + 2);
            int teamVertex = 1, fullFlow = 0;
            for (int i = 0; i < N; i++) {
                for (int j = i + 1; j < N; j++) {
                    if (i == n || j == n || against[i][j] == 0) continue;
                    fullFlow += against[i][j];
                    fn.addEdge(new FlowEdge(0, teamVertex, against[i][j]));
                    fn.addEdge(new FlowEdge(teamVertex, gameVertices + i + 1, INFINITY));
                    fn.addEdge(new FlowEdge(teamVertex, gameVertices + j + 1, INFINITY));
                    teamVertex++;
                }
            }
            for (int i = 0; i < N; i++) {
                if (i == n) continue;
                fn.addEdge(new FlowEdge(gameVertices + i + 1, gameVertices + N + 1, maxWins - wins[i]));
            }
            FordFulkerson ff = new FordFulkerson(fn, 0, gameVertices + N + 1);
            if (ff.value() == fullFlow) {
                coe.set(n, null);
                continue;
            }
            for (int i = 0; i < N; i++) {
                if (ff.inCut(gameVertices + i + 1)) {
                    coe.get(n).add(teams.get(i));
                }
            }
        }
    }

    /**
     * Return the number of teams
     *
     * @return the number of teams
     */
    public int numberOfTeams() {
        return N;
    }

    /**
     * Return all the teams
     *
     * @return all the teams
     */
    public Iterable<String> teams() {
        return teams;
    }

    /**
     * Return the number of wins for given team
     *
     * @param team a given team name
     * @return the number of wins for given team
     * @throws IllegalArgumentException if the input arguments is invalid team
     */
    public int wins(String team) {
        return wins[getIndex(team)];
    }

    /**
     * Return the number of losses for given team
     *
     * @param team a given team name
     * @return the number of losses for given team
     * @throws IllegalArgumentException if the input arguments is invalid team
     */
    public int losses(String team) {
        return losses[getIndex(team)];
    }

    /**
     * Return the number of remaining games for given team
     *
     * @param team a given team name
     * @return the number of remaining games for given team
     * @throws IllegalArgumentException if the input arguments is invalid team
     */
    public int remaining(String team) {
        return remaining[getIndex(team)];
    }

    /**
     * Return the number of remaining games between team1 and team2
     *
     * @param team1 a given team name
     * @param team2 another given team name
     * @return the number of remaining games between team1 and team2
     * @throws IllegalArgumentException if any of the input arguments is invalid team
     */
    public int against(String team1, String team2) {
        return against[getIndex(team1)][getIndex(team2)];
    }

    /**
     * Return is the given team eliminated or not
     *
     * @param team a given team name
     * @return true if the given team is eliminated; false if not
     * @throws IllegalArgumentException if the input argument is invalid team
     */
    public boolean isEliminated(String team) {
        assert validate(team, coe.get(getIndex(team)));

        return coe.get(getIndex(team)) != null;
    }

    /**
     * Return any subset R of teams that eliminates given team; null if not eliminated
     *
     * @param team a given team name
     * @return any subset R of teams that eliminates given team; null if not eliminated
     * @throws IllegalArgumentException if the input argument is invalid team
     */
    public Iterable<String> certificateOfElimination(String team) {
        assert validate(team, coe.get(getIndex(team)));

        return coe.get(getIndex(team));
    }

    private int getIndex(String team) {
        if (!teamIndex.containsKey(team)) {
            throw new IllegalArgumentException(String.format("The input argument %s is invalid team", team));
        }
        return teamIndex.get(team);
    }

    /**
     * Return if a valid certificate of elimination with the given team is calculated
     * <p>
     * To verify that you are returning a valid certificate of elimination R,
     * compute a(R) = (w(R) + g(R)) / |R|, where w(R) is the total number of wins of teams in R,
     * g(R) is the total number of remaining games between teams in R,
     * and |R| is the number of teams in R.
     * Check that a(R) is greater than the maximum number of games the eliminated team can win.
     *
     * @param team   a given team name
     * @param subset a subset of teams other than {@code team} to proof mathematical eliminated
     * @return if a valid certificate of elimination with the given team is calculated or not
     */
    private boolean validate(String team, Iterable<String> subset) {
        if (subset == null) return true;
        int w = 0, g = 0, r = 0, i = getIndex(team);
        for (String team1 : subset) {
            int index = getIndex(team1);
            w += wins[index];
            for (String team2 : subset) {
                if (team1.equals(team2)) continue;
                g += against[index][getIndex(team2)];
            }
            r++;
        }
        g /= 2;
        double a = Math.ceil((double) (w + g) / r);
        return a > wins[i] + remaining[i];
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
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
