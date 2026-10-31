package network;

import visitor.Visitor;
import java.util.Objects;

public final class Server implements NetworkNode {

    private final String name;
    private final int exposure;
    private final boolean publicFacing;

    public Server(
            String name,
            int exposure,
            boolean publicFacing
    ) {
        this.name = Objects.requireNonNull(name);
        this.exposure = exposure;
        this.publicFacing = publicFacing;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getExposure() {
        return exposure;
    }

    public boolean isPublicFacing() {
        return publicFacing;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}