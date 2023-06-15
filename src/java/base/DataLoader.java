package base;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class DataLoader {

    private List<Location> parseLocationFile(String path) {

        List<Location> locationSequence = new ArrayList();
        try (
                CSVReader csvReader = new CSVReaderBuilder(new FileReader(path)).withSkipLines(0).build()
        ) {
            String[] line;
            int vertexId;
            double latitude;
            double longitude;
            while ((line = csvReader.readNext()) != null) {
                List<String> columns = Arrays.asList(line);
                vertexId = Integer.parseInt(columns.get(0));
                latitude = Double.parseDouble(columns.get(1));
                longitude = Double.parseDouble(columns.get(2));
                locationSequence.add(new Location(vertexId, latitude, longitude));
            }
        } catch (CsvValidationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return locationSequence;
    }

    public List<Embedding> parseEmbeddingsFile(String path) {

        List<Embedding> embeddingSequence = new ArrayList();
        try (
                CSVReader csvReader = new CSVReaderBuilder(new FileReader(path)).withSkipLines(1).build()
        ) {
            String[] line;
            int vertexId;
            List<Double> embeddings;
            while ((line = csvReader.readNext()) != null) {
                List<String> columns = Arrays.asList(line);
                vertexId = Integer.parseInt(columns.get(0));
                embeddings = new ArrayList<>();
                for (int i = 1; i < columns.size(); i++) {
                    embeddings.add(Double.parseDouble(columns.get(i)));
                }
                embeddingSequence.add(new Embedding(vertexId, embeddings));
            }
        } catch (CsvValidationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return embeddingSequence;

    }

    public Graph<Integer, DefaultEdge> loadGraph(String path) {
        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        Set<Integer> vertices = new HashSet<>();
        try (
                CSVReader csvReader = new CSVReaderBuilder(new FileReader(path)).withSkipLines(0).build();
                CSVReader csvReader2 = new CSVReaderBuilder(new FileReader(path)).withSkipLines(0).build()
        ) {
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                List<String> columns = Arrays.asList(line);
                vertices.add(Integer.parseInt(columns.get(0).trim()));
                vertices.add(Integer.parseInt(columns.get(1).trim()));
            }

            for (Integer v : vertices) {
                g.addVertex(v);
            }

            while ((line = csvReader2.readNext()) != null) {
                List<String> columns = Arrays.asList(line);
                g.addEdge(Integer.parseInt(columns.get(0).trim()), Integer.parseInt(columns.get(1).trim()));
            }


        } catch (CsvValidationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return g;
    }

    public Map<Integer, User> loadData(String embeddingsFilePath, String locationFilePath) {
        List<Embedding> embeddings = parseEmbeddingsFile(embeddingsFilePath);
        List<Location> locations = parseLocationFile(locationFilePath);

        Map<Integer, Location> locationMap = new HashMap<>();
        for (Location l : locations) {
            locationMap.put(l.vertexId, l);
        }

        Map<Integer, User> userMap = new HashMap<>();

        for (Embedding e : embeddings) {

            //System.out.println(locationMap.containsKey(e.vertexId));

            userMap.put(e.vertexId, new User(e.vertexId, locationMap.get(e.vertexId).latitude,
                    locationMap.get(e.vertexId).longitude, e.getEmbeddings()));
        }

        return userMap;
    }

}
