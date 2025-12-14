package com.youlai.boot.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.boot.system.model.entity.Config;
import com.youlai.boot.system.model.form.ConfigForm;
import com.youlai.boot.system.model.query.ConfigPageQuery;
import com.youlai.boot.system.model.vo.ConfigVO;

/**
 * 시스템 설정Service인터페이스
 *
 * @author Theo
 * @since 2024-07-29 11:17:26
 */
public interface ConfigService extends IService<Config> {

    /**
     * 페이지조회시스템 설정
     * @param sysConfigPageQuery 조회参수
     * @return 시스템 설정페이지 목록
     */
    IPage<ConfigVO> page(ConfigPageQuery sysConfigPageQuery);

    /**
     * 저장시스템 설정
     * @param sysConfigForm 시스템 설정폼
     * @return 여부저장성공
     */
    boolean save(ConfigForm sysConfigForm);

    /**
     * 시스템 설정 폼 데이터 조회
     *
     * @param id 시스템 설정ID
     * @return 시스템 설정폼데이터
     */
    ConfigForm getConfigFormData(Long id);

    /**
     * 编辑시스템 설정
     * @param id  시스템 설정ID
     * @param sysConfigForm 시스템 설정폼
     * @return 여부编辑성공
     */
    boolean edit(Long id, ConfigForm sysConfigForm);

    /**
     * 삭제시스템 설정
     * @param ids 시스템 설정ID
     * @return 여부삭제성공
     */
    boolean delete(Long ids);

    /**
     * 새로고침시스템 설정캐시
     * @return 여부새로고침성공
     */
    boolean refreshCache();

    /**
     * 조회시스템 설정
     * @param key 설정클릭
     * @return 설정值
     */
    Object getSystemConfig(String key);

}
