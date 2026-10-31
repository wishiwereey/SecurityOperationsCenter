import monitoring.*;

import network.*;
import observer.*;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class MonitoringTest {

    private SecurityEvent event(
            String type,
            String source,
            int severity
    ) {
        return new SecurityEvent(
                type,
                source,
                "Test message",
                severity,
                Instant.now()
        );
    }

    @Test
    void threatScorerCreatesThreat() {
        EventBus bus = new EventBus();
        ThreatScorer scorer = new ThreatScorer(bus);

        AtomicInteger threats = new AtomicInteger();

        bus.subscribe(EventTypes.FAILED_LOGIN, scorer);

        bus.subscribe(EventTypes.THREAT_DETECTED,
                e -> threats.incrementAndGet());

        bus.publish(event(
                EventTypes.FAILED_LOGIN, "192.168.1.1", 6
        ));

        assertEquals(1, threats.get());
    }

    @Test
    void lowSeverityDoesNotCreateThreat() {
        EventBus bus = new EventBus();
        ThreatScorer scorer = new ThreatScorer(bus);

        AtomicInteger threats = new AtomicInteger();

        bus.subscribe(EventTypes.FAILED_LOGIN, scorer);

        bus.subscribe(EventTypes.THREAT_DETECTED,
                e -> threats.incrementAndGet());

        bus.publish(event(
                EventTypes.FAILED_LOGIN, "192.168.1.1", 3
        ));

        assertEquals(0, threats.get());
    }

    @Test
    void aggregatorCountsEvents() {
        EventBus bus = new EventBus();
        AggregatingListener aggregator =
                new AggregatingListener(bus);

        bus.subscribe(EventTypes.FAILED_LOGIN, aggregator);

        bus.publish(event(
                EventTypes.FAILED_LOGIN, "host", 6
        ));

        bus.publish(event(
                EventTypes.FAILED_LOGIN, "host", 9
        ));

        assertEquals(2, aggregator.getEventCount());
        assertEquals(1, aggregator.getHighSeverityCount());
    }

    @Test
    void aggregatorPublishesReport() {
        EventBus bus = new EventBus();
        AggregatingListener aggregator =
                new AggregatingListener(bus);

        AtomicInteger reports = new AtomicInteger();

        bus.subscribe(EventTypes.FAILED_LOGIN, aggregator);

        bus.subscribe(EventTypes.AGGREGATED_REPORT,
                e -> reports.incrementAndGet());

        bus.publish(event(
                EventTypes.FAILED_LOGIN, "host", 9
        ));

        assertEquals(1, reports.get());
    }

    @Test
    void incidentResponseInspectsRelatedNodeOnly() {
        EventBus bus = new EventBus();

        Network network = new Network(
                "Corporate",
                List.of(
                        new Segment(
                                "DMZ",
                                List.of(
                                        new Server("Web", 8, true),
                                        new Server("DB", 9, false)
                                )
                        )
                )
        );

        IncidentResponseService service =
                new IncidentResponseService(bus, network);

        ReportListener reports = new ReportListener();

        bus.subscribe(EventTypes.SERVER_DOWN, service);
        bus.subscribe(EventTypes.INSPECTION_REPORT, reports);

        bus.publish(event(
                EventTypes.SERVER_DOWN, "DB", 9
        ));

        assertEquals(1, reports.snapshot().size());

        assertTrue(
                reports.snapshot().get(0)
                        .getMessage()
                        .contains("risk=9")
        );
    }

    @Test
    void unknownNodeDoesNotProduceInspectionReport() {
        EventBus bus = new EventBus();

        Network network = new Network(
                "Corporate",
                List.of()
        );

        IncidentResponseService service =
                new IncidentResponseService(bus, network);

        AtomicInteger reports = new AtomicInteger();

        bus.subscribe(EventTypes.SERVER_DOWN, service);

        bus.subscribe(EventTypes.INSPECTION_REPORT,
                e -> reports.incrementAndGet());

        bus.publish(event(
                EventTypes.SERVER_DOWN, "Unknown", 9
        ));

        assertEquals(0, reports.get());
    }
}