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
     * 시스템启动完成후，加载시스템 설정到캐시
     */
    @PostConstruct
    public void init() {
        refreshCache();
    }

    /**
     * 페이지조회시스템 설정
     *
     * @param configPageQuery 조회参수
     * @return 시스템 설정페이지 목록
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
     * 저장시스템 설정
     *
     * @param configForm 시스템 설정폼
     * @return 여부저장성공
     */
    @Override
    public boolean save(ConfigForm configForm) {
        Assert.isTrue(
                super.count(new LambdaQueryWrapper<Config>().eq(Config::getConfigKey, configForm.getConfigKey())) == 0,
                "설정클릭이미存에");
        Config config = configConverter.toEntity(configForm);
        config.setCreateBy(SecurityUtils.getUserId());
        config.setIsDeleted(0);
        return this.save(config);
    }

    /**
     * 시스템 설정 폼 데이터 조회
     *
     * @param id 시스템 설정ID
     * @return 시스템 설정폼데이터
     */
    @Override
    public ConfigForm getConfigFormData(Long id) {
        Config entity = this.getById(id);
        return configConverter.toForm(entity);
    }

    /**
     * 编辑시스템 설정
     *
     * @param id         시스템 설정ID
     * @param configForm 시스템 설정폼
     * @return 여부编辑성공
     */
    @Override
    public boolean edit(Long id, ConfigForm configForm) {
        Assert.isTrue(
                super.count(new LambdaQueryWrapper<Config>().eq(Config::getConfigKey, configForm.getConfigKey()).ne(Config::getId, id)) == 0,
                "설정클릭이미存에");
        Config config = configConverter.toEntity(configForm);
        config.setUpdateBy(SecurityUtils.getUserId());
        return this.updateById(config);
    }

    /**
     * 삭제시스템 설정
     *
     * @param id 시스템 설정ID
     * @return 여부삭제성공
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
     * 새로고침시스템 설정캐시
     *
     * @return 여부새로고침성공
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
     * 조회시스템 설정
     *
     * @param key 설정클릭
     * @return 설정值
     */
    @Override
    public Object getSystemConfig(String key) {
        if (StringUtils.isNotBlank(key)) {
            return redisTemplate.opsForHash().get(RedisConstants.System.CONFIG, key);
        }
        return null;
    }

}
