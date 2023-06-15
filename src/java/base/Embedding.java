package base;

import java.util.List;

public class Embedding {

    public final List<Double> embeddings;
    public final int vertexId;

    public Embedding(int vertexId, List<Double> embeddings) {
        this.vertexId = vertexId;
        this.embeddings = embeddings;
    }

    public double[] getEmbeddings() {
        double[] array = new double[embeddings.size()];
        for (int i = 0; i < embeddings.size(); i++) array[i] = embeddings.get(i);
        return array;
    }
}
