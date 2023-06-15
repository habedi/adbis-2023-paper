import base.ExperimentResult;
import base.User;

import java.util.*;

public class Main {

    static Map<String, ExperimentResult> experimentResults = new HashMap<>();

    public static double computePrecision(Set<User> optimalR, Set<User> R) {
        Set<User> intersection = new HashSet<>(optimalR); // use the copy constructor
        intersection.retainAll(R);
        return (double) intersection.size() / (double) optimalR.size();
    }

    public static double computeGap(double optimalScore, double score) {
        return Math.round(100.0 - ((score * 100.0) / optimalScore));
    }

    public static void main(String[] args) {


        var k = 5;

        List<Double> betas = new ArrayList<>(Arrays.asList(0.0, 0.1, 0.3, 0.5, 0.7, 0.9, 1.0));
        List<String> datasets = new ArrayList<>(Arrays.asList("gowalla/newyork/mini", "gowalla/france/mini",
                "gowalla/usa/mini", "gowalla/germany/mini"));

        for (String dataset : datasets) {
            for (double beta : betas) {

                System.out.println("k=" + k + "; dataset=" + dataset + "; beta=" + beta + "\n");

                String algorithmName;

                // Running 'JustGeoSocialProximity' algorithm
                algorithmName = "JustGeoSocialProximity";
                runExperiment(algorithmName, k, dataset, beta);

                System.out.println();

                // Running 'JustUserDissimilarity' algorithm
                algorithmName = "JustUserDissimilarity";
                runExperiment(algorithmName, k, dataset, beta);

                System.out.println();

                // Running 'ProgressiveDiversifying' algorithm
                algorithmName = "BSwap";
                runExperiment(algorithmName, k, dataset, beta);

                System.out.println();

                // Running 'FetchAndRefine' algorithm
                algorithmName = "FetchAndRefine";
                runExperiment(algorithmName, k, dataset, beta);

                System.out.println();

                // Running 'BestNeighbourSearch' algorithm
                algorithmName = "BestNeighbourSearch";
                runExperiment(algorithmName, k, dataset, beta);

                System.out.println();

                // Running 'GMC' algorithm
                algorithmName = "GMC";
                runExperiment(algorithmName, k, dataset, beta);

                System.out.println();

                // Running 'GNE' algorithm
                algorithmName = "GNE";
                runExperiment(algorithmName, k, dataset, beta);

                System.out.println();

                // Running 'Optimal' algorithm
                algorithmName = "Optimal";
                runExperiment(algorithmName, k, dataset, beta);

                var optimalMethodName = "Optimal";

                for (String methodName : experimentResults.keySet()) {

                    if (methodName == optimalMethodName)
                        continue;

                    Set<User> optimalR = experimentResults.get(optimalMethodName).results.get(0);
                    double optimalScore = experimentResults.get(optimalMethodName).scores.get(0);

                    Set<User> R = experimentResults.get(methodName).results.get(0);
                    double Score = experimentResults.get(methodName).scores.get(0);

                    System.out.println("\nResult precision for" + methodName + " is " + computePrecision(optimalR, R));
                    System.out.println("Result gap for" + methodName + " is " + computeGap(optimalScore, Score));

                }

                System.out.println("------------------------------------------------------------------------------------------");
            }
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        }
    }

    public static void runExperiment(String algorithmName, int k, String dataset, double beta) {
        Experiment e = new Experiment(algorithmName, k, dataset, beta);
        e.run();

        experimentResults.put(e.experimentResults.name, e.experimentResults);

        System.out.println("Average response time for " + algorithmName + " is: " + e.averageResponseTime + "ms");
        System.out.println("Average score for results of " + algorithmName + " is: " + e.getAverageResultScore());
    }


}


