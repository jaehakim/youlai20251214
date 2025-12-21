package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.boot.system.model.entity.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.Set;

/**
 * 역할 영속성 계층 인터페이스
 *
 * @author Ray.Hao
 * @since 2022/1/14
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 최대 범위의 데이터 권한 가져오기
     *
     * @param roles 역할 코드 집합
     * @return
     */
    Integer getMaximumDataScope(Set<String> roles);
}
