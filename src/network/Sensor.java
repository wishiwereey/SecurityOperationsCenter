package network;

import visitor.Visitor;
import java.util.Objects;

public final class Sensor implements NetworkNode {

    private final String name;
    private final int severity;

    public Sensor(String name, int severity) {
        this.name = Objects.requireNonNull(name);
        this.severity = severity;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getSeverity() {
        return severity;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}