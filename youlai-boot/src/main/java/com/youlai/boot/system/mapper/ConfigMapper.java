package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.boot.system.model.entity.Config;
import org.apache.ibatis.annotations.Mapper;

/**
 * 시스템 설정 접근 계층
 *
 * @author Theo
 * @since 2024-7-29 11:41:04
 */
@Mapper
public interface ConfigMapper extends BaseMapper<Config> {

}
