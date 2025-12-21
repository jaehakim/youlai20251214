package com.youlai.boot.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.boot.system.model.entity.Config;
import com.youlai.boot.system.model.form.ConfigForm;
import com.youlai.boot.system.model.query.ConfigPageQuery;
import com.youlai.boot.system.model.vo.ConfigVO;

/**
 * 시스템 설정 Service 인터페이스
 *
 * @author Theo
 * @since 2024-07-29 11:17:26
 */
public interface ConfigService extends IService<Config> {

    /**
     * 페이지 조회 시스템 설정
     * @param sysConfigPageQuery 조회 파라미터
     * @return 시스템 설정 페이지 목록
     */
    IPage<ConfigVO> page(ConfigPageQuery sysConfigPageQuery);

    /**
     * 저장 시스템 설정
     * @param sysConfigForm 시스템 설정 폼
     * @return 저장 성공 여부
     */
    boolean save(ConfigForm sysConfigForm);

    /**
     * 시스템 설정 폼 데이터 조회
     *
     * @param id 시스템 설정 ID
     * @return 시스템 설정 폼 데이터
     */
    ConfigForm getConfigFormData(Long id);

    /**
     * 편집 시스템 설정
     * @param id  시스템 설정 ID
     * @param sysConfigForm 시스템 설정 폼
     * @return 편집 성공 여부
     */
    boolean edit(Long id, ConfigForm sysConfigForm);

    /**
     * 삭제 시스템 설정
     * @param ids 시스템 설정 ID
     * @return 삭제 성공 여부
     */
    boolean delete(Long ids);

    /**
     * 새로고침 시스템 설정 캐시
     * @return 새로고침 성공 여부
     */
    boolean refreshCache();

    /**
     * 조회 시스템 설정
     * @param key 설정 키
     * @return 설정 값
     */
    Object getSystemConfig(String key);

}
