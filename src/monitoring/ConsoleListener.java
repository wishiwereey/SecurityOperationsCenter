package monitoring;

import observer.*;

public final class ConsoleListener implements Observer<SecurityEvent> {

    @Override
    public synchronized void onEvent(SecurityEvent event) {
        System.out.println(
                "[" + event.getTimestamp() + "] "
                        + event.getType()
                        + " | source=" + event.getSource()
                        + " | severity=" + event.getSeverity()
                        + " | " + event.getMessage()
        );
    }
}