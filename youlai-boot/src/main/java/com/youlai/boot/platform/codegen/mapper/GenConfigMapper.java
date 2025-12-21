package com.youlai.boot.platform.codegen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.boot.platform.codegen.model.entity.GenConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 코드 생성 기본 설정 접근 계층
 *
 * @author Ray
 * @since 2.10.0
 */
@Mapper
public interface GenConfigMapper extends BaseMapper<GenConfig> {

}




