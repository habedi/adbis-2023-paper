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

public class BSwap extends Algorithm {

    public BSwap(Map<Integer, User> userMap, Graph socialGraph, DijkstraShortestPath dijkstraShortestPath, String name) {
        super(userMap, socialGraph, dijkstraShortestPath, name);
    }


    private User findLeastDissimilarUser(Set<User> userSet, double maxDistance) {

        PriorityQueue<ScoredUser> queue = new PriorityQueue();

        double dissimilarity;
        for (User u1 : userSet) {
            dissimilarity = 0.0;
            for (User u2 : userSet) {
                if (!u1.equals(u2)) {
                    dissimilarity += ObjectiveFunction.computeSocialDissimilarity(u1, u2, maxDistance);

                }
            }

            queue.add(new ScoredUser(u1, dissimilarity / (userSet.size() - 1), true));

        }

        return queue.poll().user;
    }


    @Override
    public Set<User> run(int queryUserId, int k, double alpha, double beta, double graphDiameter, double maxDistance, double theta, double maxSpatialDistance) {
        // The code for the algorithm goes here

        Set<User> result = new HashSet<>();
        PriorityQueue<ScoredUser> queue = new PriorityQueue<>();

        ShortestPathAlgorithm.SingleSourcePaths shortestPaths = dijkstraShortestPath.getPaths(queryUserId);

        for (Integer userId : userMap.keySet()) {

            if (userId == queryUserId) continue;

            queue.add(new ScoredUser(userMap.get(userId), ObjectiveFunction.computeGeoSocialProximity(userMap.get(queryUserId),
                    userMap.get(userId), shortestPaths, graphDiameter, alpha, maxSpatialDistance), false));
        }

        while (result.size() < k && !queue.isEmpty()) {
            result.add(queue.poll().user);
        }

        User u;
        User leastDissimilarUser;
        double drop;
        while (!queue.isEmpty()) {
            u = queue.poll().user;
            leastDissimilarUser = findLeastDissimilarUser(result, maxDistance);

            drop = ObjectiveFunction.computeGeoSocialProximity(userMap.get(queryUserId), leastDissimilarUser,
                    shortestPaths, graphDiameter, alpha, maxSpatialDistance) - ObjectiveFunction.computeGeoSocialProximity(
                    userMap.get(queryUserId), u, shortestPaths, graphDiameter, alpha, maxSpatialDistance);

            if (drop > theta)
                break;

            result.remove(leastDissimilarUser);
            result.add(u);
        }


        return result;

    }
}
