package inc.evil.crypto.etl.impl;

import org.springframework.stereotype.Component;

@Component
public class DeleteSqlStatementGenerationStrategy implements SqlStatementGenerationStrategy {
    @Override
    public String generate(CdcEvent event) {
        ColumnMetaData primaryKey = getPrimaryKey(event);
        return "delete from " +
                event.getTableName() +
                " where " + primaryKey.getName() +
                " = '" + primaryKey.getValue() + "'";
    }

    @Override
    public EventType getSupportedEventType() {
        return EventType.DELETE;
    }

    private ColumnMetaData getPrimaryKey(CdcEvent event) {
        return event.getColumns()
                .get(0);
    }
}
