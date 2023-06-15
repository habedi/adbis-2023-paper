package algorithms;

import base.Algorithm;
import base.ObjectiveFunction;
import base.ScoredUser;
import base.User;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.*;

public class BestNeighbourSearch extends Algorithm {

    int lambda = 100;
    private SortedList<ScoredUser> sortedList;
    private double farthestGeographicalDistance;
    private double UnseenUsersGeoSocialScore;
    private PriorityQueue<ScoredUser> queue;

    public BestNeighbourSearch(Map<Integer, User> userMap, Graph socialGraph,
                               DijkstraShortestPath dijkstraShortestPath, String name) {
        super(userMap, socialGraph, dijkstraShortestPath, name);
    }

    @Override
    public Set<User> run(int queryUserId, int k, double alpha, double beta, double graphDiameter, double maxDistance, double theta, double maxSpatialDistance) {
        // The code for the algorithm goes here

        ShortestPathAlgorithm.SingleSourcePaths shortestPaths = dijkstraShortestPath.getPaths(queryUserId);

        Set<User> result = new HashSet<>();
        Set<User> notResult = new HashSet<>();
        int K = userMap.size() - 1;
        int maxIterations = 10;

        PriorityQueue<ScoredUser> queue = new PriorityQueue<>();

        for (Integer userId : userMap.keySet()) {

            if (userId == queryUserId) continue;

            queue.add(new ScoredUser(userMap.get(userId), ObjectiveFunction.computeGeoSocialProximity(userMap.get(queryUserId),
                    userMap.get(userId), shortestPaths, graphDiameter, alpha, maxSpatialDistance), false));
        }

        User u;
        while ((result.size() + notResult.size()) < K && !queue.isEmpty()) {
            u = queue.poll().user;
            if (result.size() < k) {
                result.add(u);
            } else {
                notResult.add(u);
            }
        }

        Pair pair = findBestSwap(userMap.get(queryUserId), k, shortestPaths, graphDiameter, alpha, beta, result,
                notResult, maxDistance, maxSpatialDistance);
        int counter = 0;
        while (pair != null && maxIterations > counter) {
            result.remove(pair.u0);
            notResult.add(pair.u0);
            result.add(pair.u1);
            notResult.remove(pair.u1);
            pair = findBestSwap(userMap.get(queryUserId), k, shortestPaths, graphDiameter, alpha, beta, result,
                    notResult, maxDistance, maxSpatialDistance);
            counter += 1;
        }

        return result;

    }

    public Pair findBestSwap(User uQ, int k,
                             ShortestPathAlgorithm.SingleSourcePaths shortestPaths,
                             double graphDiameter, double alpha, double beta, Set<User> result, Set<User> notResult,
                             double maxDistance, double maxSpatialDistance) {

        double maxGain = -100000.0d;
        Pair p = null;
        double gain;
        double a, b;
        Set<User> t;
        for (User u0 : result) {
            for (User u1 : notResult) {
                a = ObjectiveFunction.computeOverallScore(uQ, result, k, shortestPaths, graphDiameter, alpha, beta, maxDistance, maxSpatialDistance);
                t = new HashSet<>(result);
                t.remove(u0);
                t.add(u1);
                b = ObjectiveFunction.computeOverallScore(uQ, t, k, shortestPaths, graphDiameter, alpha, beta, maxDistance, maxSpatialDistance);
                gain = b - a;

                if (gain > maxGain) {
                    p = new Pair(u0, u1, gain);
                    maxGain = gain;
                }
            }
        }

        if (p != null && p.score > 0)
            return p;
        return null;
    }

    class SortedList<E> extends AbstractList<E> {

        private final ArrayList<E> internalList = new ArrayList<>();

        @Override
        public void add(int position, E e) {
            internalList.add(e);

            if (internalList.size() % (lambda - 1) == 0)
                internalList.sort(null);
        }

        @Override
        public E get(int i) {
            return internalList.get(i);
        }

        @Override
        public int size() {
            return internalList.size();
        }

        @Override
        public E remove(int i) {
            return internalList.remove(i);
        }

    }

    class Pair implements Comparable<Pair> {
        public final User u0;
        public final User u1;
        public final double score;

        public Pair(User u0, User u1, double score) {
            this.u0 = u0;
            this.u1 = u1;
            this.score = score;
        }

        @Override
        public int compareTo(Pair p1) {
            return Double.compare(p1.score, this.score);
        }

    }

}
