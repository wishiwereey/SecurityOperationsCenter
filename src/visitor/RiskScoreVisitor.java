package visitor;

import network.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class RiskScoreVisitor implements Visitor<Integer> {

    public int calculate(NetworkNode root) {
        AtomicInteger total = new AtomicInteger();

        TreeTraversal.walk(
                root,
                node -> total.addAndGet(node.accept(this))
        );

        return total.get();
    }

    @Override
    public Integer visit(Network network) {
        return 0;
    }

    @Override
    public Integer visit(Segment segment) {
        return 0;
    }

    @Override
    public Integer visit(Server server) {
        return server.getExposure()
                + (server.isPublicFacing() ? 5 : 0);
    }

    @Override
    public Integer visit(Workstation workstation) {
        return workstation.isProtectedEndpoint() ? 0 : 5;
    }

    @Override
    public Integer visit(Sensor sensor) {
        return sensor.getSeverity();
    }

    @Override
    public Integer visit(LogRecord logRecord) {
        return logRecord.getSeverity();
    }

    @Override
    public Integer visit(Firewall firewall) {
        return firewall.getExposure();
    }
}