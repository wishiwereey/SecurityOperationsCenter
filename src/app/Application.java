package app;

import observer.*;
import monitoring.*;
import network.*;
import visitor.*;

import java.util.List;
import java.util.concurrent.*;

public final class Application {

    private Application() {
    }

    public static void main(String[] args)
            throws InterruptedException, ExecutionException {

        int threadCount = args.length > 0
                ? Integer.parseInt(args[0])
                : 4;

        if (threadCount < 3) {
            throw new IllegalArgumentException(
                    "At least 3 producer threads required"
            );
        }

        EventBus eventBus = new EventBus();

        Network network = new Network(
                "Corporate Network",
                List.of(
                        new Segment(
                                "DMZ",
                                List.of(
                                        new Server(
                                                "Web Server", 8, true
                                        ),
                                        new Server(
                                                "Database Server", 9, false
                                        )
                                )
                        ),
                        new Segment(
                                "Office Network",
                                List.of(
                                        new Workstation(
                                                "Workstation-01", true
                                        ),
                                        new Workstation(
                                                "Workstation-02", false
                                        ),
                                        new Sensor("IDS-Sensor", 8),
                                        new LogRecord(
                                                "Security-Log-001",
                                                "Multiple failed login attempts",
                                                9
                                        ),
                                        new Firewall(
                                                "Office Firewall", 2
                                        )
                                )
                        )
                )
        );

        ConsoleListener console = new ConsoleListener();
        AuditLogListener audit = new AuditLogListener();
        ReportListener reports = new ReportListener();

        ThreatScorer threatScorer = new ThreatScorer(eventBus);

        AggregatingListener aggregator =
                new AggregatingListener(eventBus);

        IncidentResponseService incidentResponse =
                new IncidentResponseService(eventBus, network);

        List<String> allEventTypes = List.of(
                EventTypes.FAILED_LOGIN,
                EventTypes.SUSPICIOUS_TRAFFIC,
                EventTypes.HIGH_CPU,
                EventTypes.SERVER_DOWN,
                EventTypes.THREAT_DETECTED,
                EventTypes.INSPECTION_REPORT,
                EventTypes.AGGREGATED_REPORT
        );

        for (String type : allEventTypes) {
            eventBus.subscribe(type, console);
        }

        List<String> monitoredTypes = List.of(
                EventTypes.FAILED_LOGIN,
                EventTypes.SUSPICIOUS_TRAFFIC,
                EventTypes.HIGH_CPU,
                EventTypes.SERVER_DOWN,
                EventTypes.THREAT_DETECTED
        );

        for (String type : monitoredTypes) {
            eventBus.subscribe(type, audit);
            eventBus.subscribe(type, aggregator);
        }

        eventBus.subscribe(
                EventTypes.FAILED_LOGIN,
                threatScorer
        );

        eventBus.subscribe(
                EventTypes.SUSPICIOUS_TRAFFIC,
                threatScorer
        );

        eventBus.subscribe(
                EventTypes.THREAT_DETECTED,
                incidentResponse
        );

        eventBus.subscribe(
                EventTypes.SERVER_DOWN,
                incidentResponse
        );

        eventBus.subscribe(
                EventTypes.INSPECTION_REPORT,
                reports
        );

        eventBus.subscribe(
                EventTypes.AGGREGATED_REPORT,
                reports
        );

        NetworkMonitor networkMonitor =
                new NetworkMonitor(eventBus);

        ServerMonitor serverMonitor =
                new ServerMonitor(eventBus);

        ExecutorService executor =
                Executors.newFixedThreadPool(threadCount);

        System.out.println("=== SOC Monitoring Started ===");

        try {
            List<Future<?>> tasks = List.of(
                    executor.submit(() ->
                            networkMonitor.reportFailedLogin(
                                    "192.168.1.25", "admin"
                            )
                    ),

                    executor.submit(() ->
                            networkMonitor.reportSuspiciousTraffic(
                                    "10.0.0.15",
                                    "Unusual outbound traffic",
                                    9
                            )
                    ),

                    executor.submit(() ->
                            serverMonitor.reportServerDown(
                                    "Database Server"
                            )
                    ),

                    executor.submit(() ->
                            serverMonitor.reportHighCpu(
                                    "Web Server", 95
                            )
                    )
            );

            for (Future<?> task : tasks) {
                task.get();
            }

        } finally {
            executor.shutdown();

            if (!executor.awaitTermination(
                    30, TimeUnit.SECONDS
            )) {
                executor.shutdownNow();

                throw new IllegalStateException(
                        "Executor did not terminate"
                );
            }
        }

        System.out.println("\n=== Network Risk Assessment ===");

        int totalRisk = new RiskScoreVisitor()
                .calculate(network);

        System.out.println("Total risk score: " + totalRisk);

        System.out.println("\n=== Security Summary ===");

        System.out.println(
                new SecuritySummaryVisitor().summarize(network)
        );

        System.out.println("\n=== Threat Filter ===");

        List<String> highRiskNodes =
                new ThreatFilterVisitor(8)
                        .filter(network)
                        .stream()
                        .map(NetworkNode::getName)
                        .toList();

        System.out.println("High-risk nodes: " + highRiskNodes);

        System.out.println("\n=== JSON Export ===");

        System.out.println(
                new JsonExportVisitor().export(network)
        );

        System.out.println(
                "\nEvents processed: "
                        + aggregator.getEventCount()
        );

        System.out.println(
                "High-severity events: "
                        + aggregator.getHighSeverityCount()
        );

        System.out.println(
                "Inspection/aggregate reports: "
                        + reports.snapshot().size()
        );

        System.out.println(
                "\n=== SOC Monitoring Finished ==="
        );
    }
}