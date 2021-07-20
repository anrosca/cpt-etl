package inc.evil.crypto.etl.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CdcEvent {
    private final String tableName;
    private final List<ColumnMetaData> columns;
    private final EventType eventType;

    private CdcEvent(CdcEventBuilder builder) {
        this.tableName = builder.tableName;
        this.columns = Collections.unmodifiableList(builder.columns);
        this.eventType = builder.eventType;
    }

    public String getTableName() {
        return tableName;
    }

    public List<ColumnMetaData> getColumns() {
        return columns;
    }

    public EventType getEventType() {
        return eventType;
    }

    public CdcEvent withEventType(EventType eventType) {
        return CdcEvent.builder()
                .tableName(tableName)
                .columns(columns)
                .eventType(eventType)
                .build();
    }

    public static CdcEventBuilder builder() {
        return new CdcEventBuilder();
    }

    public static class CdcEventBuilder {
        private String tableName;
        private List<ColumnMetaData> columns = new ArrayList<>();
        private EventType eventType;

        public CdcEventBuilder tableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public CdcEventBuilder columns(List<ColumnMetaData> columns) {
            this.columns = columns;
            return this;
        }

        public CdcEventBuilder eventType(EventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public CdcEvent build() {
            return new CdcEvent(this);
        }
    }
}
