package network;

import visitor.Visitor;
import java.util.Objects;

public final class Firewall implements NetworkNode {

    private final String name;
    private final int exposure;

    public Firewall(String name, int exposure) {
        this.name = Objects.requireNonNull(name);
        this.exposure = exposure;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getExposure() {
        return exposure;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}