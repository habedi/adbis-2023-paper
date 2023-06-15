package base;

public class User {

    public int vertexId;
    public double latitude;
    public double longitude;
    public double[] embeddings;

    public User(int vertexId, double latitude, double longitude, double[] embeddings) {
        this.vertexId = vertexId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.embeddings = embeddings;
    }

    @Override
    public String toString() {
        return "U(" + vertexId + ")";
    }

    @Override
    public int hashCode() {
        return vertexId;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof User && ((User) obj).vertexId == vertexId;
    }
}
