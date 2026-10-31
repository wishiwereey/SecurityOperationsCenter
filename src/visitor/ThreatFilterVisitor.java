package visitor;

import network.*;
import java.util.ArrayList;
import java.util.List;

public final class ThreatFilterVisitor implements Visitor<Boolean> {

    private final int threshold;

    public ThreatFilterVisitor(int threshold) {
        this.threshold = threshold;
    }

    public List<NetworkNode> filter(NetworkNode root) {
        List<NetworkNode> matches = new ArrayList<>();

        TreeTraversal.walk(root, node -> {
            if (node.accept(this)) {
                matches.add(node);
            }
        });

        return List.copyOf(matches);
    }

    @Override
    public Boolean visit(Network network) {
        return false;
    }

    @Override
    public Boolean visit(Segment segment) {
        return false;
    }

    @Override
    public Boolean visit(Server server) {
        return server.getExposure() >= threshold;
    }

    @Override
    public Boolean visit(Workstation workstation) {
        return !workstation.isProtectedEndpoint()
                && threshold <= 5;
    }

    @Override
    public Boolean visit(Sensor sensor) {
        return sensor.getSeverity() >= threshold;
    }

    @Override
    public Boolean visit(LogRecord logRecord) {
        return logRecord.getSeverity() >= threshold;
    }

    @Override
    public Boolean visit(Firewall firewall) {
        return firewall.getExposure() >= threshold;
    }
}