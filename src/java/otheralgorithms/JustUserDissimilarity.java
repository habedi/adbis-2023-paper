package otheralgorithms;

import base.Algorithm;
import base.ObjectiveFunction;
import base.ScoredUser;
import base.User;
import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class JustUserDissimilarity extends Algorithm {

    public JustUserDissimilarity(Map<Integer, User> userMap, Graph socialGraph,
                                 DijkstraShortestPath dijkstraShortestPath, String name) {
        super(userMap, socialGraph, dijkstraShortestPath, name);
    }

    @Override
    public Set<User> run(int queryUserId, int k, double alpha, double beta, double graphDiameter, double maxDistance, double theta, double maxSpatialDistance) {
        // The code for the algorithm goes here

        Set<User> result = new HashSet<>();
        PriorityQueue<ScoredUser> queue = new PriorityQueue<>();

        for (Integer userId : userMap.keySet()) {

            if (userId == queryUserId)
                continue;

            queue.add(new ScoredUser(userMap.get(userId),
                    ObjectiveFunction.computeSocialDissimilarity(userMap.get(queryUserId),
                            userMap.get(userId), maxDistance), false));
        }

        while (result.size() < k && !queue.isEmpty()) {
            result.add(queue.poll().user);
        }

        //System.out.println(queue.size());

        return result;
    }
}
