package base;

public class ScoredUser implements Comparable<ScoredUser> {
    public final User user;
    public final double score;
    private final boolean isMinHeap;

    public ScoredUser(User user, double score, boolean isMinHeap) {
        this.user = user;
        this.score = score;
        this.isMinHeap = isMinHeap;
    }

    @Override
    public int compareTo(ScoredUser user2) {
        if (this.isMinHeap)
            // min heap
            return Double.compare(this.score, user2.score);
        else
            // max heap
            return Double.compare(user2.score, this.score);
    }

    @Override
    public String toString() {
        return "U(" + this.user.vertexId + "," + this.score + ")";
    }
}