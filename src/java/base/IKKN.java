package base;

import org.davidmoten.hilbert.HilbertCurve;
import org.davidmoten.hilbert.SmallHilbertCurve;

import java.util.*;

public class IKKN {

    private final SmallHilbertCurve hilbertCurve;
    private final Map<Integer, Set> grid;
    private final PriorityQueue<ScoredUser> queue;
    private User user;
    private int leftCursor;
    private int rightCursor;


    public IKKN(int n) {
        hilbertCurve = HilbertCurve.small().bits(n).dimensions(2);
        grid = new HashMap<>();
        queue = new PriorityQueue<>();
    }


    public Map<Integer, Set> getGrid() {
        return this.grid;
    }

    public int encodeLatAndLong(double latitude, double longitude) {
        long y = Math.round((latitude + 90.0) * 10000000);
        long x = Math.round((longitude + 180.0) * 10000000);

        return (int) this.hilbertCurve.index(x, y);
    }

    private void addToGrid(User user, int key) {
        if (grid.containsKey(key)) {
            grid.get(key).add(user);
        } else {
            Set<User> newSet = new HashSet<>();
            newSet.add(user);
            grid.put(key, newSet);
        }
    }

    public void addToGrid(Map<Integer, User> userMap) {
        for (Integer i : userMap.keySet()) {
            int k = encodeLatAndLong(userMap.get(i).latitude, userMap.get(i).longitude);
            addToGrid(userMap.get(i), k);
        }
    }

    private void unpackCell(User user, int cellId) {
        if (grid.containsKey(cellId)) {
            for (User u : (Set<User>) grid.get(cellId)) {
                queue.add(new ScoredUser(u, ObjectiveFunction.computeGeographicalDistance(user, u), true));
            }
        }
    }

    public ScoredUser getNextBest() {
        if (queue.isEmpty()) {
            int counter = 0;
            while (queue.isEmpty() && counter < hilbertCurve.maxIndex() / 2) {
                if (leftCursor > 0) {
                    leftCursor -= 1;
                    unpackCell(this.user, leftCursor);
                }
                if (leftCursor < hilbertCurve.maxIndex()) {
                    this.rightCursor += 1;
                    unpackCell(user, rightCursor);
                }
                counter += 1;
            }
        }

        ScoredUser o = queue.poll();
//        if (o == null)
//            return null;

        return o;

    }

    public void unpackFirstCell(User user) {
        this.user = user;
        int cellId = this.encodeLatAndLong(user.latitude, user.longitude);
        this.leftCursor = this.rightCursor = cellId;
        unpackCell(user, cellId);
    }
}


