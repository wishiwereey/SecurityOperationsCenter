package observer;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentMap;

public final class EventBus implements Subject<SecurityEvent> {
    private final ConcurrentMap<String, CopyOnWriteArrayList<Observer<SecurityEvent>>> subscribers = new ConcurrentHashMap<>();

    @Override
    public void subscribe(String eventType, Observer<SecurityEvent> observer) {
        Objects.requireNonNull(eventType);
        Objects.requireNonNull(observer);
        subscribers.computeIfAbsent(eventType, key -> new CopyOnWriteArrayList<>()).addIfAbsent(observer);
    }

    @Override
    public void unsubscribe(String eventType, Observer<SecurityEvent> observer) {
        Objects.requireNonNull(eventType);
        Objects.requireNonNull(observer);
        CopyOnWriteArrayList<Observer<SecurityEvent>> observers = subscribers.get(eventType);
        if (observers != null) {
            observers.remove(observer);
        }
    }

    @Override
    public void publish(SecurityEvent event) {
        Objects.requireNonNull(event);
        CopyOnWriteArrayList<Observer<SecurityEvent>> observers = subscribers.get(event.getType());
        if (observers != null) {
            for (Observer<SecurityEvent> observer : observers) {
                observer.onEvent(event);
            }
        }
    }
}