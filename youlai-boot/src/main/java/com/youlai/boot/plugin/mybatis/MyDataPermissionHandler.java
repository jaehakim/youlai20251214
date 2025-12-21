package com.youlai.boot.plugin.mybatis;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;
import com.youlai.boot.common.annotation.DataPermission;
import com.youlai.boot.common.base.IBaseEnum;
import com.youlai.boot.common.enums.DataScopeEnum;
import com.youlai.boot.security.util.SecurityUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;

import java.lang.reflect.Method;

/**
 * 데이터 권한 핸들러
 *
 * @author zc
 * @since 2021-12-10 13:28
 */
@Slf4j
public class MyDataPermissionHandler implements DataPermissionHandler {

    /**
     * 데이터 권한의 SQL 조각 가져오기
     * @param where 쿼리 조건
     * @param mappedStatementId mapper 인터페이스 메서드의 전체 경로
     * @return SQL 조각
     */
    @Override
    @SneakyThrows
    public Expression getSqlSegment(Expression where, String mappedStatementId) {
        // 미로그인 상태이거나 정기 작업 실행 SQL이거나 슈퍼 관리자인 경우 바로 반환
        if(SecurityUtils.getUserId() == null || SecurityUtils.isRoot()){
            return where;
        }
        // 현재 사용자의 데이터 권한 가져오기
        Integer dataScope = SecurityUtils.getDataScope();
        DataScopeEnum dataScopeEnum = IBaseEnum.getEnumByValue(dataScope, DataScopeEnum.class);
        // 전체 데이터 권한인 경우 바로 반환
        if (DataScopeEnum.ALL.equals(dataScopeEnum)) {
            return where;
        }
        // 현재 실행 중인 인터페이스 클래스 가져오기
        Class<?> clazz = Class.forName(mappedStatementId.substring(0, mappedStatementId.lastIndexOf(StringPool.DOT)));
        // 현재 실행 중인 메서드 이름 가져오기
        String methodName = mappedStatementId.substring(mappedStatementId.lastIndexOf(StringPool.DOT) + 1);
        // 현재 실행 중인 인터페이스 클래스의 모든 메서드 가져오기
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            // 현재 실행 중인 메서드 찾기
            if (method.getName().equals(methodName)) {
                DataPermission annotation = method.getAnnotation(DataPermission.class);
                // 현재 실행 중인 메서드에 권한 어노테이션이 있는지 판단, 어노테이션이 없으면 바로 반환
                if (annotation == null ) {
                    return where;
                }
                return dataScopeFilter(annotation.deptAlias(), annotation.deptIdColumnName(), annotation.userAlias(), annotation.userIdColumnName(), dataScopeEnum,where);
            }
        }
        return where;
    }

    /**
     * 필터 조건 구성
     *
     * @param where 현재 쿼리 조건
     * @return 구성된 쿼리 조건
     */
    @SneakyThrows
    public static Expression dataScopeFilter(String deptAlias, String deptIdColumnName, String userAlias, String userIdColumnName,DataScopeEnum dataScopeEnum, Expression where) {

        // 부서와 사용자의 별칭 가져오기
        String deptColumnName = StrUtil.isNotBlank(deptAlias) ? (deptAlias + StringPool.DOT + deptIdColumnName) : deptIdColumnName;
        String userColumnName = StrUtil.isNotBlank(userAlias) ? (userAlias + StringPool.DOT + userIdColumnName) : userIdColumnName;

        Long deptId, userId;
        String appendSqlStr;
        switch (dataScopeEnum) {
            case ALL:
                return where;
            case DEPT:
                deptId = SecurityUtils.getDeptId();
                appendSqlStr = deptColumnName + StringPool.EQUALS + deptId;
                break;
            case SELF:
                userId = SecurityUtils.getUserId();
                appendSqlStr = userColumnName + StringPool.EQUALS + userId;
                break;
            // 기본 부서 및 하위 부서 데이터 권한
            default:
                deptId = SecurityUtils.getDeptId();
                appendSqlStr = deptColumnName + " IN ( SELECT id FROM sys_dept WHERE id = " + deptId + " OR FIND_IN_SET( " + deptId + " , tree_path ) )";
                break;
        }

        if (StrUtil.isBlank(appendSqlStr)) {
            return where;
        }

        Expression appendExpression = CCJSqlParserUtil.parseCondExpression(appendSqlStr);

        if (where == null) {
            return appendExpression;
        }

        return new AndExpression(where, appendExpression);
    }


}

