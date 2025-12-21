package com.youlai.boot.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;

import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;

import java.util.*;

/**
 * 시스템 모듈 코드 생성
 * <p>
 * 코드 생성, MySQL 테이블 코드 생성, 자동 코드 생성
 *
 * @author Ray Hao
 * @see <a href="https://baomidou.com/pages/981406">코드 생성기 설정</a>
 * @since 2024/4/9
 */
public class SystemCodeGenerator {

    private static final DataSourceConfig.Builder DATA_SOURCE_CONFIG = new DataSourceConfig
            .Builder("jdbc:mysql://localhost:3306/youlai_boot?serverTimezone=Asia/Shanghai", "root", "123456");

    /**
     * 실행 run
     */
    public static void main(String[] args) {
        FastAutoGenerator.create(DATA_SOURCE_CONFIG)
                // 전역 설정
                .globalConfig((scanner, builder) -> {
                    builder.outputDir(System.getProperty("user.dir") + "/src/main/java")
                            .author("Ray Hao") // 작성자 설정
                    ;
                })
                // 패키지 설정
                .packageConfig(builder -> {
                            builder
                                    .parent("com.youlai.boot.system")
                                    .entity("model.entity")
                                    .mapper("mapper")
                                    .service("platform")
                                    .serviceImpl("platform.impl")
                                    .controller("controller")
                                    .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + "/src/main/resources/mapper"));
                        }
                )
                // 주입 설정 (확장 클래스의 템플릿 경로와 패키지 경로 설정)
                .injectionConfig(consumer -> {
                    List<CustomFile> customFiles = new ArrayList<>();
                    customFiles.add(new CustomFile.Builder().fileName("VO.java").templatePath("/templates/vo.java.vm").packageName("model.vo").build());
                    customFiles.add(new CustomFile.Builder().fileName("DTO.java").templatePath("/templates/dto.java.vm").packageName("model.dto").build());
                    customFiles.add(new CustomFile.Builder().fileName("BO.java").templatePath("/templates/bo.java.vm").packageName("model.bo").build());
                    customFiles.add(new CustomFile.Builder().fileName("PageQuery.java").templatePath("/templates/query.java.vm").packageName("model.query").build());
                    customFiles.add(new CustomFile.Builder().fileName("PageVO.java").templatePath("/templates/pageVO.java.vm").packageName("model.vo").build());
                    customFiles.add(new CustomFile.Builder().fileName("Form.java").templatePath("/templates/form.java.vm").packageName("model.form").build());
                    customFiles.add(new CustomFile.Builder().fileName("Converter.java").templatePath("/templates/converter.java.vm").packageName("converter").build());
                    consumer.customFile(customFiles);
                    consumer.beforeOutputFile((tableInfo, objectMap) -> {
                        // 각 테이블에 대해 첫 글자가 소문자인 엔티티 이름 생성
                        String entityName = tableInfo.getEntityName();
                        String lowerCaseEntity = entityName.substring(0, 1).toLowerCase() + entityName.substring(1);
                        // 사용자 정의 파라미터 주입
                        objectMap.put("lowerFirstEntityName", lowerCaseEntity);

                    });

                })
                // 전략 설정
                .strategyConfig((scanner, builder) -> {

                            builder.entityBuilder()
                                    .enableLombok() // lombok 사용 여부
                                    //.enableFileOverride() // 이미 생성된 파일 덮어쓰기 활성화
                                    .logicDeleteColumnName("deleted") // 논리 삭제 필드명
                                    .enableRemoveIsPrefix() // is 접두사 제거 활성화
                            ;

                            builder.mapperBuilder()
                                    .enableBaseColumnList()
                                    .enableBaseResultMap()
                            ;

                            builder.serviceBuilder()
                                    .formatServiceFileName("%sService"
                                    );


                            builder.addTablePrefix("sys_") // 테이블 접두사 제거 필터 sys_user 테이블에서 엔티티 클래스 User.java 생성
                                    .addInclude(scanner.apply("테이블 이름을 입력하세요. 여러 테이블은 쉼표로 구분하세요"));
                        }
                )
                .execute()

        ;
    }
}
