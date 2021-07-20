package inc.evil.crypto.etl.impl;

import org.springframework.stereotype.Component;

import java.util.StringJoiner;

@Component
public class InsertSqlStatementGenerationStrategy implements SqlStatementGenerationStrategy {
    @Override
    public String generate(CdcEvent event) {
        StringJoiner columns = new StringJoiner(",", "(", ")");
        for (ColumnMetaData column : event.getColumns()) {
            columns.add(column.getName());
        }
        StringJoiner values = new StringJoiner(",", "(", ")");
        for (ColumnMetaData column : event.getColumns()) {
            values.add("'" + column.getValue() + "'");
        }
        return "insert into " +
                event.getTableName() +
                columns +
                " values " +
                values;
    }

    @Override
    public EventType getSupportedEventType() {
        return EventType.INSERT;
    }
}
