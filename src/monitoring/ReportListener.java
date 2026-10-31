package monitoring;

import observer.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class ReportListener implements Observer<SecurityEvent> {

    private final ConcurrentLinkedQueue<SecurityEvent> reports =
            new ConcurrentLinkedQueue<>();

    @Override
    public void onEvent(SecurityEvent event) {
        reports.add(event);
    }

    public List<SecurityEvent> snapshot() {
        return List.copyOf(reports);
    }
}