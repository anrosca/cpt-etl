package inc.evil.crypto.etl.impl;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InsertSqlStatementGenerationStrategyTest {
    private final InsertSqlStatementGenerationStrategy insertGenerationStrategy = new InsertSqlStatementGenerationStrategy();

    @Test
    public void shouldBeAbleToGenerateInsertStatements() {
        CdcEvent event = CdcEvent.builder()
                .tableName("investments")
                .eventType(EventType.INSERT)
                .columns(List.of(
                        ColumnMetaData.builder()
                                .name("id")
                                .value("1")
                                .build(),
                        ColumnMetaData.builder()
                                .name("name")
                                .value("Bitcoin investment")
                                .build()
                ))
                .build();

        String actualSql = insertGenerationStrategy.generate(event);

        assertEquals("insert into investments (id, name) values ('1', 'Bitcoin investment')", actualSql);
    }

    @Test
    public void supportedEventTypeShouldEqualToInsert() {
        assertEquals(EventType.INSERT, insertGenerationStrategy.getSupportedEventType());
    }
}
