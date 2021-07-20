package inc.evil.crypto.etl.impl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class SqlStatementGenerationStrategyConfig {

    @Bean
    public Map<EventType, SqlStatementGenerationStrategy> sqlStatementGenerationStrategies(Map<String, SqlStatementGenerationStrategy> strategies) {
        return strategies.values()
                .stream()
                .collect(Collectors.toMap(SqlStatementGenerationStrategy::getSupportedEventType, strategy -> strategy));
    }
}
