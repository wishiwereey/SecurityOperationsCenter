package network;

import visitor.Visitor;
import java.util.Objects;

public final class LogRecord implements NetworkNode {

    private final String name;
    private final String detail;
    private final int severity;

    public LogRecord(
            String name,
            String detail,
            int severity
    ) {
        this.name = Objects.requireNonNull(name);
        this.detail = Objects.requireNonNull(detail);
        this.severity = severity;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getDetail() {
        return detail;
    }

    public int getSeverity() {
        return severity;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}