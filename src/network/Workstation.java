package network;

import visitor.Visitor;
import java.util.Objects;

public final class Workstation implements NetworkNode {

    private final String name;
    private final boolean protectedEndpoint;

    public Workstation(
            String name,
            boolean protectedEndpoint
    ) {
        this.name = Objects.requireNonNull(name);
        this.protectedEndpoint = protectedEndpoint;
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean isProtectedEndpoint() {
        return protectedEndpoint;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}