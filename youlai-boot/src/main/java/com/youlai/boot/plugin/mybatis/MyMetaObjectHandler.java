package com.youlai.boot.plugin.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * mybatis-plus 필드 자동 채우기
 *
 * @author haoxr
 * @since 2022/10/14
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 신규 추가 시 생성 시간 채우기
     *
     * @param metaObject 메타데이터
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
    }

    /**
     * 업데이트 시 수정 시간 채우기
     *
     * @param metaObject 메타데이터
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
    }

}
