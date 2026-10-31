package network;

import visitor.Visitor;
import java.util.List;
import java.util.Objects;

public final class Network implements ContainerNode {

    private final String name;
    private final List<NetworkNode> children;

    public Network(String name, List<NetworkNode> children) {
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