package monitoring;

import observer.*;
import java.time.Instant;

public final class NetworkMonitor {

    private final Subject<SecurityEvent> eventBus;

    public NetworkMonitor(Subject<SecurityEvent> eventBus) {
        this.eventBus = eventBus;
    }

    public void reportFailedLogin(String ip, String username) {
        eventBus.publish(new SecurityEvent(
                EventTypes.FAILED_LOGIN,
                ip,
                "Failed login for user: " + username,
                6,
                Instant.now()
        ));
    }

    public void reportSuspiciousTraffic(
            String ip,
            String description,
            int severity
    ) {
        eventBus.publish(new SecurityEvent(
                EventTypes.SUSPICIOUS_TRAFFIC,
                ip,
                description,
                severity,
                Instant.now()
        ));
    }
}