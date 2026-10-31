import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;
import observer.*;
public class EventBusTest {

    private SecurityEvent event(String type, int severity) {
        return new SecurityEvent(
                type, "test", "message", severity, Instant.now()
        );
    }

    @Test
    void subscriberReceivesEvent() {
        EventBus bus = new EventBus();
        AtomicInteger count = new AtomicInteger();

        bus.subscribe(EventTypes.FAILED_LOGIN,
                e -> count.incrementAndGet());

        bus.publish(event(EventTypes.FAILED_LOGIN, 6));

        assertEquals(1, count.get());
    }

    @Test
    void differentTypeIsIgnored() {
        EventBus bus = new EventBus();
        AtomicInteger count = new AtomicInteger();

        bus.subscribe(EventTypes.FAILED_LOGIN,
                e -> count.incrementAndGet());

        bus.publish(event(EventTypes.SERVER_DOWN, 9));

        assertEquals(0, count.get());
    }

    @Test
    void unsubscribeStopsNotifications() {
        EventBus bus = new EventBus();
        AtomicInteger count = new AtomicInteger();

        Observer<SecurityEvent> listener =
                e -> count.incrementAndGet();

        bus.subscribe(EventTypes.FAILED_LOGIN, listener);
        bus.unsubscribe(EventTypes.FAILED_LOGIN, listener);
        bus.publish(event(EventTypes.FAILED_LOGIN, 6));

        assertEquals(0, count.get());
    }

    @Test
    void listenerCanUnsubscribeInsideHandler() {
        EventBus bus = new EventBus();
        AtomicInteger count = new AtomicInteger();

        @SuppressWarnings("unchecked")
        Observer<SecurityEvent>[] holder = new Observer[1];

        holder[0] = e -> {
            count.incrementAndGet();
            bus.unsubscribe(EventTypes.FAILED_LOGIN, holder[0]);
        };

        bus.subscribe(EventTypes.FAILED_LOGIN, holder[0]);

        bus.publish(event(EventTypes.FAILED_LOGIN, 6));
        bus.publish(event(EventTypes.FAILED_LOGIN, 6));

        assertEquals(1, count.get());
    }

    @Test
    void listenerCanSubscribeInsideHandler() {
        EventBus bus = new EventBus();

        AtomicInteger secondCount = new AtomicInteger();

        Observer<SecurityEvent> second =
                e -> secondCount.incrementAndGet();

        bus.subscribe(EventTypes.FAILED_LOGIN,
                e -> bus.subscribe(EventTypes.FAILED_LOGIN, second));

        bus.publish(event(EventTypes.FAILED_LOGIN, 6));
        bus.publish(event(EventTypes.FAILED_LOGIN, 6));

        assertEquals(1, secondCount.get());
    }

    @Test
    void duplicateSubscriptionIsIgnored() {
        EventBus bus = new EventBus();
        AtomicInteger count = new AtomicInteger();

        Observer<SecurityEvent> listener =
                e -> count.incrementAndGet();

        bus.subscribe(EventTypes.FAILED_LOGIN, listener);
        bus.subscribe(EventTypes.FAILED_LOGIN, listener);

        bus.publish(event(EventTypes.FAILED_LOGIN, 6));

        assertEquals(1, count.get());
    }

    @Test
    void invalidSeverityIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> event(EventTypes.FAILED_LOGIN, 11));
    }

    @Test
    void stressTestFourThreadsFourThousandEvents()
            throws Exception {

        EventBus bus = new EventBus();
        AtomicInteger count = new AtomicInteger();

        bus.subscribe(EventTypes.FAILED_LOGIN,
                e -> count.incrementAndGet());

        ExecutorService executor =
                Executors.newFixedThreadPool(4);

        try {
            Future<?>[] tasks = new Future[4];

            for (int i = 0; i < 4; i++) {
                tasks[i] = executor.submit(() -> {
                    for (int j = 0; j < 1000; j++) {
                        bus.publish(
                                event(EventTypes.FAILED_LOGIN, 6)
                        );
                    }
                });
            }

            for (Future<?> task : tasks) {
                task.get(30, TimeUnit.SECONDS);
            }

            assertEquals(4000, count.get());

        } finally {
            executor.shutdown();

            assertTrue(
                    executor.awaitTermination(
                            30, TimeUnit.SECONDS
                    )
            );
        }
    }
}