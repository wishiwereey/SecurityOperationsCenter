package monitoring;

import observer.*;
import java.time.Instant;

public final class ServerMonitor {

    private final Subject<SecurityEvent> eventBus;

    public ServerMonitor(Subject<SecurityEvent> eventBus) {
        this.eventBus = eventBus;
    }

    public void reportHighCpu(String serverName, int usage) {
        eventBus.publish(new SecurityEvent(
                EventTypes.HIGH_CPU,
                serverName,
                "High CPU usage: " + usage + "%",
                usage >= 90 ? 7 : 4,
                Instant.now()
        ));
    }

    public void reportServerDown(String serverName) {
        eventBus.publish(new SecurityEvent(
                EventTypes.SERVER_DOWN,
                serverName,
                "Server is down",
                9,
                Instant.now()
        ));
    }
}