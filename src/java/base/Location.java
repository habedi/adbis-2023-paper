package base;

public class Location {
    public final double longitude;
    public final double latitude;
    public final int vertexId;

    public Location(int vertexId, double latitude, double longitude) {
        this.vertexId = vertexId;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
