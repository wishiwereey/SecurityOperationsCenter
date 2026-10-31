package observer;

import java.time.Instant;
import java.util.Objects;

public final class SecurityEvent {

    private final String type;
    private final String source;
    private final String message;
    private final int severity;
    private final Instant timestamp;

    public SecurityEvent(
            String type,
            String source,
            String message,
            int severity,
            Instant timestamp
    ) {
        this.type = Objects.requireNonNull(type);
        this.source = Objects.requireNonNull(source);
        this.message = Objects.requireNonNull(message);
        this.timestamp = Objects.requireNonNull(timestamp);

        if (severity < 0 || severity > 10) {
            throw new IllegalArgumentException("Severity must be between 0 and 10");
        }

        this.severity = severity;
    }

    public String getType() {
        return type;
    }

    public String getSource() {
        return source;
    }

    public String getMessage() {
        return message;
    }

    public int getSeverity() {
        return severity;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}