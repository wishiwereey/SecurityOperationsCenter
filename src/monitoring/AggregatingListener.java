package monitoring;

import observer.*;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public final class AggregatingListener
        implements Observer<SecurityEvent>, Subject<SecurityEvent> {

    private final Subject<SecurityEvent> downstream;

    private final AtomicInteger eventCount = new AtomicInteger();
    private final AtomicInteger highSeverityCount = new AtomicInteger();

    public AggregatingListener(Subject<SecurityEvent> downstream) {
        this.downstream = downstream;
    }

    @Override
    public void onEvent(SecurityEvent event) {
        int total = eventCount.incrementAndGet();

        if (event.getSeverity() >= 8) {
            highSeverityCount.incrementAndGet();
        }

        publish(new SecurityEvent(
                EventTypes.AGGREGATED_REPORT,
                "aggregator",
                "Processed: " + total
                        + ", high severity: "
                        + highSeverityCount.get(),
                0,
                Instant.now()
        ));
    }

    @Override
    public void subscribe(
            String eventType,
            Observer<SecurityEvent> observer
    ) {
        downstream.subscribe(eventType, observer);
    }

    @Override
    public void unsubscribe(
            String eventType,
            Observer<SecurityEvent> observer
    ) {
        downstream.unsubscribe(eventType, observer);
    }

    @Override
    public void publish(SecurityEvent event) {
        downstream.publish(event);
    }

    public int getEventCount() {
        return eventCount.get();
    }

    public int getHighSeverityCount() {
        return highSeverityCount.get();
    }
}