package inc.evil.crypto.etl.impl;

import org.springframework.stereotype.Component;

@Component
public class UpdateSqlStatementGenerationStrategy implements SqlStatementGenerationStrategy {
    @Override
    public String generate(CdcEvent event) {
        ColumnMetaData primaryKey = getPrimaryKey(event);
        StringBuilder sql = new StringBuilder("update " + event.getTableName() + " set ");
        for (ColumnMetaData column : event.getColumns()) {
            sql.append(column.getName()).append(" = ")
                    .append("'")
                    .append(column.getValue())
                    .append("', ");
        }
        sql = new StringBuilder(sql.substring(0, sql.length() - 2));
        sql.append(" where ").append(primaryKey.getName()).append(" = '")
                .append(primaryKey.getValue())
                .append("'");
        return sql.toString();
    }

    @Override
    public EventType getSupportedEventType() {
        return EventType.UPDATE;
    }

    private ColumnMetaData getPrimaryKey(CdcEvent event) {
        return event.getColumns()
                .get(0);
    }
}
