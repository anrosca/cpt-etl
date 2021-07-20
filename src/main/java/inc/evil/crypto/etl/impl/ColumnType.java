package inc.evil.crypto.etl.impl;

import java.util.Map;

public class ColumnType {
    private String type;
    private String typeName;
    private Map<String, String> typeParameters;

    public ColumnType(String type, String typeName, Map<String, String> typeParameters) {
        this.type = type;
        this.typeName = typeName;
        this.typeParameters = typeParameters;
    }

    public String getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }

    public Map<String, String> getTypeParameters() {
        return typeParameters;
    }
}
