package com.erp_mes.erp.config.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

import com.erp_mes.mes.stock.dto.MaterialDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

// 엔티티로부터 테이블 정보 가져오기
//System.out.println("엔티티 정보 : " + TableMetadataManager.getTableInfo(item));
// ---------------------------------------------------------------
// ---------------------------------------------------------------
// DTO 객체로부터 테이블 정보 가져오기
//System.out.println("DTO 객체 정보 : " + TableMetadataManager.getTableInfo(itemDTO));
// ---------------------------------------------------------------
@Component
public class TableMetadataManager {
	
	// DTO별로 실제 테이블명과 PK를 등록할 수 있는 Map
    private static final Map<Class<?>, TableInfo> dtoTableMap = new HashMap<>();

    // static 블럭을 사용하여 클래스 로딩 시 해당 코드 블럭 실행
    static {
        // DTO 클래스별 실제 테이블명과 PK 컬럼명 등록
        // 필요에 따라 프로젝트 DTO 추가 => 이 때, TableInfo 클래스는 내부클래스 형태로 정의
//        dtoTableMap.put(ItemDTO.class, new TableInfo("items", "id"));
        dtoTableMap.put(MaterialDTO.class, new TableInfo("material", "materialId"));
    }
	
	/**
     * 주어진 엔티티 또는 DTO 객체에서 접근하는
     * 테이블명과 PK 컬럼명을 사용하여 
     * TableInfo 타입 객체 생성 및 리턴
     *
     * @param obj - JPA 엔티티 또는 DTO 객체
     */
    public static TableInfo getTableInfo(Object obj) {
    	// 전달받은 객체 null 판별
    	if (obj == null) {
            System.out.println("객체가 null 일 수 없음!");
            return null;
        }

    	// Hibernate 프록시 처리 - JPA 엔티티일 경우 안전하게 실제 클래스 추출
    	// => 일반 객체는 물론 프록시 객체일 때도 실제 클래스까지 추출 가능하도록 하이버네이트를 통해 클래스 추출
        Class<?> clazz = Hibernate.getClass(obj);
    	
        // JPA 엔티티인지 확인 => 엔티티 객체에서 테이블 정보 추출하여 리턴
        if (clazz.isAnnotationPresent(Entity.class)) { // @Entity 어노테이션 확인
            return getJpaEntityInfo(clazz);
        }
        
        // JPA 엔티티가 아니면 DTO 로 처리 - 등록된 매핑 정보 활용하여 테이블 정보로 변환하여 리턴
        TableInfo info = dtoTableMap.get(clazz);
        if (info != null) {
            return info;
        }

        // 등록 안된 DTO → 클래스명 기반 추정(클래스명을 테이블명(소문자)으로, PK 컬럼명은 기본값 "id" 로 지정)
        return new TableInfo(clazz.getSimpleName().toLowerCase(), "id");
    }
    
    // ===============================
    // JPA 엔티티 TableInfo 생성
    private static TableInfo getJpaEntityInfo(Class<?> entityClass) {
        // 테이블명
        Table table = entityClass.getAnnotation(Table.class); // @Table 어노테이션 붙은 대상 정보 가져오기
        String tableName = (table != null && !table.name().isEmpty()) ? table.name() : entityClass.getSimpleName();

        // PK 컬럼명
        String pkColumnName = null;
        for (Field field : entityClass.getDeclaredFields()) { // 엔티티 클래스 내의 모든 필드 정보 가져오기
            if (field.isAnnotationPresent(Id.class)) { // @Id 어노테이션 붙은 대상 확인
                Column column = field.getAnnotation(Column.class); // @Column 어노테이션 붙은 대상 정보 가져오기
                pkColumnName = (column != null && !column.name().isEmpty()) ? column.name() : field.getName();
                break;
            }
        }

        System.out.println("엔티티 클래스: " + entityClass.getSimpleName());
        System.out.println("테이블명: " + tableName);
        System.out.println("PK 컬럼명: " + pkColumnName);
        
        return new TableInfo(tableName, pkColumnName);
    }
    
    
    
    // ===============================
    // DTO 매핑 정보 클래스
    @Getter
    @AllArgsConstructor
    @ToString
    public static class TableInfo {
        private final String tableName; // 테이블명
        private final String pkColumnName; // PK 컬럼명
    }
	
}