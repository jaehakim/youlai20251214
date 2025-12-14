package com.youlai.boot.platform.codegen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.boot.platform.codegen.model.entity.GenConfig;
import com.youlai.boot.platform.codegen.model.form.GenConfigForm;

/**
 * 코드 생성설정인터페이스
 *
 * @author Ray
 * @since 2.10.0
 */
public interface GenConfigService extends IService<GenConfig> {

    /**
     * 조회코드 생성설정
     *
     * @param tableName 테이블명
     * @return
     */
    GenConfigForm getGenConfigFormData(String tableName);

    /**
     * 저장코드 생성설정
     *
     * @param formData 폼데이터
     * @return
     */
    void saveGenConfig(GenConfigForm formData);

    /**
     * 삭제코드 생성설정
     *
     * @param tableName 테이블명
     * @return
     */
    void deleteGenConfig(String tableName);

}
