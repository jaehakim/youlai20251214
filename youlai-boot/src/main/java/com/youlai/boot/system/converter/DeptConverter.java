package com.youlai.boot.system.converter;

import com.youlai.boot.system.model.entity.Dept;
import com.youlai.boot.system.model.vo.DeptVO;
import com.youlai.boot.system.model.form.DeptForm;
import org.mapstruct.Mapper;

/**
 * 부서 객체 변환기
 *
 * @author haoxr
 * @since 2022/7/29
 */
@Mapper(componentModel = "spring")
public interface DeptConverter {

    DeptForm toForm(Dept entity);
    
    DeptVO toVo(Dept entity);

    Dept toEntity(DeptForm deptForm);

}