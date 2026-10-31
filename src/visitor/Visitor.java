package visitor;

import network.*;

public interface Visitor<R> {

    R visit(Network network);

    R visit(Segment segment);

    R visit(Server server);

    R visit(Workstation workstation);

    R visit(Sensor sensor);

    R visit(LogRecord logRecord);

    R visit(Firewall firewall);
}