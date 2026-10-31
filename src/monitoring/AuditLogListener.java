package monitoring;

import observer.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class AuditLogListener implements Observer<SecurityEvent> {

    private final ConcurrentLinkedQueue<SecurityEvent> events =
            new ConcurrentLinkedQueue<>();

    @Override
    public void onEvent(SecurityEvent event) {
        events.add(event);
    }

    public List<SecurityEvent> snapshot() {
        return List.copyOf(events);
    }
}