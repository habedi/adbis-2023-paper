package base;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ExperimentResult {

    public final String name;
    public final List<Set<User>> results;
    public final List<Double> scores;

    public ExperimentResult(String methodName) {
        name = methodName;
        results = new ArrayList<>();
        scores = new ArrayList<>();
    }

    public void addNewRecording(Set<User> r, double s) {
        results.add(r);
        scores.add(s);
    }


}
