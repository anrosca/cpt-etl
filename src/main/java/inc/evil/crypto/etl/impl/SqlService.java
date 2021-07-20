package inc.evil.crypto.etl.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

@Service
public class SqlService {
    private final JdbcTemplate jdbcTemplate;
    private final SqlStatementCreator sqlStatementCreator;

    public SqlService(JdbcTemplate jdbcTemplate, SqlStatementCreator sqlStatementCreator) {
        this.jdbcTemplate = jdbcTemplate;
        this.sqlStatementCreator = sqlStatementCreator;
    }

    @Transactional
    public void execute(String sqlStatement) {
        jdbcTemplate.execute(sqlStatement);
    }

    @Transactional
    public void createIfNeeded(CdcEvent event) {
        Set<String> tableNames = getTableNames();
        if (!tableNames.contains(event.getTableName().toLowerCase())) {
            String createTableStatement = sqlStatementCreator.createSqlStatement(event.withEventType(EventType.CREATE_TABLE));
            execute(createTableStatement);
        }
    }

    private Set<String> getTableNames() {
        Set<String> tableNames = new HashSet<>();
        return jdbcTemplate.execute((Connection connection) -> {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getTables(null, null, "%", null);
            while (rs.next()) {
                tableNames.add(rs.getString(3).toLowerCase());
            }
            return tableNames;
        });
    }
}
