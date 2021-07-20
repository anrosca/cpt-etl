package inc.evil.crypto.etl.impl;

import java.util.Map;

public class ColumnMetaData {
    private final String name;
    private final String type;
    private final String typeName;
    private final Map<String, String> typeParameters;
    private final String value;

    private ColumnMetaData(ColumnMetaDataBuilder builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.typeName = builder.typeName;
        this.typeParameters = builder.typeParameters;
        this.value = builder.value;
    }

    public String getName() {
        return name;
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

    public String getValue() {
        return value;
    }

    public static ColumnMetaDataBuilder builder() {
        return new ColumnMetaDataBuilder();
    }

    public static class ColumnMetaDataBuilder {
        private String name;
        private String type;
        private String typeName;
        private Map<String, String> typeParameters;
        private String value;

        public ColumnMetaDataBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ColumnMetaDataBuilder type(String type) {
            this.type = type;
            return this;
        }

        public ColumnMetaDataBuilder typeName(String typeName) {
            this.typeName = typeName;
            return this;
        }

        public ColumnMetaDataBuilder typeParameters(Map<String, String> typeParameters) {
            this.typeParameters = typeParameters;
            return this;
        }

        public ColumnMetaDataBuilder value(String value) {
            this.value = value;
            return this;
        }

        public ColumnMetaData build() {
            return new ColumnMetaData(this);
        }
    }
}
