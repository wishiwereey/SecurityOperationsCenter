package visitor;

import network.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class NodeCountVisitor implements Visitor<Integer> {

    public int count(NetworkNode root) {
        AtomicInteger count = new AtomicInteger();

        TreeTraversal.walk(
                root,
                node -> count.addAndGet(node.accept(this))
        );

        return count.get();
    }

    @Override
    public Integer visit(Network network) {
        return 1;
    }

    @Override
    public Integer visit(Segment segment) {
        return 1;
    }

    @Override
    public Integer visit(Server server) {
        return 1;
    }

    @Override
    public Integer visit(Workstation workstation) {
        return 1;
    }

    @Override
    public Integer visit(Sensor sensor) {
        return 1;
    }

    @Override
    public Integer visit(LogRecord logRecord) {
        return 1;
    }

    @Override
    public Integer visit(Firewall firewall) {
        return 1;
    }
}