import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main2 {

    public static void main(String[] args) {

        List<Integer> ks = new ArrayList<>(Arrays.asList(5, 10, 15, 20, 25, 30, 35));
        List<Double> betas = new ArrayList<>(Arrays.asList(
                //0.0, 0.1, 0.3,
                0.5
                //,0.7, 0.9, 1.0
        ));
        List<String> datasets = new ArrayList<>(Arrays.asList(
                //"gowalla/usa/mini", "gowalla/france/mini", //"gowalla/germany/mini", "gowalla/newyork/mini"//,
                "gowalla/usa/500", "gowalla/france/500" //, // "gowalla/germany/500", "gowalla/newyork/500",
                //"gowalla/usa/1000", "gowalla/france/1000", // "gowalla/germany/1000", "gowalla/newyork/1000",
                //"gowalla/usa/1500", "gowalla/france/1500", //"gowalla/germany/1500", "gowalla/newyork/1500",
                //"gowalla/usa/2000", "gowalla/france/2000" //, "gowalla/germany/2000", "gowalla/newyork/2000" //,
                //"synthetic/2000", "synthetic/3000", "synthetic/4000", "synthetic/5000", "synthetic/6000",
                //"synthetic/7000", "synthetic/8000", "synthetic/9000", "synthetic/10000"
        ));


        for (String dataset : datasets) {
            for (double beta : betas) {
                for (int k : ks) {

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

//                    // Running 'Optimal' algorithm
//                    algorithmName = "Optimal";
//                    runExperiment(algorithmName, k, dataset, beta);

                }
                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            }
            System.out.println("---------------------------------------------------------------------------------------------");
        }
    }

    public static void runExperiment(String algorithmName, int k, String dataset, double beta) {
        Experiment e = new Experiment(algorithmName, k, dataset, beta);
        e.run();

        //experimentResults.put(e.experimentResults.name, e.experimentResults);

        DecimalFormat f = new DecimalFormat("##0.000");
        System.out.println("Average response time for " + algorithmName + " is: " + e.averageResponseTime);
        System.out.println("Average score for results of " + algorithmName + " is: " + f.format(e.getAverageResultScore()));
    }


}


