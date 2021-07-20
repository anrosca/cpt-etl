package inc.evil.crypto.etl.impl;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SqlStatementCreator {
    private static final SqlStatementGenerationStrategy DEFAULT_SQL_STATEMENT_GENERATION_STRATEGY = (event) -> {
        throw new UnsupportedOperationException("Can't generate SQL statements of type: " + event.getEventType());
    };

    private final Map<EventType, SqlStatementGenerationStrategy> sqlStatementGenerationStrategies;

    public SqlStatementCreator(Map<EventType, SqlStatementGenerationStrategy> sqlStatementGenerationStrategies) {
        this.sqlStatementGenerationStrategies = sqlStatementGenerationStrategies;
    }

    public String createSqlStatement(CdcEvent event) {
        SqlStatementGenerationStrategy strategy = sqlStatementGenerationStrategies.getOrDefault(event.getEventType(),
                DEFAULT_SQL_STATEMENT_GENERATION_STRATEGY);
        return strategy.generate(event);
    }
}
