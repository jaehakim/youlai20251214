package com.youlai.boot.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.youlai.boot.plugin.mybatis.MyDataPermissionHandler;
import com.youlai.boot.plugin.mybatis.MyMetaObjectHandler;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * MyBatis-Plus 설정 클래스
 *
 * @author Ray.Hao
 * @since 2022/7/2
 */

@Configuration
@EnableTransactionManagement
public class MybatisConfig {

    @Autowired
    private DataSource dataSource;

    /**
     * MyBatis databaseId 프로바이더 설정
     * 데이터베이스 종류를 자동으로 감지하여 XML에서 databaseId="mysql" 또는 default 쿼리를 선택
     */
    @Bean
    public VendorDatabaseIdProvider vendorDatabaseIdProvider() {
        VendorDatabaseIdProvider provider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.setProperty("MySQL", "mysql");
        properties.setProperty("Oracle", "oracle");
        properties.setProperty("PostgreSQL", "postgresql");
        provider.setProperties(properties);
        return provider;
    }

    /**
     * 데이터베이스 타입 감지
     */
    private DbType detectDbType() {
        try {
            try (var connection = dataSource.getConnection()) {
                String dbProduct = connection.getMetaData().getDatabaseProductName().toLowerCase();
                if (dbProduct.contains("oracle")) {
                    return DbType.ORACLE;
                } else if (dbProduct.contains("mysql")) {
                    return DbType.MYSQL;
                } else if (dbProduct.contains("postgres")) {
                    return DbType.POSTGRE_SQL;
                }
            }
        } catch (Exception e) {
            // Default to MySQL if detection fails
            // 로그 출력 (선택사항)
        }
        return DbType.MYSQL;
    }

    /**
     * 페이지네이션 플러그인과 데이터 권한 플러그인
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //데이터 권한
        interceptor.addInnerInterceptor(new DataPermissionInterceptor(new MyDataPermissionHandler()));
        //페이지네이션 플러그인 - 데이터베이스 자동 감지
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(detectDbType()));

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
