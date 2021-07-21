package inc.evil.crypto.etl.impl;

import org.springframework.stereotype.Component;

import java.util.StringJoiner;

@Component
public class UpdateSqlStatementGenerationStrategy implements SqlStatementGenerationStrategy {

    @Override
    public String generate(CdcEvent event) {
        ColumnMetaData primaryKey = getPrimaryKey(event);
        StringBuilder sql = new StringBuilder("update " + event.getTableName() + " set ");
        StringJoiner changedColumns = new StringJoiner(", ");
        for (ColumnMetaData column : event.getColumns()) {
            changedColumns.add(column.getName() + " = '" + column.getValue() + "'");
        }
        sql.append(changedColumns)
                .append(" where ").append(primaryKey.getName())
                .append(" = '")
                .append(primaryKey.getValue())
                .append("'");
        return sql.toString();
    }

    @Override
    public EventType getSupportedEventType() {
        return EventType.UPDATE;
    }

    private ColumnMetaData getPrimaryKey(CdcEvent event) {
        if (event.getColumns().isEmpty()) {
            throw new PrimaryKeyNotFoundException("Primary key for table: " + event.getTableName() + " was not found.");
        }
        return event.getColumns()
                .get(0);
    }
}
