package com.youlai.boot.platform.codegen.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 表单类型枚举
 *
 * @author Ray
 * @since 2.10.0
 */
@Getter
public enum JavaTypeEnum {

    VARCHAR("varchar", "String", "string"),
    CHAR("char", "String", "string"),
    BLOB("blob", "byte[]", "Uint8Array"),
    TEXT("text", "String", "string"),
    JSON("json", "String", "any"),
    INTEGER("int", "Integer", "number"),
    TINYINT("tinyint", "Integer", "number"),
    SMALLINT("smallint", "Integer", "number"),
    MEDIUMINT("mediumint", "Integer", "number"),
    BIGINT("bigint", "Long", "number"),
    FLOAT("float", "Float", "number"),
    DOUBLE("double", "Double", "number"),
    DECIMAL("decimal", "BigDecimal", "number"),
    DATE("date", "LocalDate", "Date"),
    DATETIME("datetime", "LocalDateTime", "Date"),
    TIMESTAMP("timestamp", "LocalDateTime", "Date");

    // 데이터베이스 타입
    private final String dbType;
    // Java 타입
    private final String javaType;
    // TypeScript 타입
    private final String tsType;

    // 데이터베이스 타입和Java 타입的映射
    private static final Map<String, JavaTypeEnum> typeMap = new HashMap<>();

    // 매핑 관계 초기화
    static {
        for (JavaTypeEnum javaTypeEnum : JavaTypeEnum.values()) {
            typeMap.put(javaTypeEnum.getDbType(), javaTypeEnum);
        }
    }

    JavaTypeEnum(String dbType, String javaType, String tsType) {
        this.dbType = dbType;
        this.javaType = javaType;
        this.tsType = tsType;
    }

    /**
     * 根据데이터베이스 타입获取对应的Java 타입
     *
     * @param columnType 컬럼 타입
     * @return 对应的Java 타입
     */
    public static String getJavaTypeByColumnType(String columnType) {
        JavaTypeEnum javaTypeEnum = typeMap.get(columnType);
        if (javaTypeEnum != null) {
            return javaTypeEnum.getJavaType();
        }
        return null;
    }

    /**
     * 根据Java 타입获取对应的TypeScript 타입
     *
     * @param javaType Java 타입
     * @return 对应的TypeScript 타입
     */
    public static String getTsTypeByJavaType(String javaType) {
        for (JavaTypeEnum javaTypeEnum : JavaTypeEnum.values()) {
            if (javaTypeEnum.getJavaType().equals(javaType)) {
                return javaTypeEnum.getTsType();
            }
        }
        return null;
    }
}
