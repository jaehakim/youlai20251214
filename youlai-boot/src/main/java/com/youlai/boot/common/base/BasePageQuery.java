package com.youlai.boot.common.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.CaseFormat;
import java.util.*;

/**
 * 기본 페이징 요청 객체
 *
 * @author haoxr
 * @since 2021/2/28
 */
@Data
@Schema
public class BasePageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "페이지 번호", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private int pageNum = 1;

    @Schema(description = "페이지당 레코드 수", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    private int pageSize = 20;

    // 정렬 관련 필드 추가
    @Schema(description = "정렬 필드 (예: createTime, updateTime)")
    private String sortBy;

    @Schema(description = "정렬 방향 (asc, desc)")
    private String sortDirection;

    /**
     * 정렬 조건이 있는지 확인
     */
    public boolean hasSortCondition() {
        return StringUtils.hasText(sortBy) && StringUtils.hasText(sortDirection);
    }


    /**
     * MyBatis Plus OrderItem으로 변환
     * 자식 클래스에서 오버라이드 가능
     * @return OrderItem 리스트
     */
    public List<OrderItem> getOrderItems() {
        List<OrderItem> orderItems = new ArrayList<>();

        if (hasSortCondition()) {
            // 정렬 필드 유효성 검사
            if (isValidSortField(sortBy)) {
                // 카멜케이스를 언더스코어로 변환 (createTime -> create_time)
                String columnName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, sortBy);

                if ("asc".equalsIgnoreCase(sortDirection)) {
                    orderItems.add(OrderItem.asc(columnName));
                } else if ("desc".equalsIgnoreCase(sortDirection)) {
                    orderItems.add(OrderItem.desc(columnName));
                }
            }
        }

        // 기본 정렬 조건 추가 (자식 클래스에서 오버라이드 가능)
        if (orderItems.isEmpty()) {
            orderItems.addAll(getDefaultOrderItems());
        }

        return orderItems;
    }

    /**
     * 기본 정렬 조건 반환 (자식 클래스에서 오버라이드)
     * @return 기본 OrderItem 리스트
     */
    protected List<OrderItem> getDefaultOrderItems() {
        return List.of(OrderItem.desc("create_time"));
    }

    /**
     * 허용된 정렬 필드인지 검사 (자식 클래스에서 오버라이드)
     * @param field 정렬 필드명
     * @return 허용 여부
     */
    public boolean isValidSortField(String field) {
        // 기본적으로 허용되는 공통 필드들
        Set<String> commonFields = Set.of(
                "id", "createTime", "updateTime", "createBy", "updateBy"
        );

        // SQL Injection 방지를 위한 기본 검사
        if (!isSafeSortField(field)) {
            return false;
        }

        return commonFields.contains(field) || getCustomAllowedSortFields().contains(field);
    }

    /**
     * 자식 클래스에서 추가로 허용할 정렬 필드 반환
     * @return 커스텀 허용 필드 집합
     */
    protected Set<String> getCustomAllowedSortFields() {
        return Collections.emptySet();
    }

    /**
     * SQL Injection 방지를 위한 필드명 안전성 검사
     * @param sortField 정렬 필드명
     * @return 안전한 필드명 여부
     */
    private boolean isSafeSortField(String sortField) {
        // 영문자, 숫자, 언더스코어만 허용 (첫 글자는 영문자 또는 언더스코어)
        return sortField != null && sortField.matches("^[a-zA-Z_][a-zA-Z0-9_]*$");
    }

    /**
     * 페이지 객체 생성 및 정렬 조건 설정
     * @param <T> 엔티티 타입
     * @return 설정된 Page 객체
     */
    public <T> Page<T> buildPage() {
        Page<T> page = new Page<>(pageNum, pageSize);
        page.setOrders(getOrderItems());
        return page;
    }

    /**
     * 페이지 객체 생성 (클래스 타입 지정)
     * @param clazz 엔티티 클래스
     * @param <T> 엔티티 타입
     * @return 설정된 Page 객체
     */
    public <T> Page<T> buildPage(Class<T> clazz) {
        return buildPage();
    }

    /**
     * 정렬 조건 초기화
     */
    public void clearSort() {
        this.sortBy = null;
        this.sortDirection = null;
    }

    /**
     * 정렬 조건 설정
     * @param sortBy 정렬 필드
     * @param sortDirection 정렬 방향
     */
    public void setSort(String sortBy, String sortDirection) {
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }

    /**
     * 오름차순 정렬 설정
     * @param sortBy 정렬 필드
     */
    public void setSortAsc(String sortBy) {
        setSort(sortBy, "asc");
    }

    /**
     * 내림차순 정렬 설정
     * @param sortBy 정렬 필드
     */
    public void setSortDesc(String sortBy) {
        setSort(sortBy, "desc");
    }

    /**
     * 페이지 번호 유효성 검사 및 보정
     */
    public void validateAndCorrectPageNum() {
        if (pageNum < 1) {
            pageNum = 1;
        }
    }

    /**
     * 페이지 크기 유효성 검사 및 보정
     */
    public void validateAndCorrectPageSize() {
        if (pageSize < 1) {
            pageSize = 10;
        } else if (pageSize > 1000) {
            pageSize = 1000; // 최대 1000개로 제한
        }
    }

    /**
     * 모든 파라미터 유효성 검사 및 보정
     */
    public void validateAndCorrect() {
        validateAndCorrectPageNum();
        validateAndCorrectPageSize();

        // 정렬 방향 보정
        if (StringUtils.hasText(sortDirection)) {
            String normalizedDirection = sortDirection.toLowerCase();
            if (!"asc".equals(normalizedDirection) && !"desc".equals(normalizedDirection)) {
                sortDirection = null;
            } else {
                sortDirection = normalizedDirection;
            }
        }
    }
}
