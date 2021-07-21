package inc.evil.crypto.etl.impl;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DeleteSqlStatementGenerationStrategyTest {
    private final DeleteSqlStatementGenerationStrategy deleteStatementGenerationStrategy = new DeleteSqlStatementGenerationStrategy();

    @Test
    public void shouldBeAbleToGenerateDeleteStatements() {
        CdcEvent event = CdcEvent.builder()
                .eventType(EventType.DELETE)
                .tableName("investments")
                .columns(List.of(
                        ColumnMetaData.builder()
                                .name("id")
                                .value("1")
                                .build()
                ))
                .build();

        String actualSql = deleteStatementGenerationStrategy.generate(event);

        assertEquals("delete from investments where id = '1'", actualSql);
    }

    @Test
    public void shouldThrowPrimaryKeyNotFoundException_whenThereIsNoPrimaryKey() {
        CdcEvent event = CdcEvent.builder()
                .eventType(EventType.DELETE)
                .tableName("investments")
                .columns(List.of())
                .build();

        PrimaryKeyNotFoundException exception = assertThrows(PrimaryKeyNotFoundException.class,
                () -> deleteStatementGenerationStrategy.generate(event));
        assertEquals("Primary key for table: investments was not found.", exception.getMessage());
    }

    @Test
    public void supportedEventTypeShouldEqualToDelete() {
        assertEquals(EventType.DELETE, deleteStatementGenerationStrategy.getSupportedEventType());
    }
}
