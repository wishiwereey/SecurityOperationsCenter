package observer;
public final class EventTypes {
    private EventTypes() {
    }
    public static final String FAILED_LOGIN = "FAILED_LOGIN";
    public static final String SUSPICIOUS_TRAFFIC = "SUSPICIOUS_TRAFFIC";
    public static final String THREAT_DETECTED = "THREAT_DETECTED";
    public static final String HIGH_CPU = "HIGH_CPU";
    public static final String SERVER_DOWN = "SERVER_DOWN";
    public static final String INSPECTION_REPORT = "INSPECTION_REPORT";
    public static final String AGGREGATED_REPORT = "AGGREGATED_REPORT";
}