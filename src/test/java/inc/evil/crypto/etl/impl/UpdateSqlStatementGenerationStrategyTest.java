package inc.evil.crypto.etl.impl;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateSqlStatementGenerationStrategyTest {
    private final UpdateSqlStatementGenerationStrategy updateGenerationStrategy = new UpdateSqlStatementGenerationStrategy();

    @Test
    public void shouldBeAbleToGenerateUpdateStatements() {
        CdcEvent event = CdcEvent.builder()
                .tableName("investments")
                .eventType(EventType.UPDATE)
                .columns(List.of(
                        ColumnMetaData.builder()
                                .name("id")
                                .value("1")
                                .build(),
                        ColumnMetaData.builder()
                                .name("name")
                                .value("Etherium investment")
                                .build()
                ))
                .build();

        String actualSql = updateGenerationStrategy.generate(event);

        assertEquals("update investments set id = '1', name = 'Etherium investment' where id = '1'", actualSql);
    }

    @Test
    public void shouldThrowPrimaryKeyNotFoundException_whenThereIsNoPrimaryKey() {
        CdcEvent event = CdcEvent.builder()
                .eventType(EventType.DELETE)
                .tableName("investments")
                .columns(List.of())
                .build();

        PrimaryKeyNotFoundException exception = assertThrows(PrimaryKeyNotFoundException.class,
                () -> updateGenerationStrategy.generate(event));
        assertEquals("Primary key for table: investments was not found.", exception.getMessage());
    }

    @Test
    public void supportedEventTypeShouldEqualToUpdate() {
        assertEquals(EventType.UPDATE, updateGenerationStrategy.getSupportedEventType());
    }
}
