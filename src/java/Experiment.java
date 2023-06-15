import algorithms.BestNeighbourSearch;
import algorithms.FetchAndRefine;
import base.*;
import com.google.common.base.Stopwatch;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import otheralgorithms.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Experiment {

    public final double alpha;
    public final double beta;
    public final int k;
    public final double theta;
    public final double graphDiameter;
    public final double maxDistance;
    public final double maxSpatialDistance;
    public final String dataset;
    public final int seed;
    public final int numberOfWarmupQueries;
    public final int totalNumberOfQueries;
    public final Map<Integer, base.User> userMap;
    public final Graph<Integer, DefaultEdge> socialGraph;
    private final DijkstraShortestPath dijkstraShortestPath;
    private final AlgorithmBuilder algorithmBuilder;
    public double averageResponseTime;
    public base.ExperimentResult experimentResults;


    public Experiment(String name, int k, String dataset, double beta) {
        SettingsLoader configLoader = new SettingsLoader("conf/settings.config");
        Properties configs = configLoader.loadConfigs();

        alpha = Double.parseDouble(configs.getProperty("ALPHA"));
        this.beta = beta;
        //beta = Double.parseDouble(configs.getProperty("BETA"));
        this.k = k;
        //k = Integer.parseInt(configs.getProperty("K"));
        theta = Double.parseDouble(configs.getProperty("THETA"));

        this.dataset = dataset;
        // dataset = configs.getProperty("DATASET");
        seed = Integer.parseInt(configs.getProperty("SEED"));
        numberOfWarmupQueries = Integer.parseInt(configs.getProperty("NUM_WARMUP_QUERIES"));
        totalNumberOfQueries = Integer.parseInt(configs.getProperty("TOTAL_NUM_QUERIES"));

        SettingsLoader metaDataLoader = new SettingsLoader("data/" + dataset + "/metadata");
        Properties metaData = metaDataLoader.loadConfigs();

        graphDiameter = Double.parseDouble(metaData.getProperty("DIAMETER"));
        maxDistance = Double.parseDouble(metaData.getProperty("MAX_DISTANCE"));
        maxSpatialDistance = Double.parseDouble(metaData.getProperty("MAX_SPATIAL_DISTANCE"));

        DataLoader dataLoader = new DataLoader();
        String vertexEmbeddingsFilePath = "data/" + dataset + "/vertex_embeddings.txt";

        String userLocationsFilePath = "data/" + dataset + "/user_locations.csv";

        userMap = dataLoader.loadData(vertexEmbeddingsFilePath, userLocationsFilePath);

        String edgeListFilePath = "data/" + dataset + "/edge_list.csv";
        socialGraph = dataLoader.loadGraph(edgeListFilePath);

        dijkstraShortestPath = new DijkstraShortestPath(socialGraph);
        algorithmBuilder = new AlgorithmBuilder(userMap, socialGraph, dijkstraShortestPath, name);

        experimentResults = new ExperimentResult(name);

    }


    public void run() {

        List<Integer> userIdList = new ArrayList<>(userMap.keySet());
        Collections.shuffle(userIdList, new Random(seed));
        List<Integer> queryUsers = userIdList.subList(0, totalNumberOfQueries);

        // Running warm up queries
        for (int queryUserId : queryUsers.subList(0, numberOfWarmupQueries)) {
            algorithmBuilder.makeNewInstance().run(queryUserId, k, alpha, beta, graphDiameter, maxDistance, theta,
                    maxSpatialDistance);
        }


        Map<Integer, Set<User>> results = new HashMap<>();

        // Measuring the response time
        Stopwatch stopwatch = Stopwatch.createStarted();

        for (int queryUserId : queryUsers.subList(numberOfWarmupQueries, totalNumberOfQueries)) {
            results.put(queryUserId, algorithmBuilder.makeNewInstance().run(queryUserId, k, alpha, beta, graphDiameter,
                    maxDistance, theta, maxSpatialDistance));
        }


        stopwatch.stop();
        Long t = stopwatch.elapsed(TimeUnit.MILLISECONDS);

        int counter = 0;
        double score;
        for (int queryUserId : results.keySet()) {
            ShortestPathAlgorithm.SingleSourcePaths shortestPaths = dijkstraShortestPath.getPaths(queryUserId);
            score = ObjectiveFunction.computeOverallScore(userMap.get(queryUserId), results.get(queryUserId), k,
                    shortestPaths, graphDiameter, alpha, beta, maxDistance, maxSpatialDistance);
            experimentResults.addNewRecording(results.get(queryUserId), score);
            counter += 1;
        }

        averageResponseTime = ((double) t / counter);

    }

    public double getAverageResultScore() {
        OptionalDouble average = experimentResults.scores.stream().mapToDouble(a -> a).average();
        return average.isPresent() ? average.getAsDouble() : 0;
    }

    public double getSTDResultScore() {

        double[] sd = experimentResults.scores.stream().mapToDouble(a -> a).toArray();

        double sum = 0;
        double newSum = 0;

        for (int i = 0; i < sd.length; i++) {
            sum = sum + sd[i];
        }
        double mean = (sum) / (sd.length);

        for (int j = 0; j < sd.length; j++) {
            // put the calculation right in there
            newSum = newSum + ((sd[j] - mean) * (sd[j] - mean));
        }
        double squaredDiffMean = (newSum) / (sd.length);
        double standardDev = (Math.sqrt(squaredDiffMean));

        return standardDev;
    }


    class AlgorithmBuilder {

        private final Map<Integer, User> userMap;
        private final Graph socialGraph;
        private final DijkstraShortestPath dijkstraShortestPath;
        private final String name;

        public AlgorithmBuilder(Map<Integer, User> userMap, Graph socialGraph,
                                DijkstraShortestPath dijkstraShortestPath, String name) {
            this.userMap = userMap;
            this.socialGraph = socialGraph;
            this.dijkstraShortestPath = dijkstraShortestPath;
            this.name = name;
        }

        public Algorithm makeNewInstance() {
            switch (this.name) {
                case "JustGeoSocialProximity":
                    return new JustGeoSocialProximity(userMap, null, dijkstraShortestPath, name);
                case "JustUserDissimilarity":
                    return new JustUserDissimilarity(userMap, socialGraph, null, name);
                case "BSwap":
                    return new BSwap(userMap, null, dijkstraShortestPath, name);
                case "FetchAndRefine":
                    return new FetchAndRefine(userMap, socialGraph, dijkstraShortestPath, name);
                case "BestNeighbourSearch":
                    return new BestNeighbourSearch(userMap, socialGraph, dijkstraShortestPath, name);
                case "GMC":
                    return new GMC(userMap, socialGraph, dijkstraShortestPath, name);
                case "GNE":
                    return new GNE(userMap, socialGraph, dijkstraShortestPath, name);
                case "Optimal":
                    return new Optimal(userMap, socialGraph, dijkstraShortestPath, name);
                default:
                    throw new java.lang.RuntimeException("Bad or wrong algorithm name:" + name);
            }
        }

    }

}
