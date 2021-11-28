package scheduler.jobs;

import java.util.function.Predicate;

public class Dependency {
    Job a;
    Job b;
    Predicate<Boolean> predicate;

    public Dependency(Job a, Job b, Predicate<Boolean> predicate) {
        this.a = a;
        this.b = b;
        this.predicate = predicate;
    }

    public Job getA() {
        return a;
    }

    public Job getB() {
        return b;
    }

    public Predicate<Boolean> getPredicate() {
        return predicate;
    }
}
