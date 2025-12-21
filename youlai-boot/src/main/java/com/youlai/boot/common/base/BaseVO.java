package com.youlai.boot.common.base;

import lombok.Data;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 뷰 객체 기본 클래스
 *
 * @author haoxr
 * @since 2022/10/22
 */
@Data
@ToString
public class BaseVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
