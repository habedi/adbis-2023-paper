package otheralgorithms;

import base.Algorithm;
import base.ObjectiveFunction;
import base.User;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.paukov.combinatorics3.Generator;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Optimal extends Algorithm {

    Set<User> result = new HashSet<>();
    private int counter = 0;
    private User queryUser;
    private int k;
    private ShortestPathAlgorithm.SingleSourcePaths shortestPaths;
    private double alpha;
    private double beta;
    private double graphDiameter;
    private double maxDistance;

    private double maxSpatialDistance;

    public Optimal(Map<Integer, User> userMap, Graph socialGraph, DijkstraShortestPath dijkstraShortestPath, String name) {
        super(userMap, socialGraph, dijkstraShortestPath, name);
    }

    public void check(List<User> S) {

        Set<User> X = new HashSet<>(S);

        counter += 1;
        if (counter % 60000000 == 0) {
            System.out.println(counter / 1000000 + "m");
        }

        if (result.size() != k) {
            result = X;
            //System.out.println(result);
            return;
        }

        double a = ObjectiveFunction.computeOverallScore(queryUser, X, k, shortestPaths, graphDiameter, alpha, beta, maxDistance, maxSpatialDistance);
        double b = ObjectiveFunction.computeOverallScore(queryUser, result, k, shortestPaths, graphDiameter, alpha, beta, maxDistance, maxSpatialDistance);

        if (a > b) {
            // System.out.println("a=" + a + "; b=" + b);
            result = X;
        }
    }

    @Override
    public Set<User> run(int queryUserId, int k, double alpha, double beta, double graphDiameter, double maxDistance, double theta, double maxSpatialDistance) {
        // The code for the algorithm goes here

        this.maxSpatialDistance = maxSpatialDistance;

        ShortestPathAlgorithm.SingleSourcePaths shortestPaths = dijkstraShortestPath.getPaths(queryUserId);

        this.queryUser = userMap.get(queryUserId);
        this.k = k;
        this.shortestPaths = shortestPaths;
        this.alpha = alpha;
        this.beta = beta;
        this.graphDiameter = graphDiameter;
        this.maxDistance = maxDistance;

        Set<User> S = new HashSet<>();

        for (int uId : userMap.keySet()) {
            if (uId != queryUserId)
                S.add(userMap.get(uId));
        }

        Generator.combination(S)
                .simple(k)
                .stream()
                .forEach(s -> check(s));

        return result;
    }
}
