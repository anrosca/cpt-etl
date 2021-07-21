package inc.evil.crypto.etl.impl;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CreateTableSqlStatementGenerationStrategyTest {
    private final CreateTableSqlStatementGenerationStrategy createTableGenerationStrategy = new CreateTableSqlStatementGenerationStrategy();

    @Test
    public void shouldBeAbleToGenerateCreateTableStatements() {
        CdcEvent event = CdcEvent.builder()
                .tableName("investments")
                .eventType(EventType.CREATE_TABLE)
                .columns(List.of(
                        ColumnMetaData.builder()
                                .name("id")
                                .value("1")
                                .type("string")
                                .build(),
                        ColumnMetaData.builder()
                                .name("name")
                                .value("Bitcoin investment")
                                .type("string")
                                .build()
                ))
                .build();

        String actualSql = createTableGenerationStrategy.generate(event);

        assertEquals("create table investments (id varchar(255), name varchar(255))", actualSql);
    }

    @Test
    public void supportedEventTypeShouldEqualToCreateTable() {
        assertEquals(EventType.CREATE_TABLE, createTableGenerationStrategy.getSupportedEventType());
    }
}
