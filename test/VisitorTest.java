
import org.junit.jupiter.api.Test;
import visitor.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import network.*;
public class VisitorTest {

    private Network createNetwork() {
        return new Network(
                "Test Network",
                List.of(
                        new Segment(
                                "DMZ",
                                List.of(
                                        new Server("Web", 8, true),
                                        new Server("DB", 9, false)
                                )
                        ),
                        new Segment(
                                "Office",
                                List.of(
                                        new Workstation("PC-1", true),
                                        new Workstation("PC-2", false),
                                        new Sensor("IDS", 8),
                                        new LogRecord(
                                                "Log", "Attack", 9
                                        ),
                                        new Firewall("Firewall", 2)
                                )
                        )
                )
        );
    }

    @Test
    void riskMatchesHandCalculation() {
        Network network = createNetwork();

        // Web: 8 + 5 = 13
        // DB: 9
        // PC-1: 0
        // PC-2: 5
        // IDS: 8
        // Log: 9
        // Firewall: 2
        // Total = 46

        assertEquals(
                46,
                new RiskScoreVisitor().calculate(network)
        );
    }

    @Test
    void subtreeRiskMatchesHandCalculation() {
        Network network = createNetwork();

        NetworkNode dmz = network.getChildren().get(0);

        assertEquals(
                22,
                new RiskScoreVisitor().calculate(dmz)
        );
    }

    @Test
    void summaryCountsElementsCorrectly() {
        String summary =
                new SecuritySummaryVisitor()
                        .summarize(createNetwork());

        assertTrue(summary.contains("Servers: 2"));
        assertTrue(summary.contains("exposed servers: 1"));
        assertTrue(summary.contains("unprotected workstations: 1"));
        assertTrue(summary.contains("firewalls: 1"));
    }

    @Test
    void threatFilterReturnsCorrectNodes() {
        List<String> names =
                new ThreatFilterVisitor(8)
                        .filter(createNetwork())
                        .stream()
                        .map(NetworkNode::getName)
                        .toList();

        assertEquals(
                List.of("Web", "DB", "IDS", "Log"),
                names
        );
    }

    @Test
    void jsonPreservesNesting() {
        String json =
                new JsonExportVisitor().export(createNetwork());

        assertTrue(json.startsWith(
                "{\"type\":\"Network\",\"name\":\"Test Network\","
        ));

        assertTrue(json.contains(
                "\"type\":\"Segment\",\"name\":\"DMZ\",\"children\":["
        ));

        assertTrue(json.contains(
                "{\"type\":\"Server\",\"name\":\"Web\"}"
        ));
    }

    @Test
    void jsonEscapesSpecialCharacters() {
        Network network = new Network(
                "Network \"A\"",
                List.of(new Segment(
                        "Line\nBreak",
                        List.of()
                ))
        );

        String json = new JsonExportVisitor().export(network);

        assertTrue(json.contains("Network \\\"A\\\""));
        assertTrue(json.contains("Line\\nBreak"));
    }

    @Test
    void nodeCountMatchesTree() {
        // 1 Network + 2 Segments + 7 leaf nodes = 10

        assertEquals(
                10,
                new NodeCountVisitor().count(createNetwork())
        );
    }
}