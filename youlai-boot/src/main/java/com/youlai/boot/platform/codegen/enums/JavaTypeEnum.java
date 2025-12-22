package com.youlai.boot.platform.codegen.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Java 유형 열거형
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

    // 데이터베이스 타입과 Java 타입의 매핑
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
     * 데이터베이스 타입에 따라 대응하는 Java 타입을 가져옴
     *
     * @param columnType 컬럼 타입
     * @return 대응하는 Java 타입
     */
    public static String getJavaTypeByColumnType(String columnType) {
        JavaTypeEnum javaTypeEnum = typeMap.get(columnType);
        if (javaTypeEnum != null) {
            return javaTypeEnum.getJavaType();
        }
        return null;
    }

    /**
     * Java 타입에 따라 대응하는 TypeScript 타입을 가져옴
     *
     * @param javaType Java 타입
     * @return 대응하는 TypeScript 타입
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
