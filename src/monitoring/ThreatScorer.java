package monitoring;

import observer.*;
import java.time.Instant;

public final class ThreatScorer implements Observer<SecurityEvent> {

    private final Subject<SecurityEvent> eventBus;

    public ThreatScorer(Subject<SecurityEvent> eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void onEvent(SecurityEvent event) {
        if (event.getSeverity() >= 6) {
            eventBus.publish(new SecurityEvent(
                    EventTypes.THREAT_DETECTED,
                    event.getSource(),
                    "Potential threat: " + event.getMessage(),
                    event.getSeverity(),
                    Instant.now()
            ));
        }
    }
}