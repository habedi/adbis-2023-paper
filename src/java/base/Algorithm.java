package base;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.Map;
import java.util.Set;

public abstract class Algorithm {

    public final Map<Integer, User> userMap;
    public final Graph socialGraph;
    public final String name;
    protected DijkstraShortestPath dijkstraShortestPath;

    public Algorithm(Map<Integer, User> userMap, Graph socialGraph, DijkstraShortestPath dijkstraShortestPath, String name) {
        this.userMap = userMap;
        this.socialGraph = socialGraph;
        this.name = name;
        this.dijkstraShortestPath = dijkstraShortestPath;
    }

    public abstract Set<User> run(int queryUserId, int k, double alpha, double beta, double graphDiameter, double maxDistance, double theta, double maxSpatialDistance);

}