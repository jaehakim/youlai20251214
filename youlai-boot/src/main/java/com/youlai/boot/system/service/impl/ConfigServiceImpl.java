package com.youlai.boot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.common.constant.RedisConstants;
import com.youlai.boot.system.converter.ConfigConverter;
import com.youlai.boot.system.mapper.ConfigMapper;
import com.youlai.boot.system.model.entity.Config;
import com.youlai.boot.system.model.form.ConfigForm;
import com.youlai.boot.system.model.query.ConfigPageQuery;
import com.youlai.boot.system.model.vo.ConfigVO;
import com.youlai.boot.system.service.ConfigService;
import com.youlai.boot.security.util.SecurityUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 시스템 설정Service인터페이스구현
 *
 * @author Theo
 * @since 2024-07-29 11:17:26
 */
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService {

    private final ConfigConverter configConverter;

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 시스템 시작 완료 후, 시스템 설정을 캐시에 로드
     */
    @PostConstruct
    public void init() {
        refreshCache();
    }

    /**
     * 페이지 조회 시스템 설정
     *
     * @param configPageQuery 조회 파라미터
     * @return 시스템 설정 페이지 목록
     */
    @Override
    public IPage<ConfigVO> page(ConfigPageQuery configPageQuery) {
        Page<Config> page = new Page<>(configPageQuery.getPageNum(), configPageQuery.getPageSize());
        String keywords = configPageQuery.getKeywords();
        LambdaQueryWrapper<Config> query = new LambdaQueryWrapper<Config>()
                .and(StringUtils.isNotBlank(keywords),
                    q -> q.like(Config::getConfigKey, keywords)
                        .or()
                        .like(Config::getConfigName, keywords)
                );
        Page<Config> pageList = this.page(page, query);
        return configConverter.toPageVo(pageList);
    }

    /**
     * 시스템 설정 저장
     *
     * @param configForm 시스템 설정 폼
     * @return 저장 성공 여부
     */
    @Override
    public boolean save(ConfigForm configForm) {
        Assert.isTrue(
                super.count(new LambdaQueryWrapper<Config>().eq(Config::getConfigKey, configForm.getConfigKey())) == 0,
                "설정 키가 이미 존재합니다");
        Config config = configConverter.toEntity(configForm);
        config.setCreateBy(SecurityUtils.getUserId());
        config.setIsDeleted(0);
        return this.save(config);
    }

    /**
     * 시스템 설정 폼 데이터 조회
     *
     * @param id 시스템 설정 ID
     * @return 시스템 설정 폼 데이터
     */
    @Override
    public ConfigForm getConfigFormData(Long id) {
        Config entity = this.getById(id);
        return configConverter.toForm(entity);
    }

    /**
     * 시스템 설정 수정
     *
     * @param id         시스템 설정 ID
     * @param configForm 시스템 설정 폼
     * @return 수정 성공 여부
     */
    @Override
    public boolean edit(Long id, ConfigForm configForm) {
        Assert.isTrue(
                super.count(new LambdaQueryWrapper<Config>().eq(Config::getConfigKey, configForm.getConfigKey()).ne(Config::getId, id)) == 0,
                "설정 키가 이미 존재합니다");
        Config config = configConverter.toEntity(configForm);
        config.setUpdateBy(SecurityUtils.getUserId());
        return this.updateById(config);
    }

    /**
     * 시스템 설정 삭제
     *
     * @param id 시스템 설정 ID
     * @return 삭제 성공 여부
     */
    @Override
    public boolean delete(Long id) {
        if (id != null) {
            return super.update(new LambdaUpdateWrapper<Config>()
                    .eq(Config::getId,id)
                    .set(Config::getIsDeleted, 1)
                    .set(Config::getUpdateBy, SecurityUtils.getUserId())
            );
        }
        return false;
    }

    /**
     * 시스템 설정 캐시 새로고침
     *
     * @return 새로고침 성공 여부
     */
    @Override
    public boolean refreshCache() {
        redisTemplate.delete(RedisConstants.System.CONFIG);
        List<Config> list = this.list();
        if (list != null) {
            Map<String, String> map = list.stream().collect(Collectors.toMap(Config::getConfigKey, Config::getConfigValue));
            redisTemplate.opsForHash().putAll(RedisConstants.System.CONFIG, map);
            return true;
        }
        return false;
    }

    /**
     * 시스템 설정 조회
     *
     * @param key 설정 키
     * @return 설정 값
     */
    @Override
    public Object getSystemConfig(String key) {
        if (StringUtils.isNotBlank(key)) {
            return redisTemplate.opsForHash().get(RedisConstants.System.CONFIG, key);
        }
        return null;
    }

}
