package com.youlai.boot.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.youlai.boot.plugin.mybatis.MyDataPermissionHandler;
import com.youlai.boot.plugin.mybatis.MyMetaObjectHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis-Plus 설정 클래스
 *
 * @author Ray.Hao
 * @since 2022/7/2
 */
@Configuration
@EnableTransactionManagement
public class MybatisConfig {

    /**
     * 페이지네이션 플러그인과 데이터 권한 플러그인
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //데이터 권한
        interceptor.addInnerInterceptor(new DataPermissionInterceptor(new MyDataPermissionHandler()));
        //페이지네이션 플러그인
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

        return interceptor;
    }

    /**
     * 데이터베이스 생성자, 생성 시간, 수정자, 수정 시간 자동 입력
     */
    @Bean
    public GlobalConfig globalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setMetaObjectHandler(new MyMetaObjectHandler());
        return globalConfig;
    }

}
