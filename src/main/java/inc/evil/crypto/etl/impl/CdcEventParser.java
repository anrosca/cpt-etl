package inc.evil.crypto.etl.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Component
public class CdcEventParser {

    public CdcEvent parse(JsonNode key, JsonNode message) {
        JsonNode payload = message.path("payload");
        JsonNode schema = message.path("schema");
        List<ColumnType> columnTypes = parseColumnTypes((ArrayNode) schema.get("fields").get(0).get("fields"));
        JsonNode before = payload.get("before");
        JsonNode after = payload.get("after");
        EventType eventType = makeEventType(payload);
        ColumnMetaData primaryKey = makePrimaryKey(key);
        return CdcEvent.builder()
                .tableName(payload.get("source").get("table").asText())
                .columns(makeColumns(before, after, columnTypes, primaryKey))
                .eventType(eventType)
                .build();
    }

    private ColumnMetaData makePrimaryKey(JsonNode key) {
        Map.Entry<String, JsonNode> primaryKey = key.get("payload").fields().next();
        ArrayNode fields = (ArrayNode) key.get("schema").get("fields");
        List<ColumnType> columnTypes = parseColumnTypes(fields);
        ColumnType pkColumnType = columnTypes.get(0);
        return ColumnMetaData.builder()
                .name(primaryKey.getKey())
                .value(primaryKey.getValue().asText())
                .type(pkColumnType.getType())
                .typeName(pkColumnType.getTypeName())
                .typeParameters(pkColumnType.getTypeParameters())
                .build();
    }

    private List<ColumnType> parseColumnTypes(ArrayNode fields) {
        List<ColumnType> columnTypes = new ArrayList<>();
        for (int i = 0; i < fields.size(); ++i) {
            JsonNode currentColumn = fields.get(i);
            String type = currentColumn.get("type").asText();
            String name = currentColumn.get("name") == null ? null : currentColumn.get("name").asText();
            JsonNode parameters = currentColumn.get("parameters");
            ColumnType columnType = new ColumnType(type, name, makeTypeParameters(parameters));
            columnTypes.add(columnType);
        }
        return columnTypes;
    }

    private Map<String, String> makeTypeParameters(JsonNode parameters) {
        Map<String, String> typeParameters = new HashMap<>();
        if (parameters == null) {
            return typeParameters;
        }
        Iterator<String> fieldNames = parameters.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            String fieldValue = parameters.get(fieldName).asText();
            typeParameters.put(fieldName, fieldValue);
        }
        return typeParameters;
    }

    private List<ColumnMetaData> makeColumns(JsonNode before, JsonNode after, List<ColumnType> columnTypes, ColumnMetaData primaryKey) {
        List<ColumnMetaData> columns = new ArrayList<>();
        columns.add(primaryKey);
        JsonNode currentNode = before.isNull() ? after : before;
        Iterator<String> fieldNames = currentNode.fieldNames();
        int columnIndex = 0;
        while (fieldNames.hasNext()) {
            String column = fieldNames.next();
            ColumnType columnType = columnTypes.get(columnIndex);
            ++columnIndex;
            if (column.equals(primaryKey.getName())) {
                continue;
            }
            columns.add(ColumnMetaData.builder()
                    .name(column)
                    .value(makeValueFor(columnType.getType(), columnType.getTypeName(), currentNode.get(column).asText(), columnType.getTypeParameters()))
                    .type(columnType.getType())
                    .typeName(columnType.getTypeName())
                    .typeParameters(columnType.getTypeParameters())
                    .build());
        }
        return columns;
    }

    private EventType makeEventType(JsonNode payload) {
        String operation = payload.get("op").asText();
        if ("u".equals(operation)) {
            return EventType.UPDATE;
        } else if ("d".equals(operation)) {
            return EventType.DELETE;
        } else if ("c".equals(operation) || "r".equals(operation)) {
            return EventType.INSERT;
        }
        return EventType.UNKNOWN;
    }

    private String makeValueFor(String type, String typeName, String value, Map<String, String> typeParameters) {
        if ("string".equals(type)) {
            return value;
        } else if ("bytes".equals(type) && "org.apache.kafka.connect.data.Decimal".equals(typeName)) {
            byte[] encoded = Base64.getDecoder().decode(value);
            String scale = typeParameters.get("scale");
            String precision = typeParameters.get("connect.decimal.precision");
            return Decimal.toLogical(new SchemaBuilder(Schema.Type.BYTES)
                    .parameter("scale", scale)
                    .parameter("connect.decimal.precision", precision).build(), encoded)
                    .toString();
        } else if ("int64".equals(type) && "io.debezium.time.MicroTimestamp".equals(typeName)) {
            long epochMicroSeconds = Long.parseLong(value);
            long epochSeconds = epochMicroSeconds / 1_000_000L;
            long nanoOffset = (epochMicroSeconds % 1_000_000L) * 1_000L;
            Instant instant = Instant.ofEpochSecond(epochSeconds, nanoOffset);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return formatter.format(Date.from(instant));
        }
        return value;
    }
}
