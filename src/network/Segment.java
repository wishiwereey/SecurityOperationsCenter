package network;

import visitor.Visitor;
import java.util.List;
import java.util.Objects;

public final class Segment implements ContainerNode {

    private final String name;
    private final List<NetworkNode> children;

    public Segment(String name, List<NetworkNode> children) {
        this.name = Objects.requireNonNull(name);
        this.children = List.copyOf(children);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<NetworkNode> getChildren() {
        return children;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}