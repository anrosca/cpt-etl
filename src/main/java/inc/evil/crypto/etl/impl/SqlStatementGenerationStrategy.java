package inc.evil.crypto.etl.impl;

public interface SqlStatementGenerationStrategy {
    String generate(CdcEvent event);

    default EventType getSupportedEventType() {
        return EventType.UNKNOWN;
    }
}
