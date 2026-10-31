import network.*;
import visitor.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class NodeCountVisitorTest {

    @Test
    void countWholeNetwork() {
        Network network = new Network(
                "Corporate Network",
                List.of(new Segment("DMZ", List.of(new Server("Web Server", 8, true), new Server("Database Server", 9, false))),
                        new Segment("Office Network", List.of(
                                        new Workstation("Workstation-01", true),
                                        new Workstation("Workstation-02", false),
                                        new Sensor("IDS-Sensor", 8),
                                        new LogRecord("Security-Log-001", "Alert", 9)
                                )
                        )
                )
        );

        NodeCountVisitor visitor = new NodeCountVisitor();
        assertEquals(9, visitor.count(network));
    }

    @Test
    void countSubtree() {
        Segment segment = new Segment(
                "DMZ",
                List.of(
                        new Server("Web Server", 8, true),
                        new Server("Database Server", 9, false)
                )
        );

        assertEquals(3, new NodeCountVisitor().count(segment));
    }

    @Test
    void countSingleNode() {
        Server server = new Server("Web Server", 8, true);

        assertEquals(1, new NodeCountVisitor().count(server));
    }
}