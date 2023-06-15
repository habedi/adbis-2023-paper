package otheralgorithms;

import base.Algorithm;
import base.ObjectiveFunction;
import base.ScoredUser;
import base.User;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class GMC extends Algorithm {

    public GMC(Map<Integer, User> userMap, Graph socialGraph, DijkstraShortestPath dijkstraShortestPath, String name) {
        super(userMap, socialGraph, dijkstraShortestPath, name);
    }


    private User findUserWithBestMMC(Set<User> R, Set<User> S, User queryUser, int k, double alpha, double beta, double graphDiameter,
                                     double maxDistance, ShortestPathAlgorithm.SingleSourcePaths shortestPaths, double maxSpatialDistance) {
        PriorityQueue<ScoredUser> queue;
        PriorityQueue<ScoredUser> mainQueue = new PriorityQueue<>();
        double mmc;
        for (User uI : S) {

            queue = new PriorityQueue<>();

            mmc = beta * ObjectiveFunction.computeGeoSocialProximity(queryUser, uI, shortestPaths, graphDiameter, alpha, maxSpatialDistance);

            double t = 0;
            for (User uJ : R) {
                t += ObjectiveFunction.computeSocialDissimilarity(uI, uJ, maxDistance);
            }

            mmc += t * ((1 - beta) / (k - 1));

            for (User uJ : S) {

                if (uI.equals(uJ))
                    continue;

                queue.add(new ScoredUser(uI, ObjectiveFunction.computeSocialDissimilarity(uI, uJ, maxDistance), false));
            }

            t = 0;
            for (int l = 0; l < (k - R.size()); l++) {
                t += queue.poll().score;
            }

            mmc += t * ((1 - beta) / (k - 1));

            mainQueue.add(new ScoredUser(uI, mmc, false));
        }

        return mainQueue.poll().user;
    }

    @Override
    public Set<User> run(int queryUserId, int k, double alpha, double beta, double graphDiameter, double maxDistance, double theta, double maxSpatialDistance) {
        // The code for the algorithm goes here

        Set<User> R = new HashSet<>();
        Set<User> S = new HashSet<>();

        ShortestPathAlgorithm.SingleSourcePaths shortestPaths = dijkstraShortestPath.getPaths(queryUserId);

        for (int uId : userMap.keySet()) {
            if (uId != queryUserId)
                S.add(userMap.get(uId));
        }

        User u;
        while (R.size() < k) {
            u = findUserWithBestMMC(R, S, userMap.get(queryUserId), k, alpha, beta, graphDiameter, maxDistance, shortestPaths, maxSpatialDistance);
            R.add(u);
            S.remove(u);
        }

        // System.out.println("1->");
        return R;

    }
}
