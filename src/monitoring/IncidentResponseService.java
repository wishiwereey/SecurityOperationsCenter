package monitoring;

import observer.*;
import network.*;
import visitor.RiskScoreVisitor;

import java.time.Instant;
import java.util.Objects;

public final class IncidentResponseService
        implements Observer<SecurityEvent> {

    private final Subject<SecurityEvent> eventBus;
    private final Network network;

    public IncidentResponseService(
            Subject<SecurityEvent> eventBus,
            Network network
    ) {
        this.eventBus = Objects.requireNonNull(eventBus);
        this.network = Objects.requireNonNull(network);
    }

    @Override
    public void onEvent(SecurityEvent event) {
        NetworkNode relatedNode = findRelated(
                network,
                event.getSource()
        );

        if (relatedNode == null) {
            return;
        }

        int risk = new RiskScoreVisitor().calculate(relatedNode);

        eventBus.publish(new SecurityEvent(
                EventTypes.INSPECTION_REPORT,
                event.getSource(),
                "Inspected subtree: " + relatedNode.getName()
                        + ", risk=" + risk,
                risk >= 15 ? 9 : 3,
                Instant.now()
        ));
    }

    private NetworkNode findRelated(
            NetworkNode node,
            String source
    ) {
        if (node.getName().equals(source)) {
            return node;
        }

        if (node instanceof ContainerNode container) {
            for (NetworkNode child : container.getChildren()) {
                NetworkNode result = findRelated(child, source);

                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }
}