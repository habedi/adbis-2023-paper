package base;

import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ObjectiveFunction {

    public static double computeGeographicalDistance(User u1, User u2) {
        double latitude1 = Math.toRadians(u1.latitude);
        double longitude1 = Math.toRadians(u1.longitude);
        double latitude2 = Math.toRadians(u2.latitude);
        double longitude2 = Math.toRadians(u2.longitude);

        if (latitude1 == latitude2 && longitude1 == longitude2)
            return 0.0d;

        double earthRadius = 6371.01; // in kilometers
        double earthDistance = earthRadius * Math.acos(Math.sin(latitude1) * Math.sin(latitude2) +
                Math.cos(latitude1) * Math.cos(latitude2) * Math.cos(longitude1 - longitude2));
        return earthDistance;
    }

    public static double computeSocialProximity(int user2Id, ShortestPathAlgorithm.SingleSourcePaths shortestPaths,
                                                double graphDiameter) {
        return 1.0 - (shortestPaths.getPath(user2Id).getLength() / graphDiameter);
    }

    public static double computeGeoSocialProximity(User u1, User u2,
                                                   ShortestPathAlgorithm.SingleSourcePaths shortestPaths,
                                                   double graphDiameter, double alpha, double maxSpatialDistance) {
        double a = computeSocialProximity(u2.vertexId, shortestPaths, graphDiameter);
        double b = computeGeographicalDistance(u1, u2);

        double normalizedDistance = b / maxSpatialDistance;
        if (normalizedDistance < 0 || normalizedDistance > 1) {
            System.out.println(b + ">>" + maxSpatialDistance + ">>" + u1 + ">>" + u2);
        }

        return a / (1.0 + alpha * (b / maxSpatialDistance));
    }

    public static double computeSocialDissimilarity(User u1, User u2, double maxDistance) {
        return computeEuclideanDistance(u1.embeddings, u2.embeddings) / maxDistance;
    }

    public static double computeOverallScore(User uQ, Set<User> rQ, int k,
                                             ShortestPathAlgorithm.SingleSourcePaths shortestPaths,
                                             double graphDiameter, double alpha, double beta, double maxDistance, double maxSpatialDistance) {

        //System.out.println("maxDistance-->" + maxSpatialDistance+";"+alpha);

        double geoSocialScore = 0;
        for (User u : rQ) {
            geoSocialScore += computeGeoSocialProximity(uQ, u, shortestPaths, graphDiameter, alpha, maxSpatialDistance);
        }

        double diversityScore = 0;
//        Set<String> seen = new HashSet<>();
//        for (User u1 : rQ) {
//            for (User u2 : rQ) {
//                if (!u1.equals(u2) && !seen.contains(Integer.toString(u1.vertexId) + u2.vertexId)) {
//                    diversityScore += computeSocialDissimilarity(u1, u2, maxDistance);
//                    seen.add(Integer.toString(u2.vertexId) + u1.vertexId);
//                }
//            }
//        }

        // Source: https://stackoverflow.com/questions/13805759/how-to-get-all-pairs-of-a-set-efficiently
        List<User> userList = new ArrayList<>(rQ);
        for (int i = 0; i < userList.size(); i++) {
            for (int j = i + 1; j < userList.size(); j++) {
                diversityScore += computeSocialDissimilarity(userList.get(i), userList.get(j), maxDistance);
            }
        }

        return beta * (geoSocialScore / (double) k)
                + (1.0 - beta) * (diversityScore * (2.0 / (k * (k - 1))));
    }


    public static double computeEuclideanDistance(double[] vectorA, double[] vectorB) {
        int p = vectorA.length;
        double sum = 0;
        for (int i = 0; i < p; i++) {
            sum += Math.pow(Math.abs(vectorA[i] - vectorB[i]), 2);
        }
        return Math.sqrt(sum);
    }

    public static double computeEuclideanDistance(Embedding vectorA, Embedding vectorB) {
        int p = vectorA.embeddings.size();
        double sum = 0;
        for (int i = 0; i < p; i++) {
            sum += Math.pow(Math.abs(vectorA.embeddings.get(i) - vectorB.embeddings.get(i)), 2);
        }
        return Math.sqrt(sum);
    }
}

