package inc.evil.crypto.etl.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

@Service
public class EtlService {
    private final CdcEventParser cdcEventParser;
    private final SqlStatementCreator sqlStatementCreator;
    private final SqlService sqlService;

    public EtlService(CdcEventParser cdcEventParser, SqlStatementCreator sqlStatementCreator, SqlService sqlService) {
        this.cdcEventParser = cdcEventParser;
        this.sqlStatementCreator = sqlStatementCreator;
        this.sqlService = sqlService;
    }

    public void process(JsonNode key, JsonNode message) {
        CdcEvent event = cdcEventParser.parse(key, message);
        sqlService.createIfNeeded(event);
        String sqlStatement = sqlStatementCreator.createSqlStatement(event);
        sqlService.execute(sqlStatement);
    }
}
