package com.youlai.boot.common.enums;

import com.youlai.boot.common.base.IBaseEnum;
import lombok.Getter;

/**
 * 데이터 권한 열거형
 *
 * @author Ray.Hao
 * @since 2.3.0
 */
@Getter
public enum DataScopeEnum implements IBaseEnum<Integer> {

    /**
     * value가 작을수록 데이터 권한 범위가 큼
     */
    ALL(1, "모든 데이터"),
    DEPT_AND_SUB(2, "부서 및 하위 부서 데이터"),
    DEPT(3, "본 부서 데이터"),
    SELF(4, "본인 데이터");

    private final Integer value;

    private final String label;

    DataScopeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
