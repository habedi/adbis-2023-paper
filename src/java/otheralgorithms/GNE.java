package otheralgorithms;

import base.Algorithm;
import base.ObjectiveFunction;
import base.ScoredUser;
import base.User;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.*;

public class GNE extends Algorithm {

    public GNE(Map<Integer, User> userMap, Graph socialGraph, DijkstraShortestPath dijkstraShortestPath, String name) {
        super(userMap, socialGraph, dijkstraShortestPath, name);
    }


    private ScoredUser findUserWithBestMMC(Set<User> R, Set<User> S, User queryUser, int k, double alpha, double beta, double graphDiameter,
                                           double maxDistance, ShortestPathAlgorithm.SingleSourcePaths shortestPaths, boolean findMin, double maxSpatialDistance) {

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

                queue.add(new ScoredUser(uI, ObjectiveFunction.computeSocialDissimilarity(uI, uJ, maxDistance), findMin));
            }

            t = 0;
            for (int l = 0; l < (k - R.size()); l++) {
                t += queue.poll().score;
            }

            mmc += t * ((1 - beta) / (k - 1));

            mainQueue.add(new ScoredUser(uI, mmc, false));
        }

        return mainQueue.poll();
    }

    private PriorityQueue<ScoredUser> findUserWithBestMMCQueued(Set<User> R, Set<User> S, User queryUser, int k, double alpha, double beta, double graphDiameter,
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

        return mainQueue;
    }

    public Set<User> findGNEConstruction(Set<User> S, User queryUser, int k, double alpha, double beta, double graphDiameter,
                                         double maxDistance, ShortestPathAlgorithm.SingleSourcePaths shortestPaths, double maxSpatialDistance) {
        Set<User> copyS = new HashSet<>(S);
        Set<User> R = new HashSet<>();
        ScoredUser uMin;
        ScoredUser uMax;
        PriorityQueue<ScoredUser> queue;
        ScoredUser user;
        double a = 0.01;
        while (R.size() < k) {
            uMin = findUserWithBestMMC(R, copyS, queryUser, k, alpha, beta, graphDiameter, maxDistance, shortestPaths, true, maxSpatialDistance);
            uMax = findUserWithBestMMC(R, copyS, queryUser, k, alpha, beta, graphDiameter, maxDistance, shortestPaths, false, maxSpatialDistance);
            queue = findUserWithBestMMCQueued(R, copyS, queryUser, k, alpha, beta, graphDiameter, maxDistance, shortestPaths, maxSpatialDistance);
            user = queue.poll();
            Set<User> rcl = new HashSet<>();
            while (!queue.isEmpty() && user.score >= (uMax.score - a * (uMax.score - uMin.score))) {
                rcl.add(user.user);
                user = queue.poll();
            }

            Random random = new Random();
            int index = random.nextInt(rcl.size());
            Iterator<User> iter = rcl.iterator();
            User s = null;
            for (int i = 0; i <= index; i++) {
                s = iter.next();
            }

            R.add(s);
            copyS.remove(s);

        }

        //S = copyS;

        return R;
    }

    public Set<User> findGNELocalSearch(Set<User> R, Set<User> S, User queryUser, int k, double alpha, double beta, double graphDiameter,
                                        double maxDistance, ShortestPathAlgorithm.SingleSourcePaths shortestPaths, double maxSpatialDistance) {
        Set<User> copyS = new HashSet<>(S);
        Set<User> copyR = new HashSet<>(R);
        Set<User> rPrime;
        PriorityQueue<ScoredUser> queue;

        User t;
        for (User u1 : copyR) {
            rPrime = new HashSet<>(copyR);
            for (User u2 : copyR) {
                if (u1.equals(u2)) continue;

                queue = new PriorityQueue<>();
                for (User u1Prime : copyS) {
                    queue.add(new ScoredUser(u1Prime, ObjectiveFunction.computeSocialDissimilarity(u1, u1Prime, maxDistance), false));
                }

                for (int i = 0; i < k; i++) {
                    t = queue.poll().user;
                    if (!copyR.contains(t) && rPrime.contains(u2)) {
                        Set<User> rPrimePrime = new HashSet<>(rPrime);

                        //System.out.println("Size of rPrimePrime (before): " + rPrimePrime.size());
                        rPrimePrime.remove(u2);
                        rPrimePrime.add(t);
                        //System.out.println("Size of rPrimePrime (after): " + rPrimePrime.size());

                        if (ObjectiveFunction.computeOverallScore(queryUser, rPrimePrime, k, shortestPaths, graphDiameter, alpha, beta, maxDistance, maxSpatialDistance) >
                                ObjectiveFunction.computeOverallScore(queryUser, rPrime, k, shortestPaths, graphDiameter, alpha, beta, maxDistance, maxSpatialDistance))
                            rPrime = rPrimePrime;

                    }
                }

            }

            if (ObjectiveFunction.computeOverallScore(queryUser, rPrime, k, shortestPaths, graphDiameter, alpha, beta, maxDistance, maxSpatialDistance) >
                    ObjectiveFunction.computeOverallScore(queryUser, copyR, k, shortestPaths, graphDiameter, alpha, beta, maxDistance, maxSpatialDistance)) {
                copyR = rPrime;
            }
        }


        return copyR;
    }


    @Override
    public Set<User> run(int queryUserId, int k, double alpha, double beta, double graphDiameter, double maxDistance, double theta, double maxSpatialDistance) {
        // The code for the algorithm goes here


        ShortestPathAlgorithm.SingleSourcePaths shortestPaths = dijkstraShortestPath.getPaths(queryUserId);
        Set<User> rPrime;
        Set<User> R = new HashSet<>();
        Set<User> S = new HashSet<>();
        int l = 10;

        User queryUser = userMap.get(queryUserId);

        for (int u : userMap.keySet()) {

            if (queryUser.vertexId != u)
                S.add(userMap.get(u));
        }

        for (int i = 0; i < l; i++) {
            rPrime = findGNEConstruction(S, queryUser, k, alpha, beta, graphDiameter, maxDistance, shortestPaths, maxSpatialDistance);
            rPrime = findGNELocalSearch(rPrime, S, queryUser, k, alpha, beta, graphDiameter, maxDistance, shortestPaths, maxSpatialDistance);

            if (ObjectiveFunction.computeOverallScore(queryUser, rPrime, k, shortestPaths, graphDiameter, alpha, beta, maxDistance, maxSpatialDistance) >
                    ObjectiveFunction.computeOverallScore(queryUser, R, k, shortestPaths, graphDiameter, alpha, beta, maxDistance, maxSpatialDistance)) {
                R = rPrime;
            }

        }

        // System.out.println("2->");

        return R;

    }
}
