package visitor;

import network.*;

public final class SecuritySummaryVisitor implements Visitor<Void> {

    private int servers;
    private int exposedServers;
    private int unprotectedWorkstations;
    private int highSeveritySensors;
    private int highSeverityLogs;
    private int firewalls;

    public String summarize(NetworkNode root) {
        servers = 0;
        exposedServers = 0;
        unprotectedWorkstations = 0;
        highSeveritySensors = 0;
        highSeverityLogs = 0;
        firewalls = 0;

        TreeTraversal.walk(root, node -> node.accept(this));

        return "Servers: " + servers
                + ", exposed servers: " + exposedServers
                + ", unprotected workstations: " + unprotectedWorkstations
                + ", high-severity sensors: " + highSeveritySensors
                + ", high-severity logs: " + highSeverityLogs
                + ", firewalls: " + firewalls;
    }

    @Override
    public Void visit(Network network) {
        return null;
    }

    @Override
    public Void visit(Segment segment) {
        return null;
    }

    @Override
    public Void visit(Server server) {
        servers++;

        if (server.isPublicFacing()) {
            exposedServers++;
        }

        return null;
    }

    @Override
    public Void visit(Workstation workstation) {
        if (!workstation.isProtectedEndpoint()) {
            unprotectedWorkstations++;
        }

        return null;
    }

    @Override
    public Void visit(Sensor sensor) {
        if (sensor.getSeverity() >= 8) {
            highSeveritySensors++;
        }

        return null;
    }

    @Override
    public Void visit(LogRecord logRecord) {
        if (logRecord.getSeverity() >= 8) {
            highSeverityLogs++;
        }

        return null;
    }

    @Override
    public Void visit(Firewall firewall) {
        firewalls++;
        return null;
    }
}