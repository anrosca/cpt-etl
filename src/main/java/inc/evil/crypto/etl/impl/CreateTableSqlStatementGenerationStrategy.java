package inc.evil.crypto.etl.impl;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

@Component
public class CreateTableSqlStatementGenerationStrategy implements SqlStatementGenerationStrategy {
    private static final Map<String, String> debeziumToSqlTypeMappings = new HashMap<>();

    static {
        debeziumToSqlTypeMappings.put("boolean", "boolean");
        debeziumToSqlTypeMappings.put("int16", "smallint");
        debeziumToSqlTypeMappings.put("int32", "integer");
        debeziumToSqlTypeMappings.put("int64", "timestamp");
        debeziumToSqlTypeMappings.put("string", "varchar(255)");
        debeziumToSqlTypeMappings.put("bytes", "numeric(19, 8)");
        debeziumToSqlTypeMappings.put("float32", "real");
        debeziumToSqlTypeMappings.put("float64", "double precision");
    }

    @Override
    public String generate(CdcEvent event) {
        return createTable(event);
    }

    @Override
    public EventType getSupportedEventType() {
        return EventType.CREATE_TABLE;
    }

    private String createTable(CdcEvent event) {
        StringBuilder createTableSql = new StringBuilder("create table ");
        createTableSql.append(event.getTableName()).append(" ");
        StringJoiner columnDefinitions = new StringJoiner(", ", "(", ")");
        for (ColumnMetaData column : event.getColumns()) {
            columnDefinitions.add(column.getName() + " " + makeSqlType(column.getType()));
        }
        createTableSql.append(columnDefinitions);
        return createTableSql.toString();
    }

    private String makeSqlType(String type) {
        return debeziumToSqlTypeMappings.get(type.toLowerCase());
    }
}
